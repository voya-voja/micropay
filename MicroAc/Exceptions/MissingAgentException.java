/*
 * MissingAgentException.java
 *
 * Created on June 5, 2001, 1:53 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class MissingAgentException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>MissingAgentException</code> without detail message.
     */
    public MissingAgentException()
    {
    }


    /**
     * Constructs an <code>MissingAgentException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public MissingAgentException(String msg)
    {
        super(msg);
    }
}



