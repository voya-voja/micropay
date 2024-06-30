/*
 * LoginThread.java
 *
 * Created on July 17, 2001, 2:35 PM
 */

package com.inexum.demo.MicroAc.Wallet;

import com.inexum.MicroAc.Exceptions.InvalidLoginException;
import com.inexum.MicroAc.Exceptions.ServiceUnavailableException;
import com.inexum.MicroAc.Wallet.IWallet;

import javax.swing.JOptionPane;

/**
 *
 * @author  rgoold
 * @version 
 */
public class LoginThread extends java.lang.Thread
{
    private String  m_name;
    private String  m_password;
    private IWallet m_wallet;
    private String  m_resultMessage;
    private boolean m_successful = false;
    
    /** Creates new LoginThread */
    public LoginThread (String name, String password, IWallet wallet)
    {
        m_name = name;
        m_password = password;
        m_wallet = wallet;
    }
    
    public boolean isSuccessful ()
    {
        return m_successful;
    }
    
    public String getResultMessage ()
    {
        return m_resultMessage;
    }

    public void run ()
    {
        try
        {
            m_wallet.open(m_name, m_password);
            m_successful = true;
        }
        catch (InvalidLoginException e)
        {
            //m_resultMessage = e.getLocalizedMessage();
            m_resultMessage = "Your login or password was invalid.";
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run()
                {
                    new JOptionPane().showMessageDialog(null, m_resultMessage);
                }
            });
            e.printStackTrace();
        }
        catch (ServiceUnavailableException e)
        {
            //m_resultMessage = e.getLocalizedMessage();
            m_resultMessage = "The processing service is temporarily unavailable."
                + "\n\nReason: " + e.getLocalizedMessage();
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run()
                {
                    new JOptionPane().showMessageDialog(null, m_resultMessage);
                }
            });
            e.printStackTrace();
        }
    }
    
}

