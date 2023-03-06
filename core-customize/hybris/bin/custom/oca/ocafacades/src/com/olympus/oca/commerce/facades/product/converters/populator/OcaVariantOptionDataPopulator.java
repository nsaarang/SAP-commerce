package com.olympus.oca.commerce.facades.product.converters.populator;

import com.olympus.oca.commerce.facades.util.OcaCommerceUtils;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.variants.model.VariantProductModel;

public class OcaVariantOptionDataPopulator implements Populator<VariantProductModel, VariantOptionData> {
    private CommonI18NService commonI18NService;

    @Override
    public void populate(VariantProductModel source, VariantOptionData target) throws ConversionException {
        if(null != target.getPriceData()){
            final CurrencyModel currency = getCommonI18NService().getCurrency(target.getPriceData().getCurrencyIso());
            String formattedPrice = OcaCommerceUtils.getFormattedPrice(target.getPriceData().getValue().doubleValue(), currency);
            target.getPriceData().setFormattedValue(formattedPrice);
        }
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
