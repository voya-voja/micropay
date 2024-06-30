/*
 * NoSuchVoucherException.java
 *
 * Created on July 16, 2001, 12:08 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class NoSuchVoucherException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>NoSuchVoucherException</code> without detail message.
     */
    public NoSuchVoucherException ()
    {
    }


    /**
     * Constructs an <code>NoSuchVoucherException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoSuchVoucherException (String msg)
    {
        super(msg);
    }
}



