/*
 * QueueSizeExceededException.java
 *
 * Created on November 20, 2001, 4:42 PM
 */

package com.inexum.MicroAc.Exceptions;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class QueueSizeExceededException extends java.lang.Exception
{

    /**
     * Creates new <code>QueueSizeExceededException</code> without
     * detail message.
     */
    public QueueSizeExceededException()
    {
    }

//----------------------------------------------------------------------------//

    /**
     * Constructs an <code>QueueSizeExceededException</code> with the
     * specified detail message.
     * @param msg the detail message.
     */
    public QueueSizeExceededException(String msg)
    {
        super(msg);
    }
}

