/*
 * ITicketAgent.java
 *
 * Created on June 5, 2001, 2:01 PM
 */

package com.inexum.MicroAc.Merchant;

import com.inexum.MicroAc.Exceptions.InvalidVoucherException;
import com.inexum.MicroAc.Exceptions.VoucherCreationException;

/**
 *
 * @author  rgoold
 * @version 
 */
public interface ITicketAgent
{
    public String redeemTicket (ITicket ticket)
        throws java.io.IOException, InvalidVoucherException;
    
    public ITicket createTicket (Product product)
        throws VoucherCreationException;
}


