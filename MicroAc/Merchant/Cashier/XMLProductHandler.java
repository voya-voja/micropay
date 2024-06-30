/*
 * XMLProductHandler.java
 *
 * Created on May 12, 2001, 4:49 PM
 */

package com.inexum.MicroAc.Merchant.Cashier;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.MicroAc.Exceptions.*;
import com.inexum.util.ConfigurationManager;

import java.util.*;
import java.io.*;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;

/**
 *
 * @author  rgoold
 * @version 
 */
public class XMLProductHandler extends java.lang.Object
    implements IProductHandler
{
    private final ConfigurationManager c_rsrcs = ConfigurationManager.Instance();
    
    private Map             m_productList;
    
    // Thrown in the event that the file could not be read.
    class InternalParseException extends java.lang.Exception
    {
        public InternalParseException()
        {
            super();
        }
        
        public InternalParseException(String message)
        {
            super(message);
        }
    }

    /** Creates new ProductHandlerImpl */
    public XMLProductHandler()
    {
        try
        {
            loadProducts();
        }
        catch (InternalParseException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Product productFromOffer(String offerID)
        throws UnknownOfferException
    {
        if (m_productList.containsKey(offerID))
        {
            return (Product)m_productList.get(offerID);
        }
        else
        {
            throw new UnknownOfferException(
                c_rsrcs.getString("UnknownOffer") + ": " + offerID);
        }
    }
    
    private Product createProduct(String identifier,
        String location, String cost, int duration)
    {
        Product product = null;
        if (duration == 0)
        {
            product = new OneShotProduct();
        }
        else
        {
            product = new ExpiryProduct();
        }
        product.setOfferID(identifier);
        product.setLocation(location);
        product.setCost(cost);
        product.setDuration(duration);
        return product;
    }
    
    private Node loadFromFile(String fileName)
        throws InternalParseException
    {
        Node warehouse = null;
        try
        {
            // Set up the document builder factory.
            DocumentBuilderFactory docFactory
                = DocumentBuilderFactory.newInstance();
            docFactory.setValidating(true);
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // We don't really "handle" parse errors, we just catch them,
            // so an error handling class declared inline will do.
            docBuilder.setErrorHandler(
                new org.xml.sax.ErrorHandler()
                {
                    public void fatalError(SAXParseException e)
                        throws SAXException
                    {
                    }
                    public void error (SAXParseException e)
                        throws SAXParseException
                    {
                        throw e;
                    }
                    public void warning(SAXParseException e)
                        throws SAXParseException
                    {
                        System.err.println("** "
                            + c_rsrcs.getString("ParseError") + ", "
                            + c_rsrcs.getString("LineNumber") + ": "
                            + e.getLineNumber() + ", URI "
                            + e.getSystemId());
                        System.err.println("   " + e.getLocalizedMessage());
                    }
                }
            );
            
            // Suck in the file and parse it.
            org.w3c.dom.Document document = docBuilder.parse(fileName);

            // The first child is the DTD. We want the second (the last) child
            // which has the actual tags with values.
            NodeList topLevelNodes = document.getChildNodes();
            for (int i = 0; i < topLevelNodes.getLength(); i++)
            {
                Node currentNode = topLevelNodes.item(i);
                if (currentNode.getNodeType() == Node.ELEMENT_NODE
                    && currentNode.getNodeName().equalsIgnoreCase("warehouse"))
                {
                    warehouse = currentNode;
                    break;
                }
                else if (i == topLevelNodes.getLength() - 1)
                {
                    // Last node, no warehouse tag.
                    throw new InternalParseException();
                }
            }
        }
        catch (org.xml.sax.SAXException e)
        {
            throw new InternalParseException(e.getLocalizedMessage());
        }
        catch (javax.xml.parsers.ParserConfigurationException e)
        {
            throw new InternalParseException(e.getLocalizedMessage());
        }
        catch (java.io.IOException e)
        {
            throw new InternalParseException(e.getLocalizedMessage());
        }
        
        return warehouse;
    }
    
    private void loadProducts()
        throws InternalParseException
    {
        Node warehouse = null;
        try
        {
            final String productFileName
                = c_rsrcs.getURL("merchant.ProductsDatabase").getFile();
            warehouse = loadFromFile(productFileName);
        }
        catch (MissingResourceException e)
        {
            throw new InternalParseException(e.getLocalizedMessage());
        }
        m_productList = Collections.synchronizedMap(new HashMap());
            
        // There's a single "warehouse" tag, which contains any number of
        // "product" tags. Iterate through them.
        NodeList products = warehouse.getChildNodes();
        for (int i = 0; i < products.getLength(); i++)
        {
            Node productNode = products.item(i);
            if (productNode.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }
            if (!productNode.getNodeName().equalsIgnoreCase("product"))
            {
                continue;
            }

            // Iterate through the values of the product.
            NodeList productChildren = productNode.getChildNodes();
            String productID = "";
            String productLocation = "";
            String productCost = "";
            int productDuration = 0;
            for (int j = 0; j < productChildren.getLength(); j++)
            {
                Node productAttribute = productChildren.item(j);
                if (productAttribute.getNodeType() != Node.ELEMENT_NODE)
                {
                    continue;
                }
                if (productAttribute.getNodeName()
                    .equalsIgnoreCase("identifier"))
                {
                    productID =
                        productAttribute.getFirstChild().getNodeValue();
                }
                else if (productAttribute.getNodeName()
                    .equalsIgnoreCase("cost"))
                {
                    productCost =
                        productAttribute.getFirstChild().getNodeValue();
                }
                else if (productAttribute.getNodeName().
                    equalsIgnoreCase("duration"))
                {
                    productDuration = Integer.parseInt(
                        productAttribute.getFirstChild().getNodeValue());
                }
                else if (productAttribute.getNodeName().
                    equalsIgnoreCase("location"))
                {
                    productLocation =
                        productAttribute.getFirstChild().getNodeValue();
                }
            }
            // Create a new Product and commit it to memory.
            Product product = createProduct(productID, productLocation,
                productCost, productDuration);
            m_productList.put(product.getOfferID(), product);
        }
    }
    
    public static void main (String[] args)
    {
        System.out.println("*** Testing ProductHandlerImpl.java ***");
        IProductHandler handler = new XMLProductHandler();
        System.out.println("Retrieving a real product.");
        try
        {
            Product z = handler.productFromOffer("zxcvbnm");
            System.out.println("Test succeeded.");
        }
        catch (com.inexum.MicroAc.Exceptions.UnknownOfferException e)
        {
            System.out.println("Unable to retrieve product: " + e.getMessage());
        }
        System.out.println("Retrieving a non-existant product (exception test).");
        try
        {
            Product z = handler.productFromOffer("zxcvanm");
            System.out.println("Test failed.");
        }
        catch (com.inexum.MicroAc.Exceptions.UnknownOfferException e)
        {
            System.out.println("Test succeeded.");
        }
    }
    
}

