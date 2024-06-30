/*
 * WalletOpenEvent.java
 *
 * Created on June 11, 2001, 6:22 PM
 */

package com.inexum.MicroAc.Wallet;

import com.inexum.MicroAc.DomainTypes.VoucherInfo;

/**
 *
 * @author  nkapov
 * @version
 */
public class WalletPurchaseEvent extends WalletEvent
{
    private final String        m_description;
    private final VoucherInfo   m_voucherInfo;
    
    
    public WalletPurchaseEvent( VoucherInfo info, String desc )
    {
        m_description = desc;
        m_voucherInfo = info;
               
    }
    
    public String getVoucherString()
    {
        if( m_voucherInfo == null )
            return "";
        else
            return m_voucherInfo.toString();
    }
    
    public VoucherInfo getVoucherInfo ()
    {
        return m_voucherInfo;
    }
    
    public String getDesc()
    {
        return m_description;
    }
    
    
    
    
    
}
