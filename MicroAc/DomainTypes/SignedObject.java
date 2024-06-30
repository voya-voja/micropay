//Source file: C:\Development\com\inexum\Nickel\Voucher\SignedObject.java

package com.inexum.MicroAc.DomainTypes;

import com.inexum.Encoding.Base64;
import com.inexum.util.ConfigurationManager;

import java.io.Serializable;
import java.io.IOException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Cipher;
import javax.crypto.Mac;

/**
 * @author 
 */
public class SignedObject extends Object
    implements java.io.Serializable
{
    
    /**
     *  The serialised form of the object being signed.
     * @author 
     */
    protected byte m_object[];
    
    /**
     * The signature for the SignedObject.
     * @author 
     */
    protected byte m_signature[];
    
    /**
     * The name of the algorithm used to sign the object.
     * @author 
     */
    protected String m_macAlgorithm;
    protected String m_cipherAlgorithm;
    
    /**
     * Creates a new SignedObject from a specified object and a cipher.
     * @param object
     * @param cipher
     * @exception IOException,IllegalBlockSizeException
     * @roseuid 3B1C0546035C
     */
    public SignedObject(java.io.Serializable object, Mac mac, Cipher cipher)
        throws IOException,IllegalBlockSizeException 
    {
        if (cipher.getAlgorithm() == null)
        {
            throw new IOException(
                ConfigurationManager.Instance().getString("CipherNotInitialisedError"));
        }
        java.io.ByteArrayOutputStream byteOut
            = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream objectOut
            = new java.io.ObjectOutputStream(byteOut);
        objectOut.writeObject(object);
        objectOut.flush();
        m_object = byteOut.toByteArray();
        m_macAlgorithm = mac.getAlgorithm();
        m_cipherAlgorithm = cipher.getAlgorithm();
        try
        {
            // Encrypt the one-way hash for the object.
            m_signature = cipher.doFinal(mac.doFinal(m_object));
        }
        catch (javax.crypto.BadPaddingException e)
        {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    /**
     * Creates a SignedObject from another SignedObject.
     * @param signedObject
     * @exception 
     * @roseuid 3B1C06EA006F
     */
    protected SignedObject(SignedObject signedObject) 
    {
        m_object = new byte[signedObject.m_object.length];
        m_signature = new byte [signedObject.m_signature.length];
        m_macAlgorithm = new String(signedObject.m_macAlgorithm);
        m_cipherAlgorithm = new String(signedObject.m_cipherAlgorithm);
        for (int i = 0; i < m_object.length; i++)
        {
            m_object[i] = signedObject.m_object[i];
        }
        for (int i = 0; i < m_signature.length; i++)
        {
            m_signature[i] = signedObject.m_signature[i];
        }
    }
    
    public java.lang.String getMacAlgorithm()
    {
        return m_macAlgorithm;
    }
    
    /**
     * Returns the name of the algorithm used to generate the signature.
     * @return java.lang.String
     * @exception 
     * @roseuid 3B1C073602C9
     */
    public java.lang.String getCipherAlgorithm() 
    {
        return m_cipherAlgorithm;
    }
    
    /**
     * Returns the SignedObject's signature.
     * @return byte[]
     * @exception 
     * @roseuid 3B1C076D019D
     */
    public byte[] getSignature() 
    {
        return m_signature;
    }
    
    /**
     * Verifies the SignedObject's signature.
     * @param cipher
     * @return boolean
     * @exception SignatureException
     * @roseuid 3B1C08140268
     */
    public boolean verify(javax.crypto.Mac mac, javax.crypto.Cipher cipher)
        throws java.security.SignatureException 
    {
        if (m_object == null || m_signature == null
            || !mac.getAlgorithm().equals(m_macAlgorithm))
        {
            return false;
        }
        try
        {
            return java.util.Arrays.equals(mac.doFinal(m_object),
                cipher.doFinal(m_signature));
        }
        catch (javax.crypto.IllegalBlockSizeException e)
        {
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (javax.crypto.BadPaddingException e)
        {
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
    }
    
    public boolean verify(javax.crypto.SecretKey key)
        throws java.security.SignatureException
    {
        try
        {
            javax.crypto.Mac mac
                = javax.crypto.Mac.getInstance(m_macAlgorithm);
            //javax.crypto.Cipher cipher
            //    = javax.crypto.Cipher.getInstance(key.getAlgorithm());
            javax.crypto.Cipher cipher
                = javax.crypto.Cipher.getInstance(m_cipherAlgorithm);
            mac.init(key);
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
            return verify(mac, cipher);
        }
        catch (java.security.InvalidKeyException e)
        {
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (javax.crypto.NoSuchPaddingException e)
        {
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
    }
    
    /**
     * @return Object
     * @exception IOException,ClassNotFoundException
     * @roseuid 3B1CF37B02DD
     */
    public Object getObject() throws IOException,ClassNotFoundException 
    {
        java.io.ByteArrayInputStream byteIn
            = new java.io.ByteArrayInputStream(m_object);
        java.io.ObjectInputStream objectIn
            = new java.io.ObjectInputStream(byteIn);
        return objectIn.readObject();
    }
}
