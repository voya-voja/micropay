/*
 * OasisTransactionLogger.java
 *
 * Created on July 28, 2001, 2:57 PM
 */

package com.inexum.MicroAc.MicroProcessing.Logging;

import com.inexum.Types.Money;

import java.sql.*;



/**
 *
 * @author  nkapov
 * @version 
 */
public class OasisTransactionLogger extends TransactionLogger 
                                                implements LogListener
{
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
            String sqlString = "INSERT INTO vCard_Hist "
                + "(BIN, PAN, SEQ_ID, TRANS_CLASS, TRANS_CD, CURR_CD, "
                + "CURR_CD_ORIGINAL, MERCHANT, TOTAL_AMT, TOTAL_AMT_ORIGINAL,"
                + "TRANS_DATE, PROC_DATE, TRANS_DESC, ADDED_BY,"
                + "ADD_DATE) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
            
            PreparedStatement update = m_dbConn.prepareStatement(sqlString);

            update.setString(1, m_logEvent.getBIN());
            update.setString(2, m_logEvent.getPAN());
            
            //defined by oasis as a internal unique ID identifying the transaction in the log
            update.setInt(3/*SEQ_ID*/, 0); 
            update.setInt(4/*TRANS_CLASS*/, 3); //This is three all the time for our purposes
            
            //oasis defined codes for crediting and debiting
            update.setInt(5/*TRANS_CD*/, (m_logEvent.getType() == TransactionLogEvent.c_Credit )? 6:5 );
            update.setString( 6/*CURR_CD*/, "124" /*ntk Money.getCurrencyCode( m_logEvent.getConsumerCost().getCurrency() )*/ );
            update.setString( 7/*CURR_CD_ORIGINAL*/,"124" /*ntk Money.getCurrencyCode( m_logEvent.getMerchantCost().getCurrency() )*/ );
            update.setString( 8/*MERCHANT*/, m_logEvent.getUserID() );
            update.setDouble( 9/*TOTAL_AMT*/, m_logEvent.getConsumerCost().getPrice() );
            update.setDouble( 10/*TOTAL_AMT_PRIGINAL*/, m_logEvent.getMerchantCost().getPrice() );
            update.setTimestamp( 11/*TRANS_DATE*/, m_logEvent.getTimestamp() );
            update.setTimestamp( 12/*PROC_DATE*/, m_logEvent.getTimestamp() );
            update.setString( 13/*TRANS_DESC*/, m_logEvent.getProductID() );
            update.setString( 14/*ADDED_BY*/, (m_logEvent.getType() == TransactionLogEvent.c_Credit )? "MAMA":"CAMA" );
            update.setTimestamp(15/*ADDED_DATE*/, new java.sql.Timestamp( System.currentTimeMillis() ) );
            
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
    
    private static Connection   m_dbConn;

    /** Creates new OasisTransactionLogger */
    public OasisTransactionLogger( final LogSource source ) 
    {
        super(source);
    
        
    }
    
   public static void setDBConnection (Connection microAcConn, Connection oasisDBConn)
   {
       TransactionLogger.setDBConnection( microAcConn );
       m_dbConn = oasisDBConn;
   }

    /**
     * @roseuid 3B2FBB7C0380
 */
    public void logMessage(LogEvent logEvent) 
    {
        if (!logEvent.getClass().equals(TransactionLogEvent.class))
            return;
       
       new DBLogger ((TransactionLogEvent)logEvent, m_dbConn).start();
       
       super.logMessage( logEvent );
    }

    
  
    
    /*
    public static void main( String[] args )
    {
        try
        {
        com.inexum.Database.DatabaseConPool conp =
         com.inexum.Database.DatabaseConPool.getDatabaseConPool();
        
        Connection cm = conp.getConnection( com.inexum.Database.DatabaseConPool.c_MicroAcPool, "iNexum", "iNexum123" );
        Connection co = conp.getConnection( com.inexum.Database.DatabaseConPool.c_OasisPool, "iNexum", "iNexum123" );
        
        
        Bob ps = new Bob();     
        
        OasisTransactionLogger oat = new OasisTransactionLogger( ps  );
        oat.setDBConnection(cm, co );
        
        System.out.println("About to log transaction");
        
        oat.logMessage(new TransactionLogEvent( ps, 
                                                TransactionLogEvent.c_Debit, 
                                                "SessionID", 
                                                1, 
                                                "Rich", 
                                                "productID", 
                                                new Money( 1.25, "CAD"), 
                                                new Money( 1.25, "CAD"), 
                                                "1300101000000100", 
                                                "13001" ) );
        
        System.out.println("Transaction logged");
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }
      
        


    }
    */
   
    
}



