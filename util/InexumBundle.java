/*
 * InexumBundle.java
 *
 * Created on March 27, 2002, 9:11 PM
 */

package com.inexum.util;

/**
 *
 * @author  nvojinov
 * @version 
 */

public class InexumBundle extends ConstantsBundle {
    public Object[][] getContents() 
    {
        return( c_Contents );
    }

    private static final String[]    c_Domains = {
        "microac", "institution", "network", "inexum"
    };

    private static final Object[][] c_Contents = {
        // ---- iNexum public
        {"domains", c_Domains},
        {"domain.microac.properties", "etc.microac"},
        {"domain.institution.class", "com.inexum.util.InstitutionBundle"},
        {"domain.network.class", "com.inexum.util.NetworkBundle"},
        {"domain.inexum.class", "com.inexum.util.InexumBundle"},
        {"algorithm.Cipher", "Blowfish/ECB/PKCS5Padding"},
        {"algorithm.MAC", "HmacSHA-1"},
        {"algorithm.RNG", "SHA1PRNG"},
        {"key.Algorithm", "SHA-1/DSA"},
        {"key.Type", "DSA"},
        {"key.store.Path", "etc/microac.keystore"},
        {"key.store.password", "iNexum123"},
        {"key.store.Type", "JKS"},
        {"key.manager.trust.factory", "SunX509"},
        {"key.manager.factory", "SunX509"},

        // ---- iNexum private
        {"ProfileTable", "Profile"},

        // ---- Network public
        {"net.MicroProductPort", "6079"},	
        {"net.WindowShoppingPort", "4217"},	
        {"net.CashierPort", "6133"},
        {"net.PeerPort", "7027"},
        {"net.Server", "localhost"},
        {"net.Service", "SessionFactory"},	
        {"net.Name", "MicroAc"},

        // ---- Network private
        {"net.password", "iNexum123"},
        {"stub.XmlFilePath", "tmp/Stubs.xml"},
        {"stub.DtdFilePath", "etc/Stubs.dtd"},

        // ---- Institution Public
        // Acquiring - MAMA
        {"acquirer.Server", "localhost"},
        {"acquirer.key.Length", "128"},
        {"acquirer.key.Algorithm", "Blowfish"},

        // Issuing - CAMA
        {"issuer.MaxConcurentPurchaseNo", "1"},
        {"issuer.WalletSignature", "xC5jWTxZwi6RwzLtwBBk2g=="}, // nmv it should not be here
        {"issuer.Server", "localhost"}, 
        {"issuer.WalletPort", "6113"},

        // ---- Institution Private
        {"issuer.password", "iNexum123"},
        {"acquirer.password", "iNexum123"},
        {"db.Driver", "thin"},
        {"db.Port", "1521"},
        {"db.SID", "microAc"},
        {"db.Host", "localhost"},
        {"db.MaxConn", "8"},
        {"db.UserID", "microac"},
        {"db.Password", "iNexum123"},
        {"ssl.key.store.Path", "etc/testkeys"},
        {"ssl.key.store.Password", "passphrase"},
        {"ssl.Context", "TLS" },
        {"LeaseDuration", "120000"},

        // ---- Messages
        {"NoContact", "Unable to contact wallet, please make sure the wallet is running."},
        {"ContactingWallet", "Contacting wallet..."},
        {"InvalidUnlock", "Invalid lock release request."},
        {"NotBlockSizeMultiple", "Length is not a multiple of block size."},
        {"ExchangeCurrencyMisMatch", "The exchange rates do not match."},
        {"InternalServerError", "An error occurred while authenticating your transaction. It is recommended that you log out and try again."},
        {"MPRetrievalError", "An error occurred retrieving the microproduct information."},
        {"PurchaseError", "Your transaction could not be completed because an error occurred."},
        {"UnknownMerchantError", "This merchant does not appear to have a valid account."},
        {"InvalidVoucherError", "The merchant did not produce a voucher matching your request."},
        {"NoHarmDone", "Your account has not been charged for this transaction."},
        {"PossibleHarmDone", "It was not possible to determine if your account was charged for this transaction."},
        {"InsufficientFundsError", "You do not have sufficient funds to complete this purchase."},
        {"RestrictedProductError", "Your account restrictions do not permit you to purchase this."},
        {"AccountUnavailableError", "The micropayment account is unavailable, please try again later."},
        {"JCEInstallationError", "You have not properly installed SunJCE.\nIt must be located in {java.home}/lib/ext.\nYou must also edit your java.security file to contain the line:\n    security.provider.x", "com.sun.crypto.provider.SunJCE\nWhere 'x' is the first available number after the existing security.provider declarations."},
        {"NullRegistryNameError", "RMI registration name cannot be null."},
        {"NoSuchAlgorithm", "No such algorithm."},
        {"UnableToReadFileError", "Unable to read file."},
        {"FileDoesNotExistError", "File does not exist."},
        {"InvalidSignature", "Invalid signature."},
        {"InvalidTicketStub", "Invalid ticket stub."},
        {"LoadingProductDatabase", "Loading product database."},
        {"ProductDatabaseLoaded", "Product database loaded."},
        {"InvalidVoucher", "Invalid voucher."},
        {"RegistryNotRunningError", "The RMI registry is not running."},
        {"InvalidRegistryNameError", "The RMI registration name is invalid."},
        {"ParseError", "Parse error."},
        {"LineNumber", "Line number."},
        {"UnknownOffer", "Unknown offer."},
        {"TicketExpiredError", "Ticket expired."},
        {"NoSuchTicketError", "No such ticket."},
        {"MissingResource", "A necessary resource could not be found."},
        {"CipherNotInitialisedError", "Cipher not initialized."},
        {"MissingAgentError", "No such agent."},
        {"TicketCreationError", "Could not create ticket."},
        {"ServerError", "Server Error."},
        {"SorryError", "Sorry, an error has occurred:"},
        {"PleaseTryAgain", "Please try again at a later time."},
        {"UnableToProcessError", "The merchant was unable to process your request."},
        {"NoDataTransmittedError", "Your wallet did not transmit any data."},
        {"InvalidTicketError", "Your ticket for this product is not valid."},
        {"InvalidVoucherError", "Your voucher for this product is invalid."},
        {"InvalidDataError", "The data sent by your wallet could not be understood."},
        {"CouldNotFindMerchant", "Could not find merchant."},
        {"OperatingWithoutNS", "Operating without a naming service."},
        {"TransactionSucceeded", "Transaction succeeded."},
        {"TransactionFailed", "Transaction failed."},
        {"RegistryBindError", "Couldn't bind to registry."},

        
        // ---- Wallet
        {"wallet.message.PurchaseSucceeded", "Purchase Succeeded" },

        // ---- Example Mearchant
        {"merchant.key.store.Path", "etc/Merchant.key"},
        {"merchant.ProductsDatabase", "etc/Products.xml"},
        {"merchant.Name", "bonzaibucko"},
        {"merchant.Server", "localhost"},
        {"merchant.Service", "Merchant"},
        {"merchant.xChangeRate", "0.67USD"}
    };
}
