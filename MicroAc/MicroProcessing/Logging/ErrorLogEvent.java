//Source file: C:\Development\com\inexum\Nickel\eMoneyAdmin\Logging\ErrorLogEvent.java

package com.inexum.MicroAc.MicroProcessing.Logging;


public class ErrorLogEvent extends LogEvent 
{
    public static final String  c_ProfileUpdateFailed   = "Profile Update Failed";
    
    // For storing stack traces
    private java.io.OutputStream    m_stackTraceOut;
    
    public ErrorLogEvent( Object source, String eventType ) 
    {
        super( source, eventType );
    }
    
    public java.io.PrintStream getTraceStream ()
    {
        return new java.io.PrintStream(m_stackTraceOut);
    }
}
