/*
 * TicketAgentFactory.java
 *
 * Created on June 5, 2001, 1:48 PM
 */

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.MicroAc.Exceptions.MissingAgentException;
import com.inexum.util.ConfigurationManager;

/**
 *
 * @author  rgoold
 * @version 
 */
public class TicketAgentFactory extends Object
{
    static private ConfigurationManager c_Msgs = ConfigurationManager.Instance();
    
    static private java.util.Map    m_MapByTicket;
    static private java.util.Map    m_MapByProduct;

    static
    {
        m_MapByTicket = java.util.Collections.synchronizedMap(
            new java.util.HashMap());
        m_MapByProduct = java.util.Collections.synchronizedMap(
            new java.util.HashMap());
    }
    
    public static void Register(Class ticketType, Class productType,
        ITicketAgent agent)
    {
        m_MapByTicket.put(ticketType, agent);
        m_MapByProduct.put(productType, agent);
    }
    
    public static ITicketAgent AgentForTicket(Class ticketType)
        throws MissingAgentException
    {
        if (!m_MapByTicket.containsKey(ticketType))
        {
            throw new MissingAgentException(
                c_Msgs.getString("MissingAgentError"));
        }
        return (ITicketAgent)m_MapByTicket.get(ticketType);
    }
    
    public static ITicketAgent AgentForProduct(Class productType)
        throws MissingAgentException
    {
        if (!m_MapByProduct.containsKey(productType))
        {
            throw new MissingAgentException(
                c_Msgs.getString("MissingAgentError"));
        }
        return (ITicketAgent)m_MapByProduct.get(productType);
    }

}

