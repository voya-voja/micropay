//
//  ProfileInfo.java
//  work
//
//  Created by rgoold on Tue Oct 23 2001.
//  Copyright (c) 2001 iNexum Systems Inc. All rights reserved.
//

package com.inexum.MicroAc.Consumer;

import com.inexum.Types.Money;
import com.inexum.MicroAc.DomainTypes.VoucherInfo;


public final class ProfileInfo extends java.lang.Object
    implements java.io.Serializable
{
    private Money           m_balance;
    private Money           m_exchangeRate;
    private VoucherInfo[]   m_vouchers;

//----------------------------------------------------------------------------//

    public ProfileInfo (Money balance, Money exchangeRate,
        VoucherInfo[] vouchers)
    {
        m_balance = balance;
        m_exchangeRate = exchangeRate;
        m_vouchers = vouchers;
    }

//----------------------------------------------------------------------------//

    public Money getBalance ()
    {
        return m_balance;
    }
    public void setBalance(Money balance)
    {
        m_balance = balance;
    }

//----------------------------------------------------------------------------//

    public Money getExchangeRate ()
    {
        return m_exchangeRate;
    }

//----------------------------------------------------------------------------//

    public int countVouchers ()
    {
        return m_vouchers.length;
    }

//----------------------------------------------------------------------------//

    public VoucherInfo[] getVouchers ()
    {
        return m_vouchers;
    }

}
