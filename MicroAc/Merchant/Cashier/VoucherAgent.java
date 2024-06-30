/*
 * VoucherAgent.java
 *
 * Created on June 5, 2001, 2:07 PM
 */

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.MicroAc.Exceptions.InvalidVoucherException;
import com.inexum.MicroAc.Exceptions.VoucherCreationException;
import com.inexum.Encoding.CryptoAgentFactory;
import com.inexum.Encoding.CryptoAgent;
import com.inexum.MicroAc.DomainTypes.Voucher;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.util.ConfigurationManager;

/**
 *
 * @author  rgoold
 * @version 
 */
public class VoucherAgent extends java.lang.Object
    implements com.inexum.MicroAc.Merchant.ITicketAgent
{
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();

    /** Creates new VoucherAgent */
    public VoucherAgent()
    {
        TicketAgentFactory.Register(SignedVoucher.class,
            ExpiryProduct.class, this);
    }
    
    public String redeemTicket(ITicket ticket) throws java.io.IOException, InvalidVoucherException
    {
        if (!ticket.getClass().equals(SignedVoucher.class))
        {
            throw new java.io.IOException();
        }
        SignedVoucher signedVoucher = (SignedVoucher)ticket;
        CryptoAgent crypto = CryptoAgentFactory.GetCryptoAgent();
        try
        {
            if (!crypto.verify(signedVoucher))
            {
                throw new InvalidVoucherException();
            }
        }
        catch (java.security.SignatureException e)
        {
            e.printStackTrace();
            throw new InvalidVoucherException(e.getLocalizedMessage());
        }
        Voucher voucher = signedVoucher.getVoucher();
        if (voucher.getExpiry().before(new java.util.Date()))
        {
            throw new InvalidVoucherException();
        }
        return voucher.getOfferID();
    }
    
    public ITicket createTicket(Product product)
        throws VoucherCreationException
    {
        if (product.getDuration() == 0)
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
            signedVoucher = crypto.sign(voucher);
        }
        catch (Exception e)
        {
            e.printStackTrace();    // Debugging only.
            throw new VoucherCreationException(e.getLocalizedMessage());
        }
        return signedVoucher;
    }
    
}

