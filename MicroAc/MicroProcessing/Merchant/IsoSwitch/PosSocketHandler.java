/*
 * PosSocketHandler.java
 *
 * Created on May 18, 2001, 11:04 AM
 */

package com.inexum.MicroAc.MicroProcessing.Merchant.IsoSwitch;

import com.inexum.Comm.SocketHandler;
import com.inexum.MicroAc.Transaction.TransactionOffer;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.util.ConfigurationManager;
import com.inexum.MicroAc.Wallet.CAMAProxy;
import com.inexum.MicroAc.PFL.MPLink;
import com.inexum.MicroAc.Exceptions.*;
import com.inexum.IPC.ServiceRegistry;
import com.inexum.MicroAc.Transaction.TransactionQueue;
import com.inexum.MicroAc.Transaction.ITransactionCompletionListener;
import com.inexum.Types.Money;
import com.inexum.MicroAc.Merchant.PurchaseRequest;
import com.inexum.MicroAc.Merchant.PurchaseResponse;
import com.inexum.MicroAc.Merchant.Product;
import com.inexum.MicroAc.Merchant.Cashier.OneShotProduct;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.Base1Packager;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import javax.crypto.SealedObject;

/**
 *
 * @author  nvojinov
 */
public class PosSocketHandler extends SocketHandler
    implements ITransactionCompletionListener
{
    private static final ConfigurationManager c_Rsrcs = 
                                            ConfigurationManager.Instance();

    private String              m_signature;

    private ISOMsg              mRequest;
    
    /** Creates a new instance of PosSocketHandler */
    public PosSocketHandler(Socket socket) 
    {
        super(socket);
        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        registry.setServiceForName(this, "Wallet");

        m_signature = c_Rsrcs.getString( "issuer.WalletSignature" );
    }

    public void run()
    {
        ISOMsg msg;
        try
        {
            msg = receivedRequest();
        }
        catch(Exception e)
        {
            reject(null, 99);
            return;
        }
        
        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        TransactionQueue queue = (TransactionQueue)registry.
            getServiceForName("TransactionQueue");

        // Don't waste time on a transaction if we don't have room for it.
        if (queue.isFull())
        {
            reject(msg, 99);
            close();
            return;
        }
        try
        {
            camaConnect(msg.getString(2));
            // 2. Extract transaction offer.
            TransactionOffer offer = offerFromLink(msg);

            // 3. Contact the merchant.
            String cashierHost = "localhost";
            try
            {
                cashierHost = c_Rsrcs.getString("acquirer.pos.cashier");
            }
            catch(java.util.MissingResourceException e)
            {
                System.out.println("No property 'acquirer.pos.cashier',"
                                    + "using default host '" + cashierHost + "'");
            }
            InetAddress merchantAddr = InetAddress.getByName(cashierHost);
            int port = Integer.parseInt(
                        c_Rsrcs.getString("net.CashierPort"), 10);
            SealedObject sealedVoucher
                = performPurchase(offer, merchantAddr, port);

            // 4. Queue the transaction with notification request.
            queue.queueOffer(offer, sealedVoucher, this);
        }
        catch(InvalidLoginException e)
        {
            reject(msg, 14);
        }
        catch(ServiceUnavailableException SUE)
        {
            reject(msg, 96);
        }
        catch(IllegalArgumentException e)
        {
            reject(msg, 30);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            reject(msg, 99);
        }
    }
    
    protected ISOMsg receivedRequest() throws java.io.IOException,
                                                org.jpos.iso.ISOException
    {
        InputStream in = getInputStream();
        mRequest = new ISOMsg();
        mRequest.setPackager(new Base1Packager());
        mRequest.unpack(in);
        return(mRequest);
    }

    protected void camaConnect(String identifier) throws InvalidLoginException, 
                                                    ServiceUnavailableException
    {
        String userID = "Mary";
        String password = "inexum123";
        // DB lookup into the Oracle based on identifier to get userid && password

        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        CAMAProxy cama = (CAMAProxy)registry.getServiceForName("CAMAProxy");
        cama.connect(userID, password, m_signature);
    }

    protected MPLink createMPLink(ISOMsg msg)
        throws java.lang.IllegalArgumentException
    {
        MPLink mpLink = new MPLink();

        // The required fields
        String isoAmount = msg.getString(4);
        String amount = isoAmount.substring(0, isoAmount.length()-2) + "."
                            + isoAmount.substring(isoAmount.length()-2) + "CAD";
        mpLink.setPrice(amount);
        mpLink.setTextLink("POS Sale");
        mpLink.setRequestURL("http://www.inexum.gotdns.com:8080/delivery/purchase");

        java.util.List mpNameList = new java.util.LinkedList();
        mpLink.setPaymentSystems(mpNameList);
        int merchantCount = 0;
        String paymentSystemName = "http://www.inexum.com/";
        
        // The recommended fields
        mpLink.setTitle("POS sale");

        // The optional fields
        mpLink.setBuyID("pos");
        mpLink.setBaseURL("http://www.inexum.gotdns.com/");
        mpLink.setLongDesc("POS sale");
        mpLink.setMerchantName("bonzaibucko");
        try
        {
            mpLink.setDuration(0);
        }
        catch (java.lang.NumberFormatException e)
        {
            /* (2001-08-20 RTG) Down-cast because the type of
             * IllegalArgumentException is irrelevant and this allows
             * NumberFormatExceptions to be identified as coming from other
             * sources.
             */
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
/*        mpLink.setExpiration( getParameter("expiration") );
        mpLink.setTarget( getParameter("target") );
        mpLink.setHREFLang( getParameter("hreflang") );
        mpLink.setType( getParameter("type") );
        mpLink.setAccessKey( getParameter("accesskey") );
        mpLink.setCharSet( getParameter("charset") );
        mpLink.setExtData( getParameter("extdata") );
        mpLink.setExtDataParam( getParameter("extdataparam") );
*/
        mpLink.setMPRMIParam("//www.inexum.gotdns.com/giogo/Merchant");
        mpLink.setMAMAName("localhost");

        try
        {
            mpLink.setXChngRate("0.73USD");
        }
        catch (java.util.MissingResourceException e)
        {
            e.printStackTrace();
            mpLink.setXChngRate("1.00USD");
        }

        return mpLink;
    }

    public void transactionCompleted(boolean succeeded,
        TransactionOffer offer, SignedVoucher signedVoucher) 
    {
        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        CAMAProxy cama = (CAMAProxy)registry.getServiceForName("CAMAProxy");
        cama.disconnect();
        if (signedVoucher == null)
            reject(mRequest, 5);
        if (succeeded)
        {
            try
            {
                approve(mRequest);
            }
            catch(Exception e)
            {
                reject(mRequest, 99);
            }
        }
        else
        {
            reject(mRequest, 5);
        }
        close();
    }

    protected void approve(ISOMsg msg) throws org.jpos.iso.ISOException,
                                                java.io.IOException
    {
        ISOMsg response = response(msg);
        try
        {
            response.set(39, Integer.toString(0));
            sendResponse(response);
        }
        catch(org.jpos.iso.ISOException e)
        {
            reject(response, 99);
        }
    }
    
    protected void reject(ISOMsg msg, int responseCode)
    {
        try
        {
            ISOMsg response = response(msg);
            response.set(39, Integer.toString(responseCode));
            sendResponse(response);
        }
        catch(java.io.IOException e)
        {
            e.printStackTrace();
        }
        catch(org.jpos.iso.ISOException e)
        {
            e.printStackTrace();
            ISOMsg reject = new ISOMsg();
            try
            {
                reject.set(0, "0110");
                reject.set(39, "99");
                sendResponse(reject);
            }
            catch(org.jpos.iso.ISOException isoExcpt)
            {
                isoExcpt.printStackTrace();
            }
            catch(java.io.IOException ioExcept)
            {
                ioExcept.printStackTrace();
            }
        }
    }

    protected ISOMsg response(ISOMsg request) throws org.jpos.iso.ISOException
    {
        int type = Integer.parseInt(request.getString(0));
        type += 10;
        ISOMsg response = (ISOMsg)request.clone();
        response.set(0, "0" + Integer.toString(type));
        return(response);
    }
    
    protected void sendResponse(ISOMsg msg) throws java.io.IOException,
                                                    org.jpos.iso.ISOException
    {
        OutputStream out = getOutputStream();
        msg.setPackager(new Base1Packager());
        out.write(msg.pack());
    }

    private TransactionOffer offerFromLink(ISOMsg msg)
        throws java.text.ParseException
    {
        String isoAmount = msg.getString(4);
        String amount = isoAmount.substring(0, isoAmount.length()-2) + "."
                            + isoAmount.substring(isoAmount.length()-2) + "CAD";
        Money price = new Money(amount);
        Money exchange = new Money("0.73USD");
        java.util.Date offerExpiry = null;
        java.util.Date productExpiry = new java.util.Date();

        String merchantName = "bonzaibucko";
        // DB get merchant name based on terminal id
        
        String mamaName = c_Rsrcs.getString("acquirer.Server");
        return(new TransactionOffer(price, exchange, offerExpiry,
                                    productExpiry, merchantName, mamaName,
                                    "_pos#", null));
    }

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
            Product product = new OneShotProduct();
            product.setOfferID(offer.getOfferID());
            product.setCost(offer.getCost().toString());
            product.setDuration(0);
            product.setLocation("");
            
            PurchaseRequest request = 
                            new PurchaseRequest(offer.getOfferID(), product);
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
}
