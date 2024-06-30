/*
 * WindowShoppingListener.java
 *
 * Created on June 12, 2001, 3:45 PM
 */

package com.inexum.MicroAc.Wallet;

import com.inexum.Types.Money;
import com.inexum.Comm.SocketHandler;
import com.inexum.Comm.SocketListener;
import com.inexum.MicroAc.PFL.MPBasicInfo;
import com.inexum.util.ConfigurationManager;

import java.net.*;
import java.io.*;

/**
 *
 * @author  nkapov
 * @version 1.0
 */
public class WindowShoppingListener extends SocketListener
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();
        

    /** Creates new WindowShoppingListener */
    public WindowShoppingListener()
        throws java.io.IOException
    {
        super(Integer.parseInt(c_Rsrcs.getString("net.WindowShoppingPort")));
    }

//----------------------------------------------------------------------------//

    protected SocketHandler createSocketHandler(Socket socket)
        throws java.io.IOException
    {
        return new WindowShoppingSocketHandler(socket);
    }

}
