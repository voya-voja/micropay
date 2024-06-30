/*
 * ISession.java
 *
 * Created on June 25, 2001, 6:45 PM
 */

package com.inexum.MicroAc.MicroProcessing.Transaction;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.Transaction.TransactionDescription;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;

import javax.crypto.SealedObject;

import java.rmi.*;
import java.rmi.server.*;

/** Remote interface.
 *
 * @author  rgoold
 * @version 1.0
 */
public interface ISession extends Remote
{
    public SignedVoucher purchase (TransactionDescription description,
    SealedObject sealedVoucher)
    throws RemoteException, UnknownMerchantException,
    InvalidVoucherException,RestrictedProductException,
    InsufficientFundsException, AccountUnavailableException;
    
    public SignedVoucher purchase (TxRequest request,
    SealedObject sealedVoucher)
    throws RemoteException, UnknownMerchantException,
    InvalidVoucherException, AccountUnavailableException;
    
    public boolean isFinished ()
    throws RemoteException;
    
    public void close() throws RemoteException;
    
    public void rollBack( TxRequest request )
        throws RemoteException, UnknownMerchantException;
}

