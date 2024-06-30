/*
 * DummyTrustManager.java
 *
 * Created on November 15, 2001, 5:40 PM
 */

package com.inexum.MicroAc.TrustMgr;

import java.security.cert.X509Certificate;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class DummyTrustManager extends java.lang.Object
    implements com.sun.net.ssl.X509TrustManager
{

    /** Creates new DummyTrustManager */
    public DummyTrustManager()
    {
    }

    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[0];
    }
    
    public boolean isClientTrusted(X509Certificate[] x509Certificate)
    {
        return true;
    }
    
    public boolean isServerTrusted(X509Certificate[] x509Certificate)
    {
        return true;
    }
    
}

