/*
 * InternalErrorException.java
 *
 * Created on October 31, 2001, 6:28 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 * Indicates that an internal server error occurred, preventing the requested
 * action from being taken. The <code>noHarmDone</code> flag should only be
 * set to true if the software can confirm that any negative effects of the
 * cancelled action (e.g., account debiting) have been successfully reversed.
 *
 * @author  rgoold
 * @version 
 */
public class InternalErrorException extends java.lang.Exception
{
    private boolean     m_noHarmDone;

    /**
     * Creates new <code>InternalErrorException</code> without detail message.
     */
    public InternalErrorException(boolean noHarmDone)
    {
        m_noHarmDone = noHarmDone;
    }


    /**
     * Constructs an <code>InternalErrorException</code> with the specified
     * detail message.
     * @param msg the detail message.
     */
    public InternalErrorException(boolean noHarmDone, String msg)
    {
        super(msg);
        m_noHarmDone = noHarmDone;
    }


    public boolean noHarmDone()
    {
        return m_noHarmDone;
    }
}


