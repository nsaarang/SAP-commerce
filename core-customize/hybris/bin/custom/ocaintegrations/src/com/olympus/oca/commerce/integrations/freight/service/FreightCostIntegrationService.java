package com.olympus.oca.commerce.integrations.freight.service;

import com.olympus.oca.commerce.integrations.exceptions.OcaIntegrationException;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface FreightCostIntegrationService {

    AbstractOrderModel fetchFreightCostForCart(AbstractOrderModel candidate) throws OcaIntegrationException;

}
