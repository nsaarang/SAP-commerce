/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.controllers;

import de.hybris.platform.commercefacades.product.data.DeliveryOptionData;
import de.hybris.platform.commercefacades.product.data.DeliveryOptionListData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.product.DeliveryOptionListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PurchaseOrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.ShippingCarrierListWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.olympus.oca.commerce.facades.cart.OcaCartFacade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * The Class OcaCheckoutController.
 */

@Controller
@ApiVersion("v2")
@Api(tags = "OCA Checkout")
public class OcaCheckoutController extends OcaBaseController
{

	/** The cart facade. */
	@Resource(name = "cartFacade")
	private OcaCartFacade cartFacade;

	/** The data mapper. */
	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;

	/**
	 * Gets the shipping carrier list for third party.
	 *
	 * @param fields
	 *           the fields
	 * @return the shipping carrier list for third party
	 */
	@ApiOperation(nickname = "GetShippingCarrierListForAddingThirdPartyCarrier", value = "Get Shipping Carrier List For Adding Third Party Carrier", notes = "Get Shipping Carrier List For Adding Third Party Carrier")
	@RequestMapping(value = "/{baseSiteId}/shippingCarrierListForThirdParty", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdUserIdAndCartIdParam
	public ShippingCarrierListWsDTO getShippingCarrierListForThirdParty(@ApiFieldsParam
	@RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL)
	final String fields)
	{
		return dataMapper.map(cartFacade.getShippingCarrierListForThirdParty(), ShippingCarrierListWsDTO.class, fields);
	}

	/**
	 * Sets the third party shipping carrier.
	 *
	 * @param carrierCode
	 *           the carrier code
	 * @param carrierAccount
	 *           the carrier account
	 * @param fields
	 *           the fields
	 */
	@ApiOperation(nickname = "setThirdPartyShippingCarrier", value = "Set Third Party Shipping Carrier", notes = "Set Third Party Shipping Carrier")
	@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/setThirdPartyShippingCarrier", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void setThirdPartyShippingCarrier(@ApiParam(value = "Shipping Carrier Code", required = true)
	@RequestParam(required = true)
	final String carrierCode, @ApiParam(value = "Carrier Account", required = true)
	@RequestParam(required = true)
	final String carrierAccount, @ApiFieldsParam
	@RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL)
	final String fields)
	{
		cartFacade.setThirdPartyShippingCarrier(carrierCode, carrierAccount);
	}


	/**
	 * Auto generate PO number.
	 *
	 * @param b2bUnitId
	 *           the b 2 b unit id
	 * @param fields
	 *           the fields
	 * @return the purchase order ws DTO
	 */
	@ApiOperation(nickname = "AutoGeneratePONumber", value = "Auto Generate PONumber", notes = "Auto Generate PONumber")
	@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/autogeneratePO", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdUserIdAndCartIdParam
	public PurchaseOrderWsDTO autoGeneratePONumber(@ApiParam(value = "Purchase order number to assign to the checkout cart.")
	@RequestParam(required = true)
	final String b2bUnitId, @ApiFieldsParam
	@RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL)
	final String fields)
	{
		validateB2bUnitId(b2bUnitId);
		return dataMapper.map(cartFacade.autoGeneratePONumber(b2bUnitId), PurchaseOrderWsDTO.class, fields);
	}

	/**
	 * Gets the delivery option.
	 *
	 * @param shippingCarrierCode
	 *           the shipping carrier code
	 * @return the delivery option
	 */
	@ApiOperation(nickname = "getDeliveryOptions", value = "getDeliveryOptions", notes = "getDeliveryOptions")
	@RequestMapping(value = "/{baseSiteId}/getDeliveryOption", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdUserIdAndCartIdParam
	public DeliveryOptionListWsDTO getDeliveryOption(@ApiParam(value = "Delivery options based on shipping carrier code")
	@RequestParam(required = true)
	final String shippingCarrierCode)
	{
		final List<DeliveryOptionData> deliveryOptions = cartFacade.getDeliveryOptions(shippingCarrierCode);
		final DeliveryOptionListData deliveryOptionListData = new DeliveryOptionListData();
		deliveryOptionListData.setDeliveryOption(deliveryOptions);
		return dataMapper.map(deliveryOptionListData, DeliveryOptionListWsDTO.class);

	}

	@ApiOperation(nickname = "selectShippingCarrier", value = "Select Shipping Carrier at Checkout", notes = "Select Shipping Carrier at Checkout")
	@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/selectShippingCarrier", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdUserIdAndCartIdParam
	public ShippingCarrierListWsDTO getThirdPartyShippingCarrier(@ApiFieldsParam
	@RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL)
	final String fields)
	{
		return dataMapper.map(cartFacade.selectShippingCarrier(), ShippingCarrierListWsDTO.class, fields);

	}
}
