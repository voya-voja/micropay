/*
 * IVoucherIssuer.java
 *
 * Created on November 7, 2001, 5:44 PM
 */

package com.inexum.MicroAc.Merchant;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.Transaction.PeerRequest;

import java.rmi.*;
import javax.crypto.SealedObject;

/** Remote interface.
 *
 * @author  rgoold
 * @version 1.0
 */
public interface IVoucherIssuer extends java.rmi.Remote
{
    public SealedObject generateSealedVoucher(String offerID)
        throws RemoteException, UnknownOfferException,
        PurchaseInterruptedException;

    public ITicket generateSignedVoucher(String offerID)
        throws RemoteException, UnknownOfferException,
        PurchaseInterruptedException;

    public PeerRequest generateRefundOffer(String voucherData,
        String redemptionURL)
        throws RemoteException, UnknownOfferException,
        PurchaseInterruptedException;
}
