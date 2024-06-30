/*
 * VoucherInfo.java
 *
 * Created on July 11, 2001, 2:16 PM
 */

package com.inexum.MicroAc.DomainTypes;

import java.util.Date;

/**
 *
 * @author  nkapov
 * @version 
 */
public class VoucherInfo extends java.lang.Object implements java.io.Serializable 
{
    private final Date m_expiryDate;
    private final String m_merchantID;
    private final String m_productID;
    private final SignedVoucher m_signedVoucher;

    /** Creates new VoucherInfo */
    public VoucherInfo(String merchantID, String productID, Date expiryDate, 
                                                      SignedVoucher signedVoucher) 
    {
        m_expiryDate = expiryDate;
        m_merchantID = merchantID;
        m_productID = productID;
        m_signedVoucher = signedVoucher;
    }

    public static String toKey(final String merchantID, final String productID)
    {
        return merchantID + "/" + productID;
    }
    
    public String toKey()
    {
        return m_merchantID + "/" + m_productID;
    }

    public java.lang.String toString() 
    {
        return m_merchantID + "  " + m_productID + "  " + m_expiryDate;
    }
    
    public final String getMerchantID()
    {
        return m_merchantID;
    }
    
    public final String getProductID()
    {
        return m_productID;
    }
    public final Date getExpiryDate()
    {
        return m_expiryDate;
    }
    public final SignedVoucher getSignedVoucher()
    {
        return m_signedVoucher;
    }
    
}
