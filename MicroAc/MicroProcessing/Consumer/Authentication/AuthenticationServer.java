/*
 * AuthenticationServer.java
 *
 * Created on June 8, 2001, 1:45 PM
 */

package com.inexum.MicroAc.MicroProcessing.Consumer.Authentication;

import com.inexum.Comm.RMISSLClientSocketFactory;
import com.inexum.Comm.RMISSLServerSocketFactory;
import com.inexum.Database.*;
import com.inexum.util.ConfigurationManager;

import java.sql.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

/** Unicast remote object implementing java.rmi.Remote interface.
 *
 * @author nkapov
 * @version 1.0
 */
public class AuthenticationServer extends UnicastRemoteObject
    implements IAuthenticationServer
{
    
    private DatabaseConPool m_dbPool;
    private Connection m_dbConn;

    /** Constructs AuthenticationServer object and exports it on default port.
     */
    public AuthenticationServer() throws RemoteException
    {
        super( 0 /* ,
                new RMISSLClientSocketFactory(),
		new RMISSLServerSocketFactory() */ );
        
        if( !initDBPool() )
        {
            throw new RemoteException();
        }
    }

    /** Constructs AuthenticationServer object and exports it on specified port.
     * @param port The port for exporting
     */
    public AuthenticationServer(int port) throws RemoteException 
    {
        super( port /* ,
                new RMISSLClientSocketFactory(),
		new RMISSLServerSocketFactory() */ );
        
        if( !initDBPool() )
        {
           throw new RemoteException();
        }
    }
    
    private boolean initDBPool()
    {
        
        boolean bSuccess = false;
        try
        {
            ConfigurationManager config = ConfigurationManager.Instance();
            
            
            m_dbPool = DatabaseConPool.getDatabaseConPool();
            m_dbConn = m_dbPool.getConnection(
                                config.getString( "db.UserID" ),
                                config.getString( "db.Password" ) );
            
            m_dbConn.setAutoCommit( false );
            
            bSuccess = true;
        }
        catch( SQLException SQLE )
        {
            bSuccess = false;
            SQLE.printStackTrace();
        }
        finally
        {

            return bSuccess;
        }
    }

    /** Register AuthenticationServer object with the RMI registry.
     * @param name - name identifying the service in the RMI registry
     * @param create - create local registry if necessary
     * @throw RemoteException if cannot be exported or bound to RMI registry
     * @throw MalformedURLException if name cannot be used to construct a valid URL
     * @throw IllegalArgumentException if null passed as name
     */
    public static void registerToRegistry(String name, Remote obj, boolean create) throws RemoteException, MalformedURLException
    {
        if (name == null)
        {
            throw new IllegalArgumentException(
                "registration name can not be null");
        }
        
        try
        {
            Naming.rebind(name, obj);
        }
        catch (RemoteException ex)
        {
            if (create)
            {
                Registry r = LocateRegistry.createRegistry(
                    Registry.REGISTRY_PORT);
                r.rebind(name, obj);
            }
            else
            {
                throw ex;
            }
        }
    }

    /** Main method.
     */
    public static void main(String[] args)
    {
        System.setSecurityManager(new RMISecurityManager());
        
        /*
        com.inexum.MicroAc.MicroProcessing.RMISocFac.
            RMISSLServerSocketFactory.seed();
         */
        try
        {
            AuthenticationServer obj = new AuthenticationServer ();
            //registerToRegistry("AuthenticationServer", obj, true);
            java.rmi.Naming.rebind("//localhost/AuthenticationServer", obj);
            while (true)
            {
                try
                {
                    Thread.currentThread().sleep(1000L);
                }
                catch (java.lang.InterruptedException e)
                {
                }
            }
        }
        catch (RemoteException ex)
        {
            ex.printStackTrace();
        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
    }
    
    /** A login request is made to the authentication server which will provide an
     * authenticator
     * @throws RemoteException Required RMI exception
     * @return Returns the interface to an authenticator
 */
    public IAuthenticator login() throws RemoteException 
    {
       /* ( NK 2001-06-17 ) Spawn a new authentiator to check the login info*/
        try
        {
            return new Authenticator( m_dbConn );
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
    
}

