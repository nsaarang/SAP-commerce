package com.olympus.oca.commerce.facades.order.converters.populator;

import com.olympus.oca.commerce.facades.util.OcaCommerceUtils;
import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.text.NumberFormat;


public class OcaOrderEntryPopulator extends OrderEntryPopulator {

    private PriceDataFactory priceDataFactory;
    @Override
    public void populate(AbstractOrderEntryModel source, OrderEntryData target) throws ConversionException {
         if( null!=source.getOrder() && null!=source.getOrder().getCurrency()) {
             if(null!=source.getContractPrice()) {
                 String formattedPrice = OcaCommerceUtils.getFormattedPrice(source.getContractPrice(), source.getOrder().getCurrency());
                 target.setContractPrice(String.valueOf(source.getContractPrice()));
                 target.setFormattedContractPrice(formattedPrice);
             }
             if(null!=source.getFreightPrice()){
                 target.setFreightPrice(createPrice(source, source.getFreightPrice()));
             }
        }

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
