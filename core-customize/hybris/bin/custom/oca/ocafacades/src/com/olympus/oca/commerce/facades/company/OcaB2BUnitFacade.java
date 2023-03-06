/**
 *
 */
package com.olympus.oca.commerce.facades.company;
import com.olympus.oca.commerce.dto.user.AccountPreferencesWsDTO;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;


public interface OcaB2BUnitFacade extends B2BUnitFacade
{

	public void setDefaultB2BUnit(String b2bUnitId);

	public  void saveAccessType(AccountPreferencesWsDTO accessType);

}
