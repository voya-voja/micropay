/*
 * IAuthenticator.java
 *
 * Created on June 6, 2001, 5:54 PM
 */

package com.inexum.MicroAc.MicroProcessing.Consumer.Authentication;

import com.inexum.MicroAc.DomainTypes.*;
import com.inexum.MicroAc.Exceptions.InvalidLoginException;
import com.inexum.MicroAc.Exceptions.ServiceUnavailableException;
import com.inexum.MicroAc.Consumer.LoginInfo;
import com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager.ProfileProxy;

import java.rmi.*;


/** Remote interface.
 *
 * @author nkapov
 * @version 1.0
 */
public interface IAuthenticator extends java.rmi.Remote {
    
    public ProfileProxy authenticate( LoginInfo loginInfo ) throws RemoteException,
                                                        InvalidLoginException,
                                                        ServiceUnavailableException;
    
}

