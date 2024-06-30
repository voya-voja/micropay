/*
 * MPLink.java
 *
 * Created on May 18, 2001, 1:47 PM
 */

package com.inexum.MicroAc.PFL;

import java.util.*;

/** Represents the information encoded in the W3C specification for a 
 * microproduct link
 *
 * @author  rgoold
 * @version 
 */

public final class MPLink extends java.lang.Object 
    implements java.io.Serializable 
{

    /** Creates new MPLink */
// nmv - gandalf    protected MPLink()
    public MPLink()
    {
    }

    // Accessor methods
    public final String   getPrice()          { return m_price;           }
    public final String   getTextLink()       { return m_textLink;        }
    public final String   getRequestURL()     { return m_requestURL;      }
    public final List     getPaymentSystems() { return m_paymentSystems;  }
    public final String   getTitle()          { return m_title;           }
    public final String   getImageLink()      { return m_imageLink;       }
    public final String   getBuyID()          { return m_buyID;           }
    public final String   getBaseURL()        { return m_baseURL;         }
    public final String   getLongDesc()       { return m_longDesc;        }
    public final String   getMerchantName()   { return m_merchantName;    }
    public final int      getDuration()       { return m_duration;        }
    public final String   getExpiration()     { return m_expiration;      }
    public final String   getTarget()         { return m_target;          }
    public final String   getHREFLang()       { return m_hrefLang;        }
    public final String   getType()           { return m_type;            }
    public final String   getAccessKey()      { return m_accessKey;       }
    public final String   getCharSet()        { return m_charset;         }
    public final String   getExtData()        { return m_extData;         }
    public final String   getExtDataParam()   { return m_extDataParam;    }
    public final String   getMPRMI()          { return m_mpRMI;           }
    public final String   getMAMAName()       { return m_mamaName;        }
    public final String   getExchangeRate()   { return m_xChngRate;       }

    public final MPBasicInfo getBasicInfo()
    {
        return new MPBasicInfo(this);
    }



    public void setPrice( String price ){
        m_price = price;   
    }

    public void setTextLink( String textLink ){
        m_textLink = textLink;      
    }

    public void setRequestURL( String requestURL ){
        m_requestURL = requestURL;  
    }

    public void  setPaymentSystems( List paymentSystems ){
        m_paymentSystems = paymentSystems;  
    }

    public void   setTitle( String title ){
        m_title = title;           
    }

    public void   setImageLink( String imageLink ){
        m_imageLink = imageLink;
    }

    public void   setBuyID( String buyID ){
        m_buyID = buyID;
    }

    public void   setBaseURL( String baseURL ){
        m_baseURL = baseURL; 
    }

    public void   setLongDesc( String longDesc ){
        m_longDesc = longDesc;
    }

    public void   setMerchantName( String merchantName ){
        m_merchantName =  merchantName;
    }

    public void   setDuration( int duration ){
        m_duration = duration;
    }

    public void   setExpiration( String expiration){
        m_expiration = expiration;
    }

    public void   setTarget( String target ){
        m_target = target;
    }

    public void   setHREFLang( String hrefLang ){
        m_hrefLang = hrefLang;
    }

    public void   setType( String type ){
        m_type = type;
    }

    public void   setAccessKey( String accessKey ){
        m_accessKey =  accessKey;
    }

    public void   setCharSet( String charSet ){
        m_charset = charSet;
    }

    public void   setExtData( String extData ){
        m_extData = extData;
    }

    public void   setExtDataParam( String extDataParam ){
        m_extDataParam = extDataParam;
    }

    public void setMPRMIParam(String mpRMI ){
        m_mpRMI = mpRMI;
    }

    public void setMAMAName(String name){
        m_mamaName = name;
    }

    public void setXChngRate( String xChngRate ){
        m_xChngRate = xChngRate;
    }


    /**Private members - Refer to "Common Markup for micropaymnet per-fee-
     * links" (W3C Document) - for defintions of members 
     * Required fields (MUST be provided)
     */
    private String   m_price;
    private String   m_textLink;
    private String   m_requestURL;
    private List     m_paymentSystems;

    // Recommended fields (SHOULD be provided)
    private String   m_title;

    // Optional fields (MAY be provided)
    private String   m_imageLink;
    private String   m_buyID;
    private String   m_baseURL;
    private String   m_longDesc;
    private String   m_merchantName;
    private int      m_duration;
    private String   m_expiration;
    private String   m_target;
    private String   m_hrefLang;
    private String   m_type;
    private String   m_accessKey;
    private String   m_charset;
    private String   m_extData;
    private String   m_extDataParam;


    /**The location of the  CAMA product provider agent*/
    private String   m_mpRMI;

    /**The name of the MAMA*/
    private String   m_mamaName;

    /** Exchange rate from native $ to base $(USD) */ 
    private String   m_xChngRate;

}

