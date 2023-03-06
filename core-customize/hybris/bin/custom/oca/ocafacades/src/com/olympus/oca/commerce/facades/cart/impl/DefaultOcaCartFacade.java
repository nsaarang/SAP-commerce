package com.olympus.oca.commerce.facades.cart.impl;


import com.olympus.oca.commerce.core.model.HeavyOrderQuestionsModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.HeavyOrderQuestionsCartData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.product.data.DeliveryOptionData;
import de.hybris.platform.commercefacades.product.data.PurchaseOrderData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ocafacades.shipping.data.ShippingCarrierData;
import de.hybris.platform.ocafacades.shipping.data.ShippingCarrierListData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import com.olympus.oca.commerce.core.cart.OcaCartService;
import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import com.olympus.oca.commerce.core.model.DeliveryOptionModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierAccountModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierModel;
import com.olympus.oca.commerce.facades.cart.OcaCartFacade;

import javax.annotation.Resource;

/**
 * The Class DefaultOcaCartFacade.
 *
 */
public class DefaultOcaCartFacade extends DefaultCartFacade implements OcaCartFacade
{

	private Converter<DeliveryOptionModel, DeliveryOptionData> deliveryOptionConverter;


	/** The oca cart service. */
	@Resource
	private CartService cartService;
	private OcaCartService ocaCartService;

	private Converter<HeavyOrderQuestionsCartData, HeavyOrderQuestionsModel> questionnaireReverseConverter;

	/** The shipping carrier converter. */
	private Converter<ShippingCarrierModel, ShippingCarrierData> shippingCarrierConverter;

	/** The configuration service. */
	private ConfigurationService configurationService;
	/** The Constant SHIP_BY_OLYMPUS_FALLBACK_CODE. */
	protected static final String SHIP_BY_OLYMPUS_FALLBACK_CODE = "shipByOlympus";

	/** The b 2 b unit service. */
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
	/**
	 * Auto generate PO number.
	 *
	 * @param b2bUnitId
	 *                     the b 2 b unit id
	 * @return the purchase order data
	 */

	@Override
	public PurchaseOrderData autoGeneratePONumber(final String b2bUnitId)
	{
		if (hasSessionCart())
		{
			final StringBuilder poNumber = new StringBuilder();
			poNumber.append(b2bUnitId);
			poNumber.append(getName(getCartService().getSessionCart().getUser().getName()));
			poNumber.append(getDate());
			final PurchaseOrderData purchaseOrderData = new PurchaseOrderData();
			purchaseOrderData.setPurchaseOrderNumber(poNumber.toString());
			return purchaseOrderData;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Set the B2B Unit in session cart
	 *
	 * @param b2bUnitId
	 * @param calculateCart
	 * @return Cart Data after persisting the b2b unit
	 */
	@Override
	public CartData updateSessionCart(final String b2bUnitId, final boolean calculateCart)
	{
		final CartModel cart = getOcaCartService().updateSessionCart(b2bUnitId, calculateCart);
		return getCartConverter().convert(cart);
	}


	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	private String getDate()
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmm");
		formatter.setTimeZone(TimeZone.getTimeZone("EST"));
		return formatter.format(new Date());
	}

	/**
	 * Gets the name.
	 *
	 * @param name
	 *                the name
	 * @return the name
	 */
	private String getName(final String name)
	{
		if (null != name)
		{
			final String[] nameparts = name.split(" ");
			if (nameparts.length == 1)
			{
				final String firstName = nameparts[0];
				return String.valueOf(firstName.charAt(0));
			}
			else if (nameparts.length >= 2)
			{
				final String firstName = nameparts[0];
				final String lastName = nameparts[nameparts.length - 1];
				final String displayName = (String.valueOf(firstName.charAt(0)) + String.valueOf(lastName.charAt(0)));
				return displayName;
			}
		}
		return StringUtils.EMPTY;
	}
	/**
	 * Gets the delivery options.
	 *
	 * @param shippingCarrierCode
	 *                               the shipping carrier code
	 * @return the delivery options
	 */
	@Override
	public List<DeliveryOptionData> getDeliveryOptions(final String shippingCarrierCode)
	{
		List<DeliveryOptionModel> deliveryOptions = null;
		deliveryOptions = getOcaCartService().getDeliveryOptions(shippingCarrierCode);
		if (CollectionUtils.isNotEmpty(deliveryOptions))
		{
			return getDeliveryOptionConverter().convertAll(deliveryOptions);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	/**
	 * /** Gets the shipping carrier list for third party.
	 *
	 * @return the shipping carrier list for third party
	 */
	@Override
	public ShippingCarrierListData getShippingCarrierListForThirdParty()
	{
		final ShippingCarrierListData shippingCarrierListData = new ShippingCarrierListData();
		shippingCarrierListData.setShippingCarriers(
				Converters.convertAll(getOcaCartService().getShippingCarrierListForThirdParty(), getShippingCarrierConverter()));
		return shippingCarrierListData;
	}
	/**
	 * Select shipping carrier.
	 *
	 * @return the shipping carrier list data
	 */
	@Override
	public ShippingCarrierListData selectShippingCarrier()
	{
		final ShippingCarrierListData shippingCarrierListData = new ShippingCarrierListData();
		final List<ShippingCarrierData> shippingCarrierList = new ArrayList();
		ShippingCarrierData shippingCarrierData = new ShippingCarrierData();
		final String shipByOlympusCode = getConfigurationService().getConfiguration()
				.getString(OcaCoreConstants.SHIP_BY_OLYMPUS_CODE, SHIP_BY_OLYMPUS_FALLBACK_CODE);
		final CartModel cartModel = getCartService().getSessionCart();
		final B2BUnitModel b2bUnitModel = getB2bUnitService().getUnitForUid(cartModel.getUnit().getUid());
		if (null != b2bUnitModel.getShippingCarrierAccountReference())
		{
			final ShippingCarrierAccountModel shippingCarrierAccount = b2bUnitModel.getShippingCarrierAccountReference();
			shippingCarrierData = shippingCarrierConverter.convert(shippingCarrierAccount.getShippingCarrier());
			shippingCarrierData.setAccountReferenceNumber(shippingCarrierAccount.getAccountReferenceNumber());
			shippingCarrierList.add(shippingCarrierData);
		}
		else if (null != cartModel.getShippingCarrier())
		{
			shippingCarrierData.setCode(cartModel.getShippingCarrier());
			shippingCarrierData.setAccountReferenceNumber(cartModel.getShippingCarrierAccount());
			shippingCarrierList.add(shippingCarrierData);
			shippingCarrierData = shippingCarrierConverter.convert(getOcaCartService().getShippingCarrierForCode(shipByOlympusCode));
			shippingCarrierList.add(shippingCarrierData);
		}
		else
		{
			shippingCarrierData = shippingCarrierConverter.convert(getOcaCartService().getShippingCarrierForCode(shipByOlympusCode));
			shippingCarrierList.add(shippingCarrierData);
		}
		shippingCarrierListData.setShippingCarriers(shippingCarrierList);
		return shippingCarrierListData;
	}



	/**
	 * Sets the third party shipping carrier.
	 *
	 * @param carrierCode
	 *                          the carrier code
	 * @param carrierAccount
	 *                          the carrier account
	 */
	@Override
	public void setThirdPartyShippingCarrier(final String carrierCode, final String carrierAccount)
	{
		getOcaCartService().setThirdPartyShippingCarrier(carrierCode, carrierAccount);
	}
	/**
	 * Gets the oca cart service.
	 *
	 * @return the ocaCartService
	 */
	public OcaCartService getOcaCartService()
	{
		return ocaCartService;
	}
	/**
	 * Sets the oca cart service.
	 *
	 * @param ocaCartService
	 *                          the ocaCartService to set
	 */
	public void setOcaCartService(final OcaCartService ocaCartService)
	{
		this.ocaCartService = ocaCartService;
	}

	public Converter<ShippingCarrierModel, ShippingCarrierData> getShippingCarrierConverter()
	{
		return shippingCarrierConverter;
	}
	/**
	 * Sets the shipping carrier converter.
	 *
	 * @param shippingCarrierConverter
	 *                                    the shippingCarrierConverter to set
	 */
	public void setShippingCarrierConverter(final Converter<ShippingCarrierModel, ShippingCarrierData> shippingCarrierConverter)
	{
		this.shippingCarrierConverter = shippingCarrierConverter;
	}
	/**
	 * @return the deliveryOptionConverter
	 */
	public Converter<DeliveryOptionModel, DeliveryOptionData> getDeliveryOptionConverter()
	{
		return deliveryOptionConverter;
	}
	/**
	 * @param deliveryOptionConverter
	 *                                   the deliveryOptionConverter to set
	 */
	public void setDeliveryOptionConverter(final Converter<DeliveryOptionModel, DeliveryOptionData> deliveryOptionConverter)
	{
		this.deliveryOptionConverter = deliveryOptionConverter;
	}
	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}
	/**
	 * @param configurationService
	 *                                the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
	/**
	 * @return the b2bUnitService
	 */
	public B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}
	/**
	 * @param b2bUnitService
	 *                          the b2bUnitService to set
	 */
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}
    @Override
	public CartData getMiniCartSummary() {
		CartData miniCartData = super.getMiniCart();
		Integer maxValue= getConfigurationService().getConfiguration()
				.getInteger(OcaCoreConstants.MINICART_MAX_DISPLAY_VALUE,999);

		if (miniCartData.getTotalUnitCount() > maxValue) {
			miniCartData.setDisplayTotalUnitCount(maxValue.toString() + OcaCoreConstants.PLUS_SYMBOL);
		} else {
			miniCartData.setDisplayTotalUnitCount(String.valueOf(miniCartData.getTotalUnitCount()));
		}
		return miniCartData;
	}
	@Override
	public void saveHeavyOrderResponse(HeavyOrderQuestionsCartData questionnaire) {
		CartModel sessionCart = cartService.getSessionCart();
		if(Objects.nonNull(sessionCart)) {
			HeavyOrderQuestionsModel heavyOrderQuestionsModel = (Objects.nonNull(sessionCart.getHeavyOrderQuestions())) ?
					sessionCart.getHeavyOrderQuestions() : getModelService().create(HeavyOrderQuestionsModel.class);
			questionnaireReverseConverter.convert(questionnaire,heavyOrderQuestionsModel);
			if(getModelService().isModified(heavyOrderQuestionsModel)) {
				getModelService().save(heavyOrderQuestionsModel);
				getModelService().refresh(heavyOrderQuestionsModel);
			}
			saveQuestionsInUsersCart(heavyOrderQuestionsModel, sessionCart);
		}
	}

	private void saveQuestionsInUsersCart(HeavyOrderQuestionsModel heavyOrderQuestionsModel, CartModel sessionCart) {
		sessionCart.setHeavyOrderQuestions(heavyOrderQuestionsModel);
		getModelService().save(sessionCart);
		getModelService().refresh(sessionCart);
	}
	public Converter<HeavyOrderQuestionsCartData, HeavyOrderQuestionsModel> getQuestionnaireReverseConverter() {
		return questionnaireReverseConverter;
	}

	public void setQuestionnaireReverseConverter(Converter<HeavyOrderQuestionsCartData, HeavyOrderQuestionsModel> questionnaireReverseConverter) {
		this.questionnaireReverseConverter = questionnaireReverseConverter;
	}
}


