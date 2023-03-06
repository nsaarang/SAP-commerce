package com.olympus.oca.commerce.facades.cart;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.HeavyOrderQuestionsCartData;
import de.hybris.platform.commercefacades.product.data.DeliveryOptionData;
import de.hybris.platform.commercefacades.product.data.PurchaseOrderData;
import de.hybris.platform.ocafacades.shipping.data.ShippingCarrierListData;

import java.util.List;


/**
 * The Interface OcaCartFacade.
 */
public interface OcaCartFacade extends CartFacade
{

	/**
	 * Gets the shipping carrier list for third party.
	 *
	 * @return the shipping carrier list for third party
	 */
	ShippingCarrierListData getShippingCarrierListForThirdParty();

	/**
	 * Sets the third party shipping carrier.
	 *
	 * @param carrierCode
	 *                          the carrier code
	 * @param carrierAccount
	 *                          the carrier account
	 */
	void setThirdPartyShippingCarrier(String carrierCode, String carrierAccount);

	/**
	 * Auto generate PO number.
	 *
	 * @param b2bUnitId
	 *                     the b 2 b unit id
	 * @return the purchase order data
	 */
	PurchaseOrderData autoGeneratePONumber(String b2bUnitId);

	/**
	 * Set the B2B Unit in session cart
	 *
	 * @param b2bUnitId
	 * @param calculateCart
	 * @return Cart Data after persisting the b2b unit
	 */
	CartData updateSessionCart(String b2bUnitId, boolean calculateCart);

	/**
	 * Gets the delivery options.
	 *
	 * @param shippingCarrierCode
	 *                               the shipping carrier code
	 * @return the delivery options
	 */
	List<DeliveryOptionData> getDeliveryOptions(String shippingCarrierCode);

	/**
	 * Select shipping carrier.
	 *
	 * @return the shipping carrier list data
	 */
	ShippingCarrierListData selectShippingCarrier();

	CartData getMiniCartSummary();

	void saveHeavyOrderResponse(HeavyOrderQuestionsCartData questionnaire);

}
