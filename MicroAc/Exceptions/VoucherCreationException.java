/*
 * VoucherCreationException.java
 *
 * Created on May 24, 2001, 4:18 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class VoucherCreationException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>VoucherCreationException</code> without detail message.
     */
    public VoucherCreationException()
    {
    }


    /**
     * Constructs an <code>VoucherCreationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public VoucherCreationException(String msg)
    {
        super(msg);
    }
}



