/*
 * InvalidLoginException.java
 *
 * Created on May 9, 2001, 11:31 AM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  Nick Kapov
 * @version 
 */
public class InvalidLoginException extends java.lang.Exception
    implements java.io.Serializable
{

    //The name of server object from which the exception was thrown
    private String m_Server = "";
    
    //The name of method from which the exception was thrown
    private String m_Method = "";
    
    /**
     * Creates new <code>InvalidLoginException</code> without detail message.
     */
    public InvalidLoginException() {
    }


    /**
     * Constructs an <code>InvalidLoginException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidLoginException(String msg) {
        super(msg);
    }
    
      /**
 * Constructs an <code>InvalidLoginException</code> with the specified detail message.
       *and functional trace infomration
       *@param server the name of the server object that threw the exception
       *@param method the method that the exception was thrown from
     * @param msg the detail message.
     */
    public InvalidLoginException(String server, String method, String msg) 
    {
        super(msg);
        m_Server = server;
        m_Method = method;
    }
    
    
    public String getServerName(){
        return m_Server;
    }
    
    public String getMethodName(){
        return m_Method;
    }
    
}


