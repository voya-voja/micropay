/*
 * Voucher.java
 *
 * Created on May 10, 2001, 4:03 PM
 */

package com.inexum.MicroAc.DomainTypes;

import com.inexum.MicroAc.Merchant.Product;
import com.inexum.MicroAc.Merchant.TicketStub;

import java.util.Date;

/** A full description of the transaction, which can be used as a
 * transaction record. Comes in two varieties: One-shot and expiry.
 * They are both implemented using the same class.
 * @author rgoold
 * @version 
 */
public class Voucher extends java.lang.Object
    implements java.io.Serializable
{
    
/** The identifier of the merchant providing the product (for use by MAMA).
 */        
    private String      m_merchant;
/** The MAMA for the merchant providing the product.
 */    
    private String      m_mama;
/** The cost of the product.
 */    
    private String      m_cost;
/** A unique identifier for the product.
 */    
    private String      m_offerID;
/** The voucher's expiry date (for expiry vouchers).
 */    
    private Date        m_expiry;
/** The challenge phrase for the transaction (for one-shot vouchers).
 */    
    private TicketStub  m_stub;
    
/** The offer ID's expiry date (must be redeemed by this time)
 */
    private Date        m_offerExpiry;

    /** Creates new Voucher */
    public Voucher()
    {
    }
    
    public Voucher(Product product)
    {
        m_cost = product.getCost();
        m_offerID = product.getOfferID();
        if (product.getDuration() == 0)
        {
            // One-shot
            m_expiry = new Date(0L);
        }
        else
        {
            m_expiry = new Date(new Date().getTime()
                + 1000L * product.getDuration());
        }
    }
    
/** Gets the cost of the transaction.
 * @return The product's cost.
 */    
    public String       getCost()       { return m_cost;        }
/** Gets the unique identifier for the product being purchased.
 * @return The product identifier.
 */    
    public String       getOfferID()    { return m_offerID;     }
/** Gets the unique identifier for the merchant.
 * @return The merchant identifier.
 */    
    public String       getMerchant()   { return m_merchant;    }
/** Gets the identifier of the merchant's MAMA.
 * @return The MAMA's identifier.
 */    
    public String       getMAMA()       { return m_mama;        }
/** Gets the voucher's expiry date.
 * @return The expiry date.
 */    
    public Date         getExpiry()     { return m_expiry;      }
/** Gets the offer ID's expiry date.
 * @return The expiry date.
 */    
    public Date         getOfferExpiry(){ return m_offerExpiry;      }
/** Gets the ticket stub for the transaction.
 * @return The ticket stub.
 */    
    public TicketStub   getStub()       { return m_stub;   }
    
    public void setCost(String cost)
    {
        m_cost = cost;
    }
    
    public void setOfferID(String offerID)
    {
        m_offerID = offerID;
    }
    
    public void setMerchant(String merchant)
    {
        m_merchant = merchant;
    }
    
    public void setMAMA(String mama)
    {
        m_mama = mama;
    }
    
    public void setExpiry(Date expiry)
    {
        m_expiry = expiry;
    }
    
    public void setOfferExpiry(Date expiry)
    {
        m_offerExpiry = expiry;
    }
    
    public void setStub(byte[] challenge)
    {
        m_stub.setChallenge(challenge);
    }
    
    public void setStub(String challenge)
    {
        m_stub.setChallenge(challenge);
    }
    
    public void setStub(TicketStub stub)
    {
        m_stub = stub;
    }

}

