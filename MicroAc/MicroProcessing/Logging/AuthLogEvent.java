//Source file: C:\Development\com\inexum\Nickel\eMoneyAdmin\Logging\AuthLogEvent.java

package com.inexum.MicroAc.MicroProcessing.Logging;

import com.inexum.MicroAc.Consumer.LoginInfo;


public class AuthLogEvent extends LogEvent 
{
    public static final String  c_LoggedIn              = "LoggedIn";
    public static final String  c_InvalidPassword       = "InvalidPassword";
    public static final String  c_InvalidUser           = "InvalidUser";
    public static final String  c_InvalidWallet         = "InvalidWallet";
    public static final String  c_LoggedOut             = "LoggedOut";
    public static final String  c_MultipleLoginAttempt  = "MultipleLoginAttempt";   
    
    private final LoginInfo m_loginInfo;
    
    
    public AuthLogEvent(Object source, LoginInfo loginInfo, String eventType ) 
    {
        super( source, eventType );
        m_loginInfo = loginInfo;
        
    }
    
    public final LoginInfo getLoginInfo()
    {
        return m_loginInfo;
    }
    
}
