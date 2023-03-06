package com.olympus.oca.commerce.core.product.interceptors;

import com.olympus.oca.commerce.core.enums.AccessType;
import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import org.apache.log4j.Logger;

public class OcaB2BCustomerInterceptor implements PrepareInterceptor
{

	private static final Logger LOG = Logger.getLogger(OcaB2BCustomerInterceptor.class);

	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = ((B2BCustomerModel) model);
			AccountPreferencesModel accountPreferencesModel= customer.getAccountPreferences();
			if(accountPreferencesModel==null){
				accountPreferencesModel=new AccountPreferencesModel();
			}
			accountPreferencesModel.setAccessType(AccessType.PLACE_ORDER_AND_CHECK_ORDERSTATUS);
			customer.setAccountPreferences(accountPreferencesModel);
		}
	}
}