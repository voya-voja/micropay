/*
 * TransInfo.java
 *
 * Created on June 25, 2001, 4:39 PM
 */

package com.inexum.MicroAc.MicroProcessing.Transaction;

import com.inexum.Types.Money;
import com.inexum.MicroAc.PFL.MPBasicInfo;

/**
 *
 * @author  nkapov
 * @version 
 */
public class TxInfo extends java.lang.Object
    implements java.io.Serializable
{
    public static final int PURCHASE = 1;
    public static final int PROCESSING = 2;
    
    private String          m_sessionID;
    private int             m_transactionType;
    private int             m_transactionID;
    private Money           m_consumerCost;
    private MPBasicInfo     m_mpBasicInfo = null;
       

    /** Creates new TransInfo */
    public TxInfo( MPBasicInfo mpBasicInfo, String sessionID, 
                                        int transactionID,int transactionType) 
    {
        m_sessionID = sessionID;
        m_transactionID = transactionID;
        m_transactionType = transactionType;
        m_mpBasicInfo = mpBasicInfo;
    }
    
    public final String getMamaID()
    {
        return m_mpBasicInfo.getMAMAID();
    }
    
    public final String getMerchantID()
    {
        return m_mpBasicInfo.getMerchantID();
    }
   
    public final String getProductID()
    {
        return m_mpBasicInfo.getBuyID();
    }
    
    public final Money getMerchantCost()
    {
        return new Money( m_mpBasicInfo.getPrice() );
    }
    
    public final Money getConsumerCost()
    {
        return  m_consumerCost;
    }
    
    public void setConsumerCost(final Money cost)
    {
        m_consumerCost = cost;
    }
    
    public final String getSessionID()
    {
        return m_sessionID;
    }
    
    public final int getTxID()
    {
        return m_transactionID;
    }
    
    public final int getTxType()
    {
        return m_transactionType;
    }


}
