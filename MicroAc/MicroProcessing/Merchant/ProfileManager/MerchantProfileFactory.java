/*
 * ProfileDirectory.java
 *
 * Created on June 9, 2001, 4:19 PM
 */

package com.inexum.MicroAc.MicroProcessing.Merchant.ProfileManager;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.Database.*;
import com.inexum.util.ConfigurationManager;

import java.rmi.*;
import java.sql.*;
import oracle.jdbc.*;

/**
 *
 * @author  rgoold
 * @version 
 */
public class MerchantProfileFactory extends java.lang.Object
    implements IMerchantProfileFactory
{
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
    
    private Connection      m_dbMicroAcConn;
//ntk private Connection      m_dbOasisConn;
    private java.util.Map   m_cachedProfiles;
    
    public MerchantProfile getProfile(String merchantID)
        throws UnknownMerchantException
    {
        if (m_cachedProfiles.containsKey(merchantID))
        {
            CachedProfile cachedProfile
                = (CachedProfile)m_cachedProfiles.get(merchantID);
            return cachedProfile.getProfile();
        }
        else
        {
            try
            {
                MerchantProfile profile = null;
                profile = getFromDB(merchantID);
                CachedProfile cachedProfile = new CachedProfile(profile);
                m_cachedProfiles.put(merchantID, cachedProfile);
                return profile;
            }
            catch (ProfileCreationException e)
            {
                e.printStackTrace();
                throw new UnknownMerchantException(e.getLocalizedMessage());
            }
            finally
            {
            }
        }
    }
    
    public void closeProfile(MerchantProfile profile)
        throws ProfileUpdateException
    {
        if (m_cachedProfiles.containsKey(profile.getMerchantID()))
        {
            CachedProfile cachedProfile
                = (CachedProfile)m_cachedProfiles.get(profile.getMerchantID());
            if (cachedProfile.closeProfile() > 0)
            {
                return;
            }
            
            // Reference count has hit zero. Commit to database.
//            try
//            {
                // the value is updated on every transaction so I nk commented
                //this out - it shouls be done as part of the transaction logging
                //event
                
//              // Write it to the database
//              PreparedStatement update = profile.toUpdateStatement(
//                    m_dbConn, c_rsrcs.getString("ProfileTable"));
//              update.execute();
//              m_dbConn.commit();
                  

                /* (2001-08-28 RTG) Disabled for development to reduce on
                 * database access. At this point, we only have one or two
                 * merchants so there's no reason not to keep them cached
                 * at all times.
                 */
//                // If updating fails, it will at least stay cached
//                m_cachedProfiles.remove(profile.getMerchantID());
//            }
//            catch (SQLException e)
//            {
//                e.printStackTrace();
//                throw new ProfileUpdateException(e.getLocalizedMessage());
//            }
          }
    }
    
    private MerchantProfile getFromDB(String merchantID)
        throws ProfileCreationException
    {
        try
        {
            PreparedStatement statement = m_dbMicroAcConn.prepareStatement(
                "SELECT * FROM " + c_rsrcs.getString("ProfileTable")
                + " WHERE UserID = ? ");
            statement.setString(1, merchantID);
            System.out.println("Querying table: " + c_rsrcs.getString("ProfileTable"));
            OracleResultSet profileSet
                = (OracleResultSet)statement.executeQuery();
            
            if (!profileSet.next())
            {
                System.out.println("Merchant '" + merchantID
                    + "' not found in database.");
                throw new ProfileCreationException();
            }
            
            statement = m_dbMicroAcConn.prepareStatement( 
               "SELECT * from Cardholders where PAN = ?" );
            statement.setString( 1, profileSet.getString( "PAN" ));
            OracleResultSet cardholderSet = (OracleResultSet)statement.executeQuery();
            
            return new MerchantProfile(merchantID, profileSet, cardholderSet, m_dbMicroAcConn, m_dbMicroAcConn );
        }
        catch (SQLException e)
        {
            throw new ProfileCreationException(e.getLocalizedMessage());
        }
    }

    /** Creates new ProfileDirectory */
    public MerchantProfileFactory()
        throws RemoteException
    {
        try
        {
            m_dbMicroAcConn = DatabaseConPool.getDatabaseConPool().getConnection(
                            c_rsrcs.getString( "db.UserID" ), 
                            c_rsrcs.getString( "db.Password" ));
            
            m_dbMicroAcConn.setAutoCommit(false);
            
 /* nmv           m_dbOasisConn =  DatabaseConPool.getDatabaseConPool().getConnection(
                            DatabaseConPool.c_OasisPool,
                            m_merchantProfileProps.getString( "UserID" ), 
                            m_merchantProfileProps.getString( "Password" ));
        
            m_dbOasisConn.setAutoCommit( false );
nmv */
        }
        catch (SQLException e)
        {
            throw new RemoteException(e.getLocalizedMessage());
        }
        m_cachedProfiles = java.util.Collections.synchronizedMap(
            new java.util.HashMap());
    }
    
    
    protected class CachedProfile extends java.lang.Object
    {
        private int             m_refCount;
        private long            m_lastWritten;
        private MerchantProfile m_profile;
        
        public CachedProfile(MerchantProfile profile)
        {
            m_refCount = 1;
            m_profile = profile;
        }
        
        public MerchantProfile getProfile()
        {
            m_refCount++;
            return m_profile;
        }
        
        public int closeProfile()
        {
            return --m_refCount;
        }
    }

}

