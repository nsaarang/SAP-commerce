package com.olympus.oca.commerce.facades.populators;

import com.olympus.oca.commerce.core.model.ShippingCarrierModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ocafacades.shipping.data.ShippingCarrierData;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class OcaShippingCarrierPopulatorTest {

    @InjectMocks
    private OcaShippingCarrierPopulator ocaShippingCarrierPopulator;
    @Before
    public void setup() {
        ocaShippingCarrierPopulator = new OcaShippingCarrierPopulator();

    }

    @Test
    public void test(){
        ShippingCarrierModel source = new ShippingCarrierModel();
        ShippingCarrierData target = new ShippingCarrierData();
        source.setCode("abc1");
        source.setName("xyz");
        ocaShippingCarrierPopulator.populate(source,target);
        Assert.assertEquals("abc1",target.getCode());
        Assert.assertEquals("xyz",target.getName());
    }

}
