/*
 * WalletOpenEvent.java
 *
 * Created on June 11, 2001, 6:22 PM
 */

package com.inexum.MicroAc.Wallet;


import com.inexum.Types.Money;

/**
 *
 * @author  nkapov
 * @version
 */
public class WalletFailureEvent extends WalletEvent
{
    private String m_description;
    
    private WalletFailureEvent()
    {
    }
    
    public WalletFailureEvent(String desc)
    {
        m_description = desc;
    }
    
    public final String getMessage()
    {
        return m_description;
    }
    
    
    
    
    
}
