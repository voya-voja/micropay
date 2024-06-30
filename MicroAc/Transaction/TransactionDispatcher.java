/*
 * TransactionDispatcher.java
 *
 * Created on November 8, 2001, 5:36 PM
 */

package com.inexum.MicroAc.Transaction;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.Types.Money;
import com.inexum.IPC.ServiceRegistry;
import com.inexum.MicroAc.Wallet.Wallet;
import com.inexum.MicroAc.Wallet.CAMAProxy;

import java.io.*;
import java.net.Socket;
import javax.crypto.SealedObject;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class TransactionDispatcher extends java.lang.Thread
{
    private Wallet              m_wallet;
    private TransactionQueue    m_queue;
    private CAMAProxy           m_camaProxy;
    
//----------------------------------------------------------------------------//

    /** Creates new TransactionDispatcher */
    public TransactionDispatcher()
    {
        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        m_wallet = (Wallet)registry.getServiceForName("Wallet");
        m_queue = (TransactionQueue)registry.
            getServiceForName("TransactionQueue");
        m_camaProxy = (CAMAProxy)registry.getServiceForName("CAMAProxy");
    }
    
//----------------------------------------------------------------------------//
    
    private void handleNextTransaction()
        throws java.util.EmptyStackException
    {
        TransactionQueue.QueueEntry entry = m_queue.next();
        TransactionOffer offer = entry.getOffer();
        ITransactionCompletionListener observer = entry.getObserver();
        //Socket socket = entry.getSocket();
        Money consumerCost = m_camaProxy.convertMerchantPrice(offer.getCost(),
            offer.getExchangeRate());
        TransactionDescription description = //m_queue.getNextDescription();
            new TransactionDescription(offer, consumerCost);
        
        boolean succeeded = false;
        SignedVoucher signedVoucher = null;
        try
        {
            ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
            CAMAProxy proxy = (CAMAProxy)registry.
                getServiceForName("CAMAProxy");
            SealedObject sealedVoucher = (SealedObject)entry.getObject();
            signedVoucher = proxy.purchase(description, sealedVoucher);
            succeeded = true;
        }
        catch (UnknownMerchantException e)
        {
            e.printStackTrace();
        }
        catch (InvalidVoucherException e)
        {
            e.printStackTrace();
        }
        catch (RestrictedProductException e)
        {
            e.printStackTrace();
        }
        catch (InsufficientFundsException e)
        {
            e.printStackTrace();
        }
        catch (AccountUnavailableException e)
        {
            e.printStackTrace();
        }
        catch (InternalErrorException e)
        {
            e.printStackTrace();
        }
        finally
        {
            observer.transactionCompleted(succeeded, offer, signedVoucher);
        }
    }
    
//----------------------------------------------------------------------------//

    public void run()
    {
        boolean running = true;
        while (running)
        {
            try
            {
                handleNextTransaction();
            }
            catch (java.util.EmptyStackException e)
            {
                try
                {
                    sleep(500L);
                }
                catch (java.lang.InterruptedException e2)
                {
                    e2.printStackTrace();
                    running = false;
                }
            }
        }
    }
    
}

