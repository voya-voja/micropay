/*
 * ITransactionAgent.java
 *
 * Created on May 12, 2001, 2:43 PM
 */

package com.inexum.MicroAc.Merchant;

import com.inexum.MicroAc.Exceptions.*;

import java.rmi.*;
import javax.crypto.SealedObject;

/** Remote interface.
 *
 * @author  rgoold
 * @version 
 */
public interface ITransactionAgent extends java.rmi.Remote
{
/** Initiates the purchase of a product.
 * @param offerID The offer identifying the product being purchased.
 * @throws RemoteException A generic exception.
 * @return A signed voucher for the product.
 */    
    public SealedObject purchase(String offerID)
                                 throws RemoteException, UnknownOfferException,
                                    PurchaseInterruptedException;

/** Requests the filesystem location of a product for a signed voucher.
 * @param String The base64 encoded <code>SignedVoucher</code>
 * @throws RemoteException An RMI exception.
 * @throws InvalidVoucherException The voucher could not be processed.
 * @returns The location where the product can be retrieved.
 */
    public String redeem (String encodedVoucher)
        throws RemoteException, InvalidVoucherException;
}

