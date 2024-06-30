/*
 * SecureNamingService.java
 *
 * Created on June 11, 2001, 6:14 PM
 */

package com.inexum.MicroAc.MicroProcessing.NameService;

import com.inexum.MicroAc.MicroProcessing.Transaction.ISessionFactory;
import com.inexum.MicroAc.Exceptions.UnknownProcessorException;

import java.rmi.*;

/** Remote interface.
 *
 * @author  rgoold
 * @version 
 */
public interface ISecureNamingService extends java.rmi.Remote,
    java.io.Serializable
{
    public void registerCAMA(final String name,
        java.security.SignedObject factoryStub)
        throws RemoteException, UnknownProcessorException,
        java.security.SignatureException, java.io.IOException;
    
    public void registerMAMA(final String name,
        java.security.SignedObject factoryStub)
        throws RemoteException, UnknownProcessorException,
        java.security.SignatureException, java.io.IOException;
    
    public ISessionFactory lookupCAMA(final String name)
        throws RemoteException, UnknownProcessorException;
    
    public ISessionFactory lookupMAMA(final String name)
        throws RemoteException, UnknownProcessorException;
}

