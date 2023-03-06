package com.olympus.oca.commerce.core.b2bCustomer;

import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import com.olympus.oca.commerce.dto.user.AccountPreferencesWsDTO;
import de.hybris.platform.b2b.model.B2BCustomerModel;

public interface OcaB2BCustomerService {

    void save(AccountPreferencesModel accountPreferencesModel, B2BCustomerModel customer);
}
