/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.facades.populators;
import de.hybris.platform.commercefacades.product.data.DeliveryOptionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import com.olympus.oca.commerce.core.model.DeliveryOptionModel;

public class DeliveryOptionPopulator implements Populator<DeliveryOptionModel,DeliveryOptionData>
{
	@Override
	public void populate(DeliveryOptionModel source, DeliveryOptionData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName());		
	}	
}
