/*
 * IFunction.java
 *
 * Created on June 13, 2001, 11:11 AM
 */

package com.inexum.IPC;

/** A function interface for client-server.
 *
 * @author Nebojsa Vojinovic - Voja
 * @version 2.0.2
 */
public interface IFunction extends IProcedure
{
    /** Define specific functionality. 
     *
     * @return an object that the function returns
     * @throws Exception - any application specific exception that is thrown by
     * the function
     */
    public Object run() throws Exception;
}
