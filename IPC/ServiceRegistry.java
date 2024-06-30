/*
 * ServiceRegistry.java
 *
 * Created on November 8, 2001, 6:03 PM
 */

package com.inexum.IPC;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class ServiceRegistry extends java.lang.Object
{
    private static ServiceRegistry  m_Registry;
    
    public static ServiceRegistry getDefaultInstance()
    {
        if (m_Registry == null)
        {
            m_Registry = new ServiceRegistry();
        }
        return m_Registry;
    }
    
    private java.util.Map   m_services;

    /** Creates new ServiceRegistry */
    protected ServiceRegistry()
    {
        m_services = new java.util.HashMap();
    }
    
    public Object getServiceForName(String name)
    {
        return m_services.get(name);
    }
    
    public void setServiceForName(Object service, String name)
    {
        m_services.put(name, service);
    }

}

