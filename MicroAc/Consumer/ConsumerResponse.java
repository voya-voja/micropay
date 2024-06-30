//
//  ConsumerResponse.java
//  work
//
//  Created by rgoold on Wed Oct 24 2001.
//  Copyright (c) 2001 iNexum Systems Inc. All rights reserved.
//

package com.inexum.MicroAc.Consumer;

import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.MicroAc.DomainTypes.SignedObject;
import com.inexum.Types.Money;
import com.inexum.MicroAc.Merchant.ITicket;
import com.inexum.MicroAc.Merchant.TicketStub;
import com.inexum.MicroAc.Merchant.Product;

public class ConsumerResponse extends java.lang.Object
    implements java.io.Serializable
{
    public static final int c_Succeeded             = 0x00;
    public static final int c_Failed                = 0x01;
    public static final int c_InvalidRequest        = 0x02;
    public static final int c_UnknownMerchant       = 0x03;
    public static final int c_AccountUnavailable    = 0x04;
    public static final int c_InvalidVoucher        = 0x05;
    public static final int c_RestrictedProduct     = 0x06;
    public static final int c_InsufficientFunds     = 0x07;
    public static final int c_InternalError         = 0xFF;
    
    static final long serialVersionUID = 6397119833708262320L;
    
    private int             m_status;
    private String          m_message;
    private SignedVoucher   m_signedVoucher;
    private Exception       m_exception;
    private Money           m_newBalance;

//----------------------------------------------------------------------------//

    public ConsumerResponse (int status)
    {
        m_status = status;
    }

//----------------------------------------------------------------------------//

    public ConsumerResponse (int status, String message)
    {
        m_status = status;
        m_message = message;
    }

//----------------------------------------------------------------------------//

    public int getStatus ()
    {
        return m_status;
    }

//----------------------------------------------------------------------------//

    public String getMessage ()
    {
        return m_message;
    }
    public void setMessage (String message)
    {
        m_message = message;
    }

//----------------------------------------------------------------------------//

    public SignedVoucher getVoucher ()
    {
        return m_signedVoucher;
    }
    public void setVoucher (SignedVoucher voucher)
    {
        m_signedVoucher = voucher;
    }

//----------------------------------------------------------------------------//

    public Exception getException ()
    {
        return m_exception;
    }
    public void setException (Exception exception)
    {
        m_exception = exception;
    }

//----------------------------------------------------------------------------//

    public Money getNewBalance()
    {
        return m_newBalance;
    }
    public void setNewBalance(Money balance)
    {
        m_newBalance = balance;
    }

}
