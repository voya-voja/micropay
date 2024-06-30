/*
 * RefundProduct.java
 *
 * Created on November 8, 2001, 12:59 PM
 */

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public final class RefundProduct extends Product
    implements java.io.Serializable
{
    public static void initialize()
    {
        TicketAgentFactory.Register(SignedVoucher.class,
            RefundProduct.class, new VoucherAgent());
    }

    /** Creates new RefundProduct */
    public RefundProduct(Product productToRefund)
    {
        super();
        setOfferID("refund-" + productToRefund.getOfferID());
        setDuration(1000 * 60 * 60 * 24 * 40);   // 40 days and 40 nights
        setLocation("/apps/inexum/products/refund.html");
        setCost("-" + productToRefund.getCost());
    }

}

