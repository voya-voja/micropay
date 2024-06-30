/*
 * Session.java
 *
 * Created on June 13, 2001, 11:11 AM
 */

package com.inexum.IPC;

import com.inexum.Exceptions.SessionException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.URL;

/** A Session.
 *
 * @author Nebojsa Vojinovic - Voja
 * @version 2.0.2
 */
public class Session extends Thread
{
    /** Default constructor. */    
    public Session()
    {
    }
    
    /** Creates a new instance of a Session .
     *
     * @param url - a Session URL.
     */    
    public Session( Socket socket )
    {
        m_socket = socket;
    }
    
    /** Open a connection.
     *
     * @param port - a Session's port.
     * @throws IOException if is unable to open a connection.
     */    
    public void start( Socket socket )
    {
        m_socket = socket;
        start();
    }
    
    /** Session run. Wait for a function or an operation. 
     * If receive a function or an operation execute it.
     */    
    public void run()
    {
        try
        {
            onStart();
            while ( !m_halt )
            {
                try
                {
                    Object object = m_inStream.readObject();
                    if( isFunction( object ) )
                        execute( (IFunction)object );
                    else if( isOperation( object ) )
                        execute( (IOperation)object );
                    else
                        throw new SessionException();
                }
                catch ( IOException e )
                {
                   // e.printStackTrace();
                    halt();
                }
                catch( SessionException e )
                {
                    e.printStackTrace();
                }
                catch( ClassNotFoundException e )
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

    /** Stop the Session. */    
    public void halt () 
    { 
        m_halt = true; 
    }
    
    /** Verify does object support function interface.
     *
     * param object - an object to be tested
     * @return true if the object is a function, otherwise false.
     */    
    public boolean isFunction( Object object ) throws ClassNotFoundException
    {
        if( g_FunctionInterface == null )
        {
            FunctionExample example = new FunctionExample();
            Class[] interfaces = example.getClass().getInterfaces();
            g_FunctionInterface = interfaces[0];
        }
        Class[] interfaces = object.getClass().getInterfaces();
        if( interfaces.length == 0 )
        {
            //check for superclass for interface
            interfaces = object.getClass().getSuperclass().getInterfaces();
        }
        return ( interfaces[0] == g_FunctionInterface ); 

    }
           
    /** Verify does object support operation interface.
     *
     * param object - an object to be tested
     * @return true if the object is a operation, otherwise false.
     */    
    public boolean isOperation( Object object )
    {
        if( g_OperationInterface == null )
        {
            OperationExample example = new OperationExample();
            Class[] interfaces = example.getClass().getInterfaces();
            g_OperationInterface = interfaces[0];
        }
        Class[] interfaces = object.getClass().getInterfaces();
        if( interfaces.length == 0 )
        {
            //check for superclass for interface
            interfaces = object.getClass().getSuperclass().getInterfaces();
        }
        
        return ( interfaces[0] == g_OperationInterface ); 
    
    }

    /** On start event creates in/out streams.
     *
     * @throws IOException if is unable to create a socket.
     */    
    protected void onStart() throws IOException
    {
        if( m_socket == null ) throw new IOException();
        m_outStream = new ObjectOutputStream(m_socket.getOutputStream());
        m_inStream = new ObjectInputStream(m_socket.getInputStream());
        m_halt = false;
    }

    /** On halt event closes a Session socket.
     * 
     * @throws IOException if is unable to close a connection.
     */    
    protected void onHalt() throws IOException
    {
        m_socket.close();
    }

    /** Execute a remote call of a function. If function successfully has been 
     * completed return back a result object. If exception has been thrown return back the
     * exception object.
     *
     * @param function - to be executed.
     * @throws IOException if it is unable to execute the remoute 
     * call of the function.
     */    
    protected void execute( IFunction function ) throws IOException
    {
        try
        {
            Object object = function.run();
            m_outStream.writeObject( object );
        }
        catch( Exception e )
        {
            m_outStream.writeObject( e );
        }
        m_outStream.reset();
    }
    
    /** Execute a remote call of an operation. If operation successfully has 
     * been completed return back an "OK" string. If exception has been thrown
     * return back the exception object.
     *
     * @param operation - to be executed.
     * @throws IOException if is unable to execute the remoute call 
     * of an operation.
     */    
    protected void execute( IOperation operation ) throws IOException
    {
        try
        {
            operation.run();
            m_outStream.writeObject( new String( "OK" ) );
        }
        catch( Exception e )
        {
            m_outStream.writeObject( e );
        }
    }

    /* a halt flag */
    private boolean m_halt = false;
    
    /* a Session socket */
    private Socket m_socket = null;

    /* a server's write stream */
    ObjectOutputStream m_outStream = null;

    /* a server's read stream */
    ObjectInputStream m_inStream = null;
    
    private static class FunctionExample implements IFunction
    {
        
        /** Define specific functionality.  */
        public Object run() throws Exception { return( null ); }
        
        /** Define specific exception class.  */
        public Class exceptionClass() { return( null ); }
        
        /** Throw the specified exception class.  */
        public void throwException() throws Exception {}
        
    }
    
    private static class OperationExample implements IOperation
    {
        
        /** Define specific functionality.  */
        public void run() throws Exception {}
        
        /** Define specific exception class.  */
        public Class exceptionClass() { return( null ); }
        
        /** Throw the specified exception class.  */
        public void throwException() throws Exception {}
        
    }
    
    private static Class g_FunctionInterface = null;
    private static Class g_OperationInterface = null;
}
