/*
 * CAMAProxy.java
 *
 * Created on May 7, 2001, 10:38 AM
 */

package com.inexum.MicroAc.Wallet;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.MicroAc.DomainTypes.VoucherInfo;
import com.inexum.MicroAc.Transaction.TransactionDescription;
import com.inexum.Types.Money;
import com.inexum.MicroAc.Consumer.ProfileInfo;
import com.inexum.MicroAc.Consumer.ConsumerResponse;
import com.inexum.MicroAc.Consumer.ConsumerRequest;
import com.inexum.IPC.ServiceRegistry;
import com.inexum.MicroAc.Consumer.LoginInfo;
import com.inexum.MicroAc.Consumer.LoginResult;
import com.inexum.MicroAc.TrustMgr.DummyTrustManager;
import com.inexum.util.ConfigurationManager;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

import javax.crypto.SealedObject;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;

/**
 *
 * @author  Nick Kapov
 * @version 1.0
 */
public class CAMAProxy extends Object
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();

    private ProfileInfo             m_profileInfo;

    /** A collection of objects that are listening for wallet events*/
    private Collection              m_walletListeners;

    private Socket                  m_camaSocket;
    private javax.net.SocketFactory m_socketFactory;
    private ObjectOutputStream      m_objectOut;
    private ObjectInputStream       m_objectIn;

//----------------------------------------------------------------------------//

    /** Creates new CAMAProxy */
    public CAMAProxy()
    {
        m_walletListeners = new ArrayList();
        try
        {
            SSLContext ctx = SSLContext.getInstance("TLS");
            DummyTrustManager mgr = new DummyTrustManager();
            TrustManager[] mgrs = new TrustManager[1];
            mgrs[0] = mgr;
            ctx.init(null, mgrs, null);
            m_socketFactory = ctx.getSocketFactory();
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            m_socketFactory = javax.net.SocketFactory.getDefault();
        }
        catch (java.security.KeyManagementException e)
        {
            e.printStackTrace();
            m_socketFactory = javax.net.SocketFactory.getDefault();
        }
        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        registry.setServiceForName(this, "CAMAProxy");
    }

//----------------------------------------------------------------------------//

    private void openConnection ()
        throws java.io.IOException
    {
        boolean connected = m_camaSocket != null;
        if (connected)
        {
            // Test the socket. There's no explicit way of
            // checking if it's still connected.
            try
            {
                m_camaSocket.close();
            }
            catch (java.io.IOException e)
            {
            }
            connected = false;
        }
        if (! connected)
        {
            InetAddress camaAddr = InetAddress.getByName(
                c_Rsrcs.getString("issuer.Server"));
            System.out.println("issuer.Server is '" + 
                                c_Rsrcs.getString("issuer.Server") + "'");
            int camaPort = 0;
            try
            {
                camaPort = Integer.parseInt(c_Rsrcs.getString("issuer.WalletPort"), 10);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
            System.out.println("Connecting to: "
                + camaAddr.getHostAddress()
                + ":" + camaPort + "...");
            m_camaSocket = m_socketFactory.createSocket(camaAddr, camaPort);
            
            //m_camaSocket.setKeepAlive(true);
            System.out.println("Connected to: "
                + m_camaSocket.getInetAddress().getHostAddress()
                + ":" + m_camaSocket.getPort());
            OutputStream out = m_camaSocket.getOutputStream();
            m_objectOut = new ObjectOutputStream(
                m_camaSocket.getOutputStream());
            m_objectOut.flush();
            InputStream in = m_camaSocket.getInputStream();
            m_objectIn = new ObjectInputStream(
                m_camaSocket.getInputStream());
        }
    }

//----------------------------------------------------------------------------//

    public void connect( String userID, String password, String walletID )
        throws InvalidLoginException, ServiceUnavailableException
    {
        try
        {
            /* ( NK 2001-05-08 ) Autheticate the user who tried to logon, using
             * the Autheticator on the CAMA connection through a SSL Connection
             */
            userID.trim();
            password.trim();

            if( userID.equals("") || password.equals("") )
                throw new InvalidLoginException();

            openConnection();
            LoginInfo loginInfo = new LoginInfo( userID, password, walletID );
            m_objectOut.writeObject(loginInfo);
            LoginResult result = (LoginResult)m_objectIn.readObject();
            if (result.succeeded())
            {
                m_profileInfo = result.getProfileInfo();
            }
            else
            {
                result.getException().printStackTrace();
                throw new InvalidLoginException(result.getException().
                    getLocalizedMessage());
            }
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            throw new ServiceUnavailableException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new ServiceUnavailableException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassCastException e)
        {
            e.printStackTrace();
            throw new ServiceUnavailableException(e.getLocalizedMessage());
        }
    }

//----------------------------------------------------------------------------//

    public void disconnect()
    {
        // Close the connection to the purchasing session
        try
        {
            if (m_camaSocket != null)
            {
                ConsumerRequest request = new ConsumerRequest(
                    ConsumerRequest.c_LogoutRequest);
                m_objectOut.writeObject(request);
                try
                {
                    ConsumerResponse response
                        = (ConsumerResponse)m_objectIn.readObject();
                }
                catch (Exception e)
                {
                    // Ignore.
                }
                m_camaSocket.close();
            }
        }
        catch (java.io.IOException e)
        {
            // Ignore. Closed is closed.
        }
        m_camaSocket = null;
    }

//----------------------------------------------------------------------------//

    public SignedVoucher purchase( final TransactionDescription description,
        final SealedObject voucher )
        throws UnknownMerchantException, InvalidVoucherException,
        RestrictedProductException, InsufficientFundsException,
        AccountUnavailableException, InternalErrorException
    {
        try
        {
            ConsumerRequest request = new ConsumerRequest(
                ConsumerRequest.c_PurchaseRequest);
            request.setDescription(description);
            request.setSealedVoucher(voucher);
            m_objectOut.writeObject(request);

            ConsumerResponse response
                = (ConsumerResponse)m_objectIn.readObject();

            Money newBalance = response.getNewBalance();
            if (newBalance != null)
            {
                m_profileInfo.setBalance(newBalance);
            }

            switch (response.getStatus())
            {
                case ConsumerResponse.c_Succeeded:
//                {
                    SignedVoucher signedVoucher = response.getVoucher();
                    VoucherInfo voucherInfo = new VoucherInfo(description.getIssuer(),
                                                    description.getOfferID(), description.getProductExpiry(),
                                                    signedVoucher );
                    String message = c_Rsrcs.getString("wallet.message.PurchaseSucceeded" ); 
                    fireWalletPurchaseEvent( new WalletPurchaseEvent( voucherInfo, message ) );
            
                    return signedVoucher;
//                };
                case ConsumerResponse.c_UnknownMerchant:
                    throw new UnknownMerchantException(response.getMessage());
                case ConsumerResponse.c_AccountUnavailable:
                    throw new AccountUnavailableException(response.getMessage());
                case ConsumerResponse.c_InvalidVoucher:
                    throw new InvalidVoucherException(response.getMessage());
                case ConsumerResponse.c_RestrictedProduct:
                    throw new RestrictedProductException(response.getMessage());
                case ConsumerResponse.c_InsufficientFunds:
                    throw new InsufficientFundsException(response.getMessage());
                case ConsumerResponse.c_InternalError:
                    throw (InternalErrorException)response.getException();
                default:
                    throw new AccountUnavailableException(response.getMessage());
            }
            
            
        }
        catch( UnknownMerchantException UME )
        {
            UME.printStackTrace();
            fireWalletPurchaseEvent(  new WalletPurchaseEvent( null,
                UME.getLocalizedMessage() ) );
            throw UME;
        }
        catch( AccountUnavailableException AUE )
        {
            AUE.printStackTrace();
            fireWalletPurchaseEvent(  new WalletPurchaseEvent( null,
                AUE.getLocalizedMessage() ) );
            throw AUE;
        }
        catch( InvalidVoucherException IVE )
        {
            IVE.printStackTrace();
            fireWalletPurchaseEvent(  new WalletPurchaseEvent( null,
                IVE.getLocalizedMessage() ) );
            throw IVE;
        }
        catch( RestrictedProductException RPE )
        {
            RPE.printStackTrace();
            fireWalletPurchaseEvent(  new WalletPurchaseEvent( null,
                RPE.getLocalizedMessage() ) );
            throw RPE;
        }
        catch( InsufficientFundsException IFE )
        {
            IFE.printStackTrace();
            fireWalletPurchaseEvent(  new WalletPurchaseEvent( null,
                IFE.getLocalizedMessage() ) );
            throw IFE;
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            fireWalletPurchaseEvent(  new WalletPurchaseEvent( null,
                e.getLocalizedMessage() ) );
            throw new AccountUnavailableException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassNotFoundException e)
        {
            e.printStackTrace();
            fireWalletPurchaseEvent(  new WalletPurchaseEvent( null,
                e.getLocalizedMessage() ) );
            throw new AccountUnavailableException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassCastException e)
        {
            e.printStackTrace();
            fireWalletPurchaseEvent(  new WalletPurchaseEvent( null,
                e.getLocalizedMessage() ) );
            throw new AccountUnavailableException(e.getLocalizedMessage());
        }
        finally
        {
            
        }
        
            

    }

//----------------------------------------------------------------------------//

    public double getBalance()
    {
        return m_profileInfo.getBalance().getPrice();
    }

//----------------------------------------------------------------------------//

    public String getNativeCur()
    {
        return m_profileInfo.getBalance().getCurrency();
    }

//----------------------------------------------------------------------------//

    public Collection getVouchers()
    {
        return java.util.Arrays.asList(m_profileInfo.getVouchers());
    }

//----------------------------------------------------------------------------//

    public synchronized void addWalletListener( IWalletListener listener )
    {
        m_walletListeners.add( listener );
    }

//----------------------------------------------------------------------------//

    public synchronized void removeWalletListener( IWalletListener listener )
    {
        if( !m_walletListeners.isEmpty() )
            m_walletListeners.remove( listener );
    }

//----------------------------------------------------------------------------//

    public void fireWalletConnectEvent (WalletConnectEvent event)
    {
        Iterator iter = m_walletListeners.iterator();
        while (iter.hasNext())
        {
            IWalletListener listener = (IWalletListener)iter.next();
            listener.walletEvent(event);
        }
    }

//----------------------------------------------------------------------------//

    public void fireWalletBrowseEvent( WalletBrowseEvent WBE )
    {
        Iterator iter = m_walletListeners.iterator();
        while( iter.hasNext() )
        {
            IWalletListener walletListener = ( IWalletListener )iter.next();
            walletListener.walletEvent( WBE );
        }
     }

//----------------------------------------------------------------------------//

     public void fireWalletPurchaseEvent( WalletPurchaseEvent WPE )
     {
        Iterator iter = m_walletListeners.iterator();
        while( iter.hasNext() )
        {
            IWalletListener walletListener = ( IWalletListener )iter.next();
            walletListener.walletEvent( WPE );
        }
     }

//----------------------------------------------------------------------------//

     public void fireWalletFailureEvent( WalletFailureEvent WFE )
     {
        Iterator iter = m_walletListeners.iterator();
        while( iter.hasNext() )
        {
            IWalletListener walletListener = ( IWalletListener )iter.next();
            walletListener.walletEvent( WFE );
        }
     }

//----------------------------------------------------------------------------//

    /** Convert the merchat price to the conusmers native currency.
     * @param price The price of the product being purchased.
     * @param merchantExchanageRate The exchange rate for the merchant to
     * convert from its native currency to a base currency ( USD )
     * @return Returns a Money object with the price converted to the consumers
     * native currency.
     */
    public final Money convertMerchantPrice( Money productPrice,
        Money merchantExchangeRate )
    {
        //check to see if the merchant and consumer currencies are the same
        if( ! productPrice.getCurrency().equals(
            m_profileInfo.getBalance().getCurrency()) )
        {
            //make sure the merchant and consumer exchange rate currencies match
            if( merchantExchangeRate.getCurrency().equals(
                m_profileInfo.getExchangeRate().getCurrency() ) )
            {
                //convert the merchant price to the consumers currency
                Money baseAmt = productPrice.convertToBaseCurrency(
                    merchantExchangeRate );
                return baseAmt.convertFromBaseCurrency(
                    m_profileInfo.getExchangeRate(),
                    m_profileInfo.getBalance().getCurrency() );
            }
            else
            {
                //we were unable to convert the product price so return the
                //price in the merchants currency
                return productPrice;
            }
        }
        else
            return productPrice;
    }

//----------------------------------------------------------------------------//

    public static void main (String[] args)
    {
    }

}
