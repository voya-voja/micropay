/*
 * JChasingArrows.java
 *
 * Created on July 19, 2001, 4:30 PM
 */

package com.inexum.demo.MicroAc.Wallet;

/**
 *
 * @author  rgoold
 * @version 
 */
public class JChasingArrows extends javax.swing.JComponent
    implements java.io.Serializable
{
    static final int            c_FrameCount = 12;
    private int                 m_frameState = 9;
    
    private java.util.Timer     m_animTimer = null;

    /** Creates new JChasingArrows */
    public JChasingArrows ()
    {
    }
    
    public void paint (java.awt.Graphics g)
    {
        int width = g.getClipBounds().width;
        int height = g.getClipBounds().height;
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        double angle = Math.toRadians((360 / c_FrameCount) * m_frameState);
        int armX = (int)(halfWidth + halfWidth * Math.cos(angle));
        int armY = (int)(halfHeight + halfHeight * Math.sin(angle));
        
        g.clearRect (0, 0, width, height);
        g.drawOval(0, 0, width, height);
        g.drawLine(halfWidth, halfHeight, armX, armY);
    }
    
    public void tick ()
    {
        if (++m_frameState >= c_FrameCount)
        {
            m_frameState = 0;
        }
        repaint();
    }
    
    private class AnimTask extends java.util.TimerTask
    {
        private JChasingArrows  m_parent;
        
        public AnimTask (JChasingArrows parent)
        {
            m_parent = parent;
        }
        
        public void run ()
        {
            m_parent.tick();
        }
    }
    
    public void animate (long period)
    {
        if (m_animTimer != null)
        {
            return;
        }
        AnimTask animTask = new AnimTask(this);
        m_animTimer = new java.util.Timer();
        m_animTimer.scheduleAtFixedRate (animTask, 0, period);
    }
    
    public void stopAnimation ()
    {
        if (m_animTimer == null)
        {
            return;
        }
        m_animTimer.cancel();
        m_animTimer = null;
    }
    
}
