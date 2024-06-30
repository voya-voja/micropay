/*
 * PurchasingSession.java
 *
 * Created on June 26, 2001, 1:36 PM
 */

package com.inexum.MicroAc.MicroProcessing.Transaction;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.IPC.*;
import com.inexum.Types.Money;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.MicroAc.DomainTypes.IRemoteProfile;
import com.inexum.MicroAc.Transaction.TransactionDescription;
import com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager.*;
import com.inexum.MicroAc.MicroProcessing.NameService.NameServiceCache;
import com.inexum.MicroAc.MicroProcessing.Logging.*;
import com.inexum.util.ConfigurationManager;

import javax.crypto.SealedObject;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

/** Unicast remote object implementing java.rmi.Remote interface.
 *
 * @author  rgoold
 * @version 
 */
public class PurchasingSession extends UnicastRemoteObject
    implements ISession, LogSource, TimeLimited, IRemoteProfile
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();

    private static final long   c_LeaseDuration
        = Long.parseLong (c_Rsrcs.getString("LeaseDuration"), 10);

    private long                m_leasedUntil = 0L;
    
    private final String        m_sessionID;
    private boolean             m_finished = false;
    private int                 m_transactionCount = 0;
    private NameServiceCache    m_nsCache;
    private ConsumerProfile     m_profile;
    private java.util.List      m_listeners;
    
//----------------------------------------------------------------------------//
    
    /* (2001-08-14 RTG) This is an ugly hack. Something needs to be done about
     * how the CAMA reports new balances back to the consumer after a purchase,
     * such as altering the class returned by a purchase call or providing a
     * separate line of communication for account inquiries.
     */
    public double getBalance ()
        throws RemoteException
    {
        try
        {
            return m_profile.getBalance();
        }
        finally
        {
        }
    }
    
//----------------------------------------------------------------------------//
    
    public SignedVoucher purchase(TxRequest request,SealedObject sealedVoucher)
        throws RemoteException, UnknownMerchantException,
        InvalidVoucherException, AccountUnavailableException
    {
        throw new RemoteException();
    }
    
//----------------------------------------------------------------------------//
    
    public SignedVoucher purchase(TransactionDescription description,
        SealedObject sealedVoucher)
        throws UnknownMerchantException, InvalidVoucherException,
        RestrictedProductException, InsufficientFundsException,
        AccountUnavailableException, RemoteException
    {
        extendLease(c_LeaseDuration);
        
        // Check if a voucher already exists for this product.
        try
        {
            SignedVoucher voucher = m_profile.getVoucher(
                description.getIssuer(), description.getOfferID());
            return voucher;
        }
        catch (NoSuchVoucherException e)
        {
            // No special action necessary. Just proceed with regular purchase.
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new AccountUnavailableException(e.getLocalizedMessage());
        }
        
        // Check that the offer is still good
        if (description.getOfferExpiry() != null &&
            description.getOfferExpiry().before(new java.util.Date()))
        {
            // #warning Localize this.
            throw new RestrictedProductException("Offer expired.");
        }
        
        Money consumerCost = null;
        if (description.getCost().getCurrency().equals(
            m_profile.getCurrency()))
        {
            consumerCost = description.getCost();
        }
        else
        {
            Money basePrice = description.getCost().convertToBaseCurrency(
                description.getIssuerExchangeRate());
            consumerCost = basePrice.convertFromBaseCurrency(
                m_profile.getXchngRate(), m_profile.getCurrency());
        }
        
        final double errorMargin = 0.0001;
        // Check that consumer has authorised it
        if ((consumerCost.getPrice() - description.getAuthorizedAmount().
            getPrice()) > errorMargin)
        {
            // #warning Localize this.
            throw new InvalidVoucherException("Insufficient funds authorized.");
        }
        
        // Check for sufficient funds
        if (consumerCost.getPrice() > m_profile.getBalance())
        {
            throw new InsufficientFundsException();
        }
        
        // Check against product restrictions
        if (false)
        {
            throw new RestrictedProductException();
        }
        
        TxRequest request = new TxRequest(m_sessionID, m_transactionCount,
            description.getOfferID(), description.getIssuer(),
            consumerCost, description.getCost());
        
        // To make sure we clear the profiling stack if exceptions
        // are thrown.
        try
        {
            ISessionFactory mamaFactory
                = m_nsCache.lookupMAMA(description.getIssuerAccountManager());

            ISession session = mamaFactory.newSession(m_sessionID,
                m_transactionCount);
            
            SignedVoucher result = null;
            result = session.purchase(request, sealedVoucher);
            
            try
            {
                m_profile.debit(consumerCost.getPrice());
            }
            catch (InsufficientFundsException e)
            {
                session.rollBack( request );
                throw e;
            }
            catch ( AccountUnavailableException  AUE )
            {
                //rollback the transaction on the merchant side
                session.rollBack( request );
                throw AUE;
            }
            
            final ConsumerProfile profile = m_profile;
            final TransactionDescription txDesc = description;
            final SignedVoucher resultTmp = result;
            new Thread()
            {
                public void run()
                {
                    profile.addVoucher( txDesc, resultTmp );
                }
            }.start();
            m_transactionCount++;
            fireSuccessEvent(request);
            return result;
        }
        catch (java.rmi.RemoteException e)
        {
            e.printStackTrace();
            fireFailureEvent(request, e);
            throw e;
        }
        catch (UnknownProcessorException e)
        {
            e.printStackTrace();
            fireFailureEvent(request, e);
            throw new UnknownMerchantException(e.getLocalizedMessage());
        }
        catch (UnknownMerchantException e)
        {
            e.printStackTrace();
            fireFailureEvent(request, e);
            throw e;
        }
        catch (InvalidVoucherException e)
        {
            e.printStackTrace();
            fireFailureEvent(request, e);
            throw e;
        }
        catch ( AccountUnavailableException  AUE )
        {
           //The merchant account was unavailable
           throw AUE;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fireFailureEvent(request, e);
            throw new java.rmi.RemoteException(e.getLocalizedMessage());
        }
        finally
        {
        }
    }
    
//----------------------------------------------------------------------------//

    /** Constructs PurchasingSession object and exports it on default port.
     */
    public PurchasingSession(String sessionID, NameServiceCache nsCache,
        ConsumerProfile profile)
        throws RemoteException
    {
        super( 0 /* ,
            new RMISSLClientSocketFactory(),
            new RMISSLServerSocketFactory() */ );
        
        m_sessionID = sessionID;
        m_nsCache = nsCache;
        m_profile = profile;
        m_listeners = new java.util.LinkedList();
//        TransactionLogEvent startEvent = new TransactionLogEvent(this,
//            TransactionLogEvent.c_StartSession, sessionID, 0,
//            m_profile.getUserID(), ...);
        new OasisTransactionLogger(this);
        startTimer();
    }
    
//----------------------------------------------------------------------------//

    /** Constructs PurchasingSession object and exports it on specified port.
     * @param port The port for exporting
     */
    public PurchasingSession(String sessionID, NameServiceCache nsCache,
        ConsumerProfile profile, int port)
        throws RemoteException
    {
        super( port /* ,
            new RMISSLClientSocketFactory(),
            new RMISSLServerSocketFactory() */ );
        
        m_sessionID = sessionID;
        m_nsCache = nsCache;
        m_profile = profile;
        m_listeners = new java.util.LinkedList();
        new OasisTransactionLogger(this);
        startTimer();
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
        // TimeLimitTask and the RMI runtime can both call this, so to
        // prevent double calls to unexportObject we ignore anything past
        // the first request.
        if (m_finished)
        {
            return;
        }
        
        m_finished = true;
        try
        {
            UnicastRemoteObject.unexportObject(this, false);
        }
        catch (java.rmi.NoSuchObjectException e)
        {
            e.printStackTrace();
        }
        m_profile.close();
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
    
    private void fireFailureEvent(TxRequest request, Exception e )
    {
        ErrorLogEvent failureEvent
            = new ErrorLogEvent(this, e.getLocalizedMessage());
       
        fireEvent(failureEvent);
    }
    
//----------------------------------------------------------------------------//
    
    private void fireSuccessEvent(TxRequest request )
    {
        TransactionLogEvent successEvent
            = new TransactionLogEvent(this, 
            TransactionLogEvent.c_Debit,
            request.getSessionID(), m_transactionCount, m_profile.getUserID(),
            request.getProductID(), request.getMerchantCost(),
            request.getConsumerCost(), m_profile.getPAN(), m_profile.getBIN());
        
           fireEvent(successEvent);
    }
    
//----------------------------------------------------------------------------//
    
    protected void fireEvent(LogEvent event)
    {
        java.util.Iterator listenerIterator = m_listeners.iterator();
        while (listenerIterator.hasNext())
        {
            LogListener listener = (LogListener)listenerIterator.next();
            listener.logMessage(event);
        }
    }
    
//----------------------------------------------------------------------------//
    
    public final String getSessionID()
    {
        return m_sessionID;
    }
    
//----------------------------------------------------------------------------//

    /** For debugging purposes.
     */
    public static void main(String[] args)
    {
    }
    
//----------------------------------------------------------------------------//
    
    public void startTimer ()
    {
        TimeLimitTask task = new TimeLimitTask(this);
        java.util.Timer timer
            = new java.util.Timer();
        timer.schedule (task, c_LeaseDuration, c_LeaseDuration);
    }
    
//----------------------------------------------------------------------------//
    
    public void extendLease (long duration)
    {
        long until = new java.util.Date().getTime() + duration;
        
        // Guarantee that extending will never shorten the lease.
        if (m_leasedUntil < until)
        {
            m_leasedUntil = until;
        }
    }
    
//----------------------------------------------------------------------------//
    
    public boolean isLeaseValid ()
    {
        if (new java.util.Date(m_leasedUntil).before (new java.util.Date()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
//----------------------------------------------------------------------------//
    
    public void close() throws RemoteException
    {
        unreferenced();
    }
    
//----------------------------------------------------------------------------//
    
    public void rollBack(TxRequest request)
        throws RemoteException, UnknownMerchantException 
    {
        throw new RemoteException();
        //if this is a possibility
        //roll back the transaction on the consumer side
        //roll back the transaction on the merchant side
    }

}
