/*
 * ConfigurationManager.java
 *
 * Created on March 27, 20021, 8:36 AM
 */

package com.inexum.util;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.io.IOException;
import java.util.HashMap;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;

// A singleton that provides MicroAc application configuration.
public  class ConfigurationManager
{
    protected static String g_ParamDelimiter = ",";

    // A static method that provides the access to the configuration
    public static final ConfigurationManager Instance()
    {
        if( g_ConfigurationManager == null )
        {
            try
            {
                g_ConfigurationManager = new ConfigurationManager();
            }
            catch( MissingResourceException e )
            {
                e.printStackTrace();
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
        }
        return( g_ConfigurationManager );
    }

    // Return the lates property resource path
    public static final String GetPropertyResourcePath()
    {
        return( g_PropertyResourcePath );
    }

    // Return a property that is an object
    public final Object getObject( String key ) throws NullPointerException, 
                                            MissingResourceException
    {
            return( m_MicroAcProperties.getObject( key ) );
    }

    // Return a property that is a string
    public final String getString( String key ) throws NullPointerException, 
                                            MissingResourceException, 
                                            ClassCastException
    {
            return( m_MicroAcProperties.getString( key ) );
    }

    // Return a property that is a string array
    public final String[] getStringArray( String key ) throws NullPointerException, 
                                            MissingResourceException
    {
            return( m_MicroAcProperties.getStringArray( key ) );
    }

    public final String[] getArrayFromString( String key ) throws NullPointerException, 
                                            MissingResourceException, 
                                            ClassCastException
 
    // Return a string array from a property that is a string
    {
          StringTokenizer tokenizer = new StringTokenizer( getString( key ),
											g_ParamDelimiter );
          String[] parameters = new String[ tokenizer.countTokens() ];
          int pCount = 0;
          while ( tokenizer.hasMoreTokens() ) 
          { 
               parameters[pCount] = tokenizer.nextToken().trim();
               pCount++;
          }
          return( parameters );
    }

    // Return an URL from a property that is a string
    public final URL getURL( String key ) throws NullPointerException, 
                                            MissingResourceException
    {
        String path = getString( key );
        URLClassLoader classLoader = (URLClassLoader)this.getClass().getClassLoader();
        return( classLoader.findResource( path ) );
    }

    // Return a resources starting with a specified domain
    public final ResourceBundle getBundle( String domain ) throws NullPointerException, 
                                            MissingResourceException 
    {
        return( (ResourceBundle)m_Domains.get(domain) );
    }

    // Creates a new instance of ConfigurationManager
    private ConfigurationManager() throws MissingResourceException,
                                            IOException
    {
        loadProperties();
    }

    // Loads properties for default or provided domains
    private final void loadProperties() throws MissingResourceException,
                                                        IOException
    {
        String[] domainNames = loadDefaultProperties();
        
        if( domainNames == null ) 
            return; // default domains to be used
        //load  custom domains
        loadProperties( domainNames );
    }
 
    // Loads properties for provided domains
    protected final void loadProperties( String[] domainNames ) 
                                                throws MissingResourceException,
                                                        IOException
    {
        // loading properties for requested order of domains
        HashMap domains = new HashMap();
        ResourceBundle microAcProperties = null; 

        for( int at = domainNames.length; --at >= 0; )
        {
            String domainName = domainNames[ at ];
            String key = "domain." + domainName;
            try    // if domain is defined it has to have defined property or class
            {
                SetPropertyResourcePath( 
                        m_MicroAcProperties.getString( key + ".properties" ) );
                PropertyResourceBundleChain bundle = 
                                                (PropertyResourceBundleChain)
                                                ResourceBundle.getBundle( 
                                "com.inexum.util.PropertyResourceBundleChain" );

                bundle.parent( microAcProperties );
                microAcProperties = bundle;
                domains.put( domainName, microAcProperties );
                continue;
            }
            catch( MissingResourceException e )
            { }

            String bundlePath = m_MicroAcProperties.getString( key + ".class" );
            ConstantsBundle bundle = (ConstantsBundle)
                                        ResourceBundle.getBundle( bundlePath );
            bundle.parent( microAcProperties );
            microAcProperties = bundle;
            domains.put( domainName, microAcProperties );
        }
        // override default configuration
        m_Domains = domains;
        m_MicroAcProperties = microAcProperties;
    }

    private final String[] findDomains(ResourceBundle properties)
    {
        try
        {
            return( properties.getStringArray( "domains" ) );
        }
        catch( MissingResourceException e )
        {
            return( null );
        }
    }

    /* Load available default domains. If any overriedes the default "domains"
     * property, return the provided list of domains. By default:
     * domains = "microac", "institution", "network", "inexum" */
    private final String[] loadDefaultProperties() 
                                                throws MissingResourceException
    {
        String[] domains = null;
        m_MicroAcProperties = ResourceBundle.getBundle( 
                                            "com.inexum.util.InexumBundle" );
        m_Domains.put( "inexum", m_MicroAcProperties );
        try
        {
            ConstantsBundle networkBundle = (ConstantsBundle)
                    ResourceBundle.getBundle( "com.inexum.util.NetworkBundle" );
            
            domains = findDomains(networkBundle);
            if( domains != null ) return( domains );

            networkBundle.parent( m_MicroAcProperties );
            m_MicroAcProperties = networkBundle;
            
            m_Domains.put( "network", m_MicroAcProperties );
        }
        catch( MissingResourceException e )
        {} // if class is not present continue 

        try
        {
            ConstantsBundle institutionBundle = (ConstantsBundle)
                                    ResourceBundle.getBundle(
                                    "com.inexum.util.InstitutionBundle" );

            domains = findDomains(institutionBundle);
            if( domains != null ) return( domains );

            institutionBundle.parent( m_MicroAcProperties );
            m_MicroAcProperties = institutionBundle;

            m_Domains.put( "institutution", m_MicroAcProperties );
        }
        catch( MissingResourceException e )
        {} // if class is not present continue 

        try
        {
            SetPropertyResourcePath( "etc.microac" );
            PropertyResourceBundleChain microacBundle = 
                                (PropertyResourceBundleChain)
                                ResourceBundle.getBundle( 
                                "com.inexum.util.PropertyResourceBundleChain" );
            
            domains = findDomains(microacBundle);
            if( domains != null ) return( domains );
            
            microacBundle.parent( m_MicroAcProperties );
            m_MicroAcProperties = microacBundle;

            m_Domains.put( "microac", m_MicroAcProperties );
        }
        catch( MissingResourceException e )
        {} // if file is not present continue
        return( domains );
    }

    // Save the latest property resource path
    private static void SetPropertyResourcePath( String path )
    {
        g_PropertyResourcePath = path;
    }
    
    public static void main(String[] args)
    {
       System.out.println( "START" );
        InexumBundle m_bundle = (InexumBundle)ResourceBundle.getBundle( "com.inexum.util.InexumBundle");
        java.util.Enumeration enu = m_bundle.getKeys();
        while( enu.hasMoreElements() )
        {
            String key = (String)enu.nextElement();
            System.out.println( key );
//            System.out.println( m_bundle.getString(key));
        }
       System.out.println( "END" );
    }

    private HashMap m_Domains = new HashMap();
    private ResourceBundle m_MicroAcProperties = null;

    private static String g_PropertyResourcePath = null;
    private static ConfigurationManager g_ConfigurationManager = null;
}
