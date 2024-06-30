/*
 * XmlIsoMessageFactory.java
 *
 * Created on July 11, 2004, 5:55 PM
 */

package com.inexum.util.pos;

import java.io.*;
import org.jpos.iso.*;
import org.jpos.util.*;
import org.jpos.iso.packager.*;
/**
 *
 * @author  inexum
 */
public class XmlIsoMessageFactory {
    
    ISOMsg mIsoMsg;
    
    /** Creates a new instance of XmlIsoMessageFactory */
    public XmlIsoMessageFactory() 
    {
        try
        {
            mIsoMsg = readMessage();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public ISOMsg defaultMessage()
    {
        return(mIsoMsg);
    }
    
    public ISOMsg readMessage() throws ISOException, IOException {
        FileInputStream fis = new FileInputStream (
            "C:\\Documents and Settings\\inexum\\My Documents\\work\\inexum\\MicroAc\\mac10\\etc/isomsg.xml"
        );
        byte[] b = new byte[fis.available()];
        fis.read (b);
        ISOMsg m = new ISOMsg ();
        m.setPackager (new XMLPackager());
        m.unpack (b);
        m.setHeader ("ISO".getBytes());
        m.setDirection (ISOMsg.INCOMING);
        return m;
    }
    
    public byte[] getImage (ISOMsg m) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream (b);
        o.writeObject (m);
        o.flush();
        return b.toByteArray();
    }
    
    public ISOMsg getISOMsg (byte[] buf) 
        throws IOException, ClassCastException, ClassNotFoundException
    {
        ByteArrayInputStream b = new ByteArrayInputStream(buf);
        ObjectInputStream o = new ObjectInputStream (b);
        return (ISOMsg) o.readObject();
    }
}
