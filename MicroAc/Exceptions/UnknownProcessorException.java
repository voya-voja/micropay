/*
 * UnknownProcessorException.java
 *
 * Created on June 12, 2001, 12:02 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class UnknownProcessorException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>UnknownProcessorException</code> without detail message.
     */
    public UnknownProcessorException()
    {
    }


    /**
     * Constructs an <code>UnknownProcessorException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnknownProcessorException(String msg)
    {
        super(msg);
    }
}


