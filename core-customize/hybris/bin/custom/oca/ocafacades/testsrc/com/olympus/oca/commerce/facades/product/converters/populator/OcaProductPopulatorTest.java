package com.olympus.oca.commerce.facades.product.converters.populator;

import com.olympus.oca.commerce.facades.search.converters.populator.OcaSearchResultProductPopulator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class OcaProductPopulatorTest {

    private final OcaProductPopulator productPopulator = new OcaProductPopulator();
    @Test
    public void testVisibilityFlags()
    {
        ProductModel source = new ProductModel();
        ProductData target = new ProductData();

        //case 1 - scenario false
        source.setSearchEnabled(false);
        source.setPurchaseEnabled(false);

        productPopulator.populate(source,target);

        assertThat(target.isPurchaseEnabled()).isFalse();
        assertThat(target.isSearchEnabled()).isFalse();

        //case 2 - scenario true
        source.setSearchEnabled(true);
        source.setPurchaseEnabled(true);

        productPopulator.populate(source,target);

        assertThat(target.isPurchaseEnabled()).isTrue();
        assertThat(target.isSearchEnabled()).isTrue();

    }

}
