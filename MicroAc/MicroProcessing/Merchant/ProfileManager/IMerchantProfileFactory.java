/*
 * IMerchantProfileFactory.java
 *
 * Created on July 3, 2001, 3:32 PM
 */

package com.inexum.MicroAc.MicroProcessing.Merchant.ProfileManager;

import com.inexum.MicroAc.Exceptions.*;

/**
 *
 * @author  rgoold
 * @version 
 */
public interface IMerchantProfileFactory
{
    public MerchantProfile getProfile(String accountID)
        throws UnknownMerchantException;
    
    public void closeProfile(MerchantProfile profile)
        throws ProfileUpdateException;
}


