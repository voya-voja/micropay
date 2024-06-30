/*
 * IRemoteProfile.java
 *
 * Created on August 14, 2001, 6:07 PM
 */

package com.inexum.MicroAc.DomainTypes;

import com.inexum.Types.Money;

import java.rmi.*;

/** Remote interface.
 *
 * @author  rgoold
 * @version 
 */
public interface IRemoteProfile extends java.rmi.Remote
{
    public double getBalance () throws java.rmi.RemoteException;
}

