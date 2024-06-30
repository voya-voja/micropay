/***** Copyright 2001 iNexum Systems Inc. *****************************
**
**      File: PurchaseServlet.java
**
**      Description:
**          Handles access to a product once it has been bought.
**
**      Revision History:
**              2001-05-22 (RTG) Initial revision.
**
**********************************************************************/
 
package com.inexum.MicroAc.Merchant.Delivery;

import com.inexum.MicroAc.Merchant.*;
import com.inexum.MicroAc.Merchant.Cashier.*;
import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.Transaction.PeerRequest;
import com.inexum.MicroAc.Transaction.TransactionOffer;
import com.inexum.util.ConfigurationManager;

import java.io.*;
import java.net.*;

import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

import java.rmi.*;
import java.rmi.registry.*;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class PurchaseServlet extends HttpServlet
{
    private static final ConfigurationManager c_Rsrcs = ConfigurationManager.Instance();

    private ICashier            m_merchant;
    private IVoucherIssuer      m_issuer;
    private IVoucherAcquirer    m_acquirer;

//----------------------------------------------------------------------------//

    /** Initializes the servlet.
     */
    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);
        
        //ntk Comment out the Security manager if running the servlet locally 
        //for debugging purposes.
        
        System.setSecurityManager (new java.rmi.RMISecurityManager());
        java.security.Policy policy = java.security.Policy.getPolicy();
        
        System.out.println("Policy: " + policy.toString());
        
        try
        {
            String rmiName = "//" + c_Rsrcs.getString("merchant.Server") + "/" 
								+ c_Rsrcs.getString("merchant.Service");
            System.out.println("Merchant RMI name: " + rmiName );
            m_merchant = (ICashier)Naming.lookup( rmiName );
        }
        catch (java.rmi.NotBoundException e)
        {
            e.printStackTrace(System.err);
            throw new ServletException(
                c_Rsrcs.getString("CouldNotFindMerchant"));
        }
        catch (java.net.MalformedURLException e)
        {
            e.printStackTrace(System.err);
            throw new ServletException(
                c_Rsrcs.getString("CouldNotFindMerchant"));
        }
        catch (java.rmi.RemoteException e)
        {
            e.printStackTrace(System.err);
            throw new ServletException(
                c_Rsrcs.getString("CouldNotFindMerchant"));
        }
    }

//----------------------------------------------------------------------------//

    /** Destroys the servlet.
     */
    public void destroy()
    {
    }

//----------------------------------------------------------------------------//

    protected void printError(String error, java.io.PrintWriter writer)
    {
        // If the error can't be printed, there's no need to throw an
        // exception. After all, what would we do with it? Print it out? :-)
        if (writer == null || writer.checkError())
        {
            return;
        }
        try
        {
            writer.println("<Html><Head><Title>"
                + c_Rsrcs.getString("ServerError") + "</Title></Head>");
            writer.println("<Body>");
            writer.println("<H3>"
                + c_Rsrcs.getString("SorryError") + "</H3>");
            writer.println("<Pre>" + error + "</Pre>");
            writer.println("<P>"
                + c_Rsrcs.getString("PleaseTryAgain") + "</P>");
            writer.println("</Body>");
            writer.println("</Html>");
        }
        catch (java.util.MissingResourceException e)
        {
            e.printStackTrace(System.err);
        }
    }

//----------------------------------------------------------------------------//

    /** Processes requests for both HTTP <code>GET</code> and
     * <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, java.io.IOException
    {
        response.setContentType("text/html");
        response.setLocale(response.getLocale());
        // Switch bundle to consumer's locale, rather than merchant's.
        java.io.PrintWriter out = response.getWriter();
        try
        {
            m_acquirer = m_merchant.getAcquirer();
            System.out.println( "Got acquirer from Cashier"); //ntk
        }
        catch (java.rmi.RemoteException e)
        {
            printError(c_Rsrcs.getString("UnableToProcessError"), out);
            e.printStackTrace(out);
            out.close();
            return;
        }
        Enumeration paramNames = request.getParameterNames();
        /*ntk Debug code*/
        System.out.println( "The ParamNames.....");
        int count = 1;
        while( paramNames.hasMoreElements() )
        {
            
             System.out.println( count + ".ParamName : " + paramNames.nextElement().toString());
             count++;
        }
        
        paramNames = request.getParameterNames();
        /*ntk Debug code*/
        
        String ticketOrVoucherData = "";
        while (paramNames.hasMoreElements())
        {
            System.out.println("There are more paramNames......");
            String paramName = paramNames.nextElement().toString();
            if (paramName.equalsIgnoreCase("data"))
            {
                /* Base64 can produce the '+' character, which is converted
                 * to a space whenever it is found in the query part of a
                 * URL. Since Base64 never outputs spaces, we can safely
                 * convert all spaces back to pluses. Base64 does not produce
                 * any other characters with special meaning in a URL.
                 */
                System.out.println("Checking the data element for paramName = " + paramName );
                System.out.println("The ticketOrVoucher Data is :" + request.getParameter(paramName) );
                ticketOrVoucherData = request.getParameter(paramName).
                    replace(' ', '+');
                ticketOrVoucherData = ticketOrVoucherData.replace( '$', '=' );
                ticketOrVoucherData = ticketOrVoucherData + "=";
                System.out.println( ticketOrVoucherData );
                break;
            }
        }

        if (ticketOrVoucherData.equals(""))
        {
            printError(c_Rsrcs.getString("NoDataTransmittedError"), out);
            out.close();
            return;
        }
        String result = "";
        try
        {
            System.out.println("trying to redeem voucher" );//ntk
            result = m_acquirer.redeemVoucher(ticketOrVoucherData);
            System.out.println( "The result of the redeemtion is : " + result ); //ntk
        }
        catch (java.rmi.RemoteException e)
        {
            printError(c_Rsrcs.getString("UnableToProcessError"), out);
            e.printStackTrace(out);
            out.close();
            return;
        }
        catch (com.inexum.MicroAc.Exceptions.InvalidVoucherException e)
        {
            printError(c_Rsrcs.getString("InvalidVoucherError"), out);
            out.close();
            return;
        }

        // Everything's okay, so we can serve up the product.
        
        java.io.File productFile = new java.io.File(result);
        if (!productFile.exists() || !productFile.canRead())
        {
            // Uh oh! Issue a refund voucher.
            out.println("An error occurred while trying to retrieve your "
                + "product. The system is now attempting to issue you a "
                + "refund. File should have been: " + result + ".");
            out.close();

            try
            {
                m_issuer = m_merchant.getIssuer();
                String redemptionURL = "https://" +
                    InetAddress.getLocalHost().getHostName() + "/refund.html";
                PeerRequest refund = m_issuer.generateRefundOffer(
                    ticketOrVoucherData, redemptionURL);
                InetAddress remoteAddr
                    = InetAddress.getByName(request.getRemoteAddr());
                sendRefundToWallet(refund, remoteAddr);
            }
            catch (java.rmi.RemoteException e)
            {
                e.printStackTrace();
            }
            catch (java.net.MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (java.net.UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch (java.lang.SecurityException e)
            {
                e.printStackTrace();
            }
            catch (UnknownOfferException e)
            {
                e.printStackTrace();
            }
            catch (PurchaseInterruptedException e)
            {
                e.printStackTrace();
            }
            return;
        }
        try
        {
            char[] fileBuffer = new char[1024];
            //System.out.println("Trying to output the product to a browser"); //ntk
            java.io.FileReader fileReader = new java.io.FileReader(productFile);
            while (fileReader.ready())
            {
                int bytesRead = fileReader.read(fileBuffer);
                if (bytesRead < fileBuffer.length)
                {
                    out.write(fileBuffer, 0, bytesRead);
                }
                else
                {
                    out.write(fileBuffer);
                }
                out.flush();
            }
            fileReader.close();
        }
        catch (java.io.FileNotFoundException e)
        {
            // This shouldn't happen, given the above checks!
            printError(c_Rsrcs.getString("UnableToProcessError"), out);
            e.printStackTrace(out);
            out.close();
            return;
        }
        out.close();
    }

//----------------------------------------------------------------------------//

    public void sendRefundToWallet(PeerRequest refund, InetAddress address)
        throws java.io.IOException
    {
        try
        {
            // Open socket and streams
            int port = Integer.parseInt(
			c_Rsrcs.getString("net.PeerPort"), 10);
            Socket socket = new Socket(address, port);
            try
            {
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();

                // Identify self, get wallet identity
                DataOutputStream dataOut = new DataOutputStream(out);
                dataOut.writeUTF("MicroAc Servlet v1.0; SN1029384756");
                dataOut.flush();
                DataInputStream dataIn = new DataInputStream(in);
                String walletID = dataIn.readUTF();

                // Go to it!
                ObjectOutputStream objOut = new ObjectOutputStream(out);
                objOut.writeObject(refund);
            }
            finally
            {
                try
                {
                    socket.close();
                }
                catch (java.io.IOException e)
                {
                    // Ignore. Closed is closed.
                }
            }
        }
        catch (java.lang.NumberFormatException e)
        {
            throw new java.io.IOException(e.getLocalizedMessage());
        }
    }

//----------------------------------------------------------------------------//

    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, java.io.IOException
    {
        processRequest(request, response);
    }

//----------------------------------------------------------------------------//

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, java.io.IOException
    {
        processRequest(request, response);
    }

//----------------------------------------------------------------------------//

    /** Returns a short description of the servlet.
    */
    public String getServletInfo()
    {
        return "Copyright (c) 2001  iNexum Systems Inc.";
    }

}
