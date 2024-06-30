/*
 * RestrictedProductException.java
 *
 * Created on July 12, 2001, 2:44 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  nkapov
 * @version 
 */
public class RestrictedProductException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>RestrictedProductException</code> without detail message.
     */
    public RestrictedProductException() {
    }


    /**
     * Constructs an <code>RestrictedProductException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public RestrictedProductException(String msg) {
        super(msg);
    }
}


