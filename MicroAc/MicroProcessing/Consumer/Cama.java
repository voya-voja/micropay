/*
 * Cama.java
 *
 * Created on June 29, 2001, 12:38 PM
 */

package com.inexum.MicroAc.MicroProcessing.Consumer;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.MicroProcessing.NameService.*;
import com.inexum.MicroAc.MicroProcessing.Transaction.*;
import com.inexum.MicroAc.MicroProcessing.Crypto.FactorySigner;
import com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager.*;
import com.inexum.MicroAc.MicroProcessing.Consumer.Authentication.SocketSessionFactory;
import com.inexum.util.ConfigurationManager;

import java.rmi.Naming;

/**
 *
 * @author  rgoold
 * @version 
 */
public class Cama extends Object
{
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
    
    private final int c_walletPort = Integer.parseInt(
        c_rsrcs.getString("issuer.WalletPort"), 10);
    
    private NameServiceCache        m_nameServiceCache;
    private ConsumerProfileManager  m_profileManager;
    private SessionFactory          m_sessionFactory;
    private SocketSessionFactory    m_socketSessionFactory;
    
    private java.security.KeyStore getKeyStore (char[] storePass)
        throws java.security.KeyStoreException
    {
        try
        {
            java.io.FileInputStream storeIn = 
                new java.io.FileInputStream(c_rsrcs.getURL("key.store.Path").getFile());
            java.security.KeyStore keystore = java.security.KeyStore.
                getInstance(c_rsrcs.getString("key.store.Type"));
            keystore.load(storeIn, storePass);
            return keystore;
        }
        catch (java.security.cert.CertificateException e)
        {
            throw new java.security.KeyStoreException(e.getLocalizedMessage());
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            throw new java.security.KeyStoreException(e.getLocalizedMessage());
        }
        catch (java.io.FileNotFoundException e)
        {
            throw new java.security.KeyStoreException(e.getLocalizedMessage());
        }
        catch (java.io.IOException e)
        {
            throw new java.security.KeyStoreException(e.getLocalizedMessage());
        }
    }
    
    private char[] getPass (String passName)
        throws java.io.IOException
    {
        try
        {
            String pass = c_rsrcs.getString( passName + ".password" );
            return pass.toCharArray();
        }
        catch (java.util.MissingResourceException e)
        {
            java.io.InputStreamReader inReader
                = new java.io.InputStreamReader(System.in);
            java.io.BufferedReader bufferedReader
                = new java.io.BufferedReader(inReader);
            System.out.println("Please enter the " + passName + " password:");
            return bufferedReader.readLine().toCharArray();
        }
    }

    /** Creates new Cama */
    public Cama()
    {
        /*
        com.inexum.MicroAc.MicroProcessing.RMISocFac.
            RMISSLServerSocketFactory.seed();
         */
        try
        {
            // Load the keystore
            java.security.KeyStore keystore
                = getKeyStore(getPass("key.store"));
            
            // Get CAMA private key
            char[] keyPass = getPass("issuer");

            java.security.PrivateKey camaKey
                = (java.security.PrivateKey) keystore.getKey("cama"+c_rsrcs.getString("issuer.Server"), keyPass);
            try
            {
                m_sessionFactory = new SessionFactory();
            }
            finally
            {
            }
            try
            {
                m_profileManager = new ConsumerProfileManager(m_sessionFactory);
            }
            finally
            {
            }
            try
            {
                m_nameServiceCache = new NameServiceCache();
            }
            finally
            {
            }
            try
            {
                m_sessionFactory.init(m_nameServiceCache, m_profileManager, null);
            }
            finally
            {
            }
            try
            {
                Naming.rebind("/ConsumerProfileManager", m_profileManager);
            }
            finally
            {
            }
            
            // Create a simple socket-based wallet listener (started later)
            m_socketSessionFactory = new SocketSessionFactory(c_walletPort);
            
            // Sign the factory stub
            java.rmi.server.RemoteStub factoryStub =
                (java.rmi.server.RemoteStub)
                java.rmi.server.UnicastRemoteObject.toStub(m_sessionFactory);
            FactorySigner signer = new FactorySigner(camaKey);
            java.security.SignedObject signedFactoryStub = null;
            try
            {
                signedFactoryStub = signer.signFactory(factoryStub);
            }
            finally
            {
            }
            
            // Submit it
            try
            {
                // Get name service key
                java.security.PublicKey nsKey = keystore.getCertificate(
                    c_rsrcs.getString("net.Name")).getPublicKey();
                
                // Submit signed factory stub
                try
                {
                    m_nameServiceCache.init(nsKey);
                }
                finally
                {
                }
                try
                {
                    m_nameServiceCache.registerCAMA(c_rsrcs.getString("issuer.Server"),
                        signedFactoryStub);
                }
                finally
                {
                }
            }
            catch (UnknownProcessorException e)
            {
                // Secure naming service does not recognise us
                System.out.println(c_rsrcs.getString("OperatingWithoutNS"));
                e.printStackTrace();
            }
            catch (Exception e)
            {
                System.out.println(c_rsrcs.getString("OperatingWithoutNS"));
                e.printStackTrace();
            }
            
            // Start the SocketSessionFactory listening for wallets
            m_socketSessionFactory.start();
        }
        catch (java.rmi.NoSuchObjectException e)
        {
            // Couldn't get session factory stub
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.rmi.RemoteException e)
        {
            // Couldn't contact naming service or couldn't create
            // one of our remote components
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.security.UnrecoverableKeyException e)
        {
            // Couldn't get our private key
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.security.KeyStoreException e)
        {
            // Problems reading keystore
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            // Couldn't get our private key
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.security.SignatureException e)
        {
            // Malfunctioning/hacked naming service
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.io.IOException e)
        {
            // Malfunctioning/hacked naming service
            e.printStackTrace();
            System.exit(1);
        }
        
    }
    
    public static void main(String[] args)
    {
        System.setSecurityManager(new java.rmi.RMISecurityManager());
        java.security.Security.insertProviderAt(
            new cryptix.jce.provider.CryptixCrypto(), 1);
        java.security.Security.insertProviderAt(
            new cryptix.jce.provider.CryptixRandom(), 2);
        java.security.Security.insertProviderAt(
            new com.sun.net.ssl.internal.ssl.Provider(), 3);
        
        Cama cama = new Cama();
        System.out.println("microAc CAMA started.");
    }

}

