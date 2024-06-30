/*
 * InvalidVoucherException.java
 *
 * Created on May 16, 2001, 4:07 PM
 */

package com.inexum.MicroAc.Exceptions;

/** Thrown if a voucher is not valid.
 *
 * @author rgoold
 * @version 
 */
public class AccountUnavailableException extends java.lang.Exception
    implements java.io.Serializable
{

    /**
     * Creates new <code>InvalidVoucherException</code> without detail message.
     */
    public AccountUnavailableException() {
    }


    /**
     * Constructs an <code>InvalidVoucherException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public AccountUnavailableException(String msg) {
        super(msg);
    }
}



