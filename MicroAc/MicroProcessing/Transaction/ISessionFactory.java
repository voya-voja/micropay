/*
 * ISessionFactory.java
 *
 * Created on June 9, 2001, 3:22 PM
 */

package com.inexum.MicroAc.MicroProcessing.Transaction;

import com.inexum.MicroAc.DomainTypes.Profile;

import java.rmi.*;

/** Remote interface.
 *
 * @author  rgoold
 * @version 
 */
public interface ISessionFactory extends java.rmi.Remote
{
    public ISession newSession(final String sessionID, int txID)
        throws RemoteException;
    
    public ISession newSession(Profile profile)
        throws RemoteException, IllegalArgumentException;
}

