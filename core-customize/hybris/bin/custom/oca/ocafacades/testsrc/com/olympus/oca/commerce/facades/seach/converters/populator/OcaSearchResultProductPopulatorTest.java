package com.olympus.oca.commerce.facades.seach.converters.populator;

import com.olympus.oca.commerce.facades.search.converters.populator.OcaSearchResultProductPopulator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class OcaSearchResultProductPopulatorTest {

    private final OcaSearchResultProductPopulator searchResultPopulator = new OcaSearchResultProductPopulator();
    @Test
    public void testVisibilityFlags()
    {

        SearchResultValueData source = new SearchResultValueData();
        ProductData target = new ProductData();

        //case 1 - purchase enabled false
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("purchaseEnabled", Boolean.FALSE);
        source.setValues(map);

        searchResultPopulator.populate(source,target);

        assertThat(target.isPurchaseEnabled()).isFalse();

        //case 2 - purchase enabled true
        map.put("purchaseEnabled", Boolean.TRUE);
        source.setValues(map);

        searchResultPopulator.populate(source,target);

        assertThat(target.isPurchaseEnabled()).isTrue();

        //case 3 - purchase enabled null
        map.put("purchaseEnabled", null);
        source.setValues(map);

        searchResultPopulator.populate(source,target);

        assertThat(target.isPurchaseEnabled()).isFalse();


    }

}
