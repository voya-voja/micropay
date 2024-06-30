/*
 * DebugWriter.java
 *
 * Created on March 27, 2002, 9:11 PM
 */

package com.inexum.util;

import java.io.Writer;
/**
 *
 * @author  nvojinov
 * @version 
 */
public class DebugWriter extends Writer
{
    public DebugWriter( String thread, String time, String message )
    {
        m_thread = thread;
        m_time = time;
        m_message = message;
    }
    
    public void flush() throws java.io.IOException 
    {
    }
    
    public void write(char[] values, int param, int param2) throws java.io.IOException 
    {
        // 13 is a value of next line character
        if( (int)values[param] == 13 ) return;

        if( m_method != null )
        {
            m_indent++;
            return;
        }

        String line = new String( values, param, param2 );
        if( line.indexOf( "Debug.out" ) != -1 )
        {
             m_nextIsMethod = true;
             return;
        }
            
        if( m_nextIsMethod )
        {
            while( values[ param ] == ' ' || values[ param ] == '\t' )
                param++;
            param = param + 3; // to skip 'at '
            m_method = new String( values, param, param2 - param );
        }
    }
    
    public void close() throws java.io.IOException 
    {
    }
    
    public void out() 
    {
        String debugMessage = m_thread + "@";
        for( int indent = 0; indent < m_indent; indent++ )
            debugMessage = debugMessage + ' ';
        debugMessage = debugMessage + m_method + " [" + m_time + "]: '" +
                                                                m_message + "'";
        try
        {
            DebugCollector collector = DebugCollector.Instance();
            collector.queue().add( debugMessage );
        }
        catch ( Exception e )
        {
            System.out.println(debugMessage);
        }
        m_indent = 0;
    }
    
    String m_thread = null;
    String m_method = null;
    String m_time = null;
    String m_message = null;
    int m_indent = 0;
    boolean m_nextIsMethod = false;
}
