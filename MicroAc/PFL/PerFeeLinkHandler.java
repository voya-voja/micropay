/***** Copyright 2001 iNexum Systems Inc. *****************************
 **
 **	File: PerFeeLinkHandler.java
 **
 **	Description:
 **
 **
 **	Revision History:
 **		2001-05-07 (RTG) Initial Release.
 **		2001-05-18 (NK) Added the serialization of the MPLink to the
 *                  wallet and made the MPLink class and external class
 *		2001-05-24 (NK) Removed the validation of required fields
 *                  the field check will be performed by the wallet and/or the
 *                  cama when the the microproduct is passed along.
 *          
 **********************************************************************/


package com.inexum.MicroAc.PFL;

import com.inexum.util.ConfigurationManager;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.URL;
import java.net.*;
import java.io.*;
import java.util.*;

/** Communicating microproduct purchase info to the wallet.
 *
 * @author  rgoold
 * @version 1.0
 */
public class PerFeeLinkHandler extends java.applet.Applet
    implements MouseListener
{

    /** The microproduct link information*/
    private MPLink          m_mpLink;

    /** The image used to represent the microproduct link*/
    private Image     m_image;
    private String    m_imageName;
    private String    m_badImageName;
    private int       m_imageWidth;
    private int       m_imageHeight;
    private int       m_WinShprPortNum;
    private int       m_MPLinkPortNum;

    /** The socket used to connect to the wallet*/
    private Socket          m_linkSocket;
    private int             m_merchantCount;

    private final static ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();

//----------------------------------------------------------------------------//

/* nmv    public static ResourceBundle loadProps(String name)
        throws java.util.MissingResourceException
    {
        /* (2001-11-30 RTG) This is necessary to overcome the problem where the
         * Java plug-in searches the web server directories before it searches
         * the jar file, when looking for resource bundles. It short-circuits
         * the mechanism by only looking in jar files.
         */ /* nmv
        URL[] oldURLPaths = ((URLClassLoader)MPTextLink.class.getClassLoader())
            .getURLs();
        URL[] newURLPaths = new URL[oldURLPaths.length];
        for (int i = 0; i < oldURLPaths.length; i++)
        {
            // Copy only JAR files
            if (!oldURLPaths[i].toString().endsWith("/"))
            {
                newURLPaths[i] = oldURLPaths[i];
            }
        }

        URLClassLoader jarLoader = new URLClassLoader(newURLPaths);
        return ResourceBundle.getBundle(name, Locale.getDefault(),
            jarLoader);
    }
nmv */
//----------------------------------------------------------------------------//

    /** Initialization method that will be called after the applet is loaded
     *  into the browser.
     */
    public void init ()
    {
        try
        {
            ConfigurationManager bundle = ConfigurationManager.Instance();
/* nmv            m_imageName = bundle.getString("LinkImage");

            m_badImageName = bundle.getString("BadLinkImage");

            m_imageWidth = Integer.parseInt(
                bundle.getString("LinkImageWidth") , 10 );

            m_imageHeight = Integer.parseInt(
                bundle.getString("LinkImageHeight"), 10 );
nmv */
	    m_WinShprPortNum = Integer.parseInt(
                bundle.getString("net.WindowShoppingPort") );

	    m_MPLinkPortNum = Integer.parseInt(
                bundle.getString("net.MicroProductPort") );
        }
        catch( NumberFormatException NFE )
        {
            //Set the defaul width and height
            System.out.println("NumberFormatException: "
                + NFE.getLocalizedMessage());
/* nmv             m_imageWidth = 20;
            m_imageHeight = 20;
nmv */
        }
        catch( MissingResourceException MRE )
        {
            System.out.println("MissingResourceException: "
                + MRE.getLocalizedMessage());
/* nmv             m_imageWidth = 20;
            m_imageHeight = 20;
            m_imageName = "Coin.gif";
            m_badImageName = "CoinBad.gif";
nmv */
        }

        m_mpLink = new MPLink();

        resize( m_imageWidth, m_imageHeight);
        setBackground(new Color(255, 255, 255, 0));

        // Initialise an MPLink from the <OBJECT> parameters
        try
        {
            getMPInfo();

           // Give me a pretty face for the link
            m_image = getImage(getCodeBase(), m_imageName);

            // Start responding to clicks
            addMouseListener(this);
        }
        catch (java.lang.IllegalArgumentException e)
        {
            // Give me a sad face for the link and don't respond to clicks
            System.out.println("IllegalArgumentException: "
                + e.getLocalizedMessage());
            m_image = getImage(getCodeBase(), m_badImageName);
        }
    }

//----------------------------------------------------------------------------//

    /** Retrieve the microproduct information from the product page.
     */
    public void getMPInfo()
        throws java.lang.IllegalArgumentException
    {
        // The required fields
        m_mpLink.setPrice( getParameter("price") );
        m_mpLink.setTextLink( getParameter("textlink") );
        m_mpLink.setRequestURL( getParameter("requesturl") );

        m_mpLink.setPaymentSystems( new LinkedList() );

        m_merchantCount = 0;

        java.util.List mpNameList = m_mpLink.getPaymentSystems();

        String paymentSystemName = getParameter("mp" + Integer.toString(
            m_merchantCount + 1 ) + ".mpname");

        while( paymentSystemName != null )
        {
            mpNameList.add( paymentSystemName );
            m_merchantCount++;
            paymentSystemName = getParameter("mp" +
                Integer.toString( m_merchantCount + 1)+ ".mpname");
        }

        // The recommended fields
        m_mpLink.setTitle( getParameter("title") );

        // The optional fields
        m_mpLink.setImageLink( getParameter("imagelink") );
        m_mpLink.setBuyID( getParameter("buyid") );
        m_mpLink.setBaseURL( getParameter("baseurl") );
        m_mpLink.setLongDesc( getParameter("longdesc") );
        m_mpLink.setMerchantName( getParameter("merchantname") );
        try
        {
            m_mpLink.setDuration( Integer.parseInt(
                getParameter("duration"), 10) );
        }
        catch (java.lang.NumberFormatException e)
        {
            /* ( RTG 2001-05-31 ) An exception should be thrown instead, to
             * indicate that this particular per-fee link should be treated
             * as invalid (in its entirety).
             */
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
        m_mpLink.setExpiration( getParameter("expiration") );
        m_mpLink.setTarget( getParameter("target") );
        m_mpLink.setHREFLang( getParameter("hreflang") );
        m_mpLink.setType( getParameter("type") );
        m_mpLink.setAccessKey( getParameter("accesskey") );
        m_mpLink.setCharSet( getParameter("charset") );
        m_mpLink.setExtData( getParameter("extdata") );
        m_mpLink.setExtDataParam( getParameter("extdataparam") );

        m_mpLink.setMPRMIParam( getParameter("mpRMI") );
        m_mpLink.setMAMAName( getParameter("mpMAMA") );

        try
        {
            m_mpLink.setXChngRate(c_Rsrcs.getString("xChangeRate"));
        }
        catch (java.util.MissingResourceException e)
        {
            e.printStackTrace();
            m_mpLink.setXChngRate("1.00USD");
        }
    }

//----------------------------------------------------------------------------//

    /** Draw the microproduct link image
     * @param g
     */
    public void paint (Graphics g)
    {
        g.drawImage(m_image, 0, 0, this);
    }

//----------------------------------------------------------------------------//

    /** Send the the microproduct info to the wallet
     * @param event
     */
    public void mouseClicked(java.awt.event.MouseEvent event)
    {
        try
        {
            /* ( NK 2001-05-18 ) Serialize the microproduct link info to the
             wallet - this is done using a planejane  socket since there's
             nothing sensitive here
             */
            try
            {
                getAppletContext().showStatus(c_Rsrcs.getString("ContactingWallet"));
            }
            catch (java.util.MissingResourceException e)
            {
                e.printStackTrace();
            }

            InetAddress myAddress = InetAddress.getLocalHost();

            m_linkSocket = new Socket( myAddress, m_MPLinkPortNum );

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
                try
                {
                    getAppletContext().showStatus(c_Rsrcs.getString("NoContact"));
                }
                catch (java.util.MissingResourceException e2)
                {
                    e2.printStackTrace();
                }
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
            try
            {
                getAppletContext().showStatus(c_Rsrcs.getString("NoContact"));
            }
            catch (java.util.MissingResourceException e)
            {
                e.printStackTrace();
            }
        }
        catch ( java.io.IOException IOE )
        {   
            IOE.printStackTrace(System.err);
            try
            {
                getAppletContext().showStatus(c_Rsrcs.getString("NoContact"));
            }
            catch (java.util.MissingResourceException e)
            {
                e.printStackTrace();
            }
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

    public void mouseEntered (java.awt.event.MouseEvent event) 
    {
        setCursor(java.awt.Cursor.getPredefinedCursor(
            java.awt.Cursor.HAND_CURSOR));
        /* ( NK 2001-05-18 ) Serialize the basic microproduct info to
         * the wallet
         */
        Socket windowShoppingSocket;
        try
        {
            InetAddress myAddress = InetAddress.getLocalHost();

            windowShoppingSocket = new Socket( myAddress, m_WinShprPortNum );
        }
        catch( java.net.UnknownHostException UHE )
        {
            try
            {
                getAppletContext().showStatus(c_Rsrcs.getString("NoContact"));
            }
            catch (java.util.MissingResourceException e)
            {
                e.printStackTrace();
            }
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

    public void mouseExited (java.awt.event.MouseEvent event)
    {
        setCursor(java.awt.Cursor.getDefaultCursor());
    }

//----------------------------------------------------------------------------//

    // These interface methods must be implemented,
    // but we don't need to actually do anything.
    public void mousePressed (java.awt.event.MouseEvent event) {}
    public void mouseReleased(java.awt.event.MouseEvent event) {}

}
