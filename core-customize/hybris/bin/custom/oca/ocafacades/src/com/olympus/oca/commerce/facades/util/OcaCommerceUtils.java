package com.olympus.oca.commerce.facades.util;

import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.text.DecimalFormat;

public class OcaCommerceUtils {
    public static String getFormattedPrice(Double price, CurrencyModel currency) {
       DecimalFormat decimalFormat = new DecimalFormat(OcaCoreConstants.PATTERN);
       return currency.getSymbol() + decimalFormat.format(price);
    }

}