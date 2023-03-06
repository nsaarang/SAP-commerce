package com.olympus.oca.commerce.core.order.impl;

import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import com.olympus.oca.commerce.integrations.contract.service.ContractPriceIntegrationService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;
import org.apache.log4j.Logger;
import javax.annotation.Resource;
import java.util.*;

public class DefaultOcaCalculationService extends DefaultCalculationService {

    private static final Logger LOG = Logger.getLogger(DefaultOcaCalculationService.class);

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;
    private CommonI18NService commonI18NService;
    private static final ThreadLocal<Boolean> saveOrderEntryUnneeded = new ThreadLocal<>();
    private ContractPriceIntegrationService contractPriceIntegrationService;
    private OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;
    private ModelService modelService;


    @Override
    public void recalculateOrderEntryIfNeeded(final AbstractOrderEntryModel entry, final boolean forceRecalculation)
            throws CalculationException
    {
        if (isTimeToLiveInvalid(entry) || forceRecalculation || orderRequiresCalculationStrategy.requiresCalculation(entry))
        {
            contractPriceIntegrationService.fetchContractPricetForCart(entry);
            resetAllValues(entry);
            calculateTotals(entry, true);
        }
    }


    @Override
    public void calculateTotals(final AbstractOrderEntryModel entry, final boolean recalculate)
    {
        if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(entry))
        {
            final AbstractOrderModel order = entry.getOrder();
            final CurrencyModel curr = order.getCurrency();
            final int digits = curr.getDigits().intValue();
            double totalPriceWithoutDiscount = 0.0;

            if(null != entry.getContractPrice()){
                totalPriceWithoutDiscount = commonI18NService
                        .roundCurrency(entry.getContractPrice().doubleValue() * entry.getQuantity().longValue(), digits);
            }else {
                totalPriceWithoutDiscount = commonI18NService
                        .roundCurrency(entry.getBasePrice().doubleValue() * entry.getQuantity().longValue(), digits);
            }
            final double quantity = entry.getQuantity().doubleValue();
            /*
             * apply discounts (will be rounded each) convert absolute discount values in case their currency doesn't match the
             * order currency
             */
            final List appliedDiscounts = DiscountValue.apply(quantity, totalPriceWithoutDiscount, digits,
                    convertDiscountValues(order, entry.getDiscountValues()),
                    curr.getIsocode());
            entry.setDiscountValues(appliedDiscounts);
            double totalPrice = totalPriceWithoutDiscount;
            for (final Iterator it = appliedDiscounts.iterator(); it.hasNext(); )
            {
                totalPrice -= ((DiscountValue) it.next()).getAppliedValue();
            }
            // set total price
            entry.setTotalPrice(Double.valueOf(totalPrice));
            // apply tax values too
            calculateTotalTaxValues(entry);
            setCalculatedStatus(entry);

            if (!isSaveOrderEntryUnneeded() || hasJaloStrategies())
            {
                getModelService().save(entry);
            }
        }
    }

    protected boolean isTimeToLiveInvalid(final AbstractOrderEntryModel entry) {
       if (null != entry.getTimeToLive()) {
            Date contractPriceUpdatedTime = entry.getTimeToLive();
            Date date = new Date();
            long differentDate = Math.abs(date.getTime() - contractPriceUpdatedTime.getTime());
            long differentHours = differentDate / (60 * 60 * 1000);
            int timeToLiveHours = configurationService.getConfiguration().getInt(OcaCoreConstants.TIME_TO_LIVE_HOURS, 1);
            LOG.info("contractPrice Updated Time" +contractPriceUpdatedTime+ "and number of hours calculated is "+ differentHours + "and timeToLive Hours" +timeToLiveHours);
                if (differentHours > timeToLiveHours) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
             }
       return Boolean.TRUE;
    }

        private static boolean isSaveOrderEntryUnneeded ()
        {
            return Boolean.TRUE.equals(saveOrderEntryUnneeded.get());
        }

    public DefaultOcaCalculationService(ContractPriceIntegrationService contractPriceIntegrationService,
                ModelService modelService, OrderRequiresCalculationStrategy orderRequiresCalculationStrategy,
                CommonI18NService commonI18NService) {
            this.contractPriceIntegrationService = contractPriceIntegrationService;
            this.modelService = modelService;
            this.orderRequiresCalculationStrategy = orderRequiresCalculationStrategy;
            this.commonI18NService = commonI18NService;
        }
}
