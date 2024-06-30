/*
 * WindowShoppingSocketHandler.java
 *
 * Created on November 23, 2001, 3:28 PM
 */

package com.inexum.MicroAc.Wallet;

import com.inexum.Types.Money;
import com.inexum.Comm.SocketHandler;
import com.inexum.IPC.ServiceRegistry;
import com.inexum.MicroAc.PFL.MPBasicInfo;

import java.io.*;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class WindowShoppingSocketHandler extends SocketHandler
{

    /** Creates new WindowShoppingSocketHandler */
    public WindowShoppingSocketHandler(java.net.Socket socket)
    {
        super(socket);
    }

//----------------------------------------------------------------------------//

    public void run()
    {
        getBasicInfo();
        close();
    }

//----------------------------------------------------------------------------//

    private void getBasicInfo()
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(getInputStream());

            MPBasicInfo mpBasicInfo = (MPBasicInfo)in.readObject();

            ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
            CAMAProxy proxy = (CAMAProxy)registry.
                getServiceForName("CAMAProxy");
            proxy.fireWalletBrowseEvent(
                new WalletBrowseEvent(
                    proxy.convertMerchantPrice(
                        new Money(mpBasicInfo.getPrice()),
                        new Money(mpBasicInfo.getXChngRate()) ),
                    mpBasicInfo.getLongDesc() ) );
        }
        catch ( OptionalDataException ODE )
        {
            System.out.println( ODE.getMessage() );
        }
        catch ( StreamCorruptedException SCE )
        {
            System.out.println( SCE.getMessage() );
        }
        catch( ClassNotFoundException CNFE )
        {
            System.out.println( CNFE.getMessage( ) );
        }
        catch ( IOException IOE )
        {
            System.out.println( IOE.getMessage() );
        }
    }

}

