/*
 * Card.java
 *
 * Created on July 11, 2004, 4:49 PM
 */

package com.inexum.util.pos;

/**
 *
 * @author  inexum
 */
public class Card 
{
    private String mCardNumber;
    private String mExpDate;
    private String mName;
    private String mInstitutionId = "1300010000000001";
    
    /** Creates a new instance of Card */
    public Card(String cardNumber, String expDate, String name) 
    {
        mName = name;
        mCardNumber = cardNumber;
        mExpDate = expDate;
    }
    
    public String getExpDate()
    {
        return(mExpDate);
    }
    
    public String getName()
    {
        return(mName);
    }
    
    public String getCardNumber()
    {
        return(mCardNumber);
    }
    
    public String getInstitutionId()
    {
        return( mInstitutionId);
    }
}
