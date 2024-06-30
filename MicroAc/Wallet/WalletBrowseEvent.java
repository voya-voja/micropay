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
public class WalletBrowseEvent extends WalletEvent
{
    private Money       m_cost;
    private String      m_prodDesc = "";
    
    
    /** Creates new WalletOpenEvent */
    private WalletBrowseEvent()
    {
    }
    
    public WalletBrowseEvent( Money cost, String prodDesc )
    {
        m_cost = cost;
        m_prodDesc = prodDesc;
    }
    
    public Money getCost()
    {
        return m_cost;
    }
    
    public String getProdDesc()
    {
        return m_prodDesc;
    }
    
    
    
    
}
