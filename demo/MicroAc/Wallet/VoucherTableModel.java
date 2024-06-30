/*
 * VoucherTableModel.java
 *
 * Created on July 17, 2001, 11:21 AM
 */

package com.inexum.demo.MicroAc.Wallet;

import com.inexum.MicroAc.DomainTypes.VoucherInfo;

import java.text.DateFormat;
import javax.swing.table.*;

/**
 *
 * @author  rgoold
 * @version 
 */
public class VoucherTableModel extends AbstractTableModel
{
    final int merchantColumn = 0;
    final int productColumn = 1;
    final int expiryColumn = 2;
    final int columns = 3;      // Maintain this
    java.util.List  m_vouchers;

    /** Creates new VoucherTableModel */
    public VoucherTableModel ()
    {
        m_vouchers = new java.util.LinkedList();
    }
    
    public void addVoucher (VoucherInfo info)
    {
        m_vouchers.add(info);
        fireTableDataChanged();
    }
    
    public void clear ()
    {
        m_vouchers.clear();
        fireTableDataChanged();
    }
    
    public boolean containsVoucher (VoucherInfo info)
    {
        java.util.Date today = new java.util.Date();
        java.util.Iterator iterator = m_vouchers.iterator();
        while (iterator.hasNext())
        {
            VoucherInfo current = (VoucherInfo)iterator.next();
            /* (2001-08-16 RTG) The criteria for VoucherInfo equality should
             * be decided (i.e., do the Vouchers need to be equal or are
they
             * considered irrelevant provided product, merchant, and expiry
             * are equal?) and then the equals method overridden.
             */
            if (info.getProductID().equals(current.getProductID())
                && info.getMerchantID().equals(current.getMerchantID())
                && !today.after(current.getExpiryDate()))
            {
                return true;
            }
        }
        return false;

    }
    
    public void removeVoucher (VoucherInfo info)
    {
        m_vouchers.remove(info);
        fireTableDataChanged();
    }
    
    public String getColumnName (int column)
    {
        switch (column)
        {
            case merchantColumn:
                return "Merchant";
            case productColumn:
                return "Product";
            case expiryColumn:
                return "Expiry";
            default:
                return "";
        }
    }

    public int getColumnCount ()
    {
        return columns;
    }
    
    public int getRowCount ()
    {
        return m_vouchers.size();
    }
    
    public Object getValueAt (int row, int column)
    {
        if (row >= m_vouchers.size())
        {
            return null;
        }
        
        VoucherInfo info = (VoucherInfo)m_vouchers.get(row);
        if (info == null)
        {
            return null;
        }
        switch (column)
        {
            case merchantColumn:
                return info.getMerchantID();
            case productColumn:
                return info.getProductID();
            case expiryColumn:
                DateFormat dateFormat = DateFormat.getDateTimeInstance(
                    DateFormat.LONG, DateFormat.LONG);
                return dateFormat.format(info.getExpiryDate());
            default:
                return null;
        }
    }
    
}

