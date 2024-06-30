/*
 * NamingServiceFactory.java
 *
 * Created on June 13, 2001, 4:40 PM
 */

package com.inexum.MicroAc.MicroProcessing.NameService;

import java.rmi.*;
import java.security.SignedObject;

/** Remote interface.
 *
 * @author  rgoold
 * @version 
 */
public interface INamingServiceFactory extends java.rmi.Remote
{
    public SignedObject getService() throws RemoteException;
}

