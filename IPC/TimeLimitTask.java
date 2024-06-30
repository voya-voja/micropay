/*
 * TimeLimitTask.java
 *
 * Created on July 10, 2001, 4:46 PM
 */

package com.inexum.IPC;

/**
 *
 * @author  Administrator
 * @version 
 */
public class TimeLimitTask extends java.util.TimerTask
{
    private TimeLimited m_limitedObject;

    /** Creates new TimeLimitTask */
    public TimeLimitTask (TimeLimited limitedObject)
    {
        m_limitedObject = limitedObject;
    }
    
    public void run ()
    {
        try
        {
            if (!m_limitedObject.isLeaseValid())
            {
                m_limitedObject.unreferenced();
                cancel();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
    }
    
}

