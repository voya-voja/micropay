/***** Copyright 2001 iNexum Systems Inc. *****************************
**
**      File: Ticket.java
**
**      Description:
**          Memento for a one-shot transaction.
**
**      Revision History:
**              2001-05-14 (RTG) Initial revision.
**
**********************************************************************/


package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import java.util.Date;

/** Maintains the state of a transaction, acting as a memento.
 * @author  rgoold
 * @version 
 */
public class StubMemento extends java.lang.Object {
    
/** The ticket's expiry date.
 */    
    private Date            m_expiry;
/** The product identifier for the transaction.
 */    
    private String          m_offerID;
/** The challenge phrase needed to redeem the ticket.
 */    
    private TicketStub      m_stub;

    /** Creates new Ticket */
    public StubMemento() {
    }
    
/** Gets the expiry date for the ticket.
 * @return The ticket's expiry date.
 */    
    public Date getExpiry ()
    {
        return m_expiry;
    }
    
/** Gets the product identifier for the ticket.
 * @return The product identifier.
 */    
    public String getOfferID ()
    {
        return m_offerID;
    }
    
/** Gets the ticket's stub.
 * @return The ticket's stub.
 */    
    public TicketStub getStub ()
    {
        return m_stub;
    }
    
/** Sets the expiry date for the ticket.
 * @param expiry An expiry date.
 */    
    public void setExpiry (Date expiry)
    {
        m_expiry = expiry;
    }
    
/** Sets the product identifier for the ticket.
 * @param offerID The product identifier.
 */    
    public void setOfferID (String offerID)
    {
        m_offerID = offerID;
    }
    
/** Sets the ticket's stub.
 * @param stub The ticket stub.
 */    
    public void setStub (TicketStub stub)
    {
        m_stub = stub;
    }

}

