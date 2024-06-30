/*
 * LogSource.java
 *
 * Created on June 16, 2001, 4:59 PM
 */

package com.inexum.MicroAc.MicroProcessing.Logging;


/**
 *
 * @author  rgoold
 * @version 
*/
public interface LogSource 
{
   
    /**
     * @roseuid 3B2FBB80035E
     */
    public void addListener(LogListener listener);
   
    /**
     * @roseuid 3B2FBB800360
     */
    public void removeListener(LogListener listener);
}
