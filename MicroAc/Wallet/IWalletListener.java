//Source file: IWalletListener.java

package com.inexum.MicroAc.Wallet;



/**
 * Wallet events are fired by the wallet when certain events occur,the wallet is closed, the user moves over a microproduct, initiates a purchase, the purchase fails( NSF, communtication failure) ,purchase completed.
 * @author
 * @version 1.0
 */
public interface IWalletListener 
{
   
   /**
    * An item is being considered for purchasing 
    * @return Void
    * @roseuid 3B226DB9031E
    * @param we The wallet event info
 */
   public void walletEvent( WalletBrowseEvent WBE );
   
  /**
    * Fired when an item is being purchased  
    * @roseuid 3B226EAA03C5
    * @param we The wallet event info
 */
   public void walletEvent( WalletPurchaseEvent WPE );
   
   /** Fired when a purchase fails
    * @roseuid 3B226FFE0215
    * @param we The wallet event info
 */
   public void walletEvent( WalletFailureEvent WFE );
   
   public void walletEvent( WalletConnectEvent WCE );
}
