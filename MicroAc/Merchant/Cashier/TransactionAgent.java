/*
 * TransactionAgent.java
 *
 * Created on May 12, 2001, 2:43 PM
 */

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.MicroAc.Exceptions.*;
import com.inexum.Encoding.*;
import com.inexum.util.ConfigurationManager;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;
import javax.crypto.SealedObject;

/** Unicast remote object implementing TransactionAgent interface.
 *
 * @author Administrator
 * @version 1.0
 */
public class TransactionAgent extends UnicastRemoteObject
    implements ITransactionAgent
{
    private final ConfigurationManager c_msgs = ConfigurationManager.Instance();

    private Cashier         m_cashier;
    private IProductHandler m_productHandler;
    
    /** Constructs TransactionAgentImpl object and exports it on default port.
     * @throws RemoteException A generic exception.
 */
    public TransactionAgent()
        throws RemoteException
    {
        super();
    }

    /** Constructs TransactionAgentImpl object and exports it on specified port.
     * @param port The port for exporting
     * @throws RemoteException A generic exception.
 */
    public TransactionAgent(int port)
        throws RemoteException
    {
        super(port);
    }
    
/** Initialises the TransactionAgent by passing it references to the agents
 * upon which it will depend.
 * @param ticketAgent The shared instance of a TicketAgent.
 * @param productHandler The shared instance of a ProductHandler.
 * @param cryptoAgent The shared instance of a CryptoAgent.
 */    
    public void init (Cashier cashier, IProductHandler productHandler)
    {
        m_cashier           = cashier;
        m_productHandler    = productHandler;
    }
    
/** Creates a signed voucher for the requested product.
 * @param offerID A string uniquely identifying the product offer.
 * @throws RemoteException If something goes bad.
 * @return A SignedVoucher object.
 */
    public SealedObject purchase(String offerID) 
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
            return CryptoAgentFactory.GetCryptoAgent().seal(ticket);
        }
        catch (com.inexum.MicroAc.Exceptions.VoucherCreationException e)
        {
            e.printStackTrace(System.err);
            throw new PurchaseInterruptedException(
                c_msgs.getString("TicketCreationError"));
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            throw new PurchaseInterruptedException(
                c_msgs.getString("VoucherCreationError"));
        }
    }
    
/** Requests the filesystem location of a product for a signed voucher.
 * @param String The base64 encoded <code>SignedVoucher</code>
 * @throws RemoteException An RMI exception.
 * @throws InvalidVoucherException The voucher could not be processed.
 * @returns The location where the product can be retrieved.
 */
    public String redeem(String encodedVoucher)
        throws RemoteException, InvalidVoucherException
    {
        // Decode it from Base64.
        ITicket ticket = null;
        try
        {
            java.io.ByteArrayInputStream byteIn
                = new java.io.ByteArrayInputStream(
                Base64.Decode(encodedVoucher.getBytes()));
            java.io.ObjectInputStream objectIn
                = new java.io.ObjectInputStream(byteIn);
            ticket = (ITicket)objectIn.readObject();
        }
        catch (java.lang.IndexOutOfBoundsException e)
        {
            e.printStackTrace(System.err);
            m_cashier.release(this);
            throw new InvalidVoucherException(
                c_msgs.getString("InvalidVoucher"));
        }
        catch (java.text.ParseException e)
        {
            e.printStackTrace(System.err);
            m_cashier.release(this);
            throw new InvalidVoucherException(
                c_msgs.getString("InvalidVoucher"));
        }
        catch (java.io.StreamCorruptedException e)
        {
            e.printStackTrace(System.err);
            m_cashier.release(this);
            throw new InvalidVoucherException(
                c_msgs.getString("InvalidVoucher"));
        }
        catch (java.lang.ClassNotFoundException e)
        {
            e.printStackTrace(System.err);
            m_cashier.release(this);
            throw new InvalidVoucherException(
                c_msgs.getString("InvalidVoucher"));
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace(System.err);
            m_cashier.release(this);
            throw new InvalidVoucherException(
                c_msgs.getString("InvalidVoucher"));
        }
        
        String productLocation = "";
        try
        {
            ITicketAgent ticketAgent
                = TicketAgentFactory.AgentForTicket(ticket.getClass());
            
            String offerID = ticketAgent.redeemTicket(ticket);
            Product product = m_productHandler.productFromOffer(offerID);
            productLocation = product.getLocation();
        }
        catch (com.inexum.MicroAc.Exceptions.MissingAgentException e)
        {
            e.printStackTrace(System.err);
            m_cashier.release(this);
            throw new InvalidVoucherException(
                c_msgs.getString("InvalidVoucher"));
        }
        catch (com.inexum.MicroAc.Exceptions.UnknownOfferException e)
        {
            e.printStackTrace(System.err);
            m_cashier.release(this);
            throw new InvalidVoucherException(
                c_msgs.getString("InvalidVoucher"));
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace(System.err);
            m_cashier.release(this);
            throw new InvalidVoucherException(e.getLocalizedMessage());
        }
        catch (InvalidVoucherException e)
        {
            e.printStackTrace(System.err);
            m_cashier.release(this);
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
        
        m_cashier.release(this);
        return productLocation;
    }
    
    // Debugging code only beyond this point

/** Main method.
 * @param args Command line arguments.
 */
    public static void main(String[] args) 
    {
    }
    
}

