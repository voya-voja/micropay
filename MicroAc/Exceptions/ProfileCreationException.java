/*
 * ProfileCreationException.java
 *
 * Created on June 28, 2001, 10:04 AM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  nkapov
 * @version 
 */
public class ProfileCreationException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>ProfileCreationException</code> without detail message.
     */
    public ProfileCreationException() {
    }


    /**
     * Constructs an <code>ProfileCreationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ProfileCreationException(String msg) {
        super(msg);
    }
}


