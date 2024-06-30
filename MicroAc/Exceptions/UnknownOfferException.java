/*
 * UnknownOfferException.java
 *
 * Created on May 16, 2001, 3:51 PM
 */

package com.inexum.MicroAc.Exceptions;

/** Thrown if an offer identifier does not match any product in the database.
 *
 * @author rgoold
 * @version 
 */
public class UnknownOfferException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>UnknownOfferException</code> without detail message.
     */
    public UnknownOfferException() {
    }


    /**
     * Constructs an <code>UnknownOfferException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnknownOfferException(String msg) {
        super(msg);
    }
}



