/*
 * MPTextLink.java
 *
 * Created on August 20, 2001, 3:55 PM
 */

package com.inexum.MicroAc.PFL.Microproduct;

import com.inexum.MicroAc.PFL.*;
import com.inexum.util.ConfigurationManager;

import java.net.*;
import java.io.*;
import java.util.Locale;

/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class MPTextLink extends MPAppletLink
{
    java.awt.Color      m_defaultColour;
    java.awt.Color      m_usedColour;
    java.awt.Color      m_pressedColour;
    java.awt.Color      m_background;
    MPLabel             m_label;

//----------------------------------------------------------------------------//
    /** Creates new MPTextLink */
    public MPTextLink ()
    {
        super();
        // Set defaults
        m_defaultColour = new java.awt.Color(0.0f, 0.0f, 1.0f);
        m_pressedColour = new java.awt.Color(1.0f, 0.0f, 1.0f);
        m_background = new java.awt.Color(1.0f, 1.0f, 1.0f);
        m_label = new MPLabel();
    }

//----------------------------------------------------------------------------//
    public java.lang.String getAppletInfo ()
    {
        return "MPTextLink copyright (c) 2001 iNexum Systems Inc";
    }

//----------------------------------------------------------------------------//
    public void init ()
    {
        super.init();
        try
        {
            // Skipped if a problem is encountered
            m_label.addMouseListener(this);
        }
        catch (java.util.MissingResourceException e)
        {
            e.printStackTrace();
            // Make it look disabled (black text)
            m_defaultColour = new java.awt.Color(0.0f, 0.0f, 0.0f);
            m_pressedColour = new java.awt.Color(0.0f, 0.0f, 0.0f);
            m_usedColour = new java.awt.Color(0.0f, 0.0f, 0.0f);
            return;
        }
        catch (java.lang.IllegalArgumentException e)
        {
            e.printStackTrace();
            // Make it look disabled (black text)
            m_defaultColour = new java.awt.Color(0.0f, 0.0f, 0.0f);
            m_pressedColour = new java.awt.Color(0.0f, 0.0f, 0.0f);
            m_usedColour = new java.awt.Color(0.0f, 0.0f, 0.0f);
            return;
        }

        try
        {
            String defaultColour = getParameter("defaultColor");
            String pressedColour = getParameter("pressedColor");
            String usedColour = getParameter("usedColor");
            String background = getParameter("backgroundColor");

            String style = getParameter("style");
            if (defaultColour != null)
                m_defaultColour = java.awt.Color.decode(defaultColour);
            if (pressedColour != null)
                m_pressedColour = java.awt.Color.decode(pressedColour);
            else
                m_pressedColour = m_defaultColour;
            if (usedColour != null)
                m_usedColour = java.awt.Color.decode(usedColour);
            else
                m_usedColour = m_pressedColour;
            
            if (background != null)
                m_background = java.awt.Color.decode(background);
            
            if (style != null && style.toLowerCase().indexOf("underline") >= 0)
            {
                m_label.underline();
            }

        }
        catch (java.lang.NumberFormatException e)
        {
            e.printStackTrace();
        }

        setBackground(m_background);
        m_label.setForeground(m_defaultColour);
        m_label.setBackground(m_background);
        m_label.setFont(font());
        m_label.setText(link().getTextLink());

        getContentPane().add(m_label, java.awt.BorderLayout.CENTER);
        m_label.textArea(font().getStringBounds(
                               link().getTextLink(),
                               new java.awt.font.FontRenderContext(
                                        new java.awt.geom.AffineTransform(), false, false)),
                        getHeight());
    }

//----------------------------------------------------------------------------//
    public void onMouseEnteredAndDepressed(java.awt.event.MouseEvent event)
    {
        m_label.setForeground(m_pressedColour);
    }

//----------------------------------------------------------------------------//
    public void onMouseExitedAndDepressed(java.awt.event.MouseEvent event)
    {
        m_label.setForeground(m_defaultColour);
    }

//----------------------------------------------------------------------------//
    public void onMousePressed(java.awt.event.MouseEvent event)
    {
        m_label.setForeground(m_pressedColour);
    }

//----------------------------------------------------------------------------//
    public void onMouseReleased(java.awt.event.MouseEvent event)
    {
        m_label.setForeground(m_usedColour);
    }

}
