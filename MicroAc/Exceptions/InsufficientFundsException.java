/*
 * InsufficientFundsException.java
 *
 * Created on July 4, 2001, 4:00 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class InsufficientFundsException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>InsufficientFundsException</code> without detail message.
     */
    public InsufficientFundsException()
    {
    }


    /**
     * Constructs an <code>InsufficientFundsException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InsufficientFundsException(String msg)
    {
        super(msg);
    }
}



