/*
 * LinkListener.java
 *
 * Created on May 18, 2001, 11:04 AM
 */

package com.inexum.MicroAc.Wallet;

import com.inexum.Comm.SocketHandler;
import com.inexum.Comm.SocketListener;
import com.inexum.util.ConfigurationManager;

import java.io.*;
import java.net.*;


/** Waits listening for purchase "clicks"
 *
 * @author  Nick Kapov
 * @version 1.0
 */
public class LinkListener extends SocketListener
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();

//----------------------------------------------------------------------------//

    /** Creates new LinkListener */
    public LinkListener()
        throws java.io.IOException
    {
        super(Integer.parseInt(c_Rsrcs.getString("net.MicroProductPort")));
    }

//----------------------------------------------------------------------------//

    protected SocketHandler createSocketHandler(Socket socket)
        throws java.io.IOException
    {
        return new LinkSocketHandler(socket);
    }

//----------------------------------------------------------------------------//

    public static void main( String[] args )
    {
    }

}
