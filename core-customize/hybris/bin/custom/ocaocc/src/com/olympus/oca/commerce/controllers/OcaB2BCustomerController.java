/**
 *
 */
package com.olympus.oca.commerce.controllers;

import com.olympus.oca.commerce.core.enums.AccessType;
import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import com.olympus.oca.commerce.dto.order.HeavyOrderQuestionsCartWsDTO;
import com.olympus.oca.commerce.dto.user.AccountPreferencesWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import com.olympus.oca.commerce.facades.company.OcaB2BUnitFacade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@ApiVersion("v2")
@Api(tags = "OCA B2B Customer")
public class OcaB2BCustomerController extends OcaBaseController
{
	@Resource(name = "b2bUnitFacade")
	private OcaB2BUnitFacade ocaB2BUnitFacade;

	@Resource(name="ocaAccessTypeValidator")
	private Validator ocaAccessTypeValidator;

	@ApiOperation(nickname = "setDefaultUnit", value = "Sets the default b2b Unit in customer's account.", notes = "Sets the default b2b Unit in customer's account.")
	@RequestMapping(value = "setDefaultUnit", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ApiBaseSiteIdAndUserIdParam
	public void setDefaultUnit(@ApiParam(value = "The id of the B2B Unit.", required = true)
	@RequestParam(required = true)
	final String b2bUnitId, @ApiFieldsParam
	@RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL)
	final String fields)
	{
		validateB2bUnitId(b2bUnitId);
		ocaB2BUnitFacade.setDefaultB2BUnit(b2bUnitId);
	}

	@ApiOperation(nickname = "saveAccessType", value = "changes the accessType", notes = "changes the accessType of a user.")
	@RequestMapping(value = "saveAccessType", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdAndUserIdParam
	public void saveAccessType(@RequestBody final AccountPreferencesWsDTO accountPreferences, @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL)final String fields){
		final Errors errors = new BeanPropertyBindingResult(accountPreferences, "accessType");
		ocaAccessTypeValidator.validate(accountPreferences,errors);
		ocaB2BUnitFacade.saveAccessType(accountPreferences);
	}
}
