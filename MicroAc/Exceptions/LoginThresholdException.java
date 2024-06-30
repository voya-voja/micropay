/*
 * LoginThresholdException.java
 *
 * Created on May 25, 2001, 1:54 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  Nick Kapov
 * @version 
 */
public class LoginThresholdException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>LoginThresholdException</code> without detail message.
     */
    public LoginThresholdException() {
    }


    /**
     * Constructs an <code>LoginThresholdException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public LoginThresholdException(String msg) {
        super(msg);
    }
}


