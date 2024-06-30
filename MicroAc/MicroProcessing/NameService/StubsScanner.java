/* StubsScanner.java
 *
 * Generated from Stubs.dtd on July 25, 2001, 4:45 PM */
package com.inexum.MicroAc.MicroProcessing.NameService;

import com.inexum.util.ConfigurationManager;

/**
 * This is a scanner of DOM tree.
 *
 * Example:
 * <pre>
 *     javax.xml.parsers.DocumentBuilderFactory builderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
 *     javax.xml.parsers.DocumentBuilder builder = builderFactory.newDocumentBuilder();
 *     org.w3c.dom.Document document = builder.parse (new org.xml.sax.InputSource (???));
 *     <font color="blue">StubsScanner scanner = new StubsScanner (document);</font>
 *     <font color="blue">scanner.scanDocument();</font>
 * </pre>
 *
 * @see org.w3c.dom.Document
 * @see org.w3c.dom.Element
 * @see org.w3c.dom.NamedNodeMap
 */
public class StubsScanner
{
/** org.w3c.dom.Document document */
    org.w3c.dom.Document document;
    
    private java.util.Map   m_camas;
    private java.util.Map   m_mamas;
    private StubEntry       m_currentEntry;
    
    private class StubEntry extends java.lang.Object
    {
        private String  m_name;
        private String  m_stubData;
        public StubEntry()                          {                        }
        public void setName (String name)           { m_name = name;         }
        public String getName ()                    { return m_name;         }
        public void setStubData (String stubData)   { m_stubData = stubData; }
        public String getStubData ()                { return m_stubData;     }
    }
    
/** Create new StubsScanner with org.w3c.dom.Document. */
    public StubsScanner (org.w3c.dom.Document document)
    {
        this.document = document;
    }
    
    public java.util.Map getCamas ()
    {
        return m_camas;
    }
    public java.util.Map getMamas ()
    {
        return m_mamas;
    }
    
/** Scan through org.w3c.dom.Document document. */
    public void scanDocument ()
    {
        org.w3c.dom.Element element = document.getDocumentElement ();
        if ((element != null) && element.getTagName ().equals ("Stubs"))
        {
            scanElement_Stubs (element);
        }
    }
    
/** Scan through org.w3c.dom.Element named Stubs. */
    void scanElement_Stubs (org.w3c.dom.Element element)
    { // <Stubs>
        m_camas = new java.util.HashMap();
        m_mamas = new java.util.HashMap();
        // element.getValue();
        org.w3c.dom.NodeList nodes = element.getChildNodes ();
        for (int i = 0; i < nodes.getLength (); i++)
        {
            org.w3c.dom.Node node = nodes.item (i);
            switch (node.getNodeType ())
            {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element)node;
                    if (nodeElement.getTagName ().equals ("Cama"))
                    {
                        scanElement_Cama (nodeElement);
                    }
                    if (nodeElement.getTagName ().equals ("Mama"))
                    {
                        scanElement_Mama (nodeElement);
                    }
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
                case org.w3c.dom.Node.TEXT_NODE:
                    // ((org.w3c.dom.Text)node).getData();
                    break;
            }
        }
    }
    
/** Scan through org.w3c.dom.Element named Cama. */
    void scanElement_Cama (org.w3c.dom.Element element)
    { // <Cama>
        m_currentEntry = new StubEntry();
        // element.getValue();
        org.w3c.dom.NamedNodeMap attrs = element.getAttributes ();
        for (int i = 0; i < attrs.getLength (); i++)
        {
            org.w3c.dom.Attr attr = (org.w3c.dom.Attr)attrs.item (i);
            if (attr.getName ().equals ("name"))
            { // <Cama name="???">
                m_currentEntry.setName(attr.getValue());
            }
        }
        org.w3c.dom.NodeList nodes = element.getChildNodes ();
        for (int i = 0; i < nodes.getLength (); i++)
        {
            org.w3c.dom.Node node = nodes.item (i);
            switch (node.getNodeType ())
            {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element)node;
                    if (nodeElement.getTagName ().equals ("Stub"))
                    {
                        scanElement_Stub (nodeElement);
                    }
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
                case org.w3c.dom.Node.TEXT_NODE:
                    // ((org.w3c.dom.Text)node).getData();
                    break;
            }
        }
        m_camas.put(m_currentEntry.getName(), m_currentEntry.getStubData());
    }
    
/** Scan through org.w3c.dom.Element named Mama. */
    void scanElement_Mama (org.w3c.dom.Element element)
    { // <Mama>
        m_currentEntry = new StubEntry();
        // element.getValue();
        org.w3c.dom.NamedNodeMap attrs = element.getAttributes ();
        for (int i = 0; i < attrs.getLength (); i++)
        {
            org.w3c.dom.Attr attr = (org.w3c.dom.Attr)attrs.item (i);
            if (attr.getName ().equals ("name"))
            { // <Mama name="???">
                m_currentEntry.setName(attr.getValue());
            }
        }
        org.w3c.dom.NodeList nodes = element.getChildNodes ();
        for (int i = 0; i < nodes.getLength (); i++)
        {
            org.w3c.dom.Node node = nodes.item (i);
            switch (node.getNodeType ())
            {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element)node;
                    if (nodeElement.getTagName ().equals ("Stub"))
                    {
                        scanElement_Stub (nodeElement);
                    }
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
                case org.w3c.dom.Node.TEXT_NODE:
                    // ((org.w3c.dom.Text)node).getData();
                    break;
            }
        }
        m_mamas.put(m_currentEntry.getName(), m_currentEntry.getStubData());
    }
    
/** Scan through org.w3c.dom.Element named Stub. */
    void scanElement_Stub (org.w3c.dom.Element element)
    { // <Stub>
        // element.getValue();
        org.w3c.dom.NodeList nodes = element.getChildNodes ();
        for (int i = 0; i < nodes.getLength (); i++)
        {
            org.w3c.dom.Node node = nodes.item (i);
            switch (node.getNodeType ())
            {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    // org.w3c.dom.Element nodeElement = (org.w3c.dom.Element)node;
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
                case org.w3c.dom.Node.TEXT_NODE:
                    // Eliminate whitespace from the text.
                    String data = ((org.w3c.dom.Text)node).getData();
                    StringBuffer trimmedData = new StringBuffer(data.length());
                    char[] dataBytes = data.toCharArray();
                    for (int j = 0; j < dataBytes.length; j++)
                    {
                        if (!Character.isWhitespace(dataBytes[j]))
                        {
                            trimmedData.append(dataBytes[j]);
                        }
                    }
                    m_currentEntry.setStubData(trimmedData.toString());
                    //m_currentEntry.setStubData(((org.w3c.dom.Text)node).getData());
                    break;
            }
        }
    }
    
    public static void main(String[] args)
    {
        try
        {
            java.io.FileInputStream stubIn = new java.io.FileInputStream(
                ConfigurationManager.Instance().getURL("stub.XmlFilePath").getFile() );
            javax.xml.parsers.DocumentBuilderFactory builderFactory
                = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder
                = builderFactory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(
                new org.xml.sax.InputSource(stubIn));
            StubsScanner scanner = new StubsScanner(document);
            scanner.scanDocument();
            java.util.Map camas = scanner.getCamas();
            java.util.Map mamas = scanner.getMamas();
            System.out.println(camas);
            System.out.println(mamas);
        }
        catch (java.io.FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (java.io.IOException e)
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
    
}

