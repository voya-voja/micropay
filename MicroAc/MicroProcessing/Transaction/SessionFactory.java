/*
 * SessionFactory.java
 *
 * Created on June 9, 2001, 3:22 PM
 */

package com.inexum.MicroAc.MicroProcessing.Transaction;

import com.inexum.MicroAc.MicroProcessing.Merchant.ProfileManager.*;
import com.inexum.MicroAc.MicroProcessing.Merchant.Transaction.ProcessingSession;
import com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager.*;
import com.inexum.MicroAc.MicroProcessing.Logging.*;
import com.inexum.MicroAc.MicroProcessing.NameService.NameServiceCache;
import com.inexum.MicroAc.PFL.*;
import com.inexum.MicroAc.Exceptions.*;
import com.inexum.Encoding.Base64;
import com.inexum.MicroAc.DomainTypes.Profile;
import com.inexum.util.ConfigurationManager;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;


/** Unicast remote object implementing ISessionFactory interface.
 *
 * @author  rgoold
 * @version 
 */
public class SessionFactory extends UnicastRemoteObject
    implements ISessionFactory, LogSource
{
    private NameServiceCache        m_nsCache;
    private IMerchantProfileFactory m_merchantFactory;
    private IConsumerProfileManager m_consumerManager;
    private java.util.List          m_listeners;
    
    public ISession newSession(final String sessionID, int txID)
        throws RemoteException
    {
        try
        {
            ProcessingSession processingSession
                = new ProcessingSession(m_merchantFactory);
            
            return processingSession;
        }
        catch (java.rmi.RemoteException e)
        {
            e.printStackTrace ();
            throw e;
        }
    }
    
    public ISession newSession(Profile profile)
        throws RemoteException, IllegalArgumentException
    {
        // Make sure this is a consumer profile
        if (!ConsumerProfile.class.isAssignableFrom(profile.getClass()))
        {
            throw new IllegalArgumentException();
        }
        
        byte[] sessionBytes = new byte[8];
        new java.util.Random(new java.util.Date().getTime()).
            nextBytes(sessionBytes);
        String sessionID = null;
        try
        {
            sessionID = new String(Base64.Encode(sessionBytes), "ISO-8859-1");
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            e.printStackTrace();
            sessionID = new String(Base64.Encode(sessionBytes));
        }
        
        try
        { 
            PurchasingSession purchasingSession
                = new PurchasingSession(sessionID, m_nsCache,
                (ConsumerProfile)profile);
            
            return purchasingSession;
        }
        catch (java.rmi.RemoteException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /** Constructs SessionFactoryImpl object and exports it on default port.
     */
    public SessionFactory()
        throws RemoteException
    {
        super();
        m_listeners = new java.util.LinkedList();
        addListener(LogManager.GetManager());
    }

    /** Constructs SessionFactoryImpl object and exports it on specified port.
     * @param port The port for exporting
     */
    public SessionFactory(int port)
        throws RemoteException
    {
        super(port);
        m_listeners = new java.util.LinkedList();
        addListener(LogManager.GetManager());
    }
    
    public void init(NameServiceCache nsCache,
        IConsumerProfileManager consumerManager,
        IMerchantProfileFactory merchantFactory)
    {
        m_nsCache = nsCache;
        m_consumerManager = consumerManager;
        m_merchantFactory = merchantFactory;
    }
    
    private void fireEvent(LogEvent event)
    {
        java.util.Iterator iterator = m_listeners.iterator();
        while (iterator.hasNext())
        {
            LogListener listener = (LogListener)iterator.next();
            listener.logMessage(event);
        }
    }
    
//    private void fireStartEvent(Object source, TxInfo txInfo)
//    {
//        TransactionLogEvent startEvent
//            = new TransactionLogEvent(source, TransactionLogEvent.c_StartSession);
//        startEvent.setSessionID(txInfo.getSessionID());
//        startEvent.setTransactionID(txInfo.getTxID());
//        fireEvent(startEvent);
//    }
    
    public void addListener(LogListener listener)
    {
        m_listeners.add(listener);
    }
    public void removeListener(LogListener listener)
    {
        m_listeners.remove(listener);
    }
    
    public static void debugMamaInit()
    {
        try
        {
            new LogManager();
            NameServiceCache nsCache = new NameServiceCache();
            SessionFactory sessionFactory = new SessionFactory();
            MerchantProfileFactory profileFactory
                = new MerchantProfileFactory();
            ConsumerProfileManager profileManager
                = new ConsumerProfileManager(sessionFactory);
            sessionFactory.init(nsCache, profileManager, profileFactory);
            // Create the profile if it doesn't exist already.
            MerchantProfile profile = profileFactory.getProfile("merchant1");
            if (profile.getMerchantID() == null)
            {
                // Debug code doesn't actually throw an exception yet
                profile.setBalance(100);
                profile.setMerchantID("merchant1");
                byte[] keyData = new byte[] {0, 0, 0, 0, 0, 0, 0, 0};
                profile.setKey(new javax.crypto.spec.SecretKeySpec(
                    keyData, "Blowfish"));
            }
            
            ConfigurationManager rsrcs = ConfigurationManager.Instance();
            Registry registry = LocateRegistry.getRegistry();
		String netRegistry = "//" + rsrcs.getString("net.Server") + "/" +
									rsrcs.getString("net.Service");
            registry.rebind( netRegistry, sessionFactory );
        }
        catch (java.rmi.AccessException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.rmi.RemoteException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (UnknownMerchantException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** Main method.
     */
    public static void main(String[] args)
    {
        SessionFactory.debugMamaInit();
       /* nmv 20011212 
	for (int i = 0; i < 10; i++)
            new TestBuyerThread().start();
	*/
    }
    
}

