/*
 * ServiceUnavailableException.java
 *
 * Created on July 4, 2001, 2:49 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class ServiceUnavailableException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>ServiceUnavailableException</code> without detail message.
     */
    public ServiceUnavailableException()
    {
    }


    /**
     * Constructs an <code>ServiceUnavailableException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ServiceUnavailableException(String msg)
    {
        super(msg);
    }
}



