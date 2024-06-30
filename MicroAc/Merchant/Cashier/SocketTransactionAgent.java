/*
 * SocketTransactionAgent.java
 *
 * Created on November 16, 2001, 11:37 AM
 */

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.Comm.SocketHandler;
import com.inexum.MicroAc.Exceptions.*;
import com.inexum.IPC.ServiceRegistry;
import com.inexum.Encoding.CryptoAgentFactory;

import java.io.*;
import java.net.*;
import javax.crypto.SealedObject;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class SocketTransactionAgent extends SocketHandler
{

    /** Creates new SocketTransactionAgent */
    public SocketTransactionAgent(Socket socket)
    {
        super(socket);
    }

//----------------------------------------------------------------------------//

    public void run()
    {
        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        try
        {
            ObjectOutputStream objOut
                = new ObjectOutputStream(getOutputStream());
            ObjectInputStream objIn
                = new ObjectInputStream(getInputStream());
            PurchaseRequest request = (PurchaseRequest)objIn.readObject();
            IProductHandler handler = (IProductHandler)registry.
                getServiceForName("ProductHandler");
            PurchaseResponse response = null;
            try
            {
                Product product;
                String offerID = request.getOfferID();
                if(offerID.equals("_pos#"))
                    product = request.getProduct();
                else
                    product = handler.productFromOffer(offerID);
                
                ITicketAgent agent = TicketAgentFactory.AgentForProduct(
                    product.getClass());
                ITicket ticket = agent.createTicket(product);
                SealedObject sealedVoucher
                    = CryptoAgentFactory.GetCryptoAgent().seal(ticket);
                response = new PurchaseResponse(PurchaseResponse.c_Succeeded,
                    sealedVoucher, null);
            }
            catch (UnknownOfferException e)
            {
                response = new PurchaseResponse(PurchaseResponse.c_Failed, null,
                    e.getLocalizedMessage());
            }
            catch (MissingAgentException e)
            {
                response = new PurchaseResponse(PurchaseResponse.c_Failed, null,
                    e.getLocalizedMessage());
            }
            catch (VoucherCreationException e)
            {
                response = new PurchaseResponse(PurchaseResponse.c_Failed, null,
                    e.getLocalizedMessage());
            }
            objOut.writeObject(response);
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        catch (java.lang.ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (java.lang.ClassCastException e)
        {
            e.printStackTrace();
        }
        catch (java.lang.NullPointerException e)
        {
            e.printStackTrace();
        }
        finally
        {
            close();
        }
    }

}

