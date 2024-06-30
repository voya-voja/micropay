/*
 * MPLabel.java
 *
 * Created on April 7, 2004, 10:09 PM
 */

package com.inexum.MicroAc.PFL.Microproduct;


/**
 *
 * @author  inexum
 */
public class MPLabel extends java.awt.Label 
{
    boolean             m_underline = false;
    java.awt.Rectangle  m_LinkRect;

    /** Creates a new instance of MPLable */
    public MPLabel() 
    {
    }
    
    public void underline()
    {
        m_underline = true;
    }
    
    public void textArea(java.awt.geom.Rectangle2D linkRect, int height)
    {
        m_LinkRect = new java.awt.Rectangle(
                        getBounds().x + (int)linkRect.getX(),
                        getBounds().y +
                            (height + (int)linkRect.getHeight())/2,
                        (int)(linkRect.getWidth()+0.5),
                        (int)(linkRect.getHeight()+0.5));
    }                      
    public void paint(java.awt.Graphics graphics) 
    {
        super.paint(graphics);
        if(m_underline)
            graphics.drawLine(m_LinkRect.x,
                                        m_LinkRect.y,
                                        m_LinkRect.x + m_LinkRect.width,
                                        m_LinkRect.y);
    }
    
}
