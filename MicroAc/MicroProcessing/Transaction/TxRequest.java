/*
 * TxRequest.java
 *
 * Created on July 10, 2001, 11:44 AM
 */

package com.inexum.MicroAc.MicroProcessing.Transaction;

import com.inexum.Types.Money;

/**
 *
 * @author  rgoold
 * @version 
 */
public class TxRequest extends java.lang.Object
    implements java.io.Serializable
{
    private String      m_sessionID;
    private int         m_transactionID;
    private String      m_productID;
    private String      m_merchantID;
    private Money       m_consumerCost;
    private Money       m_merchantCost;

    /** Creates new TxRequest */
    public TxRequest (final String sessionID, int transactionID,
        final String productID, final String merchantID,
        final Money consumerCost, final Money merchantCost)
    {
        m_sessionID = sessionID;
        m_transactionID = transactionID;
        m_productID = productID;
        m_merchantID = merchantID;
        m_consumerCost = consumerCost;
        m_merchantCost = merchantCost;
    }
    
    public final String getSessionID ()
    {
        return m_sessionID;
    }
    
    public final int getTransactionID ()
    {
        return m_transactionID;
    }
    
    public final String getProductID ()
    {
        return m_productID;
    }
    
    public final String getMerchantID ()
    {
        return m_merchantID;
    }
    
    public final Money getConsumerCost ()
    {
        return m_consumerCost;
    }
    
    public final Money getMerchantCost ()
    {
        return m_merchantCost;
    }

}

