/**
 *
 */
package com.olympus.oca.commerce.core.cart.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.impl.DefaultCartService;

import java.util.List;

import com.olympus.oca.commerce.core.cart.OcaCartService;
import com.olympus.oca.commerce.core.cart.dao.OcaCartDao;
import com.olympus.oca.commerce.core.model.DeliveryOptionModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierModel;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * The Class DefaultOcaCartService.
 */
public class DefaultOcaCartService extends DefaultCartService implements OcaCartService
{

	/** The oca cart dao. */
	private OcaCartDao ocaCartDao;

	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;


	/**
	 * Gets the shipping carrier list for third party.
	 *
	 * @return the shipping carrier list for third party
	 */
	@Override
	public List<ShippingCarrierModel> getShippingCarrierListForThirdParty()
	{
		return getOcaCartDao().getShippingCarrierListForThirdParty();
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
		final CartModel cart = getSessionCart();
		cart.setShippingCarrier(carrierCode);
		cart.setShippingCarrierAccount(carrierAccount);
		getModelService().save(cart);
	}

	/**
	 * Set the B2B Unit in session cart
	 * @param b2bUnitId
	 * @param calculateCart
	 * @return Cart Model after persisting the b2b unit
	 */
	@Override
	public CartModel updateSessionCart(String b2bUnitId, boolean calculateCart) {
		final CartModel cart = getSessionCart();
		if(null == cart.getUnit() || !b2bUnitId.equalsIgnoreCase(cart.getUnit().getUid())){
			final B2BUnitModel unit = getB2bUnitService().getUnitForUid(b2bUnitId);
			validateParameterNotNull(unit, String.format("No unit found for uid %s", b2bUnitId));
			cart.setUnit(unit);
			getModelService().save(cart);
			if(calculateCart)
			{
				final CommerceCartParameter parameter = new CommerceCartParameter();
				parameter.setEnableHooks(true);
				parameter.setCart(cart);
				getCommerceCartCalculationStrategy().calculateCart(parameter);
			}
		}
		return cart;
	}

	/**
	 * Gets the oca cart dao.
	 *
	 * @return the ocaCartDao
	 */
	public OcaCartDao getOcaCartDao()
	{
		return ocaCartDao;
	}

	/**
	 * Sets the oca cart dao.
	 *
	 * @param ocaCartDao
	 *                      the ocaCartDao to set
	 */
	public void setOcaCartDao(final OcaCartDao ocaCartDao)
	{
		this.ocaCartDao = ocaCartDao;
	}

	public B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService<B2BUnitModel, UserModel> b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

	public CommerceCartCalculationStrategy getCommerceCartCalculationStrategy() {
		return commerceCartCalculationStrategy;
	}

	public void setCommerceCartCalculationStrategy(CommerceCartCalculationStrategy commerceCartCalculationStrategy) {
		this.commerceCartCalculationStrategy = commerceCartCalculationStrategy;
	}

	/**
	 * Gets the delivery options.
	 *
	 * @param shippingCarrierCode the shipping carrier code
	 * @return the delivery options
	 */
	@Override
	public List<DeliveryOptionModel> getDeliveryOptions(final String shippingCarrierCode)
	{
		return ocaCartDao.getDeliveryOptions(shippingCarrierCode);
	}
	
	/**
	 * Gets the shipping carrier for code.
	 *
	 * @param carrierCode the carrier code
	 * @return the shipping carrier for code
	 */
	@Override
	public ShippingCarrierModel getShippingCarrierForCode(final String carrierCode)
	{
		return ocaCartDao.getShippingCarrierForCode(carrierCode);
	}
}
