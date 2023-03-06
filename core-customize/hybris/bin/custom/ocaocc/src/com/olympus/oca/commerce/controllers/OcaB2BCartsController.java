package com.olympus.oca.commerce.controllers;

import com.olympus.oca.commerce.dto.order.HeavyOrderQuestionsCartWsDTO;
import com.olympus.oca.commerce.dto.order.MiniCartWsDTO;
import com.olympus.oca.commerce.facades.cart.impl.DefaultOcaCartFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.HeavyOrderQuestionsCartData;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservices.core.cart.impl.CommerceWebServicesCartFacade;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import com.olympus.oca.commerce.facades.cart.OcaCartFacade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.HashMap;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@ApiVersion("v2")
@Api(tags = "OCA Carts")
public class OcaB2BCartsController extends OcaBaseCommerceController{

    private static final Logger LOG = LoggerFactory.getLogger(OcaB2BCartsController.class);

    @Resource(name = "cartFacade")
    private OcaCartFacade ocaCartFacade;

    @Resource(name="ocaQuestionsValidator")
    private Validator ocaQuestionsValidator;

    @PostMapping(value="/{orgUnitId}/carts")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiOperation(nickname = "createCart", value = "Creates or restore a cart for a user.", notes = "Creates a new cart or restores an anonymous cart as a user's cart (if an old Cart Id is given in the request).")
    @ApiBaseSiteIdAndUserIdParam
    public CartWsDTO createCart(@ApiParam(value = "Anonymous cart GUID.") @RequestParam(required = false) final String oldCartId,
                                @ApiParam(value = "The GUID of the user's cart that will be merged with the anonymous cart.") @RequestParam(required = false) final String toMergeCartGuid,
                                @ApiParam(value = "Organizational Unit identifier.", required = true) @PathVariable final String orgUnitId,
                                @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
    {
        LOG.debug("createCart");
        if (StringUtils.isNotEmpty(oldCartId))
        {
            restoreAnonymousCartAndMerge(oldCartId, toMergeCartGuid);
        }
        else
        {
            restoreSavedCart(toMergeCartGuid);
        }
        //set B2B unit to the new cart
        ocaCartFacade.updateSessionCart(orgUnitId, false);
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
    }


    @ApiOperation(nickname = "getCurrentOrgCart", value = "Gets the current cart.", notes = "Gets the current cart.")
    @RequestMapping(value = "/{orgUnitId}/carts/current", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    public CartWsDTO getCurrentCart(
            @ApiParam(value = "Organizational Unit identifier.", required = true) @PathVariable final String orgUnitId,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        if (getUserFacade().isAnonymousUser())
        {
            throw new AccessDeniedException("Access is denied");
        }
        //set the b2b unit to the current cart
        ocaCartFacade.updateSessionCart(orgUnitId, true);
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
    }

    @ApiOperation(nickname = "getMiniCart", value = "Gets the mini cart.", notes = "Gets the mini cart.")
    @RequestMapping(value = "/carts/current/miniCart", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    public MiniCartWsDTO miniCart(@ApiFieldsParam
                                  @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL)
                                  final String fields) {
        //set the b2b unit to the mini cart
        return getDataMapper().map(ocaCartFacade.getMiniCartSummary(), MiniCartWsDTO.class, fields);
    }

    @ApiOperation(nickname = "saveQuestionnaire", value = "Save the Questionnaire.", notes = "Save the Questionnaire.")
    @RequestMapping(value = "/carts/{cartId}/heavyOrder/questionnaire/submit", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public Object saveHeavyOrderResponse(@RequestBody final HeavyOrderQuestionsCartWsDTO questionnaire, @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL)
    final String fields) {
        final Errors errors = new BeanPropertyBindingResult(questionnaire, "questionnaire");
        ocaQuestionsValidator.validate(questionnaire,errors);
        HashMap<String,Object> responses = new HashMap<>();
        if(errors.hasErrors()) {
            HashMap<String,String> error = new HashMap<>();
            for(FieldError fieldError : errors.getFieldErrors()) {
                error.put(fieldError.getField(),fieldError.getDefaultMessage());
            }
            responses.put("errors",error);
            responses.put("success",false);
            responses.put("message","All fields are Mandatory");
            return responses;
        }
        HeavyOrderQuestionsCartData heavyOrderQuestionsCartData = getDataMapper().map(questionnaire, HeavyOrderQuestionsCartData.class,fields);
        try {
            ocaCartFacade.saveHeavyOrderResponse(heavyOrderQuestionsCartData);
            responses.put("success",true);
            responses.put("message","Response captured successfully.");
        } catch(Exception e) {
            LOG.error("Could not save Model",e);
            responses.put("success",false);
            responses.put("message","Response could not be captured. Please try again");
        }
        return responses;
    }


	 protected void restoreAnonymousCartAndMerge(final String oldCartId, final String toMergeCartGuid)
    {
        if (getUserFacade().isAnonymousUser())
        {
            throw new CartException("Anonymous user is not allowed to copy cart!");
        }
        if (!isCartAnonymous(oldCartId))
        {
            throw new CartException("Cart is not anonymous", CartException.CANNOT_RESTORE, oldCartId);
        }
        if (StringUtils.isNotEmpty(toMergeCartGuid) && !isUserCart(toMergeCartGuid))
        {
            throw new CartException("Cart is not current user's cart", CartException.CANNOT_RESTORE, toMergeCartGuid);
        }

        final String evaluatedToMergeCartGuid = StringUtils.isNotEmpty(toMergeCartGuid) ?
                toMergeCartGuid :
                getSessionCart().getGuid();
        try
        {
            getCartFacade().restoreAnonymousCartAndMerge(oldCartId, evaluatedToMergeCartGuid);
        }
        catch (final CommerceCartMergingException e)
        {
            throw new CartException("Couldn't merge carts", CartException.CANNOT_MERGE, e);
        }
        catch (final CommerceCartRestorationException e)
        {
            throw new CartException("Couldn't restore cart", CartException.CANNOT_RESTORE, e);
        }
    }

    protected void restoreSavedCart(final String toMergeCartGuid)
    {
        if (StringUtils.isNotEmpty(toMergeCartGuid))
        {
            if (!isUserCart(toMergeCartGuid))
            {
                throw new CartException("Cart is not current user's cart", CartException.CANNOT_RESTORE, toMergeCartGuid);
            }
            try
            {
                getCartFacade().restoreSavedCart(toMergeCartGuid);
            }
            catch (final CommerceCartRestorationException e)
            {
                throw new CartException("Couldn't restore cart", CartException.CANNOT_RESTORE, toMergeCartGuid, e);
            }
        }
    }

    protected boolean isUserCart(final String toMergeCartGuid)
    {
        if (getCartFacade() instanceof CommerceWebServicesCartFacade)
        {
            final CommerceWebServicesCartFacade commerceWebServicesCartFacade = (CommerceWebServicesCartFacade) getCartFacade();
            return commerceWebServicesCartFacade.isCurrentUserCart(toMergeCartGuid);
        }
        return true;
    }

    protected boolean isCartAnonymous(final String cartGuid)
    {
        if (getCartFacade() instanceof CommerceWebServicesCartFacade)
        {
            final CommerceWebServicesCartFacade commerceWebServicesCartFacade = (CommerceWebServicesCartFacade) getCartFacade();
            return commerceWebServicesCartFacade.isAnonymousUserCart(cartGuid);
        }
        return true;
    }

}
