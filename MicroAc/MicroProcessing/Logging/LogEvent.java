/*
 * LogEvent.java
 *
 * Created on June 11, 2001, 1:26 PM
 */

package com.inexum.MicroAc.MicroProcessing.Logging;


/**
 *
 * @author  rgoold
 * @version 
*/
public class LogEvent extends java.util.EventObject 
{
    private String              m_eventType;
    private java.sql.Timestamp  m_timeStamp;
   
    /**
     * Creates new LogEvent
     * @roseuid 3B2FBB7B0234
     */
    public LogEvent(Object source, String eventType ) 
    {
        super(source);
        m_timeStamp = new java.sql.Timestamp( System.currentTimeMillis() );
        m_eventType = eventType;
    }
    
    public final String getType()
    {
        return m_eventType;
    }
    
    public final java.sql.Timestamp getTimestamp()
    {
        return m_timeStamp;
       
    }
}
