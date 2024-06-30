//Source file: C:\Development\com\inexum\Nickel\eMoneyAdmin\Logging\TxLogEvent.java

package com.inexum.MicroAc.MicroProcessing.Logging;

import com.inexum.Types.Money;

public class TransactionLogEvent extends LogEvent 
{
    public static final String     c_StartSession          = "Start Session";
    public static final String     c_Credit                = "Credit";
    public static final String     c_Debit                 = "Debit";
    public static final String     c_TransactionFailed     = "Transaction Failed";
    
    private final int     m_transactionID;        // Unique transaction identifier
    private final Money   m_merchantCost;         // What product cost in the merchants native currency
    private final Money   m_consumerCost;         // What product cost in the consumers native currency 
    private final String  m_userID;               // User ID
    private final String  m_productID;            // Merchant offer ID
    private final String  m_sessionID;            // Unique session identifier
    private final String  m_PAN;                  // user's PAN
    private final String  m_BIN;                  // user's BIN
    
    /**
     * @roseuid 3B3106850343
     */
    public TransactionLogEvent( Object source, String eventType, 
                    String sessionID, int transactionID, String userID,
                    String productID, Money merchantCost, Money consumerCost,
                    String PAN, String BIN ) 
    {
        super(source, eventType );
         
        m_sessionID     = new String(sessionID);
        m_transactionID = transactionID;
        m_userID        = userID;
        m_productID     = new String( productID );
        m_merchantCost  = new Money( merchantCost.getPrice(), 
                                        merchantCost.getCurrency() );
        m_consumerCost  = new Money( consumerCost.getPrice(),
                                        consumerCost.getCurrency() );
                    
        m_PAN           = PAN;
        m_BIN           = BIN;
       
    }
    
    public final Money getMerchantCost()
    {
        return m_merchantCost;
    }
    
    public final Money getConsumerCost()
    {
        return m_consumerCost;
    }
    
    public final String getUserID()
    {
        return m_userID;
    }
    
    public final String getProductID()
    {
        return m_productID;
    }
   
    public final String getSessionID()
    {
        return m_sessionID;
    }
    
    public final int getTransactionID()
    {
        return m_transactionID;
    }
    
    public final String getPAN()
    {
        return m_PAN;
    }
    
    public final String getBIN()
    {
        return m_BIN;
    }
   
}
