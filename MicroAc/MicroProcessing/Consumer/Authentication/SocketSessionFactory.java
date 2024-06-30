//
//  SocketSessionFactory.java
//  work
//
//  Created by rgoold on Thu Oct 25 2001.
//  Copyright (c) 2001 iNexum Systems Inc. All rights reserved.
//

package com.inexum.MicroAc.MicroProcessing.Consumer.Authentication;

import com.inexum.util.ConfigurationManager;

import java.security.*;
import java.io.*;
import javax.net.*;
import com.sun.net.ssl.*;

public class SocketSessionFactory extends java.lang.Thread
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();

    private int                     m_port;
    private boolean                 m_running;
    private AuthenticationServer    m_authServer;
    private SSLContext              m_context;
    
    public
    SocketSessionFactory (int port)
    {
        m_port = port;
    }

    private void initSSLContext()
        throws GeneralSecurityException, IOException
    {
        try
        {
            String keyFileName = c_Rsrcs.getURL("ssl.key.store.Path").getFile();
            char[] passphrase = 
                            c_Rsrcs.getString("ssl.key.store.Password").toCharArray();
            KeyStore ks = KeyStore.getInstance(c_Rsrcs.getString("key.store.Type"));
            ks.load(new FileInputStream(keyFileName), passphrase);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                                        c_Rsrcs.getString("key.manager.trust.factory"));
            tmf.init(ks);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(
                                        c_Rsrcs.getString("key.manager.factory"));
            kmf.init(ks, passphrase);
            m_context = SSLContext.getInstance(c_Rsrcs.getString("ssl.Context"));
            m_context.init(kmf.getKeyManagers(),
                tmf.getTrustManagers(), null);
        }
        catch (java.util.MissingResourceException e)
        {
            throw new java.io.IOException(e.getLocalizedMessage());
        }
    }
    
    public void
    run ()
    {
        try
        {
            initSSLContext();
            m_authServer = new AuthenticationServer();
            ServerSocketFactory factory
                = m_context.getServerSocketFactory();
            java.net.ServerSocket serverSocket
                = factory.createServerSocket(m_port);
            m_running = true;   // atomic
            while (m_running)
            {
                try
                {
                    java.net.Socket socket = serverSocket.accept();
                    new SessionWrapper(socket, m_authServer.login()).start();
                }
                catch (java.io.IOException e)
                {
                    e.printStackTrace();
                }
            }
            serverSocket.close();
        }
        catch (java.security.GeneralSecurityException e)
        {
            e.printStackTrace();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void halt ()
    {
        m_running = false;  // atomic
    }
}
