/*
 * ProfileUpdateException.java
 *
 * Created on July 4, 2001, 10:53 AM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class ProfileUpdateException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>ProfileUpdateException</code> without detail message.
     */
    public ProfileUpdateException()
    {
    }


    /**
     * Constructs an <code>ProfileUpdateException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ProfileUpdateException(String msg)
    {
        super(msg);
    }
}



