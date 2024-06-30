/*
 * SocketHandler.java
 *
 * Created on November 23, 2001, 3:03 PM
 */

package com.inexum.Comm;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public abstract class SocketHandler extends java.lang.Thread
{
    private java.net.Socket         m_socket;

//----------------------------------------------------------------------------//

    /** Creates new SocketHandler */
    public SocketHandler(java.net.Socket socket)
    {
        m_socket = socket;
    }

//----------------------------------------------------------------------------//

    protected java.io.InputStream getInputStream()
        throws java.io.IOException
    {
        return m_socket.getInputStream();
    }

//----------------------------------------------------------------------------//

    protected java.io.OutputStream getOutputStream()
        throws java.io.IOException
    {
        return m_socket.getOutputStream();
    }

//----------------------------------------------------------------------------//

    protected void close()
    {
        try
        {
            m_socket.close();
        }
        catch (java.io.IOException e)
        {
            // Ignore. Closed is closed.
        }
    }

}

