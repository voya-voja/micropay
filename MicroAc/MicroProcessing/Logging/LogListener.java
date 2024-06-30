/*
 * LogListener.java
 *
 * Created on June 11, 2001, 1:34 PM
 */

package com.inexum.MicroAc.MicroProcessing.Logging;


/**
 *
 * @author  rgoold
 * @version 
*/
public interface LogListener extends java.util.EventListener 
{
   
   /**
   @roseuid 3B2FBB7C0380
   */
   public void logMessage(LogEvent logEvent);
}
