//Source file: C:\Development\com\inexum\Nickel\eMoneyAdmin\Logging\TxLogger.java

package com.inexum.MicroAc.MicroProcessing.Logging;

import com.inexum.util.ConfigurationManager;

import java.sql.*;

public class TransactionLogger extends Object
    implements LogListener
{
    private static final ConfigurationManager c_Msgs = ConfigurationManager.Instance();
    
    private static Connection   m_dbConn;
    
    
    public static void setDBConnection (Connection dbConn)
    {
        m_dbConn = dbConn;
    }
    
    private class DBLogger extends Thread
    {
        private TransactionLogEvent m_logEvent;
        private Connection          m_dbConn;
        
        public DBLogger (TransactionLogEvent logEvent, Connection dbConn)
        {
            m_dbConn = dbConn;
            m_logEvent = logEvent;
        }
        
        private void writeToDB ()
            throws java.sql.SQLException
        {
            String sqlString = "INSERT INTO TransactionLog "
                + "(SessionID, TransactionID, Timestamp, UserID, "
                + "ProductID, PAN, BIN) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement update = m_dbConn.prepareStatement(sqlString);

            update.setString(1, m_logEvent.getSessionID());
            
            update.setString(2, Integer.toString(
                m_logEvent.getTransactionID()));
            
            update.setTimestamp(3, m_logEvent.getTimestamp());
            update.setString(4, m_logEvent.getUserID());
            update.setString(5, m_logEvent.getProductID());
            update.setString(6, m_logEvent.getPAN());
            update.setString(7, m_logEvent.getBIN());
            


            // Make sure statement gets inserted correctly
            if (update.executeUpdate() != 1)
            {
                throw new java.sql.SQLException();
            }
            
            m_dbConn.commit();
        }
        
        public void run ()
        {
            try
            {
                writeToDB();
            }
            catch (java.sql.SQLException e)
            {
                e.printStackTrace();
                ErrorLogEvent failure = new ErrorLogEvent(this, "SQL Error");
                e.printStackTrace(failure.getTraceStream());
                LogManager logMgr = LogManager.GetManager();
                logMgr.logMessage(failure);
            }
        }
    }
    
    private class FileLogger extends Thread
    {
        private TransactionLogEvent m_logEvent;
        private java.io.File        m_logFile;
        
        public FileLogger(TransactionLogEvent logEvent,
            java.io.File logFile)
        {
            m_logFile = logFile;
            m_logEvent = logEvent;
        }
        
        private void writeToFile ()
            throws java.io.FileNotFoundException, java.io.IOException
        {
            java.io.File logFile = new java.io.File("Tx-"
                + m_logEvent.getSessionID().replace('/', '_') + ".txt");
            if (!logFile.exists())
            {
                logFile.createNewFile();
            }
            
            // Open file for appending
            java.io.FileWriter writer = new java.io.FileWriter(
                logFile.getAbsolutePath(), true);
            try
            {
                writer.write(m_logEvent.getTimestamp().toString());
                if (m_logEvent.getType()
                    == TransactionLogEvent.c_TransactionFailed)
                {
                    writer.write(" - "
                        + c_Msgs.getString("TransactionFailed")
                        + ".\r\n");
                }
                else if (m_logEvent.getType() == TransactionLogEvent.c_Credit || 
                    m_logEvent.getType() == TransactionLogEvent.c_Debit )
                {
                    writer.write(" - "
                        + c_Msgs.getString("TransactionSucceeded")
                        + ".\r\n");
                }
                writer.flush();
            }
            catch (java.io.IOException e)
            {
                throw e;
            }
            finally
            {
                try
                {
                    writer.close();
                }
                catch (java.io.IOException e)
                {
                    /* (2001-07-27 RTG) Even though this method declares
                     * that it throws java.io.IOException, this particular
                     * exception MUST be caught. If an exception were thrown
                     * while writing to the file and the close operation in
                     * this finally block threw an exception, the first one
                     * would be lost. See Thinking In Java, 2nd Edition by
                     * Bruce Ecker, p. 557 for a more thorough explanation.
                     */
                }
            }
        }
        
        public void run()
        {
            try
            {
                writeToFile();
            }
            catch (java.io.FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public TransactionLogger(final LogSource source)
    {
        source.addListener(this);
    }
   
    /**
     * @roseuid 3B31111E02C6
     */
    public void logMessage(LogEvent logEvent) 
    {
        if (!logEvent.getClass().equals(TransactionLogEvent.class))
        {
            return;
        }
        TransactionLogEvent txEvent = (TransactionLogEvent)logEvent;
        
        // Spawn a new thread to handle file writing.
        // new FileLogger(txEvent, m_logFile).start();
        
        new DBLogger (txEvent, m_dbConn).start();
    }
}
