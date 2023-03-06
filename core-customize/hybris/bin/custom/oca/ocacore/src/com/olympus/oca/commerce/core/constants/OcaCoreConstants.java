/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.core.constants;

import java.util.List;

import com.olympus.oca.commerce.core.enums.DistributionChainStatus;
import com.olympus.oca.commerce.core.enums.MaterialGroup;
import com.olympus.oca.commerce.core.enums.MaterialGroup4;


/**
 * Global class for all OcaCore constants. You can add global constants for your extension into this class.
 */
public final class OcaCoreConstants extends GeneratedOcaCoreConstants
{
	public static final String EXTENSIONNAME = "ocacore";


	private OcaCoreConstants()
	{
		//empty
	}

	// implement here constants used by this extension
	public static final String QUOTE_BUYER_PROCESS = "quote-buyer-process";
	public static final String QUOTE_SALES_REP_PROCESS = "quote-salesrep-process";
	public static final String QUOTE_USER_TYPE = "QUOTE_USER_TYPE";
	public static final String QUOTE_SELLER_APPROVER_PROCESS = "quote-seller-approval-process";
	public static final String QUOTE_TO_EXPIRE_SOON_EMAIL_PROCESS = "quote-to-expire-soon-email-process";
	public static final String QUOTE_EXPIRED_EMAIL_PROCESS = "quote-expired-email-process";
	public static final String QUOTE_POST_CANCELLATION_PROCESS = "quote-post-cancellation-process";
	public static final String SHIP_BY_OLYMPUS_CODE = "ship.by.olympus.code";
	public static final String RECENTLY_ORDERED_PRODUCT_COUNT = "recently.ordered.product.count";

	public static final List<MaterialGroup4> GHOST_PRODUCT_2_MG_4 = List.of(MaterialGroup4.A04, MaterialGroup4.A07,
			MaterialGroup4.A08, MaterialGroup4.A09, MaterialGroup4.A10, MaterialGroup4.A11, MaterialGroup4.A12, MaterialGroup4.A13,
			MaterialGroup4.A14, MaterialGroup4.A15, MaterialGroup4.A19, MaterialGroup4.A21);
	public static final List<DistributionChainStatus> GHOST_PRODUCT_1_DCS = List.of(DistributionChainStatus.DCS01,
			DistributionChainStatus.DCS02);

	public static final List<DistributionChainStatus> TEMP_NON_PURCHASABLE_DCS = List.of(DistributionChainStatus.DCS09,
			DistributionChainStatus.DCS19, DistributionChainStatus.DCS39);

	public static final List<DistributionChainStatus> PURCHASABLE_DCS = List.of(DistributionChainStatus.DCS07,
			DistributionChainStatus.DCS29);

	public static final List<MaterialGroup4> PURCHASABLE_MG4 = List.of(MaterialGroup4.A01, MaterialGroup4.A02, MaterialGroup4.A05,
			MaterialGroup4.A20);

	public static final List<MaterialGroup4> CAPITAL_MG4 = List.of(MaterialGroup4.A18, MaterialGroup4.A26, MaterialGroup4.A29);

	public static final List<MaterialGroup> CAPITAL_MG = List.of(MaterialGroup.MG9000);

	public static final String MINICART_MAX_DISPLAY_VALUE = "minicart.max.display.value";

	public static final String PLUS_SYMBOL = "+";

	public static final String PATTERN = "###,##0.00";
	
	public static final String TIME_TO_LIVE_HOURS = "occ.timeToLive.hours";

	public static class ErrorConstants
	{
		public static final String PRODUCT_NOT_SELLABLE = "This Product is non sellable";
		public static final String B2B_UNIT_NOT_FOUND = "The account number is not associated with current user";
	}



}
