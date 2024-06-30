/*
 * LoginInfo.java
 *
 * Created on June 27, 2001, 9:54 AM
 */

package com.inexum.MicroAc.Consumer;

/**
 *
 * @author  nkapov
 * @version 
 */
public class LoginInfo extends java.lang.Object implements java.io.Serializable 
{
    private final java.lang.String m_userID;
    private final java.lang.String m_password;
    private final java.lang.String m_walletID;
    
    private java.lang.String        m_profileID;
    
    
    /** Creates new LoginInfo */
    public LoginInfo( java.lang.String userID, 
                      java.lang.String passwd,
                      java.lang.String walletID ) 
    {
        m_userID = userID;
        m_password = passwd;
        m_walletID = walletID;
    }
    
    
    public final java.lang.String getUserID()
    {
        return m_userID;
    }

    public final java.lang.String getPassword()
    {
        return m_password;
    }
    
    public final java.lang.String getWalletID()
    {
        return m_walletID;
    }
    
    public final java.lang.String getProfileID()
    {
        return m_profileID;
    }
    
    public void setProfileID( final java.lang.String profileID )
    {
        m_profileID = profileID;
    }
        
    
}
