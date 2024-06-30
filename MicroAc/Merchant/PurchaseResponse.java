/*
 * PurchaseResponse.java
 *
 * Created on November 16, 2001, 12:29 PM
 */

package com.inexum.MicroAc.Merchant;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class PurchaseResponse extends java.lang.Object
    implements java.io.Serializable
{
    public static final int c_Succeeded = 0;
    public static final int c_Failed    = 1;

    private int     m_result;
    private Object  m_data;
    private String  m_message;

    private static final long serialVersionUID = 5601960243957729721l;

    /** Creates new PurchaseResponse */
    public PurchaseResponse(int result, java.io.Serializable data,
        String message)
    {
        m_result = result;
        m_data = data;
        m_message = message;
    }

    public int getResult()
    {
        return m_result;
    }

    public Object getData()
    {
        return m_data;
    }

    public String getMessage()
    {
        return m_message;
    }

}

