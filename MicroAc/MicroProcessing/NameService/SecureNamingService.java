/*
 * SecureNamingServiceImpl.java
 *
 * Created on June 11, 2001, 6:14 PM
 */

package com.inexum.MicroAc.MicroProcessing.NameService;

import com.inexum.MicroAc.MicroProcessing.Transaction.*;
import com.inexum.MicroAc.Exceptions.UnknownProcessorException;
import com.inexum.Encoding.Base64;
import com.inexum.util.ConfigurationManager;

import java.security.PublicKey;
import java.security.SignedObject;
import java.security.Signature;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/** Unicast remote object implementing ISecureNamingService interface.
 *
 * @author  rgoold
 * @version 
 */
public class SecureNamingService extends UnicastRemoteObject
    implements ISecureNamingService
{
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
    
    private java.util.Map   m_camaStubMap;
    private java.util.Map   m_mamaStubMap;
    private java.util.Map   m_camaKeyMap;
    private java.util.Map   m_mamaKeyMap;
    
    private org.w3c.dom.Document    m_stubDoc;
    
    private void loadStubs ()
    {
        String xmlPath = c_rsrcs.getString("stub.XmlFilePath");
        java.io.File stubFile = new java.io.File(xmlPath);
        java.io.File dtdFile = new java.io.File(
                                c_rsrcs.getURL("stub.DtdFilePath").getFile());
        if (!stubFile.exists())
        {
            org.w3c.dom.DOMImplementation domImpl = org.apache.crimson.tree.
                DOMImplementationImpl.getDOMImplementation();
            org.w3c.dom.DocumentType docType = domImpl.createDocumentType(
                "Stubs", null, "file://" + xmlPath);
//                "Stubs", null, "file://" + dtdFile.getAbsolutePath());
            m_stubDoc = domImpl.createDocument(null, "Stubs", docType);
            return;
        }
        
        try
        {
            // Parse the XML stub file
            java.io.FileInputStream stubIn
                = new java.io.FileInputStream(stubFile);
            javax.xml.parsers.DocumentBuilderFactory builderFactory
                = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            builderFactory.setValidating(true);
            javax.xml.parsers.DocumentBuilder builder
                = builderFactory.newDocumentBuilder();
            m_stubDoc = builder.parse(new org.xml.sax.InputSource(stubIn));
            
            // Scan it
            StubsScanner scanner = new StubsScanner(m_stubDoc);
            scanner.scanDocument();
            
            // Retrieve the CAMA and MAMA stubs
            java.util.Map camas = scanner.getCamas();
            java.util.Map mamas = scanner.getMamas();
            
            // Decode them (from Base64)
            java.util.Iterator iterator = camas.keySet().iterator();
            while (iterator.hasNext())
            {
                String name = iterator.next().toString();
                m_camaStubMap.put(name, (ISessionFactory)Base64.DecodeObject(
                    camas.get(name).toString().getBytes("ISO-8859-1")));
            }
            iterator = mamas.keySet().iterator();
            while (iterator.hasNext())
            {
                String name = iterator.next().toString();
                m_mamaStubMap.put(name, (ISessionFactory)Base64.DecodeObject(
                    mamas.get(name).toString().getBytes("ISO-8859-1")));
            }
        }
        catch (java.io.FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        catch (org.w3c.dom.DOMException e)
        {
            e.printStackTrace();
        }
        catch (org.xml.sax.SAXException e)
        {
            e.printStackTrace();
        }
        catch (javax.xml.parsers.ParserConfigurationException e)
        {
            e.printStackTrace();
        }
    }
    
    public void writeStubs ()
        throws java.io.IOException
    {
        java.io.File stubFile = new java.io.File(c_rsrcs.getString("stub.XmlFilePath"));
        java.io.FileOutputStream stubOut = null;
        try
        {
            // Create a new stream to clear the file.
            try
            {
                stubOut = new java.io.FileOutputStream(stubFile);
            }
            catch (java.io.FileNotFoundException e)
            {
                stubFile.createNewFile();
                stubOut = new java.io.FileOutputStream(stubFile);
            }
            
            /* (2001-07-30 RTG) Originally I was using the TransformerFactory
             * and Transformer classes to this, which is the 'correct' way to
             * do it, but that was stripping out the !DOCTYPE tag. Calling
             * the Apache Crimson implementation directly solves the problem.
             */
            org.apache.crimson.tree.XmlDocument xmlDoc
                = (org.apache.crimson.tree.XmlDocument)m_stubDoc;
            xmlDoc.write(stubOut);
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            if (stubOut != null)
            {
                try
                {
                    stubOut.close();
                }
                catch (java.io.IOException e)
                {
                    // Ignore
                }
            }
        }
    }
    
    public void registerCAMA(final String name,
        java.security.SignedObject factoryStub)
        throws RemoteException, UnknownProcessorException,
        java.security.SignatureException, java.io.IOException
    {
        try
        
        {
             register(true, name, factoryStub);
         
        }
        catch (UnknownProcessorException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch (java.security.SignatureException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
    
    public void registerMAMA(final String name,
        java.security.SignedObject factoryStub)
        throws java.rmi.RemoteException, UnknownProcessorException,
        java.security.SignatureException, java.io.IOException
    {
        try
        {
            register(false, name, factoryStub);
        }
        catch (UnknownProcessorException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch (java.security.SignatureException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
    
    private void verifyStub (PublicKey key,
        java.security.SignedObject factoryStub)
        throws java.security.SignatureException
    {
        try
        {
            // Verify the signature
            Signature verificationEngine
                = Signature.getInstance(c_rsrcs.getString("key.Algorithm"));

            if (!factoryStub.verify(key, verificationEngine))
            {
                throw new java.security.SignatureException();
            }
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (java.security.InvalidKeyException e)
        {
            e.printStackTrace();
            throw new java.security.SignatureException(e.getLocalizedMessage());
        }
        catch (java.security.SignatureException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
    
    private void register(boolean isCama, final String name,
        java.security.SignedObject factoryStub)
        throws UnknownProcessorException,
        java.security.SignatureException, java.io.IOException
    {
        java.util.Map keyMap = isCama ? m_camaKeyMap : m_mamaKeyMap;
        java.util.Map stubMap = isCama ? m_camaStubMap : m_mamaStubMap;
        
        if (keyMap.containsKey(name.toLowerCase()))
        {
            verifyStub((PublicKey)keyMap.get(name.toLowerCase()), factoryStub);
            try
            {
                stubMap.put(name, factoryStub.getObject());
                // Stick it into the XML tree
                try
                {
                    // <Stub> data
                    java.rmi.server.RemoteStub stub
                        = (java.rmi.server.RemoteStub)stubMap.get(name);
                    org.w3c.dom.Text stubText
                        = m_stubDoc.createTextNode(
                        new String(Base64.Encode(stub)));
                    
                    // <Stub> element
                    org.w3c.dom.Element stubElement
                        = m_stubDoc.createElement("Stub");
                    stubElement.appendChild(stubText);
                    
                    // <Cama> or <Mama> element
                    org.w3c.dom.Element processorElement
                        = m_stubDoc.createElement(isCama ? "Cama" : "Mama");
                    processorElement.setAttribute("name", name);
                    processorElement.appendChild(stubElement);
                    
                    // <Stubs> element
                    org.w3c.dom.Element rootElement
                        = m_stubDoc.getDocumentElement();
                    
                    // Search for duplicates to replace
                    org.w3c.dom.NodeList processors
                        = rootElement.getChildNodes();
                    int i = 0;
                    for (; i < processors.getLength(); i++)
                    {
                        org.w3c.dom.Node processor = processors.item(i);
                        if (processor.getNodeName().equals(
                            isCama ? "Cama" : "Mama")
                            && processor.getAttributes().getNamedItem("name")
                            .getNodeValue().equals(name))
                        {
                            rootElement.replaceChild(processorElement,
                                processor);
                            break;
                        }
                    }
                    if (i == processors.getLength())
                    {
                        rootElement.appendChild(processorElement);
                    }
                    
                   //ntk  writeStubs();
                }
                catch (org.w3c.dom.DOMException e)
                {
                    e.printStackTrace();
                    /* (2001-07-28 RTG) Any actions necessary? Offline storage
                     * of stubs is useful but not critical to operation.
                     * Perhaps just a message to the console or an entry in
                     * the error log DB table to allow the problem to be
                     * investigated later.
                     */
                }
                catch (java.io.IOException e)
                {
                    e.printStackTrace();
                    /* (2001-07-28 RTG) Any actions necessary? Offline storage
                     * of stubs is useful but not critical to operation.
                     * Perhaps just a message to the console or an entry in
                     * the error log DB table to allow the problem to be
                     * investigated later.
                     */
                }
            }
            catch (java.lang.ClassNotFoundException e)
            {
                e.printStackTrace();
                throw new java.io.IOException(e.getLocalizedMessage());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new java.io.IOException(e.getLocalizedMessage());
            }
        }
        else
        {
            java.util.Iterator iterator = keyMap.keySet().iterator();
            while (iterator.hasNext())
            {
                System.out.println("  " + iterator.next().toString());
            }
            throw new UnknownProcessorException();
        }
    }
    
    public ISessionFactory lookupCAMA(final String name)
        throws RemoteException, UnknownProcessorException
    {
     
        if (m_camaStubMap.containsKey(name))
        {
            return (ISessionFactory)m_camaStubMap.get(name);
        }
        else
        {
            throw new UnknownProcessorException(name);
        }
    }
    
    public ISessionFactory lookupMAMA(final String name)
        throws RemoteException, UnknownProcessorException
    {
 
        if (m_mamaStubMap.containsKey(name))
        {
            return (ISessionFactory)m_mamaStubMap.get(name);
        }
        else
        {
            throw new UnknownProcessorException(name);
        }
    }

    /** Constructs SecureNamingServiceImpl object and exports it on default port.
     */
    public SecureNamingService(java.util.Map camaKeys, java.util.Map mamaKeys)
        throws RemoteException
    {
        super();
        m_camaStubMap = new java.util.HashMap();
        m_mamaStubMap = new java.util.HashMap();
        m_camaKeyMap = (camaKeys != null) ? camaKeys : new java.util.HashMap();
        m_mamaKeyMap = (mamaKeys != null) ? mamaKeys : new java.util.HashMap();
        loadStubs();
    }

    /** Constructs SecureNamingServiceImpl object and exports it on specified port.
     * @param port The port for exporting
     */
    public SecureNamingService(java.util.Map camaKeys, java.util.Map mamaKeys,
        int port)
        throws RemoteException
    {
        super(port);
        m_camaStubMap = new java.util.HashMap();
        m_mamaStubMap = new java.util.HashMap();
        m_camaKeyMap = (camaKeys != null) ? camaKeys : new java.util.HashMap();
        m_mamaKeyMap = (mamaKeys != null) ? mamaKeys : new java.util.HashMap();
        loadStubs();
    }

    /** Register SecureNamingServiceImpl object with the RMI registry.
     * @param name - name identifying the service in the RMI registry
     * @param create - create local registry if necessary
     * @throw RemoteException if cannot be exported or bound to RMI registry
     * @throw MalformedURLException if name cannot be used to construct a valid URL
     * @throw IllegalArgumentException if null passed as name
     */
    public static void registerToRegistry(String name,
        Remote obj, boolean create)
        throws RemoteException, MalformedURLException
    {
        if (name == null)
        {
            throw new IllegalArgumentException(
                "registration name can not be null");
        }

        try
        {
            Naming.rebind(name, obj);
        }
        catch (RemoteException e)
        {
            if (create)
            {
                Registry r = LocateRegistry.createRegistry(
                    Registry.REGISTRY_PORT);
                r.rebind(name, obj);
            }
            else
            {
                throw e;
            }
        }
    }

    /** Main method.
     */
    public static void main(String[] args)
    {
        System.setSecurityManager(new RMISecurityManager());

        try
        {
            SecureNamingService obj = new SecureNamingService (null, null);
            registerToRegistry("SecureNamingService", obj, true);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
    
}

