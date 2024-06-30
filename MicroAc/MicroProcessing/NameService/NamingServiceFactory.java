/*
 * NamingServiceFactoryImpl.java
 *
 * Created on June 13, 2001, 4:40 PM
 */

package com.inexum.MicroAc.MicroProcessing.NameService;

import com.inexum.util.ConfigurationManager;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;
import java.security.*;

/** Unicast remote object implementing NamingServiceFactory interface.
 *
 * @author  rgoold
 * @version 1.0
 */
public class NamingServiceFactory extends UnicastRemoteObject
    implements INamingServiceFactory
{
    private ISecureNamingService    m_singletonNamingService;
    private SignedObject            m_signedNamingService;
    
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();
    
    public SignedObject getService()
        throws RemoteException
    {
        return m_signedNamingService;
    }
    
    private void setService(ISecureNamingService namingService,
        java.security.PrivateKey signingKey)
        throws java.security.SignatureException
    {
        m_singletonNamingService = namingService;
        try
        {
            java.security.Signature signingEngine
                = java.security.Signature.getInstance(
                c_Rsrcs.getString("key.Algorithm"));
            m_signedNamingService = new SignedObject(namingService,
                signingKey, signingEngine);
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (java.security.InvalidKeyException e)
        {
            e.printStackTrace();
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
    }

    /** Constructs NamingServiceFactory object and exports it on default port.
     */
    public NamingServiceFactory(ISecureNamingService namingService,
        java.security.PrivateKey signingKey)
        throws RemoteException, java.security.SignatureException
    {
        super();
        setService(namingService, signingKey);
    }

    /** Constructs NamingServiceFactory object and exports it on specified port.
     * @param port The port for exporting
     */
    public NamingServiceFactory(ISecureNamingService namingService,
        java.security.PrivateKey signingKey, int port)
        throws RemoteException, java.security.SignatureException
    {
        super(port);
        setService(namingService, signingKey);
    }
    
    private static char[] getPass (String passName)
        throws java.io.IOException
    {
        try
        {
            String pass = c_Rsrcs.getString( passName + ".password" );
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
    
    private static PrivateKey loadPrivateKey (KeyStore keystore)
        throws java.security.KeyStoreException
    {
        try
        {
            char[] keyPass = getPass("net");
            return (PrivateKey)keystore.getKey(
                c_Rsrcs.getString("net.Name"), keyPass);
        }
        catch (java.security.UnrecoverableKeyException e)
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
    
    private static java.util.Map loadCamaKeys (KeyStore keystore)
        throws java.security.KeyStoreException
    {
        java.util.Map camaKeys = new java.util.HashMap();
        java.util.Enumeration aliases = keystore.aliases();
        while (aliases.hasMoreElements())
        {
            String alias = aliases.nextElement().toString();
// nmv      if (alias.startsWith("cama") && keystore.isCertificateEntry(alias))
            if (alias.startsWith("cama") )
            {
                camaKeys.put(alias.substring(4),    // Remove "Cama" prefix
                    keystore.getCertificate(alias).getPublicKey());
            }
        }
        return camaKeys;
    }
    
    private static java.util.Map loadMamaKeys (KeyStore keystore)
        throws java.security.KeyStoreException
    {
        java.util.Map mamaKeys = new java.util.HashMap();
        java.util.Enumeration aliases = keystore.aliases();
        while (aliases.hasMoreElements())
        {
            String alias = aliases.nextElement().toString();
// nmv            if (alias.startsWith("mama") && keystore.isCertificateEntry(alias))
           if (alias.startsWith("mama") )
           {
               mamaKeys.put(alias.substring(4),    // Remove "Mama" prefix
               keystore.getCertificate(alias).getPublicKey());
            }
        }
        return mamaKeys;
    }

    /** Main method.
     */
    public static void main(String[] args)
    {
        System.setSecurityManager(new RMISecurityManager());

        java.security.PrivateKey signingKey = null;
        try
        {
            
            // Get keystore password
            char[] storePass = getPass("key.store");
            
            // Load the keystore
            java.io.FileInputStream storeStream = new java.io.FileInputStream(
                c_Rsrcs.getURL("key.store.Path").getFile() );
            KeyStore keystore = KeyStore.getInstance(
                c_Rsrcs.getString("key.store.Type"));
            keystore.load(storeStream, storePass);
            signingKey = loadPrivateKey(keystore);
            java.util.Map camaKeys = loadCamaKeys(keystore);
            java.util.Map mamaKeys = loadMamaKeys(keystore);
            
            SecureNamingService namingService = new SecureNamingService(
                camaKeys, mamaKeys);
            
            // Get a serializable stub for the naming service
            ISecureNamingService serviceStub
                = (ISecureNamingService)
                UnicastRemoteObject.toStub(namingService);
            
            // Initialise the naming service factory
            NamingServiceFactory obj
                = new NamingServiceFactory (serviceStub, signingKey);
            System.out.println("The NamingService is up");
            try
            {
                Naming.rebind(c_Rsrcs.getString("net.Service"), obj);
            }
            catch (java.rmi.RemoteException e)
            {
                System.out.println(c_Rsrcs.getString("RegistryBindError") + ": "
                    + e.getLocalizedMessage());
                throw e;
            }
            while (true)
            {
                try
                {
                    Thread.currentThread().sleep(1000L);
                }
                catch (java.lang.InterruptedException e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
        catch (java.security.cert.CertificateException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.security.KeyStoreException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.security.SignatureException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.rmi.NoSuchObjectException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.rmi.RemoteException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.net.MalformedURLException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

