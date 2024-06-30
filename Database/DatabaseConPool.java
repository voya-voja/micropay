/*
 * DBConnectionPool.java
 *
 * Created on June 25, 2001, 8:36 AM
 */

package com.inexum.Database;

import com.inexum.util.ConfigurationManager;

import java.sql.*;
import javax.sql.*;
import oracle.jdbc.driver.*;
import oracle.jdbc.pool.*;


/**
 *
 * @author  nkapov
 * @version
 */
public  class DatabaseConPool implements IDBConnectionPool
{
// nmv    public static final String  c_MicroAcPool =  "MicroAc Pool";
// nmv    public static final String  c_OasisPool   =  "Oasis Pool"; 
    
    private static ConfigurationManager m_Config = ConfigurationManager.Instance();
    
    private static DatabaseConPool m_DBConnPool = null;
    
    private static OracleConnectionCacheImpl m_microAcODS = null;
// nmv    private static OracleConnectionCacheImpl m_oasisODS = null;
    
    
    public static DatabaseConPool getDatabaseConPool() throws SQLException
    {
        if( m_DBConnPool == null )
        {
            //set up the connection cache for the microac database
            m_microAcODS = new OracleConnectionCacheImpl();
            
            m_microAcODS.setPortNumber( Integer.parseInt(
                                    m_Config.getString("db.Port") ) );

            m_microAcODS.setDatabaseName( m_Config.getString( "db.SID" ) ) ;
            m_microAcODS.setServerName( m_Config.getString( "db.Host" ) );
            m_microAcODS.setDriverType( m_Config.getString( "db.Driver" ) );

            m_microAcODS.setMaxLimit(Integer.parseInt(
                                       m_Config.getString("db.MaxConn") ) );
            
            m_microAcODS.setCacheScheme( OracleConnectionCacheImpl.DYNAMIC_SCHEME );
            
/*  nmv          //set up the connection cache for the oracle database
            m_oasisODS = new OracleConnectionCacheImpl();
            
            m_oasisODS.setPortNumber( Integer.parseInt(
                                    m_dbConnProps.getString("oasisDBPort") ) );
            m_oasisODS.setDatabaseName( m_dbConnProps.getString( "oasisDBSID" ) ) ;
System.out.println("DBSID: " + m_dbConnProps.getString( "oasisDBSID" ) );
            m_oasisODS.setServerName( m_dbConnProps.getString( "oasisDBHost" ) );
System.out.println( "DBHost: " + m_dbConnProps.getString( "oasisDBHost" ) );
            m_oasisODS.setDriverType( m_dbConnProps.getString( "oasisDBDriver" ) );
            m_oasisODS.setMaxLimit(Integer.parseInt(
                                       m_dbConnProps.getString("oasisDBMaxConn") ) );
            
            m_oasisODS.setCacheScheme( OracleConnectionCacheImpl.DYNAMIC_SCHEME );
nmv */
            m_DBConnPool = new DatabaseConPool();
        }
        
        return m_DBConnPool;
    }
    
    /** Creates new DBConnectionPool */
    private DatabaseConPool()
    {
    }
    
    
// nmv    public Connection getConnection( String poolName, String userName, 
    public Connection getConnection( String userName, 
                                            String password )throws SQLException
    {
/* nmv        System.out.println("Connecting to pool " + poolName);
        return ( poolName.equals(c_MicroAcPool) )? 
                            m_microAcODS.getConnection( userName, password ):
                            m_oasisODS.getConnection( userName, password );    
nmv */  return ( m_microAcODS.getConnection( userName, password ) );
    }
    
    protected void close()
    {
        try{
            m_microAcODS.close();
// nmv            m_oasisODS.close();
        }catch( SQLException SQLE )
        {
            //no need to do anything here
        }
    }
    
    public static void main( String[] args ) throws SQLException
    {
        DatabaseConPool dbPool = DatabaseConPool.getDatabaseConPool();
        ConfigurationManager  config =  ConfigurationManager.Instance();
        Connection conn = dbPool.getConnection( config.getString("db.UserID"),
								config.getString("db.Password") );

        conn.setAutoCommit(false);
        String query = "select * from Profile";
        PreparedStatement stmt = conn.prepareStatement( "select * from Profile" );
        ResultSet rset = stmt.executeQuery(query);
        
        while( rset.next() )
        {
            System.out.println( rset.getString( "UserID" ) );
            System.out.println( rset.getString( "Password" ) );
            System.out.println();
        }
        dbPool.close();
    }
    
  
    
}
