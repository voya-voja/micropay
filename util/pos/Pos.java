/*
 * Pos.java
 *
 * Created on July 11, 2004, 4:49 PM
 */

package com.inexum.util.pos;

import com.inexum.util.ConfigurationManager;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.Base1Packager;
import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;

import java.util.Date;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

import org.jpos.util.Logger;
import org.jpos.util.LogEvent;
import org.jpos.util.SimpleLogSource;
import org.jpos.util.SimpleLogListener;

/**
 *
 * @author  inexum
 */
public class Pos extends SimpleLogSource
{
    private static final ConfigurationManager c_Rsrcs = 
                                            ConfigurationManager.Instance();
    private XmlIsoMessageFactory    mIsoFactory;
    
    Card    mCard;
    int     mState = 0;
    double  mAmount = 0.0;
    int     mPin = -1;
    int     mTraceNumber = 0;
    int     mProcessingCode = 0;
    
    final int cInit = 0;
    final int cEnterAmount = 1;
    final int cEnterPin = 2;
    
    final int cChq = 1;
    final int cSav = 2;
    final int cMac = 3;
    
    Logger mLogger;
    /** Creates a new instance of Pos */
    public Pos()
    {
        init();
        mIsoFactory = new XmlIsoMessageFactory();
    }
    
    public void init()
    {
	mLogger = new Logger();
	mLogger.addListener (new SimpleLogListener (System.out));
        setLogger(mLogger, "Pos Transactions:");
    }
    
    public String swiped(Card card)
    {
        mCard = card;
        mState = cEnterAmount;
        return("Enter Amount:\n");
    }  
     
    public String initMessage()
    {
        return("Swipe the card ...");
    }
    
    public String okProcessing(String text)
    {
        if(mState == cEnterAmount)
        {
            mAmount = Double.parseDouble(text);
            return("Select the account:\n\n   |\t|\t|");
        }
        else if(mState == cEnterPin)
        {
            mPin = Integer.parseInt(text);
            return(processTransaction(true));
        }
        return(initMessage());
    }
    
    public String cancelTransaction()
    {
        mCard = null;
        mState = cInit;
        return(initMessage());
    }
    
    public String mAcTransaction()
    {
        if(mCard == null)
            return(initMessage());
        mProcessingCode = cMac;
        return(processTransaction(false));
    }
    
    public String chqTransaction()
    {
        if(mCard == null)
            return(initMessage());
        mProcessingCode = cChq;
        return(processTransaction(false));
    }
    
    public String savTransaction()
    {
        if(mCard == null)
            return(initMessage());
        mProcessingCode = cSav;
        return(processTransaction(false));
    }
    
    public String correctTransaction()
    {
        if(mState == cEnterAmount)
            return("Enter Amount:\n");
        else if(mState == cEnterPin)
            return("Enter PIN:\n");
        return(initMessage());
    }
    
    protected String processTransaction(boolean withPin)
    {
        mState = cInit;
        int posPort = 14001;
        try
        {
            String port = c_Rsrcs.getString("acquirer.pos.port");
            posPort = Integer.parseInt(port);
        }
        catch(java.util.MissingResourceException e)
        {
            System.out.println("No property 'acquirer.pos.port',"
                                + "using default port '" + posPort + "'");
        }
        String host = "localhost";
        try
        {
            host = c_Rsrcs.getString("acquirer.pos.host");
        }
        catch(java.util.MissingResourceException e)
        {
            System.out.println("No property 'acquirer.pos.host',"
                                + "using default host '" + host + "'");
        }
        try
        {
            Socket socket = new Socket(host, posPort);
            OutputStream out = socket.getOutputStream();
            ISOMsg request = createISO();
            request.setPackager(new Base1Packager());
            out.write(request.pack());
            InputStream in = socket.getInputStream();
            ISOMsg response = new ISOMsg();
            response.setPackager(new Base1Packager());
            response.unpack(in);

            LogEvent evt = new LogEvent(this, "response");
            evt.addMessage(response);
            Logger.log(evt);

            if(Integer.parseInt(response.getString(11)) 
                            != Integer.parseInt(request.getString(11)))
                return("Transaction error!!");

            String respCode = response.getString(39).trim();
            int responseCode = Integer.parseInt(respCode);
            switch(responseCode)
            {
                case 0:
                    return("Approved");
                case 5:
                    return("Rejected: Insuficient funds!");
                case 14:
                    return("Rejected: Card not found!");
                case 30:
                    return("Rejected: Message format error!");
                case 96:
                    return("Rejected: System error!");
                default:
                    return("Rejected: Unknown reason!");
            }
        }
        catch(java.net.UnknownHostException e)
        {
            return("Failed to connect to '" + host + "'!");
        }
        catch(java.io.IOException e)
        {
            return("Communication error!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return("System error!");
        }
    }
    
    protected ISOMsg createISO()
    {
        ISOMsg isoMsg = mIsoFactory.defaultMessage();
        try
        {
            String amount = Double.toString(mAmount);
            int dotIndex = amount.indexOf('.');
            amount = amount.substring(0,dotIndex) 
                                    + amount.substring(dotIndex+1, dotIndex+3);
            isoMsg.set(2, mCard.getCardNumber());
            isoMsg.set(3, Integer.toString(mProcessingCode)); 
            isoMsg.set(4, amount); 
            Date d = new Date();
            isoMsg.set(7, ISODate.getTime(d));
            isoMsg.set(11, Integer.toString(++mTraceNumber));
            isoMsg.set(12, ISODate.getTime(d));
            isoMsg.set(13, ISODate.getDate(d));
            isoMsg.set(14, mCard.getExpDate());
            isoMsg.set(35, mCard.getCardNumber() + "=" 
                                                + mCard.getInstitutionId());
            isoMsg.set(45, mCard.getName());
        }
        catch(ISOException e)
        {
            e.printStackTrace();
        }
	LogEvent evt = new LogEvent(this, "createIsoMessage");
        evt.addMessage(isoMsg);
        Logger.log(evt);
        return(isoMsg);
    }
}
