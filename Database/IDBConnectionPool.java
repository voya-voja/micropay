/*
 * IDBConnectionPool.java
 *
 * Created on June 25, 2001, 9:06 AM
 */

package com.inexum.Database;

import java.sql.*;
import javax.sql.*;

/**
 *
 * @author  nkapov
 * @version 
 */
public interface IDBConnectionPool 
{

    public Connection getConnection(String userName, String password)
        throws SQLException;

}

