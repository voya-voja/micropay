/*
 * SocketListener.java
 *
 * Created on November 21, 2001, 3:03 PM
 */

package com.inexum.Comm;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public abstract class SocketListener extends java.lang.Thread
{

    boolean                 m_running;
    private ServerSocket    m_serverSocket;
    
//----------------------------------------------------------------------------//
    
    protected abstract SocketHandler createSocketHandler(Socket socket)
        throws java.io.IOException;
    
//----------------------------------------------------------------------------//
    
    /** Creates new SocketListener */
    public SocketListener(int port)
        throws java.io.IOException
    {
        m_serverSocket = new ServerSocket(port);
    }
    
//----------------------------------------------------------------------------//
    
    public void terminate()
    {
        m_running = false;
        try
        {
            m_serverSocket.close();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
    }
    
//----------------------------------------------------------------------------//

    public void run()
    {
        m_running = true;
        while (m_running)
        {
            try
            {
                Socket socket = m_serverSocket.accept();
                createSocketHandler(socket).start();
            }
            catch (java.io.IOException e) {}
        }
    }
    
}

