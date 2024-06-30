/*
 * Lock.java
 *
 * Created on July 18, 2001, 3:44 PM
 */

package com.inexum.IPC;

import com.inexum.util.ConfigurationManager;

/**
 *
 * @author  rgoold
 * @version 
 */
public class Lock extends java.lang.Object
{
    private static final ConfigurationManager c_Msgs = ConfigurationManager.Instance();
    
    Thread      m_lockThread;

    /** Creates new Lock */
    public Lock ()
    {
    }
    
    private void startLockThread()
    {
        m_lockThread = new Thread()
        {
            public void run()
            {
                try
                {
                    synchronized(this)
                    {
                        wait();
                    }
                }
                catch (java.lang.InterruptedException e)
                {
                }
            }
        };
        m_lockThread.start();
    }
    
    public boolean isLocked()
    {
        return (m_lockThread != null);
    }
    
    public void acquire()
    {
        while (m_lockThread != null)
        {
            try
            {
                m_lockThread.join();
            }
            catch (java.lang.InterruptedException e)
            {
            }
        }
        
        startLockThread();
    }
    
    public boolean acquire (long timeout)
    {
        if (m_lockThread != null)
        {
            try
            {
                m_lockThread.join(timeout);
            }
            catch (java.lang.InterruptedException e)
            {
                return false;
            }
        }
        
        startLockThread();
        return true;
    }
    
    public void release()
    {
        if (m_lockThread != null)
        {
            synchronized (m_lockThread)
            {
                m_lockThread.notify();
                m_lockThread = null;
            }
        }
        else
        {
            System.err.println(c_Msgs.getString("InvalidUnlock"));
        }
    }

}

