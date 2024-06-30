/*
 * ProcessingSession.java
 *
 * Created on June 25, 2001, 6:52 PM
 */

package com.inexum.MicroAc.MicroProcessing.Merchant.Transaction;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.MicroProcessing.Logging.*;
import com.inexum.MicroAc.MicroProcessing.Merchant.ProfileManager.*;
import com.inexum.Types.Money;
import com.inexum.MicroAc.MicroProcessing.Transaction.*;
import com.inexum.MicroAc.Transaction.TransactionDescription;
import com.inexum.Encoding.CryptoAgent;
import com.inexum.MicroAc.DomainTypes.*;

import javax.crypto.SealedObject;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

/** Unicast remote object implementing java.rmi.Remote interface.
 *
 * @author  rgoold
 * @version 1.0
 */
public class ProcessingSession extends UnicastRemoteObject
    implements ISession, Unreferenced, LogSource
{
    private boolean                 m_finished = false;
    private IMerchantProfileFactory m_profileFactory;
    private java.util.List          m_listeners;
    
//----------------------------------------------------------------------------//

    /** Constructs ProcessingSession object and exports it on default port.
     */
    public ProcessingSession(IMerchantProfileFactory profileFactory)
        throws RemoteException
    {
        super (0 /* , new RMISSLClientSocketFactory(),
            new RMISSLServerSocketFactory() */ );
        m_profileFactory = profileFactory;
        m_listeners = new java.util.LinkedList();
        try
        {
            new OasisTransactionLogger(this);
        }
        finally
        {
        }
    }
    
//----------------------------------------------------------------------------//

    /** Constructs ProcessingSession object and exports it on specified port.
     * @param port The port for exporting
     */
    public ProcessingSession(IMerchantProfileFactory profileFactory, int port)
        throws RemoteException
    {
        super (port /* , new RMISSLClientSocketFactory(),
            new RMISSLServerSocketFactory() */ );
        m_profileFactory = profileFactory;
        m_listeners = new java.util.LinkedList();
        try
        {
            new OasisTransactionLogger(this);
        }
        finally
        {
        }
    }
    
//----------------------------------------------------------------------------//
    
    public SignedVoucher purchase(TransactionDescription description,
        SealedObject sealedVoucher)
        throws RemoteException, UnknownMerchantException,
        InvalidVoucherException, RestrictedProductException,
        InsufficientFundsException
    {
        throw new RemoteException();
    }
    
//----------------------------------------------------------------------------//

    private boolean voucherMatchesRequest (Voucher voucher, TxRequest request)
    {
        if (!voucher.getMerchant().equalsIgnoreCase(request.getMerchantID()))
        {
            return false;
        }
        
        Money merchantCost = new Money(voucher.getCost());
        if (!merchantCost.equals(request.getMerchantCost()))
        {
            return false;
        }
        return true;
    }
    
//----------------------------------------------------------------------------//
    
    public void rollBack(TxRequest request) throws RemoteException,
        UnknownMerchantException
    {
        // Retrieve profile and crypto agent for profile key.
        MerchantProfile profile = null;
        try
        {
            profile = m_profileFactory.getProfile(request.getMerchantID());
        }
        finally
        {
        }
        
        // Close the profile
        try
        {
            m_profileFactory.closeProfile(profile);
        }
        catch (ProfileUpdateException e)
        {
            ErrorLogEvent errorEvent = new ErrorLogEvent(this,
                ErrorLogEvent.c_ProfileUpdateFailed);
            e.printStackTrace(errorEvent.getTraceStream());
            fireEvent(errorEvent);
        }
        finally
        {
        }
        
        //The transaction failed at the other end so roll back the transaction
        TransactionLogEvent event = new TransactionLogEvent( this, 
            TransactionLogEvent.c_Debit, request.getSessionID(),
            request.getTransactionID(), request.getMerchantID(),
            request.getProductID(), request.getMerchantCost(), 
            request.getConsumerCost(), profile.getPAN(), 
            profile.getBIN() );
        fireEvent(event);
    }
    
//----------------------------------------------------------------------------//
    
    public SignedVoucher purchase(TxRequest request,SealedObject sealedVoucher)
        throws RemoteException, UnknownMerchantException,
        InvalidVoucherException, AccountUnavailableException
    {
        // Retrieve profile and crypto agent for profile key.
        MerchantProfile profile = null;
        profile = m_profileFactory.getProfile(request.getMerchantID());
        CryptoAgent cryptoAgent = null;
        cryptoAgent = new CryptoAgent(profile.getKey());
        try
        {
            SignedVoucher signedVoucher = null;
            signedVoucher = cryptoAgent.unseal(sealedVoucher);
            Voucher voucher = signedVoucher.getVoucher();
            
            // Make sure the voucher is valid
            if (voucherMatchesRequest(voucher, request))
            {
                profile.credit(request.getMerchantCost().getPrice());
                
                // Fire an appropriate event
                TransactionLogEvent event = new TransactionLogEvent(this,
                    TransactionLogEvent.c_Credit, 
                    request.getSessionID(), request.getTransactionID(), 
                    request.getMerchantID(), request.getProductID(),
                    request.getMerchantCost(), request.getConsumerCost(), 
                    profile.getPAN(), profile.getBIN() );
                fireEvent(event);
                
                return signedVoucher;
            }
            else
            {
                // Caught below and rethrown.
                throw new InvalidVoucherException();
            }
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            
            //these events should be replaced with events in an eventlog table
            TransactionLogEvent event = new TransactionLogEvent(this,
                    TransactionLogEvent.c_TransactionFailed, 
                    request.getSessionID(), request.getTransactionID(), 
                    request.getMerchantID(), request.getProductID(),
                    request.getMerchantCost(), request.getConsumerCost(), 
                    profile.getPAN(), profile.getBIN() );
            
            fireEvent(event);
            throw new InvalidVoucherException(e.getLocalizedMessage());
        }
        catch ( AccountUnavailableException AUE )
        {
            AUE.printStackTrace();
            throw AUE;
        }
        catch (InvalidVoucherException e)
        {
            e.printStackTrace();
            TransactionLogEvent event = new TransactionLogEvent(this,
                    TransactionLogEvent.c_TransactionFailed, 
                    request.getSessionID(), request.getTransactionID(), 
                    request.getMerchantID(), request.getProductID(),
                    request.getMerchantCost(),  request.getConsumerCost(), 
                    profile.getPAN(), profile.getBIN() );
            
            fireEvent(event);
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            TransactionLogEvent event = new TransactionLogEvent(this,
                    TransactionLogEvent.c_TransactionFailed, 
                    request.getSessionID(), request.getTransactionID(), 
                    request.getMerchantID(), request.getProductID(),
                    request.getMerchantCost(),  request.getConsumerCost(), 
                    profile.getPAN(), profile.getBIN() );
            fireEvent(event);
            throw new InvalidVoucherException(e.getLocalizedMessage());
        }
        finally
        {
            try
            {
                m_profileFactory.closeProfile (profile);
            }
            catch (ProfileUpdateException e)
            {
                e.printStackTrace();
            }
        }
    }
    
//----------------------------------------------------------------------------//
    
    public boolean isFinished ()
        throws java.rmi.RemoteException
    {
        return m_finished;
    }
    
//----------------------------------------------------------------------------//
    
    public void unreferenced()
    {
        m_finished = true;
        try
        {
            UnicastRemoteObject.unexportObject(this, true);
        }
        catch (java.rmi.NoSuchObjectException e)
        {
            e.printStackTrace();
        }
    }
    
//----------------------------------------------------------------------------//
    
    public void addListener(LogListener listener)
    {
        m_listeners.add(listener);
    }
    
//----------------------------------------------------------------------------//
    
    public void removeListener(LogListener listener)
    {
        if (m_listeners.contains(listener))
        {
            m_listeners.remove(listener);
        }
    }
    
//----------------------------------------------------------------------------//
    
    private void fireEvent(LogEvent event)
    {
        java.util.Iterator iterator = m_listeners.iterator();
        while (iterator.hasNext())
        {
            LogListener listener = (LogListener)iterator.next();
            listener.logMessage(event);
        }
    }
    
//----------------------------------------------------------------------------//
    
    /** For debugging purposes only.
     */
    public static void main(String[] args)
    {
    }
    
//----------------------------------------------------------------------------//
    
    public void close()
        throws RemoteException
    {
        // Allow call to complete and then unreference this object.
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                public void run()
                {
                    unreferenced();
                }
            },
            2000L
        );
    }

}
