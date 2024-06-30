/*
 * IVoucherAcquirer.java
 *
 * Created on November 7, 2001, 6:15 PM
 */

package com.inexum.MicroAc.Merchant;

import com.inexum.MicroAc.Exceptions.*;

import java.rmi.*;

/** Remote interface.
 *
 * @author  rgoold
 * @version 1.0
 */
public interface IVoucherAcquirer extends java.rmi.Remote
{
    public String redeemVoucher(String encodedVoucher)
        throws RemoteException, InvalidVoucherException;
}

