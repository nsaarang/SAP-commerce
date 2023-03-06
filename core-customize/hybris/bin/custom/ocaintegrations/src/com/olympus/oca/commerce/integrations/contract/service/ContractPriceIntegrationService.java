package com.olympus.oca.commerce.integrations.contract.service;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

public interface ContractPriceIntegrationService {
    AbstractOrderEntryModel fetchContractPricetForCart(AbstractOrderEntryModel entry);

}
