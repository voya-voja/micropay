/*
 * IOperation.java
 *
 * Created on June 13, 2001, 11:11 AM
 */

package com.inexum.IPC;

/** An operation interface for client-server.
 *
 * @author Nebojsa Vojinovic - Voja
 * @version 2.0.2
 */
public interface IOperation extends IProcedure
{
    /** Define specific functionality. 
     *
     * @throws Exception - any application specific exception that is thrown by
     * the function
     */
    public abstract void run() throws Exception;
}
