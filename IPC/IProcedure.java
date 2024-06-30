/*
 * IProcedure.java
 *
 * Created on June 13, 2001, 11:11 AM
 */

package com.inexum.IPC;

import java.io.Serializable;

/** A function interface for client-server.
 *
 * @author Nebojsa Vojinovic - Voja
 * @version 2.0.2
 */
public interface IProcedure extends Serializable
{
    /** Define specific exception class. 
     *
     * @return an exception class.
     */
    public Class exceptionClass();
    
    /** Throw the specified exception class.
     *
     * throws Exception a procedure's specific exception.
     */
    public void throwException() throws Exception;
}
