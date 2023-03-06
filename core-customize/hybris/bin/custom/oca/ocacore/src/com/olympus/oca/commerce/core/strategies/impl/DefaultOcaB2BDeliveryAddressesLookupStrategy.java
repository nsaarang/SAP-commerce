package com.olympus.oca.commerce.core.strategies.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.strategies.DeliveryAddressesLookupStrategy;
import de.hybris.platform.commerceservices.util.ItemComparator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class DefaultOcaB2BDeliveryAddressesLookupStrategy implements DeliveryAddressesLookupStrategy {

        @Override
        public List<AddressModel> getDeliveryAddressesForOrder(final AbstractOrderModel abstractOrder, final boolean visibleAddressesOnly)
        {
            B2BUnitModel b2BUnitModel = abstractOrder.getUnit();
            final Set<AddressModel> addresses = new HashSet<>();
            if (null != b2BUnitModel && CollectionUtils.isNotEmpty(b2BUnitModel.getAddresses()))
            {
                addresses.addAll(b2BUnitModel.getAddresses());
            }

            return sortAddresses(addresses);
        }

        protected List<AddressModel> sortAddresses(final Collection<AddressModel> addresses)
        {
            final ArrayList<AddressModel> result = new ArrayList<>(addresses);
            result.sort(ItemComparator.INSTANCE);
            return result;
        }


}
