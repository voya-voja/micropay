/*
 * ConsumerProfileManager.java
 *
 * Created on June 20, 2001, 9:26 AM
 */

package com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.MicroProcessing.Transaction.*;
import com.inexum.Database.*;
import com.inexum.MicroAc.Consumer.LoginInfo;
import com.inexum.util.ConfigurationManager;

import java.rmi.*;
import java.rmi.server.*;
import java.sql.*;
import javax.sql.*;
import oracle.jdbc.*;
import java.util.*;


/**
 *
 * @author  nkapov
 * @version 
 */
public class ConsumerProfileManager extends UnicastRemoteObject
    implements IConsumerProfileManager
{
    private static java.util.Map    m_profileCache;
    private ISessionFactory         m_sessionFactory;
    private Connection              m_dbMicroAcConn;
 //ntk   private Connection              m_dbOasisConn;
    
    private int                     m_sessionCounter = 0;

    /** Creates new ConsumerProfileFactory */
    public ConsumerProfileManager(ISessionFactory sessionFactory)
        throws RemoteException
    {
        super();
        if (m_profileCache == null)
        {
            m_profileCache = java.util.Collections.synchronizedMap(
                new java.util.HashMap());
        }
        m_sessionFactory = sessionFactory;
        try
        {
            setDBConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RemoteException(e.getLocalizedMessage());
        }
        finally
        {
        }
    }
    
    public ConsumerProfileManager(ISessionFactory sessionFactory, int port)
        throws RemoteException
    {
        super(port);
        if (m_profileCache == null)
        {
            m_profileCache = java.util.Collections.synchronizedMap(
                new java.util.HashMap());
        }
        m_sessionFactory = sessionFactory;
        try
        {
            setDBConnection();
        }
        catch (SQLException e)
        {
            throw new RemoteException(e.getLocalizedMessage());
        }
        finally
        {
        }
    }
    
    private void setDBConnection()
        throws SQLException
    {
        ConfigurationManager config = ConfigurationManager.Instance();
   
        m_dbMicroAcConn = DatabaseConPool.getDatabaseConPool().getConnection(
                            config.getString( "db.UserID" ), 
                            config.getString( "db.Password" ));
        
        m_dbMicroAcConn.setAutoCommit(false);
        
/* nmv        m_dbOasisConn = DatabaseConPool.getDatabaseConPool().getConnection(
                            DatabaseConPool.c_OasisPool,
                            config.getString( "UserID" ), 
                            config.getString( "Password" ));
        
        m_dbOasisConn.setAutoCommit(false);
nmv */        
    }
    
    // "add" as in add yourself to the cache of checked out profiles,
    // not as in create a new profile.
    public ProfileProxy add (String userID)
        throws ProfileCreationException, RemoteException
    {
        try
        {
            ConsumerProfile profile = null;
            profile = createProfile(userID);
            m_profileCache.put(userID, profile);
            return profile.getProfileProxy();
        }
        catch (ProfileCreationException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ProfileCreationException(e.getLocalizedMessage());
        }
        finally
        {
        }
    }

    public boolean isLoggedIn (final LoginInfo loginInfo)
        throws RemoteException
    {
        ConsumerProfile profile
            = (ConsumerProfile)m_profileCache.get (loginInfo.getUserID ());
        
        // Not logged in
        if (profile == null)
        {
            return false;
        }
        
        // Logged in but disconnected
        try
        {
            if ( profile.isFinished ())
            {
                logOff( loginInfo.getUserID() );
                return false;
            }
        }
        catch (ExcludedMiddleException e)
        {
            e.printStackTrace ();
            logOff( loginInfo.getUserID() );
            return false;
        }
        
        return true;
    }
    
    public void logOff( String userID )
    {
        m_profileCache.remove( userID );
    }
    
    private void removeExpiredVouchers (String userID)
        throws java.sql.SQLException
    {
        java.sql.PreparedStatement statement
            = m_dbMicroAcConn.prepareStatement(
            "DELETE FROM Voucher WHERE ExpiryDate < SYSDATE");
        statement.executeUpdate();
        statement.close();
        m_dbMicroAcConn.commit();
    }
    
    private ConsumerProfile createProfile(String userID)
        throws ProfileCreationException
    {
        PreparedStatement statement = null;
        OracleResultSet profileSet = null;
        OracleResultSet voucherSet = null;
        OracleResultSet cardholderSet = null;
        
        try
        { 
            // Get rid of expired vouchers
            removeExpiredVouchers(userID);
        }
        catch (java.sql.SQLException e)
        {
            e.printStackTrace();
        }
        
        try
        {
            // Get profile
            statement = m_dbMicroAcConn.prepareStatement(
                "SELECT * FROM Profile WHERE UserID = ? ", 
                                    ResultSet.TYPE_SCROLL_SENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY );
            statement.setString(1, userID);
            profileSet = (OracleResultSet)statement.executeQuery();
            
            //make sure only one profile was found in the database
            if( rowCount( profileSet ) != 1  )
                throw new ProfileCreationException();
            
            profileSet.next();
               
            statement = m_dbMicroAcConn.prepareStatement(
              "SELECT * from Cardholders where PAN = ? " );
            statement.setString( 1, profileSet.getString( "PAN" ));
            cardholderSet = (OracleResultSet)statement.executeQuery();
            
            
            // Get vouchers
            statement = m_dbMicroAcConn.prepareStatement(
                "SELECT t.* FROM Voucher t WHERE t.UserID = ?" );
            
            statement.setString(1, userID);
                      
            voucherSet = (OracleResultSet)statement.executeQuery();
            
            if( rowCount(voucherSet) == 0 ) 
                voucherSet = null;
                        
            voucherSet = (OracleResultSet)statement.executeQuery();
            
            // Create profile
            ConsumerProfile profile = null;
            try
            {
                profile = new ConsumerProfile(this, userID,
                    profileSet, voucherSet, cardholderSet, null );
            }
            catch (SQLException e)
            {
                throw e;
            }
            
            // Create a session
            TxInfo txInfo = new TxInfo( null, getNextSessionID(), 0, TxInfo.PURCHASE);
            ISession session = null;
            try
            {
                session = m_sessionFactory.newSession(profile);
            }
            catch (RemoteException e)
            {
                throw e;
            }
            catch (IllegalArgumentException e)
            {
                throw e;
            }
                       
            // Set the session on the profile
            profile.setSession(session);
            
            m_profileCache.put(userID, profile);
            return profile;
        }
        catch (java.rmi.RemoteException e)
        {
            throw new ProfileCreationException(e.getLocalizedMessage());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new ProfileCreationException(e.getLocalizedMessage());
        }
        finally
        {
            try
            {
                statement.close();
                profileSet.close();
                voucherSet.close();
                cardholderSet.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            catch (java.lang.NullPointerException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    
    private int rowCount( OracleResultSet  rs ) throws SQLException
    {
        int count = 0;
        
        while( rs.next() )
            count++;
        
        if( rs.getType() != ResultSet.TYPE_FORWARD_ONLY )
            rs.beforeFirst();
        
        return count;
        
        
    }
    
    private String getNextSessionID()
    {
       return  Integer.toString(m_sessionCounter++); 
    }

     public static void main(String[] args)
    {
    }
    
}

