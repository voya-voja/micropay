/*
 * Client.java
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
import java.net.URL;

//import com.inexum.util.Debug;

/** A client.
 *
 * @author Nebojsa Vojinovic - Voja
 * @version 2.0.2
 */
public class Client
{
    /** Default constructor. */    
    public Client()
    {
    }
    
    /** Creates a new instance of a client .
     *
     * @param url - a server URL.
     */    
    public Client( String host, int port )
    {
        m_host = host;
        m_port = port;
    }
    
    /** Establish a connection with a server.
     *
     * @throws IOException if is unable to open a connection.
     */    
    public void connect() throws IOException
    {
        if( m_host == null || m_port == -1 ) 
            throw new IOException();
        
        open( false );
    }
    
    /** Establish a connection with a server.
     *
     * @param url - a server URL.
     * @throws IOException if is unable to open a connection.
     */    
    public void connect( String host, int port ) throws IOException
    {
       if( host == null || port == -1 ) 
            throw new IOException();
         
        m_host = host;
        m_port = port;
        open( false );
    }
    
    /** Close a connection.
     * 
     * @throws IOException if is unable to close a connection.
     */    
    public void disconnect() throws IOException
    {
        m_socket.close();
        m_reopen = true;
    }

    /** Execute a remote function.
     *
     * @param function - to be executed on a server.
     * @return object result.
     * @throws IOException if it is unable to execute the remoute function.
     * @throws ClassNotFoundException if it is unable to return a result.
     * @throws Exception a function's specific exception.
     */    
    public Object execute( IFunction function ) 
                        throws IOException, ClassNotFoundException, Exception
    {
//        if( com.inexum.util.Debug.on )
//            com.inexum.util.Debug.out(com.inexum.util.Debug.g_net, "The client is executing an IFunction on, Server: " + m_host + " and Port: " + m_port );
//        System.out.println( "Execute remote IFunction@" 
//                                                  + m_host + ":" + m_port + "..." );
            
        if( m_reopen ) 
            open( true );
        
//        if( com.inexum.util.Debug.on )
//            com.inexum.util.Debug.out(com.inexum.util.Debug.g_net, "The function is being written to the server" ); 
//        System.out.println( "Executing the function..." );  
        m_outStream.writeObject( function );
        Object returnObject = m_inStream.readObject();
        
        if( m_reopen ) 
            disconnect();
        else
            m_outStream.reset();
        
        if( (new Exception()).getClass().isAssignableFrom( returnObject.getClass() ) )
            throw (Exception)returnObject;
        
//        System.out.println( "The remote function executed OK!" );  
        return( returnObject );
    }
    
    /** Execute a remote operation.
     *
     * @param operation - to be executed on a server.
     * @throws IOException if is unable to execute the remoute operation.
     * @throws Exception an operation's specific exception.
     */    
    public void execute( IOperation operation ) throws IOException, Exception
    {
        if( m_reopen ) 
            open( true );
        
        m_outStream.writeObject( operation );
        Object returnObject = m_inStream.readObject();
        
        if( m_reopen ) disconnect();

        if( (new Exception()).getClass().isAssignableFrom( returnObject.getClass() ) )
            throw (Exception)returnObject;
    }
    
    /** Create a socket to be used for a remoute connection.
     *
     * @param url - a server URL.
     * @throws IOException if is unable to create a socket connection.
     */    
    protected Socket createSocket() throws IOException
    {
        return( new Socket( m_host, m_port ) );
    }

    /* Open socket.
     *
     * @param url - a server URL.
     * @param reopen - client state 
     * @throws IOException if is unable to open a connection.
     */    
    private void open( boolean reopen ) throws IOException
    {
        if( m_socket != null ) disconnect();
        m_socket = createSocket();
        OutputStream out = m_socket.getOutputStream();
        m_outStream = new ObjectOutputStream( out );
//        m_outStream.flush();

        InputStream in = m_socket.getInputStream();
        m_inStream  = new ObjectInputStream( in );
//        m_inStream .flush();
        
        m_reopen = reopen;
    }
    
    /* a url of server part */
    private String  m_host = null;
    private int     m_port = -1;
    
    /* a socket to a server */
    private Socket m_socket = null;
    
    /* a client's write stream */
    private ObjectOutputStream m_outStream = null;
    
    /* a client's write stream */
    private ObjectInputStream m_inStream  = null;
    
    /* a client state: 
     * true - internal responsibility to close a connection after execution 
     * false - external responsibility to close a connection
     */
    private boolean m_reopen = true;
}
