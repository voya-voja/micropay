/*
 * IProductHandler.java
 *
 * Created on May 12, 2001, 3:29 PM
 */

package com.inexum.MicroAc.Merchant;

import com.inexum.MicroAc.Exceptions.*;

/** An interface for TransactionAgents to use in looking up products given
 * a particular offer identifier.
 *
 * Implementation is dependent on the environment, and may include database
 * access, remote method invocation, or simply a lookup in memory.
 * @author  rgoold
 * @version 
 */
public interface IProductHandler {
/** Gets the product corresponding to a given offer identifier.
 * @param offerID The offer identifier to look up.
 * @return The corresponding product.
 */    
    public Product productFromOffer(String offerID)
        throws UnknownOfferException;
}


