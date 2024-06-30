/*
 * FactorySigner.java
 *
 * Created on June 29, 2001, 1:27 PM
 */

package com.inexum.MicroAc.MicroProcessing.Crypto;

import com.inexum.MicroAc.MicroProcessing.Transaction.ISessionFactory;
import com.inexum.util.ConfigurationManager;

import java.security.PrivateKey;
import java.security.SignedObject;
import java.security.Signature;

/**
 *
 * @author  rgoold
 * @version 
 */
public class FactorySigner extends Object
{
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
    
    private PrivateKey  m_key;

    /** Creates new FactorySigner */
    public FactorySigner(PrivateKey key)
    {
        m_key = key;
    }
    
    // For debugging only
  /*  public FactorySigner()
    {
        try
        {
            java.security.KeyPairGenerator generator
                = java.security.KeyPairGenerator.getInstance(
                c_rsrcs.getString("key.Type"));
            generator.initialize(1024);
            java.security.KeyPair keyPair = generator.generateKeyPair();
            m_key = keyPair.getPrivate();
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }
    */
    public SignedObject signFactory(java.io.Serializable factory)
        throws java.security.SignatureException
    {
        if (m_key == null)
        {
            throw new java.security.SignatureException();
        }
        
        try
        {
            Signature signingEngine = Signature.getInstance(
                c_rsrcs.getString("key.Algorithm"));
            return new SignedObject(factory, m_key, signingEngine);
        }
        catch (java.io.IOException e)
        {
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (java.security.InvalidKeyException e)
        {
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
    }

}

