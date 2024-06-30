/*
 * Server.java
 *
 * Created on June 13, 2001, 11:11 AM
 */

package com.inexum.IPC;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.URL;

/** A Server.
 *
 * @author Nebojsa Vojinovic - Voja
 * @version 2.0.2
 */
public class Server extends Thread
{
    /** Default constructor. */    
    public Server()
    {
    }
    
    /** Creates a new instance of a Server .
     *
     * @param url - a server URL.
     * @throws IOException if is unable to open a connection.
     */    
    public Server( int port ) throws IOException
    {
        m_port = port;
        onStart();
    }
    
    /** Open a connection.
     *
     * @param port - a server's port.
     * @throws IOException if is unable to open a connection.
     */    
    public void open( int port ) throws IOException
    {
        m_port = port;
        onStart();
    }
    
    /** Server run. */    
    public void run()
    {
        try
        {
            while ( !m_halt )
            {
                try
                {
                    Socket socket = m_serverSocket.accept();
                    Session session = new Session(socket);
                    session.start();
                }
                catch (java.io.IOException e)
                {
                    e.printStackTrace();
                }
            }
            onHalt();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    /** Stop the server. */    
    public void halt () { m_halt = true; }

    /** Return server's port.
     *
     * @return port number if available, otherwise -1.
     */    
    public int port () { return( m_port ); }

    /** Create a server socket. Override for specific socket.
     *
     * @param port - a server's port.
     * @throws IOException if is unable to create a server socket.
     */    
    protected ServerSocket createServerSocket( int port) throws IOException
    {
        return( new ServerSocket( port ) );
    }

    /** On start event creates a server socket if port is specified.
     *
     * @throws IOException if is unable to create a socket.
     */    
    protected void onStart() throws IOException
    {
        if( m_port == -1 ) throw new IOException();
        m_serverSocket = createServerSocket( m_port );
        m_halt = false;
    }

    /** On halt event closes a server socket.
     * 
     * @throws IOException if is unable to close a connection.
     */    
    protected void onHalt() throws IOException
    {
        m_serverSocket.close();
    }

    /* a server's part */
    private int m_port = -1;
    
    /* a halt flag */
    private boolean m_halt = false;

    /* a server socket */
    private ServerSocket m_serverSocket = null;
}
