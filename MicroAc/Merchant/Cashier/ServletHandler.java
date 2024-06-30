/*
 * ServletHandler.java
 *
 * Created on November 8, 2001, 12:24 PM
 */

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.MicroAc.Exceptions.*;
import com.inexum.Encoding.*;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.MicroAc.DomainTypes.Voucher;
import com.inexum.MicroAc.Transaction.PeerRequest;
import com.inexum.MicroAc.Transaction.TransactionOffer;
import com.inexum.Types.Money;
import com.inexum.util.ConfigurationManager;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;
import javax.crypto.SealedObject;

/** Unicast remote object implementing java.rmi.Remote interface.
 *
 * @author  rgoold
 * @version 1.0
 */
public class ServletHandler extends java.rmi.server.UnicastRemoteObject
    implements IVoucherIssuer, IVoucherAcquirer
{
    private static final ConfigurationManager c_Msgs = ConfigurationManager.Instance();

    private Cashier         m_cashier;
    private IProductHandler m_productHandler;

//----------------------------------------------------------------------------//

    /** Constructs ServletHandler object and exports it on default port.
     */
    public ServletHandler()
        throws RemoteException
    {
        super();
    }

//----------------------------------------------------------------------------//

    /** Constructs ServletHandler object and exports it on specified port.
     * @param port The port for exporting
     */
    public ServletHandler(int port)
        throws RemoteException
    {
        super(port);
    }

//----------------------------------------------------------------------------//

    public void init(Cashier cashier, IProductHandler productHandler)
    {
        m_cashier = cashier;
        m_productHandler = productHandler;
    }

//----------------------------------------------------------------------------//

    public ITicket generateSignedVoucher(String offerID)
        throws RemoteException, UnknownOfferException,
        PurchaseInterruptedException
    {
        try
        {
            // Get a voucher corresponding to the offer
            Product product = m_productHandler.productFromOffer(offerID);
            ITicketAgent ticketAgent
                = TicketAgentFactory.AgentForProduct(product.getClass());
            ITicket ticket = ticketAgent.createTicket(product);
            return ticket;
        }
        catch (com.inexum.MicroAc.Exceptions.VoucherCreationException e)
        {
            e.printStackTrace(System.err);
            throw new PurchaseInterruptedException(
                c_Msgs.getString("TicketCreationError"));
        }
        catch (com.inexum.MicroAc.Exceptions.MissingAgentException e)
        {
            e.printStackTrace(System.err);
            throw new PurchaseInterruptedException(
                c_Msgs.getString("TicketCreationError"));
        }
    }

//----------------------------------------------------------------------------//

    public String redeemVoucher(String encodedVoucher)
        throws RemoteException, InvalidVoucherException
    {
        try
        {
            ITicket ticket = (ITicket)Base64.DecodeObject(
                encodedVoucher.getBytes("ISO-8859-1"));

            ITicketAgent ticketAgent
                = TicketAgentFactory.AgentForTicket(ticket.getClass());

            String offerID = ticketAgent.redeemTicket(ticket);
            Product product = m_productHandler.productFromOffer(offerID);
            return product.getLocation();
        }
        catch (UnknownOfferException e)
        {
            throw new InvalidVoucherException(e.getLocalizedMessage());
        }
        catch (MissingAgentException e)
        {
            throw new InvalidVoucherException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassCastException e)
        {
            throw new InvalidVoucherException(e.getLocalizedMessage());
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            throw new InvalidVoucherException(e.getLocalizedMessage());
        }
        catch (java.io.IOException e)
        {
            throw new InvalidVoucherException(e.getLocalizedMessage());
        }
    }

//----------------------------------------------------------------------------//

    public PeerRequest generateRefundOffer(String voucherData,
        String redemptionURL)
        throws RemoteException, UnknownOfferException,
        PurchaseInterruptedException
    {
        String offerID = null;
        try
        {
            SignedVoucher voucher = (SignedVoucher)Base64.DecodeObject(
                voucherData.getBytes());
            offerID = voucher.getVoucher().getOfferID();
            if (offerID == null)
            {
                throw new UnknownOfferException();
            }
        }
        catch (java.lang.ClassCastException e)
        {
            throw new PurchaseInterruptedException(e.getLocalizedMessage());
        }
        catch (java.io.IOException e)
        {
            throw new PurchaseInterruptedException(e.getLocalizedMessage());
        }

        try
        {
            SignedVoucher signedVoucher = generateRefundVoucher(offerID);
            Voucher voucher = signedVoucher.getVoucher();
            Money cost = new Money(voucher.getCost());
            Money rate = new Money(1.0, "USD");

            TransactionOffer offer = new TransactionOffer(cost, rate,
                voucher.getOfferExpiry(), voucher.getExpiry(),
                voucher.getMerchant(), voucher.getMAMA(), voucher.getOfferID(),
                redemptionURL);
            SealedObject sealedVoucher = CryptoAgentFactory.GetCryptoAgent().
                seal(signedVoucher);
            PeerRequest refund = new PeerRequest();
            refund.setOffer(offer);
            refund.setRequestType(PeerRequest.c_PaymentOffer);
            refund.setData(sealedVoucher);
            return refund;
        }
        catch (java.io.IOException e)
        {
            throw new PurchaseInterruptedException(e.getLocalizedMessage());
        }
    }

//----------------------------------------------------------------------------//

    public SignedVoucher generateRefundVoucher(String offerID)
        throws RemoteException, UnknownOfferException,
        PurchaseInterruptedException
    {
        try
        {
            // Get a voucher corresponding to the offer
            RefundProduct product = new RefundProduct(
                m_productHandler.productFromOffer(offerID));
            ITicketAgent ticketAgent
                = TicketAgentFactory.AgentForProduct(product.getClass());
            return (SignedVoucher)ticketAgent.createTicket(product);
            //return CryptoAgentFactory.GetCryptoAgent().seal(ticket);
            //return ticket;
        }
        catch (com.inexum.MicroAc.Exceptions.VoucherCreationException e)
        {
            e.printStackTrace(System.err);
            throw new PurchaseInterruptedException(
                c_Msgs.getString("TicketCreationError"));
        }
        catch (com.inexum.MicroAc.Exceptions.MissingAgentException e)
        {
            e.printStackTrace(System.err);
            throw new PurchaseInterruptedException(
                c_Msgs.getString("TicketCreationError"));
        }
    }

//----------------------------------------------------------------------------//

    public SealedObject generateSealedVoucher(String offerID)
        throws RemoteException, UnknownOfferException,
        PurchaseInterruptedException
    {
        try
        {
            return CryptoAgentFactory.GetCryptoAgent().seal(
                generateSignedVoucher(offerID));
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace(System.err);
            throw new PurchaseInterruptedException(
                c_Msgs.getString("TicketCreationError"));
        }
    }

//----------------------------------------------------------------------------//

    /** Main method.
     */
    public static void main(String[] args)
    {
        System.setSecurityManager(new RMISecurityManager());

        try
        {
            ServletHandler obj = new ServletHandler();
            Naming.rebind("ServletHandler", obj);
        }
        catch (RemoteException ex)
        {
            ex.printStackTrace();
        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
    }

}
