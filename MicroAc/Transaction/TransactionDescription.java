/*
 * TransactionDescription.java
 *
 * Created on November 8, 2001, 5:04 PM
 */

package com.inexum.MicroAc.Transaction;

import com.inexum.Types.Money;

import java.util.Date;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public final class TransactionDescription extends java.lang.Object
    implements java.io.Serializable
{
    private TransactionOffer    m_offer;
    private Money               m_authorizedAmount;

    /** Creates new TransactionDescription */
    public TransactionDescription(TransactionOffer offer,
        Money authorizedAmount)
    {
        m_offer             = offer;
        m_authorizedAmount  = authorizedAmount;
    }
    
    public Money
    getCost ()
    {
        return m_offer.m_cost;
    }
    public void
    setCost (Money cost)
    {
        m_offer.m_cost = cost;
    }
    
    public Money
    getAuthorizedAmount ()
    {
        return m_authorizedAmount;
    }
    public void
    setAuthorizedAmount (Money amount)
    {
        m_authorizedAmount = amount;
    }
    
    public Money
    getIssuerExchangeRate ()
    {
        return m_offer.m_exchangeRate;
    }
    public void
    setIssuerExchangeRate (Money rate)
    {
        m_offer.m_exchangeRate = rate;
    }
    
    public String
    getOfferID ()
    {
        return m_offer.m_offerID;
    }
    public void
    setOfferID (String offerID)
    {
        m_offer.m_offerID = offerID;
    }
    
    public String
    getIssuer ()
    {
        return m_offer.m_issuer;
    }
    public void
    setIssuer (String issuer)
    {
        m_offer.m_issuer = issuer;
    }
    
    public String
    getIssuerAccountManager ()
    {
        return m_offer.m_issuerAccountManager;
    }
    public void
    setIssuerAccountManager (String manager)
    {
        m_offer.m_issuerAccountManager = manager;
    }
    
    public Date
    getOfferExpiry ()
    {
        return m_offer.m_offerExpiry;
    }
    public void
    setOfferExpiry (Date expiry)
    {
        m_offer.m_offerExpiry = expiry;
    }
    
    public Date
    getProductExpiry ()
    {
        return m_offer.m_productExpiry;
    }
    public void
    setProductExpiry (Date expiry)
    {
        m_offer.m_productExpiry = expiry;
    }

}

