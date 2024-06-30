/*
 * TicketStub.java
 *
 * Created on May 10, 2001, 4:16 PM
 */

package com.inexum.MicroAc.Merchant;

/** Contains the challenge phrase for a transaction. Also acts as a unique
 * session identifier.
 * @author rgoold
 * @version 
 */
public class TicketStub extends java.lang.Object
    implements java.io.Serializable, ITicket {
    
/** The challenge phrase.
 */        
    private byte[]  m_challenge;

    /** Creates new TicketStub */
    public TicketStub()
    {
    }
    
/** Creates new TicketStub using the specified string as challenge phrase.
 * @param challenge The challenge phrase to use.
 */    
    public TicketStub(String challenge)
    {
        m_challenge = challenge.getBytes();
    }
    
/** Creates new TicketStub using the specified challenge phrase.
 * @param challenge The challenge phrase.
 */    
    public TicketStub(byte[] challenge)
    {
        m_challenge = challenge;
    }
    
/** Gets the challenge phrase.
 * @return The challenge phrase.
 */    
    public byte[] getChallenge()
    {
        return m_challenge;
    }
    
/** Sets the challenge phrase.
 * @param challenge The challenge phrase.
 */    
    public void setChallenge(byte[] challenge)
    {
        m_challenge = challenge;
    }
    
/** Sets the challenge phrase from a string.
 * @param challenge The challenge phrase.
 */    
    public void setChallenge(String challenge)
    {
        m_challenge = challenge.getBytes();
    }
    
}

