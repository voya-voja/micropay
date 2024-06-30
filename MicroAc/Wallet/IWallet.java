/*
 * WalletI.java
 *
 * Created on May 27, 2001, 2:31 PM
 */

package com.inexum.MicroAc.Wallet;

import java.util.*;

import com.inexum.MicroAc.Exceptions.InvalidLoginException;
import com.inexum.MicroAc.Exceptions.ServiceUnavailableException;

/**
 *
 * @author Nick Kapov
 * @version 1.0
 */
public interface IWallet 
{
   
   /** Open the wallet for purchases
    * @param userID The users unique identifier    
    * @return void
    * @param password The users password
    * @throws InvalidLoginException Exception thrown if the userID or the 
    *      password is invalid.
    * @roseuid 3B15010E0092
 */
   public void open( final java.lang.String userID, 
                     final java.lang.String password) 
                                                   throws InvalidLoginException,
                                                   ServiceUnavailableException;
   
   /** Close the wallet    
    * @roseuid 3B15010E0095
 */
   public void close();
   
   /** Get the wallet balance.
    * @return The amount of money in the wallet.
    * @roseuid 3B15010E009C
 */
   public double getBalance();
   
   
   /** Adds the specified wallet listener to receive wallet events from this 
    * wallet. 
    * @param listener The wallet listener
    * @roseuid 3B226CC0037A
 */
   public void addWalletListener( IWalletListener listener );
   
   public void removeWalletListener( IWalletListener listener );
   

   public Collection getVouchers();
   
}
