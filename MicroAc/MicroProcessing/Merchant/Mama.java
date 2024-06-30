/*
 * Mama.java
 *
 * Created on July 4, 2001, 2:31 PM
 */

package com.inexum.MicroAc.MicroProcessing.Merchant;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.DomainTypes.*;
import com.inexum.MicroAc.MicroProcessing.Transaction.*;
import com.inexum.MicroAc.MicroProcessing.NameService.*;
import com.inexum.MicroAc.MicroProcessing.Crypto.FactorySigner;
import com.inexum.MicroAc.MicroProcessing.Merchant.ProfileManager.*;
import com.inexum.MicroAc.MicroProcessing.Merchant.Transaction.*;
import com.inexum.util.ConfigurationManager;

import java.rmi.*;

/**
 *
 * @author  rgoold
 * @version 
 */
public class Mama extends java.lang.Object
{
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
    
    private SessionFactory          m_sessionFactory;
    private IMerchantProfileFactory m_profileFactory;
    private NameServiceCache        m_nsCache;
    
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
            String pass = c_rsrcs.getString( passName + ".password");
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

    /** Creates new Mama */
    public Mama()
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
            
            // Get MAMA private key
            char[] keyPass = getPass("acquirer");
            java.security.PrivateKey mamaKey
                = (java.security.PrivateKey) keystore.getKey("Mama"
                + c_rsrcs.getString("acquirer.Server"), keyPass);
            
            try
            {
                m_nsCache = new NameServiceCache();
            }
            finally
            {
            }
            try
            {
                m_sessionFactory = new SessionFactory();
            }
            finally
            {
            }
            try
            {
                m_profileFactory = new MerchantProfileFactory();
            }
            finally
            {
            }
            try
            {
                m_sessionFactory.init(m_nsCache, null, m_profileFactory);
            }
            finally
            {
            }
            Naming.rebind("/MAMASessionFactory", m_sessionFactory);
            
            // Sign the factory stub
            FactorySigner signer = new FactorySigner(mamaKey);
            java.rmi.server.RemoteStub factoryStub =
                (java.rmi.server.RemoteStub)
                java.rmi.server.UnicastRemoteObject.toStub(m_sessionFactory);
            java.security.SignedObject signedFactoryStub
                = signer.signFactory(factoryStub);
            
            try
            {
                // Get name service key
                java.security.PublicKey nsKey = keystore.getCertificate(
                                                    c_rsrcs.getString("net.Name")).getPublicKey();
                // Submit signed factory stub
                m_nsCache.init(nsKey);
                m_nsCache.registerMAMA( c_rsrcs.getString("acquirer.Server"), signedFactoryStub );
             }
            catch (Exception e)
            {
                System.out.println("Proceeding without naming service.");
                e.printStackTrace();
            }
        }
        catch (java.rmi.NoSuchObjectException e)
        {
            // Couldn't get session factory stub
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.rmi.RemoteException e)
        {
            // Couldn't create one of our remote components
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
            // Couldn't sign our factory stub
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.net.MalformedURLException e)
        {
            // Failed to bind factory to registry
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.io.IOException e)
        {
            // Problems reading from standard input
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
        
        new Mama();
        System.out.println("microAc MAMA started.");
    }

}

