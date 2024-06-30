/*
 * Debug.java
 *
 * Created on March 27, 2002, 9:11 PM
 */

package com.inexum.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 *
 * Example:
 *     public void method ()
 *     {
 *         if (Debug.on)                   // These 
 *           Debug.out("Debug message");   // are removed
 *     }
 *
 * @author  nvojinov
 * @version 
 */
public class Debug extends Exception
{
    //set dmode to false to compile out debug code
    public static final boolean on = true;
    public static final short g_basic = 1;
    public static final short g_net = 2;
    public static final short g_security = 4;
    public static final short g_db = 8;
    public static final short g_transaction = 16;
    public static final short g_profile = 32;
    public static final short g_ui = 64;
    public static final short  g_all = 32767; //ntk changed max value ( a java short is signed, the char is like an unsigned short )

    public static void out()
    {
        if( on )
        {
            out( g_all, "" );
        }
    }

    public static void out( short mode )
    {
        if( on )
        {
            out( mode, "" );
        }
    }

    public static void out( String message )
    {
        if( on )
        {
            out( g_all, message );
        }
    }

    public static void out( short mode, String message )
    {
        if( on )
        {
            if( g_mode == 0 ) 
                DebugCollector.Instance(); // initialize the debugger
            
            if( ( mode & g_mode ) == 0 ) 
                return;
            
            String now = (new SimpleDateFormat().format( new Date() ));
            String thread = Thread.currentThread().getName();
            
            try { throw new Debug(); }
            catch( Debug debug )
            {
                DebugWriter writer = new DebugWriter( thread, now, message );
                debug.printStackTrace( new PrintWriter( writer ) );
                writer.out();
                try
                {
                    writer.flush();
                }
                catch( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void out( Exception exception )
    {
        out( g_all, exception );
    }
    
    public static void out( short mode, Exception exception )
    {
        if( on )
        {
            if( g_mode == 0 ) 
                DebugCollector.Instance(); // initialize the debugger
            
            if( ( mode & g_mode ) == 0 ) 
                return;
            
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            exception.printStackTrace( new PrintStream( bStream ) );
            out( mode, bStream.toString() );
        }
    }
    
    public static void addMode( short mode ) 
    {
        g_mode = (short)((int)g_mode | (int)mode); 
    } 

    public static short getMode() { return( g_mode ); }
    
    public static void setName( String name ) { g_name = name; } 
    
    public static String getName() { return( g_name ); } 

    private static short g_mode = 0; 
    private static String g_name = null; 
}
