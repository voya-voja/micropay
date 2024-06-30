/*
 * ConsumerListener.java
 *
 * Created on November 16, 2001, 12:43 PM
 */

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.Comm.SocketHandler;
import com.inexum.Comm.SocketListener;

import java.net.*;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class ConsumerListener extends SocketListener
{

    /** Creates new ConsumerListener */
    public ConsumerListener(int port)
        throws java.io.IOException
    {
        super(port);
    }

//----------------------------------------------------------------------------//

    protected SocketHandler createSocketHandler(Socket socket)
        throws java.io.IOException
    {
        return new SocketTransactionAgent(socket);
    }

}

