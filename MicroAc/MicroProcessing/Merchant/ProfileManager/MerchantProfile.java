/*
 * MerchantProfile.java
 *
 * Created on June 9, 2001, 4:24 PM
 */

package com.inexum.MicroAc.MicroProcessing.Merchant.ProfileManager;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.util.ConfigurationManager;

import javax.crypto.SecretKey;
import java.math.*;
import java.sql.*;

/**
 *
 * @author  rgoold
 * @version 
 */
public class MerchantProfile extends java.lang.Object
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();
    
    private double          m_balance;
    private String          m_userID;
    private Connection      m_dbMicroAcConn;
    private Connection      m_dbOasisConn;
    private SecretKey       m_key;
    private final   String  m_BIN;
    private final   String  m_PAN;

    /** Creates new Profile */
    public MerchantProfile(String userID, ResultSet sqlProfileSet, ResultSet sqlCardholderSet, Connection microAcConn, Connection oasisConn)
        throws ProfileCreationException
    {
        m_userID = userID;
        m_dbMicroAcConn = microAcConn;
        m_dbOasisConn   = oasisConn;
        try
        {
            oracle.jdbc.OracleResultSet profileSet
                = (oracle.jdbc.OracleResultSet)sqlProfileSet;
           
            
            oracle.jdbc.OracleResultSet cardholderSet
                = (oracle.jdbc.OracleResultSet)sqlCardholderSet;
            
            if (!cardholderSet.next())
            {
                System.out.println("Merchant '" + m_userID
                    + "' not found in database.");
                throw new ProfileCreationException();
            }
            
            
            m_balance = sqlCardholderSet.getDouble("Open_to_Buy");//this could be CR_limit, they're the same amount
            
            m_BIN = profileSet.getString("BIN");
            m_PAN = profileSet.getString("PAN");
            
            oracle.sql.RAW rawData = profileSet.getRAW("SecretKey");
            byte[] keyData = rawData.getBytes();
            javax.crypto.spec.SecretKeySpec keySpec
                = new javax.crypto.spec.SecretKeySpec(keyData,
                c_Rsrcs.getString("acquirer.key.Algorithm"));
            m_key = keySpec;
            
            // Remove raw key data from memory
            for (int i = 0; i < keyData.length; i++)
            {
                keyData[i] = 0;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new ProfileCreationException(e.getLocalizedMessage());
        }
    }
    
    protected PreparedStatement toUpdateStatement(Connection dbConn,
        String tableName)
        throws SQLException
    {
        PreparedStatement statement = dbConn.prepareStatement(
            "UPDATE " + tableName + " SET Balance = ? WHERE UserID = ?");
        statement.setDouble(1, m_balance);
        statement.setString(2, m_userID);
        return statement;
    }

    
    public double getBalance()
    {
        return m_balance;
    }
    public void setBalance( double balance)
    {
        m_balance = balance;
    }
    public double credit( double amount ) throws AccountUnavailableException
    {
        m_balance += amount;
        
        try
        { 
            updateDatabase();
        }
        catch( AccountUnavailableException AUE )
        {
            m_balance -= amount;
            throw AUE;
        }
        
        return m_balance;
    }
    
    private void updateDatabase() throws AccountUnavailableException
    {
        /* (2001-08-28 RTG) This error reporting mechanism will have to be
         * rethought, since exceptions cannot be thrown from within threads.
         * It might not even matter, provided the thread logs the exceptions
         * that arise to an appropriate errors database.
         */
        if (false) { throw new AccountUnavailableException(); }
        Thread updateThread = new Thread()
        {
            public void run()
            {
                try
                {
                    PreparedStatement updateStatement = m_dbMicroAcConn.prepareStatement(
                        "UPDATE Profile SET Balance = ? WHERE UserID = ?");
                    updateStatement.setDouble(1, m_balance);
                    updateStatement.setString(2, m_userID);
                    updateStatement.executeUpdate();
                    m_dbMicroAcConn.commit();
            
                    updateStatement = m_dbOasisConn.prepareStatement(
                     "UPDATE Cardholders SET open_to_buy = ? , cr_limit = ? " + 
                     "WHERE PAN = ? ");
                    updateStatement.setDouble(1, m_balance );
                    updateStatement.setDouble(2, m_balance );
                    updateStatement.setString(3, m_PAN );
                    updateStatement.executeUpdate();
                    m_dbOasisConn.commit();
                }
                catch (java.sql.SQLException e)
                {
                    e.printStackTrace();
                    //throw new AccountUnavailableException( e.getLocalizedMessage() );
                }
                finally
                {
                }
            }
        };
        updateThread.start();
    }

    
    public String getMerchantID()
    {
        return m_userID;
    }
    public void setMerchantID(final String merchantID)
    {
        m_userID = merchantID;
    }

    
    public SecretKey getKey()
    {
        return m_key;
    }
    public void setKey(SecretKey key)
    {
        m_key = key;
    }
    
    public final String getBIN()
    {
        return m_BIN;
    }
    
    public final String getPAN()
    {
        return m_PAN;
    }
}

