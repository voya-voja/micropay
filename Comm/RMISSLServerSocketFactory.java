/*
 * SSLServerSocketFactory.java
 *
 * Created on May 8, 2001, 11:21 AM
 */

package com.inexum.Comm;

import com.inexum.util.ConfigurationManager;

import java.io.*;
import java.net.*;
import java.rmi.server.*;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import com.sun.net.ssl.*;

/**
 *
 * @author Nick Kapov
 * @version
 */
public class RMISSLServerSocketFactory
    implements java.rmi.server.RMIServerSocketFactory, java.io.Serializable
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();
    private static java.security.SecureRandom   m_SecureRandom;
    
    public static void seed ()
    {
        if (m_SecureRandom == null)
        {
            byte[] seed = new byte[48];
            for (byte i = 0; i < seed.length; i++)
            {
                seed[i] = i;
            }
            m_SecureRandom = new java.security.SecureRandom(seed);
        }
    }
    
    public static void seed (byte[] seedBytes)
    {
        if (m_SecureRandom == null)
        {
            m_SecureRandom = new java.security.SecureRandom(seedBytes);
        }
    }
    
    /** Creates new SSLServerSocketFactory */
    public RMISSLServerSocketFactory()
    {
        if (m_SecureRandom == null)
        {
            seed();
        }
    }
    
    /**
     * Create a server socket on the specified port (port 0 indicates
     * an anonymous port).
     * @param  port the port number
     * @return the server socket on the specified port
     * @exception IOException if an I/O error occurs during server socket
     * creation
     */
    public java.net.ServerSocket createServerSocket(int port)
        throws java.io.IOException 
    {
        try {
                // set up key manager to do server authentication
                javax.net.ssl.KeyManagerFactory kmf;
                KeyStore ks; 
                char[] passphrase = 
                            c_Rsrcs.getString("ssl.key.store.Password").toCharArray();

                ks = KeyStore.getInstance(c_Rsrcs.getString("key.store.Type"));
                ks.load(
                    new java.io.FileInputStream(
                                    c_Rsrcs.getURL("ssl.key.store.Path").getFile()), 
                    passphrase);
                
                kmf = javax.net.ssl.KeyManagerFactory.getInstance(
                                        c_Rsrcs.getString("key.manager.factory"));
                kmf.init(ks, passphrase);
                
                javax.net.ssl.TrustManagerFactory tmf
                    = javax.net.ssl.TrustManagerFactory.getInstance(
                                        c_Rsrcs.getString("key.manager.trust.factory"));
                tmf.init(ks);
                
                javax.net.ssl.SSLContext ctx = javax.net.ssl.SSLContext.getInstance(c_Rsrcs.getString("ssl.Context"));
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(),
                    m_SecureRandom);

                SSLServerSocketFactory ssf = ctx.getServerSocketFactory();
                java.net.ServerSocket serverSocket
                    = ssf.createServerSocket(port);
                return serverSocket;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
    }
    
    
    public boolean equals(Object obj)
    {
        return super.equals(obj);
    }
    
    public int hashCode()
    {
        return super.hashCode();
    }
}

