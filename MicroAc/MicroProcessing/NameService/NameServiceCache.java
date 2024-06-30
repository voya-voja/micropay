/*
 * NameServiceCache.java
 *
 * Created on June 12, 2001, 12:06 PM
 */

package com.inexum.MicroAc.MicroProcessing.NameService;

import com.inexum.MicroAc.Exceptions.UnknownProcessorException;
import com.inexum.MicroAc.MicroProcessing.Transaction.*;
import com.inexum.util.ConfigurationManager;

import java.rmi.RemoteException;

import java.security.PublicKey;
import java.security.SignedObject;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class NameServiceCache extends Object
{
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();

    private PublicKey               m_nameServiceKey;
    private ISecureNamingService    m_namingService;
    private java.util.Map           m_camaMap;
    private java.util.Map           m_mamaMap;

    /** Creates new NameServiceCache */
    public NameServiceCache()
    {
        m_camaMap = new java.util.HashMap();
        m_mamaMap = new java.util.HashMap();
    }
    
    public void init (PublicKey nameServiceKey)
        throws java.rmi.RemoteException, java.security.SignatureException,
        java.io.IOException
    {
        m_nameServiceKey = nameServiceKey;
        locateNameService();
    }
    
    public void registerCAMA(final String name,
        java.security.SignedObject factoryStub)
        throws RemoteException, UnknownProcessorException,
        java.security.SignatureException, java.io.IOException
    {
        m_namingService.registerCAMA(name, factoryStub);
    }
    
    public void registerMAMA(final String name,
        java.security.SignedObject factoryStub)
        throws RemoteException, UnknownProcessorException,
        java.security.SignatureException, java.io.IOException
    {
        m_namingService.registerMAMA(name, factoryStub);
    }
    
    public ISessionFactory lookupCAMA(final String name)
        throws UnknownProcessorException
    {
        // Check local cache first
        if (m_camaMap.containsKey(name)) 
        {
            return (ISessionFactory)m_camaMap.get(name);
        }
        
        try
        { 
            return m_namingService.lookupCAMA(name);
        }
        catch (java.rmi.RemoteException e)
        {
            throw new UnknownProcessorException(e.getLocalizedMessage());
        }
    }
    
    public ISessionFactory lookupMAMA(final String name)
        throws UnknownProcessorException
    {
        // Check local cache first
        if (m_mamaMap.containsKey(name))
        {
            return (ISessionFactory)m_mamaMap.get(name);
        }
        
        try
        { 
            return m_namingService.lookupMAMA(name);
        }
        catch (java.rmi.RemoteException e)
        {
            throw new UnknownProcessorException(e.getLocalizedMessage());
        }
    }
    
    private void locateNameService()
        throws java.rmi.RemoteException, java.io.IOException,
        java.security.SignatureException
    {
        try
        {
            java.rmi.registry.Registry registry
                = java.rmi.registry.LocateRegistry.getRegistry(
                c_rsrcs.getString("net.Server"));
            INamingServiceFactory factory
                = (INamingServiceFactory)registry.lookup(
                c_rsrcs.getString("net.Service"));
            SignedObject signedService = factory.getService();
            
            boolean verified = signedService.verify(m_nameServiceKey,
                java.security.Signature.getInstance(
                c_rsrcs.getString("key.Algorithm")));
            if (verified)
            {
                m_namingService
                    = (ISecureNamingService)signedService.getObject();
            }
            else
            {
                throw new java.security.SignatureException();
            }
        }
        catch (java.security.InvalidKeyException e)
        {
            e.printStackTrace();
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (java.rmi.ConnectException e)
        {
            e.printStackTrace();
            throw new java.rmi.RemoteException(e.getLocalizedMessage());
        }
        catch (java.rmi.NotBoundException e)
        {
            e.printStackTrace();
            throw new java.rmi.RemoteException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
    }
    
    static void main(String[] args)
    {
        
        try
        {
            NameServiceCache cache = new NameServiceCache();
            java.security.PublicKey key = java.security.KeyPairGenerator.
                getInstance("DSA").generateKeyPair().getPublic();
            cache.init(key);
        }
        catch (java.rmi.RemoteException e)
        {
            e.printStackTrace();
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (java.security.SignatureException e)
        {
            e.printStackTrace();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
    }

}

