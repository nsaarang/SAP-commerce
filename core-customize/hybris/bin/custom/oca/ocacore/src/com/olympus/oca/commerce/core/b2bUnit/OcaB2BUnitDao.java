/**
 * 
 */
package com.olympus.oca.commerce.core.b2bUnit;

import de.hybris.platform.b2b.model.B2BUnitModel;

import java.util.List;

/**
 * The Interface OcaB2BUnitDao.
 */
public interface OcaB2BUnitDao
{
	/**
	 * Gets the list of B2BUnitModel from b2bUnits.
	 *
	 * @param b2bUnits the b 2 b units
	 * @return the list of B2BUnitModel from b2bUnits
	 */
	List<B2BUnitModel> getB2BUnitModelFromCode(List<String> b2bUnits);

}
