/*
 * ConfigurationResourceBundle.java
 *
 * Created on March 27, 2002, 9:11 PM
 */

package com.inexum.util;

import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author  nvojinov
 * @version 
 */

public class PropertyResourceBundleChain extends ResourceBundle 
{
    /** Creates new PropertyResourceBundleChain */
    public PropertyResourceBundleChain() throws IOException
    {
        String propertyPath = makePath( 
			ConfigurationManager.GetPropertyResourcePath() );
        URLClassLoader classLoader = (URLClassLoader)this.getClass().getClassLoader();
        URL propertyURL = classLoader.findResource( propertyPath );
        FileInputStream propertyStream = new FileInputStream( propertyURL.getFile() );
        m_Properties = new PropertyResourceBundle( propertyStream );
    }

    public final void parent( ResourceBundle parent)
    {
        setParent( parent );
    }
    
    private final String  makePath( String properties )
    {
        if( properties.indexOf( '/' ) > -1 ) return( properties );
        return( properties.replace( '.', '/' ) + ".properties" );
    }
    
    public java.util.Enumeration getKeys() 
    {
        return( m_Properties.getKeys() );
    }
    
    protected java.lang.Object handleGetObject(java.lang.String str) throws java.util.MissingResourceException 
    {
        return( m_Properties.handleGetObject( str ) );
    }
    
    private PropertyResourceBundle m_Properties = null;
}
