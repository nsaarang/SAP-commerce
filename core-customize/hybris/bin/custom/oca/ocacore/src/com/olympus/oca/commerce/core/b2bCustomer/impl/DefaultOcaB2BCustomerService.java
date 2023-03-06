package com.olympus.oca.commerce.core.b2bCustomer.impl;

import com.olympus.oca.commerce.core.b2bCustomer.OcaB2BCustomerService;
import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import com.olympus.oca.commerce.dto.user.AccountPreferencesWsDTO;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

public class DefaultOcaB2BCustomerService implements OcaB2BCustomerService {

    private ModelService modelService;

    @Override
    public void save(AccountPreferencesModel accountPreferencesModel, B2BCustomerModel customer) {
        getModelService().save(accountPreferencesModel);
        customer.setAccountPreferences(accountPreferencesModel);
        getModelService().save(customer);
    }


    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

}
