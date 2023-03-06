package com.olympus.oca.commerce.core.strategies.impl;

import com.olympus.oca.commerce.integrations.freight.service.FreightCostIntegrationService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.strategies.calculation.FindDeliveryCostStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.PriceValue;
import org.apache.log4j.Logger;

public class DefaultOcaFindDeliveryCostStrategy implements FindDeliveryCostStrategy {

    private final FreightCostIntegrationService freightCostIntegrationService;
    private final ModelService modelService;

    public DefaultOcaFindDeliveryCostStrategy(FreightCostIntegrationService freightCostIntegrationService, ModelService modelService) {
        this.freightCostIntegrationService = freightCostIntegrationService;
        this.modelService = modelService;
    }

    @Override
    public PriceValue getDeliveryCost(AbstractOrderModel order) {
        double totalDeliveryCost = 0.0;
        AbstractOrderModel orderWithFreightCost = freightCostIntegrationService.fetchFreightCostForCart(order);

        for (final AbstractOrderEntryModel e : orderWithFreightCost.getEntries()) {
            if (null != e.getFreightPrice()) {
                totalDeliveryCost += e.getFreightPrice();
                modelService.save(e);
            }
        }
        return new PriceValue(orderWithFreightCost.getCurrency().getIsocode(), totalDeliveryCost, orderWithFreightCost.getNet());
    }
}
