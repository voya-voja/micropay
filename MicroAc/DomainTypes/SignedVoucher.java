/*
 * SignedVoucher.java
 *
 * Created on May 12, 2001, 3:56 PM
 */

package com.inexum.MicroAc.DomainTypes;

import com.inexum.MicroAc.Merchant.ITicket;
import com.inexum.MicroAc.Merchant.Product;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import java.io.Serializable;
import java.io.IOException;
import javax.crypto.IllegalBlockSizeException;

/**
 * @author  rgoold
 * @version 
 */
public class SignedVoucher extends SignedObject
    implements java.io.Serializable, ITicket
{
    
    /**
     * @param voucher
     * @param cipher
     * @exception IOException,IllegalBlockSizeException
     * @roseuid 3B1CF3850309
     */
    public SignedVoucher(Voucher voucher, Mac mac, Cipher cipher)
        throws IOException,IllegalBlockSizeException 
    {
        super(voucher, mac, cipher);
    }
    
    /**
     * @return The serialised voucher.
     * @exception IOException
     * @roseuid 3B1CF385031D
     */
    public Voucher getVoucher() throws IOException 
    {
        Voucher result = null;
        try
        {
            result = (Voucher)super.getObject();
        }
        catch (java.lang.ClassNotFoundException e)
        {
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        return result;
    }

    /* For debugging purposes. */
    public static void main (String[] args)
    {
        try
        {
            Product product = new Product();
            product.setOfferID("OfferID");
            product.setCost("1.23CAD");
            product.setDuration(5000);
            product.setLocation("/tmp/asdf.txt");
            Voucher voucher = new Voucher(product);
            Mac mac = Mac.getInstance("HmacSHA-1");
            Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
            byte[] keyBytes = new byte[16];
            javax.crypto.spec.SecretKeySpec key
                = new javax.crypto.spec.SecretKeySpec(keyBytes, "Blowfish");
            mac.init(key);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            SignedVoucher signedVoucher
                = new SignedVoucher(voucher, mac, cipher);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            javax.crypto.SealedObject sealedVoucher
                = new javax.crypto.SealedObject(signedVoucher, cipher);
            cipher.init(Cipher.DECRYPT_MODE, key);
            SignedVoucher signedVoucher2
                = (SignedVoucher) sealedVoucher.getObject(cipher);
            mac.init(key);
            cipher.init(Cipher.DECRYPT_MODE, key);
            if (signedVoucher2.verify(mac, cipher))
            {
                Voucher voucher2 = signedVoucher2.getVoucher();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
