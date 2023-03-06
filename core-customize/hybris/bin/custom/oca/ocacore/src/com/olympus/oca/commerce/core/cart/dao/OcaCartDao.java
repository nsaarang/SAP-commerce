/**
 *
 */
package com.olympus.oca.commerce.core.cart.dao;

import java.util.List;

import com.olympus.oca.commerce.core.model.DeliveryOptionModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierModel;


/**
 * The Interface OcaCartDao.
 */
public interface OcaCartDao
{

	/**
	 * Gets the shipping carrier list for third party.
	 *
	 * @return the shipping carrier list for third party
	 */
	public List<ShippingCarrierModel> getShippingCarrierListForThirdParty();

	/**
	 * Gets the shipping carrier for code.
	 *
	 * @param carrierCode
	 *                       the carrier code
	 * @return the shipping carrier for code
	 */
	ShippingCarrierModel getShippingCarrierForCode(String carrierCode);
	
	/**
	 * Gets the delivery options.
	 *
	 * @param shippingCarrierCode the shipping carrier code
	 * @return the delivery options
	 */
	List<DeliveryOptionModel> getDeliveryOptions(String shippingCarrierCode);
}
