/*
 * LinkSocketHandler.java
 *
 * Created on November 20, 2001, 1:56 PM
 */

package com.inexum.MicroAc.Wallet;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.Encoding.Base64;
import com.inexum.Comm.SocketHandler;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.Types.Money;
import com.inexum.MicroAc.Merchant.PurchaseRequest;
import com.inexum.MicroAc.Merchant.PurchaseResponse;
import com.inexum.MicroAc.PFL.MPLink;
import com.inexum.MicroAc.Transaction.TransactionOffer;
import com.inexum.IPC.ServiceRegistry;
import com.inexum.MicroAc.Transaction.ITransactionCompletionListener;
import com.inexum.MicroAc.Transaction.TransactionQueue;
import com.inexum.util.ConfigurationManager;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import javax.crypto.SealedObject;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class LinkSocketHandler extends SocketHandler
    implements ITransactionCompletionListener
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();

//----------------------------------------------------------------------------//

    /** Creates new LinkSocketHandler */
    public LinkSocketHandler(Socket socket)
    {
        super(socket);
    }

//----------------------------------------------------------------------------//

    public void run()
    {
        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        TransactionQueue queue = (TransactionQueue)registry.
            getServiceForName("TransactionQueue");

        // Don't waste time on a transaction if we don't have room for it.
        if (queue.isFull())
        {
            System.out.println("Queue is full.");
            close();
            return;
        }

        boolean successful = false;
        try
        {
            // Let the source immediately open an input stream
            OutputStream out = getOutputStream();

            // 1. Retrieve the Microproduct Link data
            ObjectInputStream in = new ObjectInputStream(getInputStream());
            MPLink link = (MPLink)in.readObject();

            // 2. Extract transaction offer.
            TransactionOffer offer = offerFromLink(link);

            // 3. Contact the merchant.
            InetAddress merchantAddr = getMerchantAddress(link.getMPRMI());
            int port = Integer.parseInt(
			c_Rsrcs.getString("net.CashierPort"), 10);
            SealedObject sealedVoucher
                = performPurchase(offer, merchantAddr, port);

            // 4. Queue the transaction with notification request.
            queue.queueOffer(offer, sealedVoucher, this);
            successful = true;

            // 5. Fire the event.
            firePurchaseEventForLink(link);
        }
        catch (java.lang.ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (java.lang.ClassCastException e)
        {
            e.printStackTrace();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        catch (java.text.ParseException e)
        {
            e.printStackTrace();
        }
        catch (PurchaseInterruptedException e)
        {
            e.printStackTrace();
        }
        catch (UnknownOfferException e)
        {
            e.printStackTrace();
        }
        catch (ServiceUnavailableException e)
        {
            e.printStackTrace();
        }
        catch (QueueSizeExceededException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (!successful)
            {
                close();
            }
        }
    }

//----------------------------------------------------------------------------//

    public void transactionCompleted(boolean succeeded,
        TransactionOffer offer, SignedVoucher signedVoucher)
    {
        if (signedVoucher == null)
            System.out.println("SignedVoucher is null!");
        if (succeeded)
        {
            try
            {
                sendProductRequest(offer, signedVoucher);
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
        }
        close();
    }

//----------------------------------------------------------------------------//

    private TransactionOffer offerFromLink(MPLink link)
        throws java.text.ParseException
    {
        Money price = new Money(link.getPrice());
        Money exchange = new Money(link.getExchangeRate());
        java.util.Date offerExpiry = null;
        if (link.getExpiration() != null)
        {
            java.text.SimpleDateFormat formatter
                = new java.text.SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            offerExpiry = formatter.parse(link.getExpiration());
        }
        java.util.Date productExpiry = new java.util.Date(
            new java.util.Date().getTime() + link.getDuration() * 1000L);

        return new TransactionOffer(price, exchange, offerExpiry,
            productExpiry, link.getMerchantName(), link.getMAMAName(),
            link.getBuyID(), link.getRequestURL());
    }

//----------------------------------------------------------------------------//

    private InetAddress getMerchantAddress(String rmiName)
        throws java.net.UnknownHostException
    {
        // The part between the '//' and the first '/' is extracted, then
        // the ':port-number' part is removed (if present).
        if (!rmiName.startsWith("//"))
        {
            throw new java.net.UnknownHostException();
        }
        int nameEnd = rmiName.indexOf('/', 2);
        if (nameEnd <= 0)
        {
            throw new java.net.UnknownHostException();
        }
        rmiName = rmiName.substring(2, rmiName.indexOf('/', 2));
        if (rmiName == null)
        {
            throw new java.net.UnknownHostException();
        }
        int colonIndex = rmiName.indexOf(':');
        int port;
        if (colonIndex > 0)
        {
            rmiName = rmiName.substring(0, colonIndex - 1);
        }
        return java.net.InetAddress.getByName(rmiName);
    }

//----------------------------------------------------------------------------//

    private SealedObject performPurchase(TransactionOffer offer,
        InetAddress merchantAddress, int port)
        throws PurchaseInterruptedException, UnknownOfferException,
        ServiceUnavailableException, java.io.IOException
    {
        Socket socket = null;
        SealedObject result = null;
        try
        {
            System.out.print( "Merchant @: " + merchantAddress.getHostAddress() + ":");
            System.out.println( port );
            socket = new Socket(merchantAddress, port);
            ObjectOutputStream objOut
                = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objIn
                = new ObjectInputStream(socket.getInputStream());
            PurchaseRequest request = new PurchaseRequest(offer.getOfferID());
            objOut.writeObject(request);
            PurchaseResponse response = (PurchaseResponse)objIn.readObject();
            if (response.getResult() == PurchaseResponse.c_Succeeded)
            {
                result = (SealedObject)response.getData();
            }
            else
            {
                throw new PurchaseInterruptedException(response.getMessage());
            }
        }
        catch (java.lang.NumberFormatException e)
        {
            e.printStackTrace();
            throw new ServiceUnavailableException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new PurchaseInterruptedException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassCastException e)
        {
            e.printStackTrace();
            throw new PurchaseInterruptedException(e.getLocalizedMessage());
        }
        finally
        {
            if (socket != null)
            {
                try
                {
                    socket.close();
                }
                catch (java.io.IOException e)
                {
                    // Ignore. Closed is closed.
                }
            }
        }
        return result;
    }

//----------------------------------------------------------------------------//

    private void sendProductRequest(TransactionOffer offer,
        SignedVoucher voucher)
        throws java.io.IOException
    {
        
        try
        {
            if (voucher != null)
            {
                DataOutputStream dataOut
                    = new DataOutputStream(getOutputStream());
                String base64voucher = new String(Base64.Encode(voucher), "ISO-8859-1");
                String urlString = offer.getRedemptionURL() + "?data=" +
                    base64voucher.replace( '=', '$' );
                System.out.println(urlString); //ntk
           
                dataOut.writeUTF(urlString);
            }
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
    }

//----------------------------------------------------------------------------//

    private void firePurchaseEventForLink(MPLink link)
    {
        CAMAProxy camaProxy = (CAMAProxy)ServiceRegistry.
            getDefaultInstance().getServiceForName("CAMAProxy");
            
        camaProxy.fireWalletPurchaseEvent(
            new WalletPurchaseEvent(
                null, "Purchasing " + link.getLongDesc() + " from "
                + link.getMerchantName() + " for $ " + (
                    camaProxy.convertMerchantPrice(
                        new Money(link.getPrice()),
                        new Money(link.getBasicInfo().getXChngRate())
                ) ).toString()
            )
        );
    }

}

