/*
 * TransactionOffer.java
 *
 * Created on November 9, 2001, 1:11 PM
 */

package com.inexum.MicroAc.Transaction;

import com.inexum.Types.Money;

import java.util.Date;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class TransactionOffer extends java.lang.Object
    implements java.io.Serializable
{
    public Money    m_cost;
    public Money    m_exchangeRate;
    public Date     m_offerExpiry;
    public Date     m_productExpiry;
    public String   m_issuer;
    public String   m_issuerAccountManager;
    public String   m_offerID;
    public String   m_redemptionURL;

//----------------------------------------------------------------------------//

    /** Creates new TransactionOffer */
    public TransactionOffer(Money cost, Money exchangeRate, Date offerExpiry,
        Date productExpiry, String issuer, String issuerAccountManager,
        String offerID, String redemptionURL)
    {
        m_cost                  = cost;
        m_exchangeRate          = exchangeRate;
        m_offerExpiry           = offerExpiry;
        m_productExpiry         = productExpiry;
        m_issuer                = issuer;
        m_issuerAccountManager  = issuerAccountManager;
        m_offerID               = offerID;
        m_redemptionURL         = redemptionURL;
    }

//----------------------------------------------------------------------------//

    public TransactionOffer(TransactionOffer source)
    {
        m_cost                  = source.m_cost;
        m_exchangeRate          = source.m_exchangeRate;
        m_offerExpiry           = source.m_offerExpiry;
        m_productExpiry         = source.m_productExpiry;
        m_issuer                = source.m_issuer;
        m_issuerAccountManager  = source.m_issuerAccountManager;
        m_offerID               = source.m_offerID;
        m_redemptionURL         = source.m_redemptionURL;
    }

//----------------------------------------------------------------------------//

    public Money getCost()
    {
        return m_cost;
    }

//----------------------------------------------------------------------------//

    public Money getExchangeRate()
    {
        return m_exchangeRate;
    }

//----------------------------------------------------------------------------//

    public Date getOfferExpiry()
    {
        return m_offerExpiry;
    }

//----------------------------------------------------------------------------//

    public Date getProductExpiry()
    {
        return m_productExpiry;
    }

//----------------------------------------------------------------------------//

    public String getIssuer()
    {
        return m_issuer;
    }

//----------------------------------------------------------------------------//

    public String getIssuerAccountManager()
    {
        return m_issuerAccountManager;
    }

//----------------------------------------------------------------------------//

    public String getOfferID()
    {
        return m_offerID;
    }

//----------------------------------------------------------------------------//

    public String getRedemptionURL()
    {
        return m_redemptionURL;
    }

}

