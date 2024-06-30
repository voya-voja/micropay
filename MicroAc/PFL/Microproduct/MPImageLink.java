/*
 * MPImageLink.java
 *
 * Created on April 6, 2004, 11:06 PM
 */

package com.inexum.MicroAc.PFL.Microproduct;

import java.awt.Image;
import java.awt.Graphics;
import java.net.URL;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.MediaTracker;

/**
 *
 * @author  inexum
 */
public class MPImageLink extends MPAppletLink 
{
     MediaTracker   mMediaTracker; 
     Image          mImage;
     boolean        mDisplayPrice = false;
     String         mPrice;
     Rectangle2D    mPriceRect;

    /** Initialization method that will be called after the applet is loaded
     *  into the browser.
     */
    public void init() 
    {
        super.init();
        mPrice = link().getPrice();
        if(mPrice != null)
        {
            mPriceRect = font().getStringBounds(mPrice,
                               new java.awt.font.FontRenderContext(
                                        new java.awt.geom.AffineTransform(), 
                                        false, false));
        }
        try 
        { 
            URL base = getDocumentBase(); 
            String url = getParameter("url");
            if(url != null)
                base = new URL(url);
            String file = getParameter("file");
            if(file != null)
            {
                mImage = getImage(base, file);
                mMediaTracker = new MediaTracker(this);
                mMediaTracker.addImage(mImage, 1);
            }
            addMouseListener(this);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
    }
    
//----------------------------------------------------------------------------//
    public java.lang.String getAppletInfo ()
    {
        return "MPImageLink copyright (c) 2001 iNexum Systems Inc";
    }

//----------------------------------------------------------------------------//
    public void paint(Graphics g)  
    { 
    // now we are going to draw the gif on the screen 
    // (image name,x,y,observer); 

        if(mImage != null)
            g.drawImage(mImage, 0, 0, this); 

        if(mPrice != null && mDisplayPrice)
        {
            int x = 9;
            int y = 9;
            int w = (int)(mPriceRect.getWidth() + 2.5);
            int h = (int)(mPriceRect.getHeight() + 1.5);
            g.drawRoundRect(x-1, y-1, w+1, h+1, 2, 1);
            Color oldColor = g.getColor();
            g.setColor(new Color(255, 255, 127));
            g.fillRoundRect(x, y, w, h, 2, 1);
            g.setColor(new Color(0, 0, 255));
            Font oldFont = g.getFont();
            g.setFont(font());
            g.drawString(mPrice, 10, 10 + (int)(mPriceRect.getHeight() - 1));
            g.setFont(oldFont);
            g.setColor(oldColor);
        }
    // you can resize the image easily 
    //          g.drawImage(my_gif,20,140,30,40,this); 
    } 
//----------------------------------------------------------------------------//
     
    protected void onMouseEntered(java.awt.event.MouseEvent event) 
    {
        mDisplayPrice = true;
        repaint();
    }
    
    protected void onMouseExited(java.awt.event.MouseEvent event) 
    {
        mDisplayPrice = false;
        repaint();
    }
}
