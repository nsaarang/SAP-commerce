/**
 *
 */
package com.olympus.oca.commerce.facades.b2bcustomer.converters.populator;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class OcaB2BCustomerPopulator implements Populator<B2BCustomerModel, CustomerData>
{

	@Override
	public void populate(final B2BCustomerModel source, final CustomerData target) throws ConversionException
	{
		final List<PrincipalGroupModel> units = source.getGroups().stream().filter(unit -> unit instanceof B2BUnitModel)
				.collect(Collectors.toList());
		Collections.sort(units, (unit1, unit2) -> (unit1.getLocName().compareTo(unit2.getLocName())));
		final List<B2BUnitData> b2bunitDataList = new ArrayList();
		units.stream().forEach(unit -> {
			b2bunitDataList.add(poulateB2BUnitdata((B2BUnitModel) unit, source.getDefaultB2BUnit()));
		});
		target.setUnits(b2bunitDataList);
		if (source.getAccountPreferences() != null){
		if(Objects.nonNull(source.getAccountPreferences().getAccessType())) {
			target.setAccountPreferences(String.valueOf(source.getAccountPreferences().getAccessType()));
		}
	}}

	protected B2BUnitData poulateB2BUnitdata(final B2BUnitModel source, final B2BUnitModel defaultB2BUnit)
	{
		final B2BUnitData b2BUnitData = new B2BUnitData();
		b2BUnitData.setUid(source.getUid());
		b2BUnitData.setName(source.getLocName());
		b2BUnitData.setActive(Boolean.TRUE.equals(source.getActive()));
		if (null != defaultB2BUnit)
		{
			b2BUnitData.setDefaultUnit(source.getUid().equals(defaultB2BUnit.getUid()));
		}
		return b2BUnitData;
	}
}

