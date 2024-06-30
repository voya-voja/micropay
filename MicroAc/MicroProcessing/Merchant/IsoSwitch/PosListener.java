/*
 * PosListener.java
 *
 * Created on May 18, 2001, 11:04 AM
 */

package com.inexum.MicroAc.MicroProcessing.Merchant.IsoSwitch;

import com.inexum.Comm.SocketHandler;
import com.inexum.Comm.SocketListener;
import com.inexum.util.ConfigurationManager;

import java.io.*;
import java.net.*;

/**
 *
 * @author  nvojinov
 */
public class PosListener extends SocketListener
{
    private static final ConfigurationManager c_Rsrcs = 
                                                ConfigurationManager.Instance();

//----------------------------------------------------------------------------//

    /** Creates new PosListener */
    public PosListener(int posPort)
        throws java.io.IOException
    {
        super(posPort);
    }

//----------------------------------------------------------------------------//

    protected SocketHandler createSocketHandler(Socket socket)
        throws java.io.IOException
    {
        return new PosSocketHandler(socket);
    }
}
