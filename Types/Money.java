/*
 * Money.java
 *
 * Created on June 13, 2001, 11:11 AM
 */

package com.inexum.Types;

import java.util.*;
import com.inexum.Database.*;
import java.sql.*;


/**
 *
 * @author  nkapov
 * @version 
 */
public class Money extends java.lang.Object
    implements java.io.Serializable
{
    
    private static Map m_strToCdCurrencyMap = null;
    private static Map m_cdToStrCurrencyMap = null;
    
    private final int c_currencyCodeLength = 3;
    
    private String m_currency; //The currency for the money
    private double  m_price; //The physical price
 
   /** Creates new Money object */
    public Money( String amount )
    {
       //Parse the value passed in.
       parseAmount( amount );
    }
    
    public Money( double price, String currency)
    {
        m_price = price;
        m_currency = currency;
        
    }
    
    public static String getCurrencyString( String currencyCode )
    {
        LoadCurrencyMap();
        
        return (String)m_cdToStrCurrencyMap.get( currencyCode );
        
    }
    
    public static String getCurrencyCode( String currencyString )
    {
        LoadCurrencyMap();
        
        return (String)m_strToCdCurrencyMap.get( currencyString );
    }
    
    private static void LoadCurrencyMap()
    {
/*         if( m_strToCdCurrencyMap == null || m_cdToStrCurrencyMap == null  )
        {
            m_strToCdCurrencyMap = new HashMap();
            m_cdToStrCurrencyMap = new HashMap();
             
            try
            {
                DatabaseConPool dbConnPool = DatabaseConPool.getDatabaseConPool();
                Connection conn = dbConnPool.getConnection( DatabaseConPool.c_OasisPool,
                                                        "microac","iNexum123" );
                PreparedStatement stmt = conn.prepareStatement(
                            "SELECT CURR_CD, ABBREV FROM Currency" );
                       
                ResultSet rset = stmt.executeQuery();
            
                while( rset.next())
                {
                    m_strToCdCurrencyMap.put( rset.getString( "ABBREV" ), 
                                        rset.getString( "CURR_CD" ) );
                    
                    m_cdToStrCurrencyMap.put( rset.getString("CURR_CD"),
                                        rset.getString( "ABBREV" ) );
                                    
                }

                rset.close();
                stmt.close(); 
            }
            catch( SQLException SQLE )
            {
                SQLE.printStackTrace();
            }

        }
*/       
    }
    
    public boolean equals (Object obj)
    {
        if (!this.getClass().isAssignableFrom(obj.getClass()))
        {
            return false;
        }
        Money moneyObj = (Money)obj;
        if (moneyObj.getPrice() != m_price
            || !moneyObj.getCurrency().equals(m_currency))
        {
            return false;
        }
        return true;
    }
    
    private void parseAmount( String amount )
    {
        findPrice( findCurrency( amount ) );
    }
    
    
    private String findCurrency( String amount )
    {
        //Take the last three characters, ISO 4217 currency names
        m_currency = amount.substring( amount.length() - c_currencyCodeLength );
        
        //return just the price string
        return amount.substring( 0, amount.length() - c_currencyCodeLength );
    }
    
    private void findPrice( String amount )
    {
        //Check for an exponential component
        int index = amount.indexOf('E');
        
        if( index == -1 )
                
            m_price = Double.parseDouble(amount);
        else
        {
            //Take the numbers to the left of E
            String base = amount.substring(0,index);
            String exp = amount.substring( index+1, amount.length() );
            double baseFlt = Double.parseDouble( base );
            double expFlt = Double.parseDouble( exp );
            
            m_price = baseFlt * java.lang.Math.pow( 10, expFlt );
   
        }
        
    }
    
    public String getCurrency(){ return m_currency; }
    public double getPrice() { return m_price; }
    public String toString() { return Double.toString(m_price) + m_currency; }
    
   public Money convertToBaseCurrency( Money exchangeRate )
   {
       return new Money( m_price * exchangeRate.getPrice(),
                            exchangeRate.getCurrency() );
   }
   
   public Money convertFromBaseCurrency( Money exchangeRate, String newCurrency )
   {
       return new Money( m_price / exchangeRate.getPrice(),
                            newCurrency );
                                                        
            
   }
    
    public static void main( String[] args )
    {
        Collection monCol = new ArrayList( 4 );
        
        monCol.add( new Money( "+4.25USD" ) );
        monCol.add( new Money( "-4.00CAD" ) );
        monCol.add( new Money( "452222USD" ) );
        monCol.add( new Money( "-25E-2.5USD" ) );
        
        Iterator iter = monCol.iterator();
        
        int count = 1;
        while( iter.hasNext() )
        {
            Money mon = (Money)iter.next();
            System.out.println("Price: " + mon.getPrice() );
            System.out.println("Currency: " + mon.getCurrency() );
        }
        
        System.out.println( Money.getCurrencyCode("CAD") );
        
    }
}
