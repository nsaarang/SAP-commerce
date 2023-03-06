/**
 *
 */
package com.olympus.oca.commerce.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.ocafacades.shipping.data.ShippingCarrierData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.olympus.oca.commerce.core.model.ShippingCarrierModel;


/**
 * The Class OcaShippingCarrierPopulator.
 */
public class OcaShippingCarrierPopulator implements Populator<ShippingCarrierModel, ShippingCarrierData>
{

	/**
	 * Populate.
	 *
	 * @param source
	 *                  the source
	 * @param target
	 *                  the target
	 * @throws ConversionException
	 *                                the conversion exception
	 */
	@Override
	public void populate(final ShippingCarrierModel source, final ShippingCarrierData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName());
	}

}
