/*
 * PeerRequest.java
 *
 * Created on November 8, 2001, 4:12 PM
 */

package com.inexum.MicroAc.Transaction;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class PeerRequest extends java.lang.Object
    implements java.io.Serializable
{
    public static final int c_NullRequest       = 0;
    public static final int c_PaymentOffer      = 1;
    public static final int c_PaymentRequest    = 2;

    private int                 m_requestType;
    
    private TransactionOffer    m_offer;
    private java.lang.Object    m_data;
    
    /** Creates new PeerRequest */
    public PeerRequest()
    {
    }

    public int
    getRequestType()
    {
        return m_requestType;
    }
    public void
    setRequestType(int requestType)
    {
        m_requestType = requestType;
    }
    
    public TransactionOffer
    getOffer()
    {
        return m_offer;
    }
    public void
    setOffer(TransactionOffer offer)
    {
        m_offer = offer;
    }
    
    public java.lang.Object
    getData()
    {
        return m_data;
    }
    public void
    setData(java.io.Serializable data)
    {
        m_data = data;
    }
    
}

