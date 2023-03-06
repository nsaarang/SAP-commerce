package com.olympus.oca.commerce.facades.populators;

import com.olympus.oca.commerce.core.model.HeavyOrderQuestionsModel;
import de.hybris.platform.commercefacades.order.data.HeavyOrderQuestionsCartData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class QuestionnaireReversePopulator implements Populator<HeavyOrderQuestionsCartData, HeavyOrderQuestionsModel> {
    @Override
    public void populate(HeavyOrderQuestionsCartData source, HeavyOrderQuestionsModel target) throws ConversionException {
        target.setEmail(source.getEmail());
        target.setName(source.getName());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setLargeTruckEntry(source.getLargeTruckEntry());
        target.setLiftAvailable(source.getLiftAvailable());
        target.setLoadingDock(source.getLoadingDock());
        target.setOrderDeliveredInside(source.getOrderDeliveredInside());
        target.setTruckSize(source.getTruckSize());
    }
}
