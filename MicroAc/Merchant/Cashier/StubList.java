/*
 * StubList.java
 *
 * Created on June 5, 2001, 4:11 PM
 */

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.Encoding.CryptoAgentFactory;
import com.inexum.Encoding.CryptoAgent;

/**
 *
 * @author  rgoold
 * @version 
 */
public class StubList extends java.lang.Object
{
    private java.util.Map   m_stubMap;

    /** Creates new StubList */
    public StubList()
    {
        m_stubMap = java.util.Collections.synchronizedMap(
            new java.util.HashMap());
    }
    
    public TicketStub newStubFromProduct(Product product)
        throws Exception
    {
        CryptoAgent cryptoAgent = CryptoAgentFactory.GetCryptoAgent();
        TicketStub stub = new TicketStub(cryptoAgent.uniquePhrase());
        
        StubMemento memento = new StubMemento();
        memento.setExpiry(new java.util.Date(new java.util.Date().getTime()
            + 1000L * product.getDuration()));
        memento.setOfferID(product.getOfferID());
        memento.setStub(stub);
        m_stubMap.put(stub.getChallenge(), memento);
        
        return stub;
    }
    
    public StubMemento getMementoFromStub(TicketStub stub)
        throws java.lang.Exception
    {
        if (!m_stubMap.containsKey(stub.getChallenge()))
        {
            throw new Exception();
        }
        return (StubMemento)m_stubMap.get(stub.getChallenge());
    }
    
    public void removeStub(TicketStub stub)
    {
        if (m_stubMap.containsKey(stub.getChallenge()))
        {
            m_stubMap.remove(stub.getChallenge());
        }
    }

}

