//
//  SessionWrapper.java
//  work
//
//  Created by rgoold on Thu Oct 25 2001.
//  Copyright (c) 2001 iNexum Systems Inc. All rights reserved.
//

package com.inexum.MicroAc.MicroProcessing.Consumer.Authentication;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.MicroAc.DomainTypes.VoucherInfo;
import com.inexum.Types.Money;
import com.inexum.MicroAc.DomainTypes.*;
import com.inexum.MicroAc.Consumer.ConsumerResponse;
import com.inexum.MicroAc.Consumer.ConsumerRequest;
import com.inexum.MicroAc.Consumer.ProfileInfo;
import com.inexum.MicroAc.Consumer.LoginInfo;
import com.inexum.MicroAc.Consumer.LoginResult;
import com.inexum.MicroAc.Transaction.TransactionDescription;
import com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager.ProfileProxy;

import java.io.*;
import java.net.Socket;
import javax.crypto.SealedObject;


public class SessionWrapper extends java.lang.Thread
{
    private java.net.Socket m_socket;
    private IAuthenticator  m_authenticator;
    private ProfileProxy    m_proxy;
    private ObjectInputStream   m_objIn;
    private ObjectOutputStream  m_objOut;
    
//----------------------------------------------------------------------------//
    
    public SessionWrapper (Socket socket, IAuthenticator authenticator)
        throws java.io.IOException
    {
        m_socket = socket;
        m_authenticator = authenticator;
        m_objOut = new ObjectOutputStream(m_socket.getOutputStream());
        m_objIn = new ObjectInputStream(m_socket.getInputStream());
    }
    
//----------------------------------------------------------------------------//
    
    private boolean authenticate ()
    {
        boolean authenticated = false;
        try
        {
            LoginInfo info = (LoginInfo)m_objIn.readObject();
            m_proxy = m_authenticator.authenticate(info);
            VoucherInfo[] vouchers
                = new VoucherInfo[m_proxy.getVouchers().size()];
            java.util.Iterator iter = m_proxy.getVouchers().iterator();
            int i = 0;
            while (iter.hasNext())
            {
                vouchers[i++] = (VoucherInfo)iter.next();
            }
            ProfileInfo profile = new ProfileInfo(
                new Money(m_proxy.getBalance(), m_proxy.getNativeCur()),
                m_proxy.getXchngRate(), vouchers);
            LoginResult result = new LoginResult(true, profile, null);
            m_objOut.writeObject(result);
            authenticated = true;
        }
        catch (InvalidLoginException e)
        {
            try
            {
                LoginResult result = new LoginResult(false, null, e);
                m_objOut.writeObject(result);
            }
            catch (java.io.IOException e2)
            {
                // Ignore.
            }
        }
        catch (ServiceUnavailableException e)
        {
            try
            {
                LoginResult result = new LoginResult(false, null, e);
                m_objOut.writeObject(result);
            }
            catch (java.io.IOException e2)
            {
                // Ignore.
            }
        }
        catch (java.rmi.RemoteException e)
        {
            try
            {
                LoginResult result = new LoginResult(false, null, e);
                m_objOut.writeObject(result);
            }
            catch (java.io.IOException e2)
            {
                // Ignore.
            }
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
        finally
        {
            return authenticated;
        }
    }
    
//----------------------------------------------------------------------------//
    
    private ConsumerResponse logout ()
    {
        m_proxy.close();
        return new ConsumerResponse (ConsumerResponse.c_Succeeded);
    }
    
//----------------------------------------------------------------------------//
    
    private ConsumerResponse purchase(TransactionDescription description,
        SealedObject sealedVoucher)
    {
        ConsumerResponse response = null;
        try
        {
            SignedVoucher voucher = m_proxy.
                purchase(description, sealedVoucher);
            response = new ConsumerResponse(ConsumerResponse.c_Succeeded);
            response.setVoucher(voucher);
        }
        catch (UnknownMerchantException e)
        {
            response = new ConsumerResponse(ConsumerResponse.c_UnknownMerchant);
            response.setMessage(e.getLocalizedMessage());
        }
        catch (InvalidVoucherException e)
        {
            response = new ConsumerResponse(ConsumerResponse.c_InvalidVoucher);
            response.setMessage(e.getLocalizedMessage());
        }
        catch (RestrictedProductException e)
        {
            response = new ConsumerResponse(ConsumerResponse.c_RestrictedProduct);
            response.setMessage(e.getLocalizedMessage());
        }
        catch (InsufficientFundsException e)
        {
            response = new ConsumerResponse(ConsumerResponse.c_InsufficientFunds);
            response.setMessage(e.getLocalizedMessage());
        }
        catch (AccountUnavailableException e)
        {
            response = new ConsumerResponse(ConsumerResponse.c_AccountUnavailable);
            response.setMessage(e.getLocalizedMessage());
        }
        catch (InternalErrorException e)
        {
            response = new ConsumerResponse(ConsumerResponse.c_InternalError);
            response.setMessage(e.getLocalizedMessage());
            response.setException(e);
        }
        finally
        {
            response.setNewBalance(m_proxy.getMoneyBalance());
            return response;
        }
    }
    
//----------------------------------------------------------------------------//
    
    public void run ()
    {
        boolean active = false;
        active = authenticate();
        if (!active)
        {
            try
            {
                m_socket.close();
            }
            catch (java.io.IOException e)
            {
                // Ignore. Closed is closed.
            }
            System.out.println("Login failed.");
            return;
        }
        try
        {
            while (active)
            {
                try
                {
                    System.out.println("Reading request...");
                    ConsumerRequest request = (ConsumerRequest)m_objIn.readObject();
                    ConsumerResponse response = null;
                    switch (request.getRequestType())
                    {
                        case ConsumerRequest.c_LogoutRequest:
                            System.out.println("LogoutRequest");
                            response = logout();
                            active = false;
                            break;
                        case ConsumerRequest.c_PurchaseRequest:
                            System.out.println("PurchaseRequest");
                            response = purchase(request.getDescription(),
                                request.getSealedVoucher());
                            break;
                        case ConsumerRequest.c_KeepAlive:
                            System.out.println("KeepAlive");
                            response = new ConsumerResponse(
                                ConsumerResponse.c_Succeeded);
                            break;
                        default:
                            System.out.println("InvalidRequest");
                            response = new ConsumerResponse(
                                ConsumerResponse.c_InvalidRequest);
                            break;
                    }
                    m_objOut.writeObject(response);
                }
                catch (java.io.IOException e)
                {
                    if (active)
                    {
                        logout();
                        active = false;
                    }
                    e.printStackTrace();
                }
                catch (java.lang.ClassNotFoundException e)
                {
                    if (active)
                    {
                        logout();
                        active = false;
                    }
                    e.printStackTrace();
                }
                catch (java.lang.ClassCastException e)
                {
                    if (active)
                    {
                        logout();
                        active = false;
                    }
                    e.printStackTrace();
                }
            }
            m_socket.close();
        }
        catch (java.io.IOException e)
        {
            // Ignore. Closed is closed.
        }
    }

}
