/**
 *
 */
package com.olympus.oca.commerce.validators;

import com.olympus.oca.commerce.core.enums.AccessType;
import com.olympus.oca.commerce.dto.user.AccountPreferencesWsDTO;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;


public class OcaAccessTypeValidator implements Validator
{
	@Resource(name = "b2bCustomerFacade")
	private CustomerFacade customerFacade;

	@Override
	public boolean supports(final Class<?> arg0)
	{
		return String.class.isAssignableFrom(arg0);
	}

	@Override
	public void validate(Object target, Errors errors) {
			final AccountPreferencesWsDTO accountPreferences = (AccountPreferencesWsDTO) target;
			if(Objects.nonNull(accountPreferences.getAccessType())){
				if(!(accountPreferences.getAccessType().equals(AccessType.PLACE_ORDER_AND_CHECK_ORDERSTATUS.toString()) || accountPreferences.getAccessType().equals(AccessType.CHECK_ORDERSTATUS.toString()))) {
					throw new IllegalArgumentException("Invalid access type: " + accountPreferences.getAccessType() + ". Allowed values are PLACE_ORDER_AND_CHECK_ORDERSTATUS or CHECK_ORDERSTATUS.");
				}
			}
	}
	}
