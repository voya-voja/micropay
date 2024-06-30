/*
 * IConsumerProfileManager.java
 *
 * Created on June 27, 2001, 5:13 PM
 */

package com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager;

import com.inexum.MicroAc.Consumer.LoginInfo;
import com.inexum.MicroAc.Exceptions.ProfileCreationException;

import java.rmi.*;

/**
 *
 * @author  nkapov
 * @version 
 */
public interface IConsumerProfileManager extends Remote
{
    public ProfileProxy add (String userID)
        throws ProfileCreationException, RemoteException;
    
    public boolean isLoggedIn (final LoginInfo loginInfo)
        throws RemoteException;
}


