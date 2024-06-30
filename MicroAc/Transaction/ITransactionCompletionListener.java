/*
 * ITransactionCompletionListener.java
 *
 * Created on November 20, 2001, 1:54 PM
 */

package com.inexum.MicroAc.Transaction;

import com.inexum.MicroAc.DomainTypes.SignedVoucher;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public interface ITransactionCompletionListener
{

    void transactionCompleted(boolean succeeded, TransactionOffer offer,
        SignedVoucher signedVoucher);

}


