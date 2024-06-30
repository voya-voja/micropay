/*
 * AuthorizationLogger.java
 *
 * Created on July 25, 2001, 1:09 PM
 */

package com.inexum.MicroAc.MicroProcessing.Logging;

import java.sql.*;
import java.io.*;

/**
 *
 * @author  nkapov
 * @version 
 */
public class AuthorizationLogger extends java.lang.Object implements LogListener 
{
   private class Logger extends Thread
   {
       private AuthLogEvent        m_logEvent;
       private Connection          m_dbConn;
       
       private final String        c_SQLString;
       
       public Logger( LogEvent logEvent, Connection dbConn )
       {
           
           m_logEvent = (AuthLogEvent)logEvent;
           m_dbConn = dbConn;
           
           c_SQLString = "INSERT INTO AuthorizationLog (Userid, TimeStamp, " + 
                            "Description, WalletID ) VALUES (?,?,?,?)";
       }
       
       private void writeToFile( AuthLogEvent logEvent ) throws IOException,
                                                           FileNotFoundException
       {
             
             File logFile = new File("Auth_" + 
                                   logEvent.getLoginInfo().getUserID()+"_"+
                                   logEvent.getTimestamp().toString() + ".txt");
             
             FileWriter writer = new FileWriter( logFile.getAbsolutePath(), 
                                                                          true);
            
             writer.write(  m_logEvent.getLoginInfo().getUserID() +
                              "\t" + logEvent.getTimestamp().toString() +
                              "\t" + logEvent.getType() +
                              "\t" + logEvent.getLoginInfo().getWalletID() +
                              "\n" );
             writer.flush();
             writer.close();
                          
        }
       
       public void run()
       {
           //write the event to the database
           boolean bDone = false;
           int tryCount = 0;
           
           while( !bDone )
           {
               try
               {
                    PreparedStatement stmt = m_dbConn.prepareStatement( 
                                                                c_SQLString );
                    stmt.setString( 1, m_logEvent.getLoginInfo().getUserID()); 
                    stmt.setTimestamp(2, m_logEvent.getTimestamp() );
                    stmt.setString( 3, m_logEvent.getType() );
                    stmt.setString( 4, m_logEvent.getLoginInfo().getWalletID());
                               
                    stmt.executeUpdate();
                    m_dbConn.commit();
                    stmt.close();                  
                    
                    bDone = true;
               }
               catch(SQLException SQLE )
               {
                   SQLE.printStackTrace();
                   tryCount++;
                   
                   if( tryCount == 10 )
                   {
                       try
                       {
                           writeToFile( m_logEvent );
                       }
                       catch( FileNotFoundException FNFE )
                       {
                           FNFE.printStackTrace();
                       }
                       catch( IOException IOE )
                       {
                           IOE.printStackTrace();
                       }
                       finally
                       {
                           // No sense looping forever
                           bDone = true;
                       }
                   }
               }
           } /* while (!bDone) */
       }
   }

    
    /** Creates new AuthorizationLogger */
    Connection m_dbConn;
    
    public AuthorizationLogger( Connection dbConn ) 
    {
        m_dbConn = dbConn;
       
    }

    /**
     * @roseuid 3B2FBB7C0380
 */
    public void logMessage(LogEvent logEvent) 
    {
        
        
        //log to the database
        if( logEvent.getClass().equals(AuthLogEvent.class) )
        {
              Logger p =  new Logger( logEvent, m_dbConn );
              p.start();
        }
        
        //remove myself
        LogManager.GetManager().removeListener(this);
        
    }
    
   
    
}
