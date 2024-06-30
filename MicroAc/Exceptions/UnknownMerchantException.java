/*
 * UnknownMerchantException.java
 *
 * Created on June 11, 2001, 12:14 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class UnknownMerchantException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>UnknownMerchantException</code> without detail message.
     */
    public UnknownMerchantException()
    {
    }


    /**
     * Constructs an <code>UnknownMerchantException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnknownMerchantException(String msg)
    {
        super(msg);
    }
}


