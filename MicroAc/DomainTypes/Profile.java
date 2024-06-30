/*
 * Profile.java
 *
 * Created on June 6, 2001, 7:54 PM
 */

package com.inexum.MicroAc.DomainTypes;

/**
 *
 * @author  nkapov
 * @version 
 */
public class Profile extends java.lang.Object
    implements IProfile
{
    protected double    m_balance = 0.0;

    /** Creates new Profile */
    public Profile()
    {
    }

    public double getBalance()
    {
        return m_balance;
    }
  
    
}
