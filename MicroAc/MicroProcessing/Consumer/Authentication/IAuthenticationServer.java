/*
 * IAuthenticationServer.java
 *
 * Created on June 8, 2001, 1:37 PM
 */

package com.inexum.MicroAc.MicroProcessing.Consumer.Authentication;

import java.rmi.*;


/** Remote interface.
 *
 * @author nkapov
 * @version 1.0
 */
public interface IAuthenticationServer extends java.rmi.Remote {
/** A login request is made to the authentication server which will provide an
 * authenticator
 * @throws RemoteException Required RMI exception
 * @return Returns the interface to an authenticator
 */    
     public IAuthenticator login() throws RemoteException;
   
        
}

