/*
 * IsoSwitch.java
 *
 * Created on May 7, 2001, 10:11 AM
 */

package com.inexum.MicroAc.MicroProcessing.Merchant.IsoSwitch;

import com.inexum.MicroAc.Transaction.TransactionQueue;
import com.inexum.util.ConfigurationManager;
import com.inexum.MicroAc.Wallet.CAMAProxy;
import com.inexum.IPC.ServiceRegistry;

public class IsoSwitch
{
    private static final ConfigurationManager c_Rsrcs = 
                                                ConfigurationManager.Instance();

    private CAMAProxy           m_CAMAProxy;
    
/** Tracks pending micropayment transactions. */
    private TransactionQueue    m_transactionQueue;

/** Listens for incoming requests from microproduct links. */
    private PosListener        mPosListener;

    public IsoSwitch()
    {
        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        registry.setServiceForName(null, "Wallet");
        
        m_CAMAProxy = new CAMAProxy();
        registry.setServiceForName(m_CAMAProxy, "CAMAProxy");

        m_transactionQueue = TransactionQueue.getDefaultInstance();
        
        try
        {
            int posPort = 14001;
            try
            {
                String port = c_Rsrcs.getString("acquirer.pos.port");
                posPort = Integer.parseInt(port);
            }
            catch(java.util.MissingResourceException e)
            {
                System.out.println("No property 'acquirer.pos.port',"
                                    + "using default port '" + posPort + "'");
            }
            mPosListener = new PosListener(posPort);
            mPosListener.start();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main (String[] args)
    {
        IsoSwitch isoSwitch = new IsoSwitch();
        System.out.println("ISO Switch up!");
    }
}

