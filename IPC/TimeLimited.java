/*
 * TimeLimited.java
 *
 * Created on July 10, 2001, 4:44 PM
 */

package com.inexum.IPC;

/**
 *
 * @author  rgoold
 * @version 
 */
public interface TimeLimited extends java.rmi.server.Unreferenced
{
    public void startTimer();
    public void extendLease(long duration);
    public boolean isLeaseValid();
}


