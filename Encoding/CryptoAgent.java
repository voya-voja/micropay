/***** Copyright 2001 iNexum Systems Inc. *****************************
**
**      File: CryptoAgent.java
**
**      Description:
**          Supplies cryptographic and message digest services.
**
**      Revision History:
**              2001-05-10 (RTG) Initial revision.
**
**********************************************************************/


package com.inexum.Encoding;

import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.MicroAc.DomainTypes.SignedObject;
import com.inexum.MicroAc.DomainTypes.Voucher;
import com.inexum.util.ConfigurationManager;

import java.io.*;
import java.security.*;
import javax.crypto.*;
//import javax.crypto.spec.*;


/** Handles all cryptographic matters.
 * @author  rgoold
 * @version 
 */
public class CryptoAgent extends java.lang.Object
{
    
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
    
/** The name of the encryption/decryption cipher to use.
 */    
    private final String    c_cipherAlgorithm
        = c_rsrcs.getString("algorithm.Cipher");
/** The name of the MAC algorithm to use.
 */    
    private final String    c_macAlgorithm
        = c_rsrcs.getString("algorithm.MAC");
/** The name of the random number generator algorithm to use.
 */    
    private final String    c_randAlgorithm
        = c_rsrcs.getString("algorithm.RNG");
/** The merchant's secret key.
 */    
    private SecretKey       m_key;
    
    public CryptoAgent(SecretKey key)
    {
        m_key = key;
    }
    
    public SealedObject seal (java.io.Serializable object)
        throws java.io.IOException
    {
        try
        {
            Cipher cipher = Cipher.getInstance(c_cipherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, m_key);
            return new SealedObject(object, cipher);
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (javax.crypto.NoSuchPaddingException e)
        {
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (java.security.InvalidKeyException e)
        {
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (javax.crypto.IllegalBlockSizeException e)
        {
            throw new java.io.IOException(e.getLocalizedMessage());
        }
    }
    
    /* (2001-06-18 RTG) It is unclear how to distinguish between
     * exceptions caused because the wrong key was used and
     * exceptions indicating an actual error. The only likely
     * candidate thrown by SealedObject.getObject() is a
     * java.io.IOException, but this can mean other errors too.
     */
    public SignedVoucher unseal (SealedObject sealedObject)
        throws java.io.IOException
    {
        try
        {
            Cipher cipher = Cipher.getInstance(c_cipherAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, m_key);
            return (SignedVoucher)sealedObject.getObject(cipher);
        }
        catch (java.lang.ClassCastException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (javax.crypto.NoSuchPaddingException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (java.security.InvalidKeyException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (javax.crypto.IllegalBlockSizeException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (javax.crypto.BadPaddingException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
    }
    
/** Generates a unique, 8-byte sequence.
 * @return An array of 8 bytes.
 */    
    public byte[] uniquePhrase()
        throws Exception
    {
        /* Initialise to null to prevent stupid compiler warnings about it
         * possibly being uninitialised (due to initialisation within the
         * try/catch block).
         */
        SecureRandom randomNumberGenerator = null;
        try
        {
            randomNumberGenerator = SecureRandom.getInstance(c_randAlgorithm);
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            System.err.println(c_rsrcs.getString("NoSuchAlgorithm")
                + ": " + e.getLocalizedMessage());
            throw new Exception(e.getLocalizedMessage());
        }
        byte[] randomPhrase = new byte[8];
        randomNumberGenerator.nextBytes(randomPhrase);
        return randomPhrase;
    }
    
/** Creates a checksum for an array of bytes.
 * @param inData The data for which to generate the checksum.
 * @return A checksum.
 */    
    public SignedVoucher sign (Voucher voucher)
        throws Exception
    {
        Mac mac = Mac.getInstance(c_macAlgorithm);
        mac.init(m_key);
        Cipher cipher = Cipher.getInstance(c_cipherAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, m_key);
        return new SignedVoucher(voucher, mac, cipher);
    }
    
    public boolean verify (SignedObject signedObject)
        throws java.security.SignatureException
    {
        return signedObject.verify(m_key);
    }
    
/** Checks the signature on a chunk of data.
 * @param inData The data to check against.
 * @param signature The signature to examine.
 * @return Whether the signature is valid.
 */    
    public boolean verify (byte[] inData, byte[] signature)
        throws Exception
    {
        boolean result = false;
        try
        {
            Cipher cipher = Cipher.getInstance(c_cipherAlgorithm);
            Mac mac = Mac.getInstance(c_macAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, m_key);
            byte[] decryptedDigest = cipher.doFinal(signature);
            mac.init(m_key);
            byte[] actualDigest = mac.doFinal(inData);
            result = java.util.Arrays.equals(decryptedDigest, actualDigest);
        }
        // The process failure exceptions (internal errors)
        catch (java.util.MissingResourceException e)
        {
            System.err.println(c_rsrcs.getString("MissingResourceError")
                + ": " + e.getLocalizedMessage());
            throw new Exception(e.getLocalizedMessage());
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            throw new Exception(e.getLocalizedMessage());
        }
        catch (javax.crypto.NoSuchPaddingException e)
        {
            throw new Exception(e.getLocalizedMessage());
        }
        catch (java.security.InvalidKeyException e)
        {
            throw new Exception(e.getLocalizedMessage());
        }
        catch (java.lang.IllegalStateException e)
        {
            throw new Exception(e.getLocalizedMessage());
        }
        catch (javax.crypto.IllegalBlockSizeException e)
        {
            throw new Exception(e.getLocalizedMessage());
        }
        // The verification failure exceptions (bad signatures)
        catch (javax.crypto.BadPaddingException e)
        {
            e.printStackTrace();
            return false;
        }
        return result;
    }
    
/** For debugging purposes only.
 * @param args Command line arguments.
 */

    public static void main (String[] args)
    {
        // Provide CryptoAgents with a key to use.
        final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
        java.io.File keyFile = new java.io.File( 
                                c_rsrcs.getURL("merchant.key.store.Path").getFile() );
        int keyLength = Integer.parseInt(c_rsrcs.getString("acquirer.key.Length"), 10);
        try
        {
            CryptoAgentFactory.InitWithKeyFile(keyFile, keyLength);
            CryptoAgent cra = CryptoAgentFactory.GetCryptoAgent();
            Voucher voucher = new Voucher();
            System.out.println("Signing ..." );
            cra.sign(voucher);
            System.out.println("Signed!!!" );
        }
        catch (java.security.InvalidKeyException e)
        {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        catch (java.io.FileNotFoundException e)
        {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        catch (java.lang.Exception e)
        {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}

