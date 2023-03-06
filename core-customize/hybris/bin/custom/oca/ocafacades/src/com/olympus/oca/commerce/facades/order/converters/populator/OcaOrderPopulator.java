package com.olympus.oca.commerce.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.OrderPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;

public class OcaOrderPopulator extends OrderPopulator {

    private PriceDataFactory priceDataFactory;

    @Override
    public void populate(final OrderModel source, final OrderData target){
        target.setDeliveryCost(source.getDeliveryAddress() != null ? createPrice(source, source.getDeliveryCost()) : null);

    }

    @Override
    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    @Override
    public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

}
