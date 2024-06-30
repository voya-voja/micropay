/*
 * EBanking.java
 *
 * Created on June 1, 2003, 10:27 AM
 */

package com.inexum.MicroAc.MicroProcessing.eBanking;

import java.io.*;
import java.lang.*;
import java.sql.*;
import javax.sql.*;
import oracle.jdbc.driver.*;
import oracle.jdbc.pool.*;  
import com.inexum.Database.*;
import com.inexum.util.*;
import com.inexum.MicroAc.MicroProcessing.Consumer.Authentication.*;
import com.inexum.MicroAc.Consumer.*;
import com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager.*;
import com.inexum.Types.Money;
import com.inexum.util.ConfigurationManager;

/**
 *
 * @author  inexum
 */
public class EBanking 
{
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
    
    private Connection mDbConn;
    private ProfileProxy mProfile;
    private String mUserId;
    private String mPassword;
    
    /** Creates a new instance of EBanking */
    public EBanking( String userId, String password ) 
    {
        mUserId = userId;
        mPassword = password;
        try
        {
            DatabaseConPool dbPool = DatabaseConPool.getDatabaseConPool();
            mDbConn = dbPool.getConnection( c_rsrcs.getString("db.UserID"),
                                            c_rsrcs.getString("db.Password"));
            mDbConn.setAutoCommit( false );

            Authenticator auth =  new Authenticator( mDbConn );
            LoginInfo li = new LoginInfo( userId, password,
                                            c_rsrcs.getString( 
                                                "issuer.WalletSignature" ) );
            mProfile = auth.authenticate(li);
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
    
    public String userName()
    {
        return( mUserId );
    }
    
    public boolean isLogged()
    {
        return( mProfile != null );
    }
    
    public ResultSet balances() 
    {
        try
        {
            PreparedStatement stmt = mDbConn.prepareStatement(
                    "select pan, open_to_buy,cr_limit from cardholders where id_nbr=" + 
                    "(select id_nbr from clients where surname= ?)");
            stmt.setString( 1, mUserId );
            return( (OracleResultSet)stmt.executeQuery() );
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }
        return( null );
    }
    
    public ResultSet statament(int account) 
    {
        return( null );
    }
    
    public void transfer(int from, int to, Money amount) 
    {
    }
}
