/*
 * CryptoAgentFactory.java
 *
 * Created on June 6, 2001, 11:14 AM
 */

package com.inexum.Encoding;

import com.inexum.util.ConfigurationManager;

import javax.crypto.SecretKey;

/**
 *
 * @author  rgoold
 * @version 
 */
public abstract class CryptoAgentFactory extends java.lang.Object
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();
    
    private static SecretKey    m_Key;
    
    public static SecretKey GetKey()
    {
        return m_Key;
    }

    public static void InitWithKey (SecretKey key)
    {
        m_Key = key;
    }
    
    public static void InitWithKeyFile (java.io.File keyFile, int keyLength)
        throws java.security.InvalidKeyException,
        java.io.FileNotFoundException, java.io.IOException
    {
        java.io.FileInputStream fileIn
            = new java.io.FileInputStream(keyFile);
        if (fileIn.available() < (keyLength / 8))
        {
            throw new java.security.InvalidKeyException();
        }
        // Length is in bits.
        byte[] keyData = new byte[keyLength / 8];
        if (fileIn.read(keyData) < keyData.length)
        {
            throw new java.io.IOException();
        }
        fileIn.close();
        
        m_Key = new javax.crypto.spec.SecretKeySpec(keyData,
            c_Rsrcs.getString("acquirer.key.Algorithm"));
    }
    
    public static CryptoAgent GetCryptoAgent()
    {
        return new CryptoAgent(m_Key);
    }

}

