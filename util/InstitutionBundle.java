/*
 * InstitutionBundle.java
 *
 * Created on March 27, 2002, 9:11 PM
 */

package com.inexum.util;

/**
 *
 * @author  nvojinov
 * @version 
 */

public class InstitutionBundle extends ConstantsBundle {
    public Object[][] getContents() 
    {
        System.out.println( "Loading Institution Configuration ..." );
        return( c_Contents );
    }

    private static final Object[][] c_Contents = {
        // Issuing - CAMA
        {"issuer.Server", " 24.156.26.171"}, 
        
        // ---- Wallet
        {"wallet.message.PurchaseSucceeded", "Purchase Succeeded" },
        {"wallet.Login", "Login"},
        {"wallet.Logout", "Logout"},
        {"wallet.Expand", "Expand"},
        {"wallet.Shrink", "Shrink"}
    };
}
