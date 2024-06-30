/*
 * PeerSocketHandler.java
 *
 * Created on November 20, 2001, 5:13 PM
 */

package com.inexum.MicroAc.Transaction;

import com.inexum.Comm.SocketHandler;
import com.inexum.MicroAc.Exceptions.QueueSizeExceededException;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.Types.Money;

import java.io.*;
import java.net.Socket;
import javax.crypto.SealedObject;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class PeerSocketHandler extends SocketHandler
    implements ITransactionCompletionListener
{

    /** Creates new PeerSocketHandler */
    public PeerSocketHandler(Socket socket)
    {
        super(socket);
    }

//----------------------------------------------------------------------------//

    public void run()
    {
        boolean okayToProceed = true;
        try
        {
            // 1. Retrieve the offer
            PeerRequest request = readRequest();
            // 2. Process the offer
            dispatchRequest(request);
            // 3. Exit and allow asynchronous completion.
        }
        catch (QueueSizeExceededException e)
        {
            e.printStackTrace();
            okayToProceed = false;
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            okayToProceed = false;
        }
        finally
        {
            if (!okayToProceed)
            {
                close();
            }
        }
    }
    
//----------------------------------------------------------------------------//

    public void transactionCompleted(boolean succeeded, TransactionOffer offer,
        SignedVoucher signedVoucher)
    {
        close();
    }

//----------------------------------------------------------------------------//

    private void handlePaymentOffer(TransactionOffer offer,
        SealedObject sealedVoucher)
        throws QueueSizeExceededException
    {
        // Do we assume the user never minds being handed money?
        TransactionQueue queue = TransactionQueue.getDefaultInstance();
        queue.queueOffer(offer, sealedVoucher, this);
    }

//----------------------------------------------------------------------------//

    private void handlePaymentRequest(TransactionOffer offer,
        SealedObject sealedVoucher)
        throws QueueSizeExceededException
    {
        // Confirmation should be requested from the user first!
        TransactionQueue queue = TransactionQueue.getDefaultInstance();
        queue.queueOffer(offer, sealedVoucher, this);
    }

//----------------------------------------------------------------------------//

    private void dispatchRequest(PeerRequest request)
        throws QueueSizeExceededException
    {
        switch (request.getRequestType())
        {
            case PeerRequest.c_PaymentOffer:
                System.out.println("PaymentOffer");
                handlePaymentOffer(request.getOffer(),
                    (SealedObject)request.getData());
                break;
            case PeerRequest.c_PaymentRequest:
                System.out.println("PaymentRequest");
                handlePaymentRequest(request.getOffer(),
                    (SealedObject)request.getData());
                break;
            default:
                System.out.println("Luzer!");
        }
    }

//----------------------------------------------------------------------------//

    private PeerRequest readRequest()
        throws java.io.IOException
    {
        try
        {
            // Create data streams
            java.io.ObjectOutputStream objOut
                = new java.io.ObjectOutputStream(getOutputStream());
            java.io.ObjectInputStream objIn
                = new java.io.ObjectInputStream(getInputStream());

            // Wait for other end to identify itself
            String peerID = objIn.readUTF();
            System.out.println("Talking to " + peerID);
            objOut.writeUTF("MicroAc Wallet v1.0; SN1029384756");

            // Get request
            PeerRequest request = (PeerRequest)objIn.readObject();

            return request;
        }
        catch (java.lang.ClassCastException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
        catch (java.lang.ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new java.io.IOException(e.getLocalizedMessage());
        }
    }

}

