/***** Copyright 2001 iNexum Systems Inc. *****************************
**
**      File: ICashier.java
**
**      Description:
**          Remote interface for initial point of access for
**          merchant transactions.
**
**      Revision History:
**              2001-05-12 (RTG) Initial revision.
**
**********************************************************************/


package com.inexum.MicroAc.Merchant;

import java.rmi.*;

/** Remote interface.
 *
 * @author  rgoold
 * @version 1.0
 */
public interface ICashier extends java.rmi.Remote
{
/** Allows a remote wallet to connect to the server.
 * @throws RemoteException A generic exception.
 * @return A TransactionAgent for processing transactions.
 */    
    public ITransactionAgent connect() throws RemoteException;
    public IVoucherIssuer getIssuer()
        throws RemoteException, SecurityException;
    public IVoucherAcquirer getAcquirer()
        throws RemoteException, SecurityException;
}

