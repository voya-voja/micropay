/***** Copyright 2001 iNexum Systems Inc. *****************************
**
**      File: Base64.java
**
**      Description:
**          Implements a Base 64 encoder/decoder using
**          using static methods.
**
**      $Revision: 1.1.1.1 $
**      Revision History:
**              2001-05-09 (RTG) Initial Release.
**
**********************************************************************/


package com.inexum.Encoding;

import com.inexum.util.ConfigurationManager;

/**
 *
 * @author  rgoold
 * @version 
 */
public class Base64 {
    private static final ConfigurationManager c_Msgs = ConfigurationManager.Instance();
    
    private static final byte[] m_encodingMap = {
        (byte)'A', (byte)'B', (byte)'C', (byte)'D',
        (byte)'E', (byte)'F', (byte)'G', (byte)'H',
        (byte)'I', (byte)'J', (byte)'K', (byte)'L',
        (byte)'M', (byte)'N', (byte)'O', (byte)'P',
        (byte)'Q', (byte)'R', (byte)'S', (byte)'T',
        (byte)'U', (byte)'V', (byte)'W', (byte)'X',
        (byte)'Y', (byte)'Z', (byte)'a', (byte)'b',
        (byte)'c', (byte)'d', (byte)'e', (byte)'f',
        (byte)'g', (byte)'h', (byte)'i', (byte)'j',
        (byte)'k', (byte)'l', (byte)'m', (byte)'n',
        (byte)'o', (byte)'p', (byte)'q', (byte)'r',
        (byte)'s', (byte)'t', (byte)'u', (byte)'v',
        (byte)'w', (byte)'x', (byte)'y', (byte)'z',
        (byte)'0', (byte)'1', (byte)'2', (byte)'3',
        (byte)'4', (byte)'5', (byte)'6', (byte)'7',
        (byte)'8', (byte)'9', (byte)'+', (byte)'/'
    };
    private static byte[] m_decodingMap;
    
    // Static (class) initialiser
    static
    {
        // Create the reverse map. Half the entries in the decoding map will
        // be unused, but it's still more efficient than other solutions.
        m_decodingMap = new byte[128];
        for (int i = 0; i < m_encodingMap.length; i++)
        {
            m_decodingMap[m_encodingMap[i]] = (byte)i;
        }
    }
    
    public static byte[] Encode (java.io.Serializable obj)
        throws java.io.IOException
    {
        java.io.ByteArrayOutputStream byteOut
            = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream objectOut
            = new java.io.ObjectOutputStream(byteOut);
        objectOut.writeObject(obj);
        objectOut.flush();
        return Encode(byteOut.toByteArray());
    }
    
    public static Object DecodeObject (byte[] data)
        throws java.io.IOException
    {
        try
        {
            java.io.ByteArrayInputStream byteIn
                = new java.io.ByteArrayInputStream(Decode(data));
            java.io.ObjectInputStream objectIn
                = new java.io.ObjectInputStream(byteIn);
            return objectIn.readObject();
        }
        catch (java.text.ParseException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (java.io.StreamCorruptedException e)
        {
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (java.io.OptionalDataException e)
        {
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassNotFoundException e)
        {
            throw new java.io.IOException(e.getLocalizedMessage());
        }
    }
    
    public static byte[] Encode (byte[] data)
    {
        final int slack = data.length % 3;
        int outLength;
        int numBlocks;
        // Determine the number of WHOLE blocks. We will use slack
        // to process an incomplete block at the end.
        if (slack > 0)
        {
            numBlocks = (data.length - slack) / 3;
            outLength = (numBlocks + 1) * 4;
        }
        else
        {
            numBlocks = data.length / 3;
            outLength = numBlocks * 4;
        }
        byte[] encoding = new byte[outLength];
        for (int i = 0; i < numBlocks; i++)
        {
            /* Watch out! Bytes get promoted to ints when doing bitshifts in
             * Java, which means 0xFC becomes 0xFFFFFFFC, and 0xFC >>> 2 becomes
             * (when cast to a byte) 0xFF instead of 0x3F! The solution is to
             * do (number) & 0x3F at the end to force an unsigned byte bitshift.
             */
            
            // abcdefgh -> __abcdef [gh] == __abcdef
            encoding[i * 4]
                = m_encodingMap[(data[i*3] & 0xFF) >> 2];
            
            // abcdefgh -> __gh____, ijklmnop -> ____ijkl [mnop] == __ghijkl
            encoding[i * 4 + 1]
                = m_encodingMap[((data[i*3] & 0x03) << 4)
                    | ((data[i*3+1] & 0xFF) >> 4)];
            
            // ijklmnop -> __mnop__, qrstuvwx -> ______qr [stuvwx] == __mnopqr
            encoding[i * 4 + 2]
                = m_encodingMap[((data[i*3+1] & 0x0F) << 2)
                    + ((data[i*3+2] & 0xCF) >> 6)];
            
            // qrstuvwx -> __stuvwx
            encoding[i * 4 + 3]
                = m_encodingMap[data[i*3+2] & 0x3F];
        }
        // Handle trailing characters
        if (slack == 1)
        {
            // Just convert one character (abcdefgh -> __abcdef, __gh____)
            encoding[numBlocks * 4]
                = m_encodingMap[(data[numBlocks * 3] & 0xFF) >> 2];
            encoding[numBlocks * 4 + 1]
                = m_encodingMap[(data[numBlocks * 3] & 0x03) << 4];
            encoding[numBlocks * 4 + 2] = (byte)'=';
            encoding[numBlocks * 4 + 3] = (byte)'=';
        }
        else if (slack == 2)
        {
            // Just convert two characters (abcdefgh, ijklmnop -> __abcdef,
            // __ghijkl, __mnop__)
            encoding[numBlocks * 4]
                = m_encodingMap[(data[numBlocks * 3] & 0xFF) >> 2];
            encoding[numBlocks * 4 + 1]
                = m_encodingMap[((data[numBlocks * 3] & 0x03) << 4)
                    | ((data[numBlocks * 3 + 1] & 0xF0) >>> 4)];
            encoding[numBlocks * 4 + 2]
                = m_encodingMap[(data[numBlocks * 3 + 1] & 0x0F) << 2];
            encoding[numBlocks * 4 + 3] = (byte)'=';
        }
        
        return encoding;
    }
    
    public static byte[] Decode (byte[] data)
        throws java.text.ParseException
    {
        if ((data.length & 0x03) != 0)
        {
            throw new java.text.ParseException(
                c_Msgs.getString("NotBlockSizeMultiple"), data.length);
        }
        int numBlocks = data.length / 4;
        int outSize;
        if (data[data.length - 2] == '=')
        {
            outSize = numBlocks-- * 3 - 2;
        }
        else if (data[data.length - 1] == '=')
        {
            outSize = numBlocks-- * 3 - 1;
        }
        else
        {
            outSize = numBlocks * 3;
        }
        byte[] decoding = new byte[outSize];
        for (int i = 0; i < numBlocks; i++)
        {
            decoding[i * 3]
                = (byte)((m_decodingMap[data[i * 4]] << 2)
                    | (m_decodingMap[data[i * 4 + 1]] >>> 4));
            decoding[i * 3 + 1]
                = (byte)((m_decodingMap[data[i * 4 + 1]] << 4)
                    | (m_decodingMap[data[i * 4 + 2]] >>> 2));
            decoding[i * 3 + 2]
                = (byte)((m_decodingMap[data[i * 4 + 2]] << 6)
                    | m_decodingMap[data[i * 4 + 3]]);
        }
        // Handle incomplete (equals-padded) block at the end
        if (outSize % 3 == 1)
        {
            decoding[outSize - 1]
                = (byte)((m_decodingMap[data[data.length - 4]] << 2)
                    | (m_decodingMap[data[data.length - 3]] >>> 4));
        }
        else if (outSize % 3 == 2)
        {
            decoding[outSize - 2]
                = (byte)((m_decodingMap[data[data.length - 4]] << 2)
                    | (m_decodingMap[data[data.length - 3]] >>> 4));
            decoding[outSize - 1]
                = (byte)((m_decodingMap[data[data.length - 3]] << 4)
                    | (m_decodingMap[data[data.length - 2]] >>> 2));
        }
        
        return decoding;
    }

}

