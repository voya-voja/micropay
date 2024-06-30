//
//  LoginResult.java
//  work
//
//  Created by rgoold on Tue Oct 23 2001.
//  Copyright (c) 2001 iNexum Systems Inc. All rights reserved.
//

package com.inexum.MicroAc.Consumer;


public final class LoginResult extends java.lang.Object
    implements java.io.Serializable
{
    private boolean     m_succeeded;
    private ProfileInfo m_profileInfo;
    private Exception   m_exception;
    
    public LoginResult (boolean succeeded, ProfileInfo profileInfo,
        Exception exception)
    {
        m_succeeded = succeeded;
        m_profileInfo = profileInfo;
        m_exception = exception;
    }
    
    public boolean succeeded ()
    {
        return m_succeeded;
    }
    
    public ProfileInfo getProfileInfo ()
    {
        return m_profileInfo;
    }
    
    public Exception getException ()
    {
        return m_exception;
    }
    
    public void throwException ()
        throws Exception
    {
        throw m_exception;
    }
}
