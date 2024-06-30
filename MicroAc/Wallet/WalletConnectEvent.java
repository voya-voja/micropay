/*
 * WalletConnectEvent.java
 *
 * Created on July 17, 2001, 12:03 PM
 */

package com.inexum.MicroAc.Wallet;

/**
 *
 * @author  rgoold
 * @version 
 */
public class WalletConnectEvent extends WalletEvent
{
    private boolean     m_connected;

    /** Creates new WalletConnectEvent */
    public WalletConnectEvent (boolean connected)
    {
        m_connected = connected;
    }
    
    public final boolean isConnected ()
    {
        return m_connected;
    }

}

