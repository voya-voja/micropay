/*
 * StubAgent.java
 *
 * Created on June 5, 2001, 6:14 PM
 */

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.MicroAc.Exceptions.InvalidVoucherException;
import com.inexum.MicroAc.Exceptions.VoucherCreationException;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.MicroAc.DomainTypes.Voucher;
import com.inexum.Encoding.CryptoAgentFactory;
import com.inexum.Encoding.CryptoAgent;
import com.inexum.util.ConfigurationManager;

/**
 *
 * @author  rgoold
 * @version 
 */
public class StubAgent extends java.lang.Object
    implements ITicketAgent
{
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
    
    private StubList    m_stubList;

    /** Creates new StubAgent */
    public StubAgent()
    {
        m_stubList = new StubList();
        TicketAgentFactory.Register(TicketStub.class,
            OneShotProduct.class, this);
    }

    public String redeemTicket(ITicket ticket) throws java.io.IOException, InvalidVoucherException
    {
        if (!ticket.getClass().equals(TicketStub.class))
        {
            throw new java.io.IOException();
        }
        
        String offerID = "";
        try
        {
            TicketStub ticketStub = (TicketStub)ticket;
            StubMemento memento = m_stubList.getMementoFromStub(ticketStub);
            m_stubList.removeStub(ticketStub);
            if (memento.getExpiry().before(new java.util.Date()))
            {
                throw new InvalidVoucherException();
            }
            offerID = memento.getOfferID();
        }
        catch (InvalidVoucherException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new InvalidVoucherException(e.getLocalizedMessage());
        }
        return offerID;
    }
    
    public ITicket createTicket(Product product)
        throws VoucherCreationException
    {
        if (product.getDuration() != 0)
        {
            throw new VoucherCreationException();
        }
        Voucher voucher = new Voucher(product);
        voucher.setMAMA(c_rsrcs.getString("acquirer.Server"));
        voucher.setMerchant(c_rsrcs.getString("merchant.Name"));
        
        CryptoAgent crypto = CryptoAgentFactory.GetCryptoAgent();
        
        SignedVoucher signedVoucher = null;
        try
        {
            TicketStub stub = m_stubList.newStubFromProduct(product);
            voucher.setStub(stub);
            signedVoucher = crypto.sign(voucher);
        }
        catch (Exception e)
        {
            throw new VoucherCreationException(e.getLocalizedMessage());
        }
        return signedVoucher;
    }
    
}
