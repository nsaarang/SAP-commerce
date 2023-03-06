package com.olympus.oca.commerce.core.product.interceptors;

import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.CAPITAL_MG;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.CAPITAL_MG4;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.GHOST_PRODUCT_1_DCS;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.GHOST_PRODUCT_2_MG_4;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.PURCHASABLE_DCS;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.PURCHASABLE_MG4;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.TEMP_NON_PURCHASABLE_DCS;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import org.apache.log4j.Logger;
import com.olympus.oca.commerce.core.enums.NonPurchasableDisplayStatus;

public class OcaProductPrepareInterceptor implements PrepareInterceptor
{

	private static final Logger LOG = Logger.getLogger(OcaProductPrepareInterceptor.class);

	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof ProductModel)
		{
			if (ctx.isModified(model, ProductModel.DISTRIBUTIONCHAINSTATUS) || ctx.isModified(model, ProductModel.MATERIALGROUP)
					|| ctx.isModified(model, ProductModel.MATERIALGROUP4))
			{

				final ProductModel product = ((ProductModel) model);

				if ((null == product.getDistributionChainStatus() || PURCHASABLE_DCS.contains(product.getDistributionChainStatus()))
						&& (null == product.getMaterialGroup4() || PURCHASABLE_MG4.contains(product.getMaterialGroup4())))
				{
					product.setSearchEnabled(true);
					product.setPurchaseEnabled(true);
				}

				if (null != product.getDistributionChainStatus()
						&& TEMP_NON_PURCHASABLE_DCS.contains(product.getDistributionChainStatus()))
				{
					product.setNonPurchasableDisplayStatus(NonPurchasableDisplayStatus.TEMP_UNAVAILABLE);
				}

				if ((null != product.getMaterialGroup4() && CAPITAL_MG4.contains(product.getMaterialGroup4()))
						|| (null != product.getMaterialGroup() && CAPITAL_MG.contains(product.getMaterialGroup())))
				{
					product.setNonPurchasableDisplayStatus(NonPurchasableDisplayStatus.CONTACT_SALES_REP);

				}
				if ((null != product.getDistributionChainStatus()
						&& TEMP_NON_PURCHASABLE_DCS.contains(product.getDistributionChainStatus()))
						|| ((null != product.getMaterialGroup4() && CAPITAL_MG4.contains(product.getMaterialGroup4()))
								|| (null != product.getMaterialGroup() && CAPITAL_MG.contains(product.getMaterialGroup()))))
				{
					product.setSearchEnabled(true);
					product.setPurchaseEnabled(false);
				}
				if ((null != product.getMaterialGroup4() && GHOST_PRODUCT_2_MG_4.contains(product.getMaterialGroup4()))
						|| ((null != product.getDistributionChainStatus()
								&& GHOST_PRODUCT_1_DCS.contains(product.getDistributionChainStatus()))))
				{
					product.setSearchEnabled(false);
					product.setPurchaseEnabled(false);
				}

				if (!(ctx.isModified(model, ProductModel.SEARCHENABLED) || ctx.isModified(model, ProductModel.PURCHASEENABLED)))
				{
					LOG.info(product.getCode() + " - Product doesn't match any visibility rules");
				}

			}
		}
	}
}
