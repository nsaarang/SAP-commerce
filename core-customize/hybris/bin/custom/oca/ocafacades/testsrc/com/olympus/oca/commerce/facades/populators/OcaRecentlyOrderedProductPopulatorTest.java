package com.olympus.oca.commerce.facades.populators;

import static org.junit.Assert.assertEquals;

import com.olympus.oca.commerce.facades.populators.OcaRecentlyOrderedProductPopulator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ocafacades.order.data.RecentlyOrderedProductData;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.variants.model.GenericVariantProductModel;
import javolution.testing.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaRecentlyOrderedProductPopulatorTest {
    @InjectMocks
    private OcaRecentlyOrderedProductPopulator ocaRecentlyOrderedProductPopulator;

    @Before
    public void setup(){
        ocaRecentlyOrderedProductPopulator = new OcaRecentlyOrderedProductPopulator();
    }
    @Test
    public void test()
    {
        ProductModel source = new ProductModel();
        RecentlyOrderedProductData target = new RecentlyOrderedProductData();
        source.setCode("abc");
        ocaRecentlyOrderedProductPopulator.populate(source,target);
        Assert.assertEquals("abc",target.getCode());

    }
}