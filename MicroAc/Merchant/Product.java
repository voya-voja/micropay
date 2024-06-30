/*
 * Product.java
 *
 * Created on May 12, 2001, 3:30 PM
 */

package com.inexum.MicroAc.Merchant;

import java.io.Serializable;

/** The interface for a merchant's product.
 * @author  rgoold
 * @version 
 */
public class Product implements Serializable {
    
/** The product's offer identifier.
 */    
    private String  m_offerID;
/** The cost of the product.
 */    
    private String  m_cost;
/** The duration in seconds of this product (for each purchase).
 */    
    private int     m_duration;
/** The filesystem location of this product.
 */
    private String  m_location;

    /** Creates new Product
 */
    public Product() {
    }
    
/** Gets the product's offer identifier.
 * @return The product's offer identifier.
 */    
    public String getOfferID()
    {
        return m_offerID;
    }
    
/** Gets the product's cost.
 * @return The product's cost.
 */    
    public String getCost()
    {
        return m_cost;
    }
    
/** Gets the product's duration.
 * @return The product's duration.
 */    
    public int getDuration()
    {
        return m_duration;
    }
    
    public String getLocation()
    {
        return m_location;
    }
    
/** Sets the product's offer identifier.
 * @param offerID The product's offer identifier.
 */    
    public void setOfferID(String offerID)
    {
        m_offerID = offerID;
    }
    
/** Sets the product's cost.
 * @param cost The product's cost.
 */    
    public void setCost (String cost)
    {
        m_cost = cost;
    }
    
/** Sets the product's duration.
 * @param duration The product's duration.
 */    
    public void setDuration (int duration)
    {
        m_duration = duration;
    }
    
    public void setLocation(String location)
    {
        m_location = location;
    }

}

