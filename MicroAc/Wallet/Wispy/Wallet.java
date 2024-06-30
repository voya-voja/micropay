/*
 * Wallet.java
 *
 * Created on May 7, 2001, 10:11 AM
 */

package com.inexum.MicroAc.Wispy.Wallet;

import com.inexum.Types.Money;
import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.Transaction.TransactionQueue;
import com.inexum.MicroAc.Transaction.PeerListener;
import com.inexum.IPC.ServiceRegistry;
import com.inexum.util.ConfigurationManager;
import com.inexum.MicroAc.Wallet.*;

import java.util.Collection;
/**
 *
 * @author  Nick Kapov
 * @version 1.0
 */
public class Wallet extends java.lang.Object
    implements IWallet 
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();

/** Unique identifier for the wallet*/
    private String              m_signature;

/** Interface for the wallet to its associated CAMA*/    
    private CAMAProxy           m_CAMAProxy;
    
/** Tracks pending micropayment transactions. */
    private TransactionQueue    m_transactionQueue;

/** Listens for incoming display data from microproduct links. */
    private WindowShoppingListener  m_windowShoppingListener;
    
/** Listens for incoming requests from microproduct links. */
    private LinkListener        m_linkListener;
    
/** Listens for incoming requests from peers. */
    private PeerListener     m_voucherListener;
    
/** True if the walle is open*/   
    private boolean             m_isOpen = false;
    
//----------------------------------------------------------------------------//
    
/**
 * Creates new Wallet, reading the properties file to get the wallet
 * signature so its ready for action
 */
    public Wallet() 
    {
        /* (RTG 2001-11-20) Install the appropriate security managers. */
 /*       java.security.Security.insertProviderAt(
            new cryptix.jce.provider.CryptixCrypto(), 1);
        java.security.Security.insertProviderAt(
            new cryptix.jce.provider.CryptixRandom(), 2);
        java.security.Security.insertProviderAt(
            new com.sun.net.ssl.internal.ssl.Provider(), 3);
*/        
        /* (RTG 2001-11-08) Register with the named service registry. */
        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        registry.setServiceForName(this, "Wallet");
        
        m_CAMAProxy = new CAMAProxy();
        registry.setServiceForName(m_CAMAProxy, "CAMAProxy");
        
        m_transactionQueue = TransactionQueue.getDefaultInstance();
        
        /* ( NK 2001-05-27 ) Read the walletID from the properties file */
        m_signature = c_Rsrcs.getString( "issuer.WalletSignature" );
    }
    
//----------------------------------------------------------------------------//

/**
 * Opens the wallet for purchases, logging in the user and starting up the
 * transaction manager.
 * @param userID The userID, the consumer defined login name
 * @param password The consumers password
 * @throws InvalidLoginException The wallet is unable to login the user.
 */    
    public void open(final String userID, final String password)
        throws InvalidLoginException, ServiceUnavailableException
    {
        try
        {
            if( !m_isOpen )
            {
                /* ( NK 2001-05-26 ) An exception will be thrown if the 
                 * connectiuon cannot be established
                 */
                try
                {
                    m_CAMAProxy.connect( userID, password, m_signature );
                }
                catch (InvalidLoginException e)
                {
                    m_CAMAProxy.fireWalletConnectEvent(
                        new WalletConnectEvent(false));
                    throw e;
                }
                catch (ServiceUnavailableException e)
                {
                    m_CAMAProxy.fireWalletConnectEvent(
                        new WalletConnectEvent(false));
                    throw e;
                }

                startServices();
                
                m_isOpen = true;
            }
        }
        catch( InvalidLoginException ILE )
        {
            /* ( NK 2001-05-23 ) Rethrow the exception to the wallet interface,
             * where the wallet can display an appropriate message to the user
             */
            throw ILE;
        }
        catch( ServiceUnavailableException SUE )
        {
            throw SUE;
        }
        finally
        {
            m_CAMAProxy.fireWalletConnectEvent(
                new WalletConnectEvent(m_isOpen));
        }
    }
   
//----------------------------------------------------------------------------//
    
    private void startServices()
    {
            try
            {
                m_linkListener = new LinkListener();
                m_linkListener.start();
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                m_windowShoppingListener = new WindowShoppingListener();
                m_windowShoppingListener.start();
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }

            // Start listening for peer requests.
            try
            {
                m_voucherListener = new PeerListener();
                m_voucherListener.start();
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
    }
    
//----------------------------------------------------------------------------//
   
/**
 * Close the wallet 
 */   
    public void close()
    {
        if( m_isOpen )
        {
            m_CAMAProxy.disconnect();
            m_linkListener.terminate();
            m_windowShoppingListener.terminate();
            m_voucherListener.terminate();
            m_isOpen = false;
        }
        m_CAMAProxy.fireWalletConnectEvent(new WalletConnectEvent(false));
    }
   
//----------------------------------------------------------------------------//
   
/**
 * Get the amount of money available for spending from the wallet.
 * @return The amount of money in the wallet, in the users denomination.
 */
   public double getBalance()
   {
       if( m_isOpen )
           return m_CAMAProxy.getBalance();
       else
           return 0.00;
   }
   
//----------------------------------------------------------------------------//
  
/**
 * Get the voucher info for the consumer
 * @return A collection of voucher strings
 */   
   public Collection getVouchers()
   {
       if( m_isOpen )
           return m_CAMAProxy.getVouchers();
       else
           return null;
   }
   
//----------------------------------------------------------------------------//
   
/**
 * Add a listener to the wallet
 * @param listener The listener object
 */   
   public synchronized void addWalletListener( IWalletListener listener )
   {
      m_CAMAProxy.addWalletListener( listener );
   }
   
//----------------------------------------------------------------------------//
   
/**
 * Remove a listener from the wallet
 * @param listener The listener object.
 */   
   public synchronized void removeWalletListener( IWalletListener listener )
   {
       m_CAMAProxy.removeWalletListener( listener );
   }
   
//----------------------------------------------------------------------------//
     
/**
 * For debugging purposes
 * @param args Command line arguments
 */
   public static void main (String[] args)
   {
        Wallet wally = new Wallet();
        try
        {
            wally.open("Nick", "Nick123");
            System.out.println("Logged on successfully.");
        }
        catch (InvalidLoginException e)
        {
            System.out.println("InvalidLoginException: " + e.getMessage());
        }
        catch ( ServiceUnavailableException SUE )
        {
            System.out.println("Service Unavailable: " + SUE.getMessage());
            
        }
        wally.close();
   }

}
