/**
 *
 */
package com.olympus.oca.commerce.validators;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class OcaB2bUnitValidator implements Validator
{
	@Resource(name = "b2bCustomerFacade")
	private CustomerFacade customerFacade;

	@Override
	public boolean supports(final Class<?> arg0)
	{
		return String.class.isAssignableFrom(arg0);
	}

	@Override
	public void validate(final Object obj, final Errors error)
	{
		final String b2bUnitId = (String) obj;
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		if (StringUtils.isBlank(b2bUnitId)
				|| !customerData.getUnits().stream().filter(unit -> unit.getUid().equals(b2bUnitId)).findAny().isPresent())
		{
			error.reject("user.b2bUnitIdInvalid");
		}
	}

}


