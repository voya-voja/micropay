/*
 * PeerListener.java
 *
 * Created on November 8, 2001, 3:18 PM
 */

package com.inexum.MicroAc.Transaction;

import com.inexum.Comm.SocketHandler;
import com.inexum.Comm.SocketListener;
import com.inexum.Types.Money;

import java.net.Socket;

import javax.crypto.SealedObject;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class PeerListener extends SocketListener
{

    /** Creates new PeerListener */
    public PeerListener()
        throws java.io.IOException
    {
        super(12345);
    }

//----------------------------------------------------------------------------//

    protected SocketHandler createSocketHandler(Socket socket)
        throws java.io.IOException
    {
        return new PeerSocketHandler(socket);
    }

}

