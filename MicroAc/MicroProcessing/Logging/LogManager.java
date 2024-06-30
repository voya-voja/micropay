/*
 * LogManager.java
 *
 * Created on June 11, 2001, 1:37 PM
 */

package com.inexum.MicroAc.MicroProcessing.Logging;

import com.inexum.Database.*;
import com.inexum.util.ConfigurationManager;

import java.sql.*;
import java.util.*;

/**
 *
 * @author  rgoold
 * @version 
 */
public class LogManager extends Object
implements LogListener, LogSource
{
    private static  LogManager  m_singleton;
    private         Connection  m_microAcDBConn;
  //ntk  private         Connection  m_oasisDBConn;
    
    private java.util.List      m_listenerList;
    
    /**
     * Creates new LogManager
     * @roseuid 3B2FBB800083
     */
    public LogManager ()
    {
        m_listenerList = new java.util.LinkedList ();
        SetManager (this);
        initDBConnection ();
        
        OasisTransactionLogger.setDBConnection ( m_microAcDBConn, m_microAcDBConn);
    }
    
    private boolean initDBConnection ()
    {
        boolean bSuccess = false;
        try
        {
            ConfigurationManager config = ConfigurationManager.Instance();
            
            m_microAcDBConn = DatabaseConPool.getDatabaseConPool ().getConnection (
                        config.getString ( "db.UserID" ),
                        config.getString ( "db.Password" ) );
            
            m_microAcDBConn.setAutoCommit ( false );
            
/* nmv            m_oasisDBConn = DatabaseConPool.getDatabaseConPool ().getConnection (
            DatabaseConPool.c_OasisPool,
            m_LogProps.getString ( "oasisUserID" ),
            m_LogProps.getString ( "oasisPassword" ) );
            
            m_oasisDBConn.setAutoCommit ( false );
nmv */        
            bSuccess = true;
            
        }
        catch( SQLException SQLE )
        {
            bSuccess = false;
            SQLE.printStackTrace ();
        }
        finally
        {
            return bSuccess;
        }
    }
    
    private static void SetManager (LogManager manager)
    {
        m_singleton = manager;
    }
    
    /**
     * @roseuid 3B30E5B60334
     */
    public static LogManager GetManager ()
    {
        if (m_singleton == null)
        {
            m_singleton = new LogManager ();
        }
        return m_singleton;
    }
    
    private class ListenerModifierThread extends Thread
    {
        private boolean         m_isAdding = true;
        private LogListener     m_listener;
        private java.util.List  m_listenerList;
        
        public ListenerModifierThread (java.util.List listenerList,
            LogListener listener)
        {
            m_listenerList = listenerList;
            m_listener = listener;
        }
        
        public void add ()
        {
            m_isAdding = true;
            start ();
        }
        
        public void remove ()
        {
            m_isAdding = false;
            start ();
        }
        
        public void run ()
        {
            synchronized(m_listenerList)
            {
                if (m_isAdding)
                {
                    m_listenerList.add (m_listener);
                }
                else if (m_listenerList.contains (m_listener))
                {
                    m_listenerList.remove (m_listener);
                }
            }
        }
    }
    
    /**
     * @roseuid 3B31108700EF
     */
    public void addListener (LogListener listener)
    {
        ListenerModifierThread modifier
            = new ListenerModifierThread (m_listenerList, listener);
        modifier.add ();
    }
    
    /**
     * @roseuid 3B31108700F9
     */
    public void removeListener (LogListener listener)
    {
        ListenerModifierThread modifier
            = new ListenerModifierThread (m_listenerList, listener);
        modifier.remove ();
    }
    
    /**
     * @roseuid 3B3110870103
     */
    public void logMessage (LogEvent logEvent)
    {
        synchronized(m_listenerList)
        {
            java.util.Iterator listIterator
            = m_listenerList.iterator ();
            
            //Pump the logevent to the registered event listeners
            while (listIterator.hasNext ())
            {
                ((LogListener)listIterator.next ()).logMessage (logEvent);
            }
        }
    }
    
    public void createAuthorizationLogger ()
    {
        addListener ( new AuthorizationLogger ( m_microAcDBConn ) );
        
    }
    
    public static void main (String[] args)
    {
        System.out.println ("Creating a LogManager.");
        LogManager logMgr = new LogManager ();
        //System.out.println("Creating TransactionLoggers.");
        //new TransactionLogger();
        System.out.println ("Starting the event source thread.");
        // nmv 20011212 new TestSourceThread ().start ();
    }
}
