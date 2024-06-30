/*
 * ConversionException.java
 *
 * Created on August 3, 2001, 3:48 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 
 */
public class ConversionException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>ConversionException</code> without detail message.
     */
    public ConversionException ()
    {
    }


    /**
     * Constructs an <code>ConversionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ConversionException (String msg)
    {
        super(msg);
    }
}



