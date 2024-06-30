/*
 * ExcludedMiddleException.java
 *
 * Created on July 9, 2001, 1:06 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class ExcludedMiddleException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>ExcludedMiddleException</code> without detail message.
     */
    public ExcludedMiddleException ()
    {
    }


    /**
     * Constructs an <code>ExcludedMiddleException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ExcludedMiddleException (String msg)
    {
        super(msg);
    }
}



