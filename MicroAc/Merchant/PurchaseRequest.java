/*
 * PurchaseRequest.java
 *
 * Created on November 16, 2001, 11:30 AM
 */

package com.inexum.MicroAc.Merchant;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class PurchaseRequest extends java.lang.Object
    implements java.io.Serializable
{
    private String  m_offerID;
    private Product  m_product;

    /** Creates new PurchaseRequest */
    public PurchaseRequest(String offerID)
    {
        m_offerID = offerID;
    }

    /** Creates new PurchaseRequest */
    public PurchaseRequest(String offerID, Product product)
    {
        m_offerID = offerID;
        m_product  = product;
    }

    public String getOfferID()
    {
        return m_offerID;
    }

    public Product getProduct()
    {
        return m_product;
    }
}

