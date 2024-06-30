/*
 * SessionException.java
 *
 * Created on April 08, 2002, 11:11 AM
 */

package com.inexum.Exceptions;

/**
 * A session exception.
 *
 * @author Nebojsa Vojinovic - Voja
 * @version 2.0.2
 */
public class SessionException extends iNexumException
{
    /** Create a new instance of a session exception. */
    public SessionException ()
    {
    }

    /** 
     * Create a new instance of a session exception.
     *
     * @param exception - a exception reason.
    */
    public SessionException ( Exception exception )
    {
    }
}
