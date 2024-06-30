/*
 * Authenticator.java
 *
 * Created on June 16, 2001, 1:58 PM
 */

package com.inexum.MicroAc.MicroProcessing.Consumer.Authentication;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

import java.sql.*;
import javax.sql.*;
import oracle.sql.*;
import oracle.jdbc.*;


import com.inexum.Encoding.Base64;
import com.inexum.Comm.RMISSLClientSocketFactory;
import com.inexum.Comm.RMISSLServerSocketFactory;
import com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager.*;
import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.MicroProcessing.Logging.*;
import com.inexum.MicroAc.Consumer.LoginInfo;
import com.inexum.util.ConfigurationManager;

/** Unicast remote object implementing IAuthenticator interface.
 *
 * @author nkapov
 * @version 1.0
 */
public class Authenticator extends java.rmi.server.UnicastRemoteObject 
                                                        implements IAuthenticator,
                                                        LogSource
{
    private  IConsumerProfileManager   m_profileManager;
    private  Connection                m_dbConn;
    private  String                    m_profileID;
   
    private java.util.List             m_listeners;
    

    /** Constructs IAuthenticatorImpl object and exports it on default port.
     */
    public Authenticator( Connection dbConn ) throws RemoteException 
    {
       super( 0 /* ,
                new RMISSLClientSocketFactory(),
		new RMISSLServerSocketFactory() */ );
       initialize( dbConn );
    }

    /** Constructs IAuthenticatorImpl object and exports it on specified port.
     * @param port The port for exporting
     */
    public Authenticator( int port, Connection dbConn ) throws RemoteException 
    {
        super( port /* , new RMISSLClientSocketFactory(),
            new RMISSLServerSocketFactory() */ );
       initialize( dbConn );
    }

    private final void initialize( Connection dbConn ) throws RemoteException
    {
        m_dbConn = dbConn;
        
        //Look up the ConsumerProfileManager
        try
        {
            m_profileManager = (IConsumerProfileManager)Naming.
                lookup("/ConsumerProfileManager");
            
            m_listeners = new java.util.LinkedList();
            addListener(LogManager.GetManager());
            LogManager.GetManager().createAuthorizationLogger( );
        }
        catch( NotBoundException NBE )
        {
           NBE.printStackTrace();
           throw new RemoteException(NBE.getLocalizedMessage());
        }
        catch (java.net.MalformedURLException e)
        {
            e.printStackTrace();
            throw new RemoteException(e.getLocalizedMessage());
        }
    }
   
    public ProfileProxy authenticate(LoginInfo loginInfo) throws RemoteException,
                                                    InvalidLoginException,
                                                    ServiceUnavailableException
    {
        
        try
        {
            //check the profile managers cache to see if the person or wallet
            //is currently in use
            if( m_profileManager.isLoggedIn( loginInfo ) )
            {
                //Log that the user is already logged in
                log( new AuthLogEvent(  this, loginInfo, 
                                        AuthLogEvent.c_MultipleLoginAttempt ) );
                
                throw new InvalidLoginException();
            }
            
            try
            {
                if( !isWallet( loginInfo ) )
                {
                    log( new AuthLogEvent( this, loginInfo, 
                                                AuthLogEvent.c_InvalidWallet ) );
                    
                    throw new InvalidLoginException(loginInfo.getWalletID());
                }
                
                if( !isConsumer( loginInfo ) )
                {
                 
                    log( new AuthLogEvent( this, loginInfo, 
                                            AuthLogEvent.c_InvalidUser ) );
                    
                    throw new InvalidLoginException();
                }
                
                log( new AuthLogEvent( this, loginInfo, AuthLogEvent.c_LoggedIn ) );
                
                ProfileProxy proxy;

                proxy = m_profileManager.add( loginInfo.getUserID() );
                
                return proxy;
                //return m_profileManager.add( loginInfo.getUserID() );
            }
            catch ( SQLException SQLE )
            {
                //unable to query the database, log the reason for the falure
                SQLE.printStackTrace();
                throw new RemoteException( SQLE.getMessage() );
            }
            catch( ProfileCreationException PCE )
            {
                //log the reason the creation failed
                PCE.printStackTrace();
                throw new ServiceUnavailableException(PCE.getMessage());
            }
        }
        catch (java.rmi.RemoteException e)
        {
            e.printStackTrace();
            throw new ServiceUnavailableException(e.getLocalizedMessage());
        }
        catch (InvalidLoginException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ServiceUnavailableException(e.getLocalizedMessage());
        }
        finally
        {
            removeListener( LogManager.GetManager() );
        
        }
    
    }
    
    
    private boolean isWallet( LoginInfo loginInfo ) throws SQLException
    {
        boolean bSuccess = false;
        
        PreparedStatement stmt = m_dbConn.prepareStatement(
                            "SELECT walletID FROM Wallet WHERE walletID = ?" );
        
        
        stmt.setString(1, loginInfo.getWalletID() );
        
        ResultSet rset = stmt.executeQuery();

        // Make sure that there was ony and only one record returned
        if (rset.next() && !rset.next())
        {
            bSuccess = true;
        }
        
        rset.close();
        stmt.close();   
        
        return bSuccess;
        
        
    }
    
    private boolean isConsumer( LoginInfo loginInfo ) throws SQLException 
    {
        //This statement should be a stored procedure on the oracle database
        // that will check the user name and password.
        boolean bSuccess = false;
       
        PreparedStatement stmt = m_dbConn.prepareStatement(
                                "SELECT Password FROM Profile WHERE UserID = ?" );
            
        stmt.setString(1, loginInfo.getUserID() );
            
        OracleResultSet rset = (OracleResultSet)stmt.executeQuery();

        if( rset.next() )
        {
            //Mash the clear text password and check it
            String passwordAttempt = mash( loginInfo.getPassword() ) ;
                
            if( passwordAttempt.equals(rset.getString( "Password" ) ) )
                 bSuccess = true;
            
        }
        
        rset.close();
        stmt.close();
        return bSuccess;
     }
     
     private String mash( String password )
     {
        
        //generate the mashed password
         java.security.MessageDigest md; 
        try
        {
            md = java.security.MessageDigest.getInstance("MD5");
        }
        catch( java.security.NoSuchAlgorithmException NSAE )
        {
            NSAE.printStackTrace();
            return "";
        }
        //calculate the digest
        md.update( password.getBytes() );
        byte[] rawPwd = md.digest();
        
        Base64 encoder = new Base64();
        return new String( encoder.Encode( rawPwd ) );
      
     }

    public static void main( String[] args ) throws SQLException, 
                                                    RemoteException, 
                                                    InvalidLoginException,
                                                    ServiceUnavailableException
    {
        com.inexum.Database.DatabaseConPool m_dbPool;
        Connection m_dbConn;
        
        m_dbPool = com.inexum.Database.DatabaseConPool.
            getDatabaseConPool();
        
        ConfigurationManager rsrcs = ConfigurationManager.Instance();
        m_dbConn = m_dbPool.getConnection( rsrcs.getString("db.UserID"), 
                                                rsrcs.getString("db.Password"));
        m_dbConn.setAutoCommit( false );
        
        Authenticator auth =  new Authenticator( m_dbConn );
        LoginInfo li = new LoginInfo( "Nick", "Nick123", "123qqe123rewq" );
        auth.authenticate(li);
    }
    
    /**
     * @roseuid 3B2FBB80035E
 */
    public void addListener(LogListener listener) 
    {
        m_listeners.add(listener);
     
    }
    
    /**
     * @roseuid 3B2FBB800360
 */
    public void removeListener(LogListener listener)
    {
        if (m_listeners.contains(listener))
        {
            m_listeners.remove(listener);
        }
    }
    
    
    private void log(LogEvent event)
    {
        java.util.Iterator iterator = m_listeners.iterator();
        while (iterator.hasNext())
        {
            LogListener listener = (LogListener)iterator.next();
            listener.logMessage(event);
        }
    }
    
}
