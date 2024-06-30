/*
 * MPAppletLink.java
 *
 * Created on April 6, 2004, 11:08 PM
 */

package com.inexum.MicroAc.PFL.Microproduct;

import com.inexum.MicroAc.PFL.MPLink;
import com.inexum.util.ConfigurationManager;

import java.net.URL;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.DataInputStream;
import java.awt.Font;

/**
 *
 * @author  inexum
 */
abstract public class MPAppletLink extends javax.swing.JApplet
    implements java.awt.event.MouseListener
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();

    private Font               m_font;

    private MPLink              m_mpLink;
    private boolean             m_depressed = false;
    private Socket              m_linkSocket;

//----------------------------------------------------------------------------//
    /** Creates new MPAppletLink */
    public MPAppletLink ()
    {
        super();
        // Set defaults
        m_font = new java.awt.Font("Arial", java.awt.Font.PLAIN, 12);
    }

//----------------------------------------------------------------------------//
    /** Initialization method that will be called after the applet is loaded
     *  into the browser.
     */
    public void init ()
    {
        try
        {
            m_mpLink = getMPInfo();
            // Skipped if a problem is encountered
            
            String font = getParameter("font");
            if(font == null)
                font = "Arial";
            String size = getParameter("size");
            
            int fontSize = 12;
            if(size != null)
                fontSize = Integer.parseInt(size);
            
            String style = getParameter("style");
            int fontStyle = java.awt.Font.PLAIN;
            if (style != null)
            {
                if (style.toLowerCase().indexOf("bold") >= 0)
                {
                    fontStyle += java.awt.Font.BOLD;
                }
                if (style.toLowerCase().indexOf("italic") >= 0)
                {
                    fontStyle += java.awt.Font.ITALIC;
                }
            }
            m_font = new java.awt.Font(font, fontStyle, fontSize);
        }
        catch (java.util.MissingResourceException e)
        {
            e.printStackTrace();
            // Make it look disabled (black text)
            return;
        }
        catch (java.lang.IllegalArgumentException e)
        {
            e.printStackTrace();
            // Make it look disabled (black text)
            return;
        }

        if (getContentPane().getLayout().getClass().equals(
            java.awt.BorderLayout.class))
        {
            getContentPane().setLayout(new java.awt.BorderLayout());
        }
    }
    
    public void mouseClicked (java.awt.event.MouseEvent event)
    {
        purchaseProduct();
    }
    
//----------------------------------------------------------------------------//
    public void mouseEntered(java.awt.event.MouseEvent event)
    {
        setCursor(java.awt.Cursor.getPredefinedCursor(
            java.awt.Cursor.HAND_CURSOR));
        // Check if the mouse was depressed when it last exited but
        // isn't anymore.
        onMouseEntered(event);
        if (m_depressed)
        {
            if ((event.getModifiers() & event.BUTTON1_MASK) != 0)
            {
                onMouseEnteredAndDepressed(event);
                validate();
                repaint();
            }
            else
            {
                m_depressed = false;
                onMouseEnteredAndNotDepressed(event);
            }
        }

        /* ( NK 2001-05-18 ) Serialize the basic microproduct info to
         * the wallet
         */
        Socket windowShoppingSocket;
        try
        {
            InetAddress myAddress = InetAddress.getLocalHost();

            windowShoppingSocket = new Socket( myAddress,
                Integer.parseInt( c_Rsrcs.getString("net.WindowShoppingPort") ));
        }
        catch( java.net.UnknownHostException UHE )
        {
            getAppletContext().showStatus(c_Rsrcs.getString("NoContact"));
            return;
        }
        catch( IOException IOE )
        {
            //Unable to create the socket, probably no wallet out there 
            return;
        }

        try
        {
            ObjectOutputStream out = new ObjectOutputStream( 
                windowShoppingSocket.getOutputStream() );
            out.writeObject( m_mpLink.getBasicInfo() );
            out.flush();
            out.close();
            windowShoppingSocket.close();
        }
        catch (java.io.IOException e)
        {
            // Ignore.
        }
    }
    
//----------------------------------------------------------------------------//
    public void mouseExited(java.awt.event.MouseEvent event)
    {
        setCursor(java.awt.Cursor.getDefaultCursor());
        onMouseExited(event);
        if (m_depressed)
        {
            onMouseExitedAndDepressed(event);
            validate();
            repaint();
        }
        else
            onMouseExitedAndNotDepressed(event);
    }
    
//----------------------------------------------------------------------------//
    public void mousePressed (java.awt.event.MouseEvent event)
    {
        m_depressed = true;
        onMousePressed(event);
        validate();
        repaint();
    }
    
//----------------------------------------------------------------------------//
    public void mouseReleased (java.awt.event.MouseEvent event)
    {
        m_depressed = false;
        onMouseReleased(event);
        validate();
        repaint();
    }

//----------------------------------------------------------------------------//

    public void purchaseProduct()
    {
        try
        {
            /* ( NK 2001-05-18 ) Serialize the microproduct link info to the
             * wallet - this is done using a planejane  socket since there's
             * nothing sensitive here
             */
            getAppletContext().showStatus(c_Rsrcs.getString("ContactingWallet"));

            InetAddress myAddress = InetAddress.getLocalHost();

            m_linkSocket = new Socket( myAddress,
                Integer.parseInt( c_Rsrcs.getString("net.MicroProductPort" ) ));

            ObjectOutputStream out = null;
            DataInputStream in = null;
            try
            {
                out = new ObjectOutputStream(m_linkSocket.getOutputStream());
                out.writeObject(m_mpLink);
                out.flush();

                in = new DataInputStream(m_linkSocket.getInputStream());

                /* (2001-07-19 RTG ) If the wallet is unable to unlock the
                 * voucher and it does not provide an error web page to go
                 * to, it will simply close the socket here, resulting in
                 * an IOException when the PerFeeLinkHandler tries to read
                 * in a UTF string.
                 */
                String result = in.readUTF();
                getAppletContext().showDocument(new URL(result), "_blank");
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
                getAppletContext().showStatus(c_Rsrcs.getString("NoContact"));
            }
            finally
            {
                try
                {
                    in.close();
                    out.close();
                }
                catch (java.io.IOException e)
                {
                    // Ignore.
                }
            }
            m_linkSocket.close();
        }
        catch (java.net.UnknownHostException UHE )
        {
            getAppletContext().showStatus(c_Rsrcs.getString("NoContact"));
        }
        catch ( java.io.IOException IOE )
        {
            IOE.printStackTrace(System.err);
            getAppletContext().showStatus(c_Rsrcs.getString("NoContact"));
        }
        finally
        {
            if (m_linkSocket != null)
            {
                try
                {
                    m_linkSocket.close();
                }
                catch (java.io.IOException e)
                {
                    // Ignore
                }
            }
        }
    }

//----------------------------------------------------------------------------//
    /** Retrieve the microproduct information from the product page.
     */
    public MPLink getMPInfo()
        throws java.lang.IllegalArgumentException
    {
        MPLink mpLink = new MPLink();

        // The required fields
        mpLink.setPrice( getParameter("price") );
        mpLink.setTextLink( getParameter("textlink") );
        System.out.println("Textlink: " + getParameter("textlink"));
        mpLink.setRequestURL( getParameter("requesturl") );

        java.util.List mpNameList = new java.util.LinkedList();
        mpLink.setPaymentSystems(mpNameList);
        int merchantCount = 0;
        String paymentSystemName = getParameter("mp" + Integer.toString(
            merchantCount + 1 ) + ".mpname");

        while( paymentSystemName != null )
        {
            mpNameList.add( paymentSystemName );
            merchantCount++;
            paymentSystemName = getParameter("mp" +
                Integer.toString(merchantCount + 1)+ ".mpname");
        }

        // The recommended fields
        mpLink.setTitle( getParameter("title") );

        // The optional fields
        mpLink.setImageLink( getParameter("imagelink") );
        mpLink.setBuyID( getParameter("buyid") );
        mpLink.setBaseURL( getParameter("baseurl") );
        mpLink.setLongDesc( getParameter("longdesc") );
        mpLink.setMerchantName( getParameter("merchantname") );
        try
        {
            String duration = getParameter("duration");
            if(duration != null)
                mpLink.setDuration(Integer.parseInt(duration, 10));
            else
                mpLink.setDuration(0);
        }
        catch (java.lang.NumberFormatException e)
        {
            /* (2001-08-20 RTG) Down-cast because the type of
             * IllegalArgumentException is irrelevant and this allows
             * NumberFormatExceptions to be identified as coming from other
             * sources.
             */
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
        mpLink.setExpiration( getParameter("expiration") );
        mpLink.setTarget( getParameter("target") );
        mpLink.setHREFLang( getParameter("hreflang") );
        mpLink.setType( getParameter("type") );
        mpLink.setAccessKey( getParameter("accesskey") );
        mpLink.setCharSet( getParameter("charset") );
        mpLink.setExtData( getParameter("extdata") );
        mpLink.setExtDataParam( getParameter("extdataparam") );

        mpLink.setMPRMIParam( getParameter("mpRMI") );
        mpLink.setMAMAName( getParameter("mpMAMA") );

        try
        {
            mpLink.setXChngRate( c_Rsrcs.getString("merchant.xChangeRate") );
        }
        catch (java.util.MissingResourceException e)
        {
            e.printStackTrace();
            mpLink.setXChngRate("1.00USD");
        }

        return mpLink;
    }

//----------------------------------------------------------------------------//
    protected Font font()
    {
        return(m_font);
    }
    
//----------------------------------------------------------------------------//
    protected MPLink link()
    {
        return(m_mpLink);
    }
    
//----------------------------------------------------------------------------//
    protected void onMouseEntered(java.awt.event.MouseEvent event) {}
    protected void onMouseEnteredAndDepressed(java.awt.event.MouseEvent event) {}
    protected void onMouseEnteredAndNotDepressed(java.awt.event.MouseEvent event) {}

    protected void onMouseExited(java.awt.event.MouseEvent event) {}
    protected void onMouseExitedAndDepressed(java.awt.event.MouseEvent event) {}
    protected void onMouseExitedAndNotDepressed(java.awt.event.MouseEvent event) {}
    
    protected void onMousePressed(java.awt.event.MouseEvent event) {}
    protected void onMouseReleased(java.awt.event.MouseEvent event) {}
//----------------------------------------------------------------------------//
}
