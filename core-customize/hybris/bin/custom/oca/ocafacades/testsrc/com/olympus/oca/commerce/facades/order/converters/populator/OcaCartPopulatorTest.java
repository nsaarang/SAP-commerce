package com.olympus.oca.commerce.facades.order.converters.populator;

import com.olympus.oca.commerce.core.enums.LoadingGroup;
import com.olympus.oca.commerce.core.enums.MaterialGroup;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaCartPopulatorTest {

    @InjectMocks
    private OcaCartPopulator ocaCartPopulator;

    @Before
    public void setUp() {
        ocaCartPopulator = new OcaCartPopulator();
    }

    @Test
    public void testAddShippingNotificationsForProductWithMatchingLoadingAndMaterialGroups() {
        CartModel source = new CartModel();
        CartData target = new CartData();
        ProductModel productModel = new ProductModel();
        CurrencyModel currencyModel = new CurrencyModel();
        productModel.setLoadingGroup(LoadingGroup.LG0001);
        productModel.setMaterialGroup(MaterialGroup.MG9004);
        productModel.setGrossWeight(0.415);
        List<AbstractOrderEntryModel> entries = new ArrayList<>();
        AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setProduct(productModel);
        abstractOrderEntryModel.setQuantity((long) 1);
        entries.add(abstractOrderEntryModel);
        source.setEntries(entries);
        source.setCurrency(currencyModel);
        source.getCurrency();

        //Case 1 testing for just ship by ground value.
        ocaCartPopulator.populate(source, target);
        assertThat(target.isShipByGround()).isTrue();
        assertThat(target.isHeavyOrder()).isFalse();
    }

    @Test
    public void testAddShippingNotificationsForHeavyOrder() {
        CartModel source = new CartModel();
        CartData target = new CartData();
        ProductModel productModel = new ProductModel();
        CurrencyModel currencyModel = new CurrencyModel();
        productModel.setGrossWeight((double) 80);
        List<AbstractOrderEntryModel> entries = new ArrayList<>();
        AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setProduct(productModel);
        abstractOrderEntryModel.setQuantity((long) 2);
        entries.add(abstractOrderEntryModel);
        source.setEntries(entries);
        source.setCurrency(currencyModel);
        source.getCurrency();

        //Case 2 Heavy Order.
        ocaCartPopulator.populate(source, target);
        assertThat(target.isShipByGround()).isFalse();
        assertThat(target.isHeavyOrder()).isTrue();
    }

    @Test
    public void testAddShippingNotificationsForHeavyOrderAndShipByGround() {
        CartModel source = new CartModel();
        CartData target = new CartData();
        ProductModel productModel = new ProductModel();
        CurrencyModel currencyModel = new CurrencyModel();
        productModel.setLoadingGroup(LoadingGroup.LG0001);
        productModel.setMaterialGroup(MaterialGroup.MG9004);
        productModel.setGrossWeight((double) 80);
        List<AbstractOrderEntryModel> entries = new ArrayList<>();
        AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setProduct(productModel);
        abstractOrderEntryModel.setQuantity((long) 2);
        entries.add(abstractOrderEntryModel);
        source.setEntries(entries);
        source.setCurrency(currencyModel);
        source.getCurrency();

        //Case 3 Both the shipByGround and heavyOrder flags are true.
        ocaCartPopulator.populate(source, target);
        assertThat(target.isShipByGround()).isTrue();
        assertThat(target.isHeavyOrder()).isTrue();
    }

    @Test
    public void testAddShippingNotificationsForHeavyOrderAndShipByGroundBothFlagsFalse() {
        CartModel source = new CartModel();
        CartData target = new CartData();
        ProductModel productModel = new ProductModel();
        CurrencyModel currencyModel = new CurrencyModel();
        productModel.setGrossWeight((double) 80);
        List<AbstractOrderEntryModel> entries = new ArrayList<>();
        AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setProduct(productModel);
        abstractOrderEntryModel.setQuantity((long) 1);
        entries.add(abstractOrderEntryModel);
        source.setEntries(entries);
        source.setCurrency(currencyModel);
        source.getCurrency();

        //Case 4 Both the shipByGround and heavyOrder flags are false.
        ocaCartPopulator.populate(source, target);
        assertThat(target.isShipByGround()).isFalse();
        assertThat(target.isHeavyOrder()).isFalse();
    }
}
