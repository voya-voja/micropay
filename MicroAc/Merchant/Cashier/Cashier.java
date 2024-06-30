/***** Copyright 2001 iNexum Systems Inc. *****************************
 **
 **      File: Cashier.java
 **
 **      Description:
 **          Initial point of access for merchant transactions.
 **
 **      Revision History:
 **              2001-05-12 (RTG) Initial revision.
 **
 **********************************************************************/

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.Encoding.*;
import com.inexum.IPC.ServiceRegistry;
import com.inexum.util.ConfigurationManager;

import java.util.*;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

import java.net.MalformedURLException;

/** Unicast remote object implementing Cashier interface.
 *
 * @author  rgoold
 * @version
 */
public class Cashier extends UnicastRemoteObject
implements ICashier {
    private static final ConfigurationManager c_Rsrcs
    = ConfigurationManager.Instance();
    
    /** A list of active TransactionAgents.
     */
    private java.util.List      m_transactionAgents;
    /** A shared CryptoAgent object.
     */
    private IProductHandler     m_productHandler;
    
    /** Constructs Cashier object and exports it on default port.
     * @throws RemoteException A generic exception.
     */
    public Cashier()
    throws RemoteException {
        super();
        createAgents();
    }
    
    /** Constructs Cashier object and exports it on specified port.
     * @param port The port for exporting
     * @throws RemoteException A generic exception.
     */
    public Cashier(int port)
    throws RemoteException {
        super(port);
        createAgents();
    }
    
    /** Allows a remote wallet to connect to the server.
     * @throws RemoteException A generic exception.
     * @return An ITransactionAgent for processing transactions.
     */
    public ITransactionAgent connect() throws RemoteException {
        TransactionAgent newAgent = new TransactionAgent();
        newAgent.init(this, m_productHandler);
        m_transactionAgents.add(newAgent);
        return newAgent;
    }
    
    public IVoucherIssuer getIssuer() throws RemoteException, SecurityException {
        try {
            if (java.net.InetAddress.getByName(getClientHost()).equals(
            java.net.InetAddress.getLocalHost())) {
                ServletHandler handler = new ServletHandler();
                handler.init(this, m_productHandler);
                return handler;
            }
            else {
                throw new java.lang.SecurityException();
            }
        }
        catch (java.net.UnknownHostException e) {
            e.printStackTrace();
            throw new java.lang.SecurityException(e.getLocalizedMessage());
        }
        catch (java.rmi.server.ServerNotActiveException e) {
            e.printStackTrace();
            throw new java.rmi.RemoteException(e.getLocalizedMessage());
        }
    }
    
    public IVoucherAcquirer getAcquirer() throws RemoteException, SecurityException {
        try {
            if (java.net.InetAddress.getByName(getClientHost()).equals(
            java.net.InetAddress.getLocalHost())) {
                ServletHandler handler = new ServletHandler();
                handler.init(this, m_productHandler);
                return handler;
            }
            else {
                throw new java.lang.SecurityException();
            }
        }
        catch (java.net.UnknownHostException e) {
            e.printStackTrace();
            throw new java.lang.SecurityException(e.getLocalizedMessage());
        }
        catch (java.rmi.server.ServerNotActiveException e) {
            e.printStackTrace();
            throw new java.rmi.RemoteException(e.getLocalizedMessage());
        }
    }
    
    /** Releases a TransactionAgent for garbage collection.
     * @param theAgent The TransactionAgent to release.
     */
    public void release(TransactionAgent theAgent) {
        if (m_transactionAgents.contains(theAgent)) {
            // This will eliminate the one and only reference, thereby
            // marking it for garbage collection.
            try {
                UnicastRemoteObject.unexportObject(theAgent, true);
            }
            catch (java.rmi.NoSuchObjectException e) {
            }
            m_transactionAgents.remove(theAgent);
        }
    }
    
    /** Register Cashier object with the RMI registry.
     * @throw RemoteException If cannot be exported or bound to RMI registry.
     * @throw MalformedURLException If name cannot be used to construct a valid URL.
     * @throw IllegalArgumentException If null passed as name.
     * @param name A name identifying the service in the RMI registry.
     * @param create Create local registry if necessary.
     * @param obj The object to register.
     * @throws RemoteException If the object cannot be exported or bound to the RMI registry.
     * @throws MalformedURLException If the name cannot be used to construct a valid URL.
     */
    public static void registerToRegistry(String name,
    Remote obj, boolean create)
    throws RemoteException, MalformedURLException {
        if (name == null) {
            throw new IllegalArgumentException(
            c_Rsrcs.getString("NullRegistryNameError"));
        }
        
        try {
            Naming.rebind(name, obj);
        }
        catch (RemoteException e) {
            if (create) {
                Registry r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                r.rebind(name, obj);
            }
            else {
                throw e;
            }
        }
    }
    
    private void createAgents() {
        // Provide CryptoAgents with a key to use.
        java.io.File keyFile = new java.io.File(
        c_Rsrcs.getURL("merchant.key.store.Path").getFile() );
        int keyLength = Integer.parseInt(c_Rsrcs.getString("acquirer.key.Length"), 10);
        try {
            CryptoAgentFactory.InitWithKeyFile(keyFile, keyLength);
        }
        catch (java.security.InvalidKeyException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        catch (java.io.FileNotFoundException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        catch (java.io.IOException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        // The product handler used by this system.
        m_productHandler = new XMLProductHandler();
        // Make the ProductHandler globally available
        ServiceRegistry.getDefaultInstance().
        setServiceForName(m_productHandler, "ProductHandler");
        // Create singletons. They will register with (and be retained by)
        // the TicketAgentFactory.
        new VoucherAgent();
        new StubAgent();
        try {
            ConsumerListener listener = new ConsumerListener(
            Integer.parseInt(
            c_Rsrcs.getString("net.CashierPort"), 10));
            ServiceRegistry.getDefaultInstance().
            setServiceForName(listener, "ConsumerListener");
            listener.start();
        }
        catch (java.lang.NumberFormatException e) {
            e.printStackTrace();
        }
        catch (java.util.MissingResourceException e) {
            e.printStackTrace();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
        // The (empty) list of transaction agents.
        m_transactionAgents = Collections.synchronizedList(new LinkedList());
    }
    
    /** Main method.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        System.setSecurityManager(new RMISecurityManager());
        java.security.Security.insertProviderAt(
            new cryptix.jce.provider.CryptixCrypto(), 1);
        java.security.Security.insertProviderAt(
            new cryptix.jce.provider.CryptixRandom(), 2);
        java.security.Security.insertProviderAt(
            new com.sun.net.ssl.internal.ssl.Provider(), 3);
       Cashier obj = null;
        try {
            obj = new Cashier();
            String rmiName = "//" + c_Rsrcs.getString("merchant.Server") + "/"
            + c_Rsrcs.getString("merchant.Service");
            Naming.rebind( rmiName, obj );
            System.out.println("Cashier is up!");
        }
        catch (RemoteException e) {
            System.err.println(c_Rsrcs.getString("RegistryNotRunningError"));
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            System.err.println(c_Rsrcs.getString("InvalidRegistryNameError"));
            e.printStackTrace();
        }
        finally {
        }
    }
    
}
