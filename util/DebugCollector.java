/*
 * DebugCollector.java
 *
 * Created on March 27, 2002, 9:11 PM
 */

package com.inexum.util;

import java.util.LinkedList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Date;



/**
 *
 * @author  nvojinov
 * @version 
 */
class DebugCollector implements Runnable
{
    public synchronized static DebugCollector Instance()
    {
        if( g_instance == null )
        {
            g_instance = new DebugCollector();
            if( (Debug.getMode() & Debug.g_all) == 0 ) return g_instance;
            
            Thread debugCollector = new Thread( g_instance );
            debugCollector.start();
        }
        return( g_instance );
    }

    public void run()
    {
        Thread.currentThread().setName( "Debug Collector" );
        while ( !m_halt )
        {
            try 
            {
                out( (String)m_queue.waitElement() );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
        
    }

    /** Stop the server. */    
    public void halt () { m_halt = true; }

    public ProcessingQueue queue() { return( m_queue ); }

    protected DebugCollector()
    {
        String debug = System.getProperty( "microac.debug" );
        if ( debug == null ) return;

        debug = debug.toLowerCase();
        if( debug.equalsIgnoreCase("y") 
            || debug.equalsIgnoreCase("yes") 
            || debug.equalsIgnoreCase("true") )
        {
            try
            {
                m_out = createDebugStream();
                String date = (new SimpleDateFormat()).format( new Date() );
                out("");
                out( "====== " + Debug.getName() + " STARTED:" + date + " ======");
                out("");
            }
            catch ( IOException  e )
            {
                System.out.println("Unable to create output file" );
                System.exit(-1);
            }
            m_queue = new ProcessingQueue();
            String options = System.getProperty( "microac.debug.option" );
		if( options == null )
            {
                Debug.addMode( Debug.g_all );
            }
		else
            {
                setOptions( options );
            }
        }
    }
    
    protected FileWriter createDebugStream() throws IOException
    {
        String path = System.getProperty( "debug.file" );
        if ( path == null )
        {
            path = defaultFilePath();
        }
        return( new FileWriter( path, true ) );
        
    }

    protected String defaultFilePath()
    {
        String path = System.getProperty( "java.io.tmpdir" );
        String debugName = Debug.getName();
        if( debugName == null )
            return( path + Thread.currentThread().getThreadGroup().getName() 
                                                                    + ".din" );
        return( path  + "/" + debugName + ".din" );
    }

    protected void out( String message )
    {
        try
        {
            m_out.write( message + "\n");
            m_out.flush();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }
    
    private void setOptions( String options )
    {
        int start = 0;
        int end = options.indexOf( '|' );
        if( end == -1 )
            end = options.length();
        while( start < end )
        {
            String option = options.substring( start, end - 1 );
            short optionValue = Short.parseShort( "debug." + 
                                c_rsrcs.getString( option ) );
            Debug.addMode( optionValue );

            start = end + 1;
            end = options.indexOf( '|', start );
            if( end == -1 )
                end = options.length();
        }
    }
    
    private static final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
    
    private static DebugCollector g_instance = null;
    
    private FileWriter m_out = null;

    /* a halt flag */
    private boolean m_halt = false;

    /* a debug message queue*/
    private ProcessingQueue m_queue = null;

    /* A unit test. */
    public static void main( String[] args ) 
    {
        Debug.setName( "debugUnitTest" );
        Debug.addMode( Debug.g_all );
        for( int threadCount = 0; threadCount < 5; threadCount++ )
        {
            final int threadCnt = threadCount;
            Thread t = new Thread()
            {
                public void run()
                {
                   
                    Thread.currentThread().setName( "Unit Test " + threadCnt );
                    java.util.Random rnd = new java.util.Random( (new Date()).getTime() );
                    for( short msgCount = 0; msgCount < 100; msgCount++ )
                    {
                        try{ sleep( (int)( 500 *rnd.nextDouble() ) );}
                        catch( java.lang.InterruptedException e ){ e.printStackTrace();}
                        
                        if( Debug.on )
                            com.inexum.util.Debug.out( msgCount, "message : " + msgCount + "." );
 
                    }
                }
            };
            t.start();
        }
    }
}
