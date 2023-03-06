/**
 *
 */
package com.olympus.oca.commerce.facades.b2bcustomer.converters.populator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaB2BCustomerPopulatorTest
{
	OcaB2BCustomerPopulator ocaB2BCustomerPopulator = new OcaB2BCustomerPopulator();

	@Mock
	private LocaleProvider localeProvider;

	@Test
	public void testPopulate()
	{
		localeProvider = Mockito.mock(LocaleProvider.class);
		final Locale locale = new Locale("EN");
		final B2BCustomerModel source = new B2BCustomerModel();
		final CustomerData target = new CustomerData();
		final B2BUnitModel unit1 = new B2BUnitModel();
		((ItemModelContextImpl) (ModelContextUtils.getItemModelContext(unit1))).setLocaleProvider(localeProvider);
		Mockito.when(localeProvider.getCurrentDataLocale()).thenReturn(locale);
		unit1.setLocName("firstUnit");
		unit1.setUid("firstUnit");
		unit1.setActive(true);

		final B2BUnitModel unit2 = new B2BUnitModel();
		((ItemModelContextImpl) (ModelContextUtils.getItemModelContext(unit2))).setLocaleProvider(localeProvider);
		unit2.setLocName("secondUnit");
		unit2.setUid("secondUnit");
		unit2.setActive(true);

		final UserGroupModel userGroup = new UserGroupModel();
		((ItemModelContextImpl) (ModelContextUtils.getItemModelContext(userGroup))).setLocaleProvider(localeProvider);
		userGroup.setUid("userGroup");
		userGroup.setLocName("userGroup");

		final Set<PrincipalGroupModel> groups = new HashSet();
		groups.add(unit2);
		groups.add(unit1);
		groups.add(userGroup);

		source.setGroups(groups);
		source.setDefaultB2BUnit(unit2);
		ocaB2BCustomerPopulator.populate(source, target);
		assertEquals(target.getUnits().get(0).getName(), "firstUnit");
		Assert.assertTrue(target.getUnits().get(1).isDefaultUnit());
	}


}
