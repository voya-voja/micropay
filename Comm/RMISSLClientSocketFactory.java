/*
 * SSLClientSocketFactory.java
 *
 * Created on May 14, 2001, 3:48 PM
 */

package com.inexum.Comm;

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
 * @author  Nick Kapov
 * @version 
 */
public class RMISSLClientSocketFactory implements 
    java.rmi.server.RMIClientSocketFactory, java.io.Serializable 
{
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

    /** Creates new RMISSLClientSocketFactory */
    public RMISSLClientSocketFactory()
    {
        super();
        seed();
    }

    /** Create a client socket connected to the specified host and port.
     * @param host - the host name
     * @param port - the port number
     * @return a socket connected to the specified host and port.
     * @exception IOException if an I/O error occurs during socket creation.
     */
    public java.net.Socket createSocket(java.lang.String host, int port)
        throws java.io.IOException 
    {
        try
        {
            javax.net.ssl.SSLContext ctx = javax.net.ssl.SSLContext.getInstance("TLS");
            ctx.init(null, null, m_SecureRandom);
            
            SSLSocketFactory factory = ctx.getSocketFactory();
//                (SSLSocketFactory)SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket)
                factory.createSocket(host, port);
            return socket;
        }
        catch (java.security.KeyManagementException e)
        {
            e.printStackTrace();
            throw new IOException(e.getLocalizedMessage());
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new IOException(e.getLocalizedMessage());
        }
        catch (java.net.UnknownHostException e)
        {
            e.printStackTrace();
            throw new IOException(e.getLocalizedMessage());
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
