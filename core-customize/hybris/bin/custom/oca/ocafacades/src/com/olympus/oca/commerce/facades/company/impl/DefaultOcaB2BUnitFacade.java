/**
 *
 */
package com.olympus.oca.commerce.facades.company.impl;

import com.olympus.oca.commerce.core.b2bCustomer.OcaB2BCustomerService;
import com.olympus.oca.commerce.core.enums.AccessType;
import com.olympus.oca.commerce.core.jalo.AccountPreferences;
import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import com.olympus.oca.commerce.core.model.HeavyOrderQuestionsModel;
import com.olympus.oca.commerce.dto.user.AccountPreferencesWsDTO;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.company.impl.DefaultB2BUnitFacade;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.user.UserService;

import com.olympus.oca.commerce.facades.company.OcaB2BUnitFacade;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * The Class DefaultOcaB2BUnitFacade.
 */
public  class DefaultOcaB2BUnitFacade extends DefaultB2BUnitFacade implements OcaB2BUnitFacade
{

	/** The user service. */
	private UserService userService;

	@Resource
	private OcaB2BCustomerService ocaB2BCustomerService;

	/**
	 * Sets the default B 2 B unit.
	 *
	 * @param b2bUnitId
	 *                     the new default B 2 B unit
	 */
	@Override
	public void setDefaultB2BUnit(final String b2bUnitId)
	{
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		customer.setDefaultB2BUnit(getB2BUnitService().getUnitForUid(b2bUnitId));
		getModelService().save(customer);
	}

	@Override
	public void saveAccessType(AccountPreferencesWsDTO accountPreferences) {
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		if(Objects.nonNull(customer)){
			AccountPreferencesModel accountPreferencesModel = (Objects.nonNull(customer.getAccountPreferences())) ?
					customer.getAccountPreferences() : getModelService().create(AccountPreferencesModel.class);
			if((null==accountPreferencesModel.getAccessType()) || (Objects.nonNull((accountPreferencesModel.getAccessType().getCode())) && !accountPreferences.getAccessType().equals(accountPreferencesModel.getAccessType().getCode()))) {
				accountPreferencesModel.setAccessType(AccessType.valueOf(accountPreferences.getAccessType()));
		ocaB2BCustomerService.save(accountPreferencesModel,customer);
		}}}

	/**
	 * @return the userService
	 */
	@Override
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *                       the userService to set
	 */
	@Override
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
