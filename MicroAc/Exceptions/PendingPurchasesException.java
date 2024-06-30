/*
 * PendingPurchasesException.java
 *
 * Created on May 28, 2001, 4:18 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  Nick Kapov
 * @version 
 */
public class PendingPurchasesException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>PendingPurchasesException</code> without detail message.
     */
    public PendingPurchasesException() {
    }


    /**
     * Constructs an <code>PendingPurchasesException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PendingPurchasesException(String msg) {
        super(msg);
    }
}


