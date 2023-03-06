/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.constants;

public class OcaoccConstants extends GeneratedOcaoccConstants
{
	public static final String EXTENSIONNAME = "ocaocc";
	public final static String OCC_OVERLAPPING_BASE_SITE_USER_PATH = "#{ ${occ.rewrite.overlapping.paths.enabled:false} ? '/{baseSiteId}/orgUsers/{userId}' : '/{baseSiteId}/users/{userId}'}";

	private OcaoccConstants()
	{

	}
}

