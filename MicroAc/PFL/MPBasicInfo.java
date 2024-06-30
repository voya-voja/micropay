/*
 * MPBasicInfo.java
 *
 * Created on August 10, 2001, 11:25 AM
 */

package com.inexum.MicroAc.PFL;

/**
 *
 * @author  Administrator
 * @version 
 */
public class MPBasicInfo extends java.lang.Object
    implements java.io.Serializable
{
    private String   m_price;
    private String   m_buyID;
    private String   m_longDesc;
    private String   m_xChngRate;
    private String   m_merchantID;
    private String   m_mamaID;
    private int      m_duration;

    MPBasicInfo(MPLink parent)
    {
        m_price          = parent.getPrice();
        m_buyID          = parent.getBuyID();
        m_longDesc       = parent.getLongDesc();
        m_xChngRate      = parent.getExchangeRate();
        m_merchantID     = parent.getMerchantName();
        m_mamaID         = parent.getMAMAName();
        m_duration       = parent.getDuration();
    }

    public final String  getPrice()        { return m_price;      }
    public final String  getBuyID()        { return m_buyID;      }
    public final String  getLongDesc()     { return m_longDesc;   }
    public final String  getXChngRate()    { return m_xChngRate;  }
    public final String  getMerchantID()   { return m_merchantID; }
    public final String  getMAMAID()       { return m_mamaID;     }
    public final int     getDuration()     { return m_duration;   }

}
