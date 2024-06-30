//
//  ConsumerRequest.java
//  work
//
//  Created by rgoold on Wed Oct 24 2001.
//  Copyright (c) 2001 iNexum Systems Inc. All rights reserved.
//

package com.inexum.MicroAc.Consumer;

import com.inexum.MicroAc.Transaction.TransactionDescription;

import javax.crypto.SealedObject;


public class ConsumerRequest extends java.lang.Object
    implements java.io.Serializable
{
    static final long serialVersionUID = 8239798244443999401L;
    public static final int c_KeepAlive         = 0x00;
    public static final int c_LogoutRequest     = 0x01;
    public static final int c_PurchaseRequest   = 0x02;

    private int                     m_requestType;
    private TransactionDescription  m_description;
    private SealedObject            m_sealedVoucher;

    public ConsumerRequest (int requestType)
    {
        m_requestType = requestType;
    }

    public int getRequestType ()
    {
        return m_requestType;
    }

    public TransactionDescription getDescription ()
    {
        return m_description;
    }
    public void setDescription (TransactionDescription description)
    {
        m_description = description;
    }

    public SealedObject getSealedVoucher ()
    {
        return m_sealedVoucher;
    }
    public void setSealedVoucher (SealedObject sealedVoucher)
    {
        m_sealedVoucher = sealedVoucher;
    }

}
