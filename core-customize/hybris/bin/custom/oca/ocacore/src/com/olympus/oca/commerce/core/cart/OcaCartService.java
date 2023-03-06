/**
 *
 */
package com.olympus.oca.commerce.core.cart;

import java.util.List;

import com.olympus.oca.commerce.core.model.DeliveryOptionModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierModel;
import de.hybris.platform.core.model.order.CartModel;


/**
 * The Interface OcaCartService.
 */
public interface OcaCartService
{

	/**
	 * Gets the shipping carrier list for third party.
	 *
	 * @return the shipping carrier list for third party
	 */
	List<ShippingCarrierModel> getShippingCarrierListForThirdParty();

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
	 * Set the B2B Unit in session cart
	 * @param b2bUnitId
	 * @param calculateCart
	 * @return Cart Model after persisting the b2b unit
	 */
	CartModel updateSessionCart(String b2bUnitId, boolean calculateCart);
	
	/**
	 * Gets the delivery options.
	 *
	 * @param shippingCarrierCode the shipping carrier code
	 * @return the delivery options
	 */
	List<DeliveryOptionModel> getDeliveryOptions(String shippingCarrierCode);
	
	/**
	 * Gets the shipping carrier for code.
	 *
	 * @param carrierCode the carrier code
	 * @return the shipping carrier for code
	 */
	ShippingCarrierModel getShippingCarrierForCode(String carrierCode);

}
