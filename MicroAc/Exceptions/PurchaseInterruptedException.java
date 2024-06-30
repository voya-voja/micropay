/*
 * PurchaseInterruptedException.java
 *
 * Created on May 24, 2001, 4:12 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class PurchaseInterruptedException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>PurchaseInterruptedException</code> without detail message.
     */
    public PurchaseInterruptedException()
    {
    }


    /**
     * Constructs an <code>PurchaseInterruptedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PurchaseInterruptedException(String msg)
    {
        super(msg);
    }
}



