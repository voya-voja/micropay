/*
 * ConsumerProfile.java
 *
 * Created on June 21, 2001, 3:15 PM
 */

package com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.Merchant.*;
import com.inexum.Encoding.*;
import com.inexum.MicroAc.DomainTypes.*;
import com.inexum.Types.Money;
import com.inexum.Database.*;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.MicroAc.DomainTypes.VoucherInfo;
import com.inexum.MicroAc.DomainTypes.Voucher;
import com.inexum.MicroAc.Transaction.TransactionDescription;
import com.inexum.MicroAc.MicroProcessing.Transaction.ISession;
import com.inexum.MicroAc.MicroProcessing.Transaction.PurchasingSession;
import com.inexum.MicroAc.MicroProcessing.Transaction.TxInfo;
import com.inexum.util.ConfigurationManager;

import java.util.*;
import javax.crypto.SealedObject;
import java.rmi.*;
import java.io.*;

import java.sql.*;
import javax.sql.*;
import oracle.jdbc.*;
import oracle.sql.BLOB;

/**
 *
 * @author  nkapov
 * @version 1.0
 */
public class ConsumerProfile extends Profile
{
    private   final     String                  m_BIN;
    private   final     String                  m_PAN;
    private   final     String                  m_nativeCur;
    private   final     Money                   m_xChngRate;
    private   final     HashMap                 m_vouchers;
    private             ISession                m_session;
    private             Connection              m_dbMicroAcConn;
   //ntk  private             Connection              m_dbOasisConn;
    private   final     String                  m_userID;
    private   final     ConsumerProfileManager  m_parent;
    private   final     ConfigurationManager	c_Rsrcs = ConfigurationManager.Instance();

    /** Creates new ConsumerProfile */
    public ConsumerProfile( ConsumerProfileManager parent,
                            String userID,
                            ResultSet profileSet,
                            ResultSet voucherSet,
                            ResultSet cardholderSet,
                            ISession session )
        throws SQLException
    {
        m_parent = parent;
        m_userID = userID;

        // Position the profile and cardholder ResultSets at first record
        //profileSet.next();
        cardholderSet.next();

        m_BIN = profileSet.getString("BIN");
        m_PAN = profileSet.getString("PAN");

// nmv        m_nativeCur = Money.getCurrencyString( (cardholderSet.getString(
// nmv            "Local_curr_cd") ) );
        m_nativeCur = "CAD";
        //m_nativeCur = profileSet.getString( "NativeCurrency" );
        System.out.println("The consumer currency string is " + m_nativeCur );

        m_xChngRate = new Money( profileSet.getString( "XChngRate" ) );

        //this could be CR_limit, they're the same amount
        m_balance = cardholderSet.getDouble( "Open_to_Buy" );
        System.out.println( "The consumer balance is " + m_balance );

        m_vouchers = new HashMap();

        try
        {
            //could use the object functionality in oracle and
            // java.......getCustomDatum(...)
            if( voucherSet != null )
            {
                while( voucherSet.next() )
                {
                    //get the LOB locator
                    java.sql.Blob blob = voucherSet.getBlob( "SignedVoucher" );
                    ObjectInputStream in
                        = new ObjectInputStream(blob.getBinaryStream());

                    VoucherInfo vouchInfo = new VoucherInfo(
                        voucherSet.getString( "MerchantID" ),
                        voucherSet.getString( "ProductID" ),
                        voucherSet.getTimestamp( "ExpiryDate" ),
                        (SignedVoucher)in.readObject() );

                    m_vouchers.put( vouchInfo.toKey(), vouchInfo ) ;

                    in.close();
                }
            }
            m_session = session;
            setDBConnection();
        }
        catch( IOException IOE )
        {
            IOE.printStackTrace ();
        }
        catch( ClassNotFoundException CNFE )
        {
            CNFE.printStackTrace ();
        }
        catch( SQLException SQLE )
        {
            SQLE.printStackTrace();
        }
    }

     private void setDBConnection()
        throws SQLException
    {
       m_dbMicroAcConn = DatabaseConPool.getDatabaseConPool().getConnection(
            c_Rsrcs.getString( "db.UserID" ), 
            c_Rsrcs.getString( "db.Password" ) );

        m_dbMicroAcConn.setAutoCommit(false);

 /* nmv       m_dbOasisConn =  DatabaseConPool.getDatabaseConPool().getConnection(
            DatabaseConPool.c_OasisPool,
            m_consumerProfileProps.getString( "UserID" ), 
            m_consumerProfileProps.getString( "Password" ));

        m_dbOasisConn.setAutoCommit( false );
nmv */
    }

    public boolean isFinished ()
        throws ExcludedMiddleException
    {
        try
        {
            return m_session.isFinished ();
        }
        catch (java.rmi.RemoteException e)
        {
            throw new ExcludedMiddleException (e.getLocalizedMessage ());
        }
    }

    public void debit(double amount)
        throws InsufficientFundsException, AccountUnavailableException
    {
        if (m_balance < amount)
        {
            throw new InsufficientFundsException();
        }

        m_balance -= amount;

        try
        {
            updateDatabase();
        }
        catch( AccountUnavailableException AUE )
        {
            m_balance += amount;
            throw AUE;
        }
        finally
        {
        }
    }

    public Money getXchngRate ()
    {
        return m_xChngRate;
    }

    public String getCurrency ()
    {
        return m_nativeCur;
    }

    protected void setSession(ISession session)
    {
        m_session = session;
    }

    public final String getUserID()
    {
        return m_userID;
    }

    private void getVoucherInfo( Collection voucherInfo )
    {
        Iterator iter = m_vouchers.values().iterator();
        while( iter.hasNext() )
        {
           voucherInfo.add( iter.next() );
        }
    }

    public SignedVoucher getVoucher (final String merchantID,
        final String productID)
        throws NoSuchVoucherException
    {
        VoucherInfo voucherInfo = (VoucherInfo)m_vouchers.get(
            VoucherInfo.toKey(merchantID, productID));
        if (voucherInfo == null)
        {
            throw new NoSuchVoucherException();
        }

        // Check if voucher has expired.
        if (voucherInfo.getExpiryDate().before(new java.util.Date()))
        {
            removeVoucher(voucherInfo);
            throw new NoSuchVoucherException();
        }
        return voucherInfo.getSignedVoucher();
    }

    public ProfileProxy getProfileProxy()
    {
        ProfileProxy proxy = new ProfileProxy();
        proxy.m_nativeCurrency = m_nativeCur;
        proxy.m_xChangeRate = m_xChngRate;
        proxy.m_balance = m_balance;
        proxy.m_vouchers = new java.util.LinkedList();
        getVoucherInfo(proxy.m_vouchers);
        proxy.m_session = m_session;
        return proxy;
    }

    private void checkRestrictions() throws RestrictedProductException
    {
        boolean noRestrictions = true;

        if( !noRestrictions )
            throw new RestrictedProductException();
    }

    private final Money convertMerchantPrice( Money productPrice,
        Money merchantExchangeRate )
        throws ConversionException
    {
        //check to see if the merchant and consumer currencies are the same
        if( !productPrice.getCurrency().equals(m_nativeCur) )
        {
            //make sure the merchant and consumer exchange rate currencies match
            if( merchantExchangeRate.getCurrency().equals(
                m_xChngRate.getCurrency()))
            {
                //convert the product price to the base currency
                Money baseAmt = productPrice.convertToBaseCurrency(
                    merchantExchangeRate );

                //convert from the base currency to the consumers currency
                return baseAmt.convertFromBaseCurrency( m_xChngRate,
                    m_nativeCur ) ;
            }
            else
            {
                //we were unable to convert the product price so return the
                //price in the merchants currency
                throw new ConversionException(c_Rsrcs.getString("ExchangeCurrencyMisMatch"));
            }
        }
        else
            return productPrice;
    }

    public void removeVoucher (VoucherInfo voucherInfo)
    {
        if (!m_vouchers.containsKey(voucherInfo.toKey()))
        {
            return;
        }

        m_vouchers.remove(voucherInfo.toKey());
        try
        {
            PreparedStatement removal = m_dbMicroAcConn.prepareStatement(
                "DELETE FROM Voucher WHERE UserID = ? AND MerchantID = ?" +
                " AND ProductID = ?");
            removal.setString(1, m_userID);
            removal.setString(2, voucherInfo.getMerchantID());
            removal.setString(3, voucherInfo.getProductID());
            if (removal.execute())
            {
                m_dbMicroAcConn.commit();
            }
        }
        catch (java.sql.SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void addVoucher( TransactionDescription description,
        SignedVoucher signedVoucher )
    {
        if(description.getProductExpiry().after(new java.util.Date()))
        {
            //insert the voucher into the collection
            VoucherInfo vouchInfo = new VoucherInfo(description.getIssuer(),
                description.getOfferID(), description.getProductExpiry(),
                signedVoucher );
            m_vouchers.put( vouchInfo.toKey(), vouchInfo ) ;

            try
            {
                //insert the voucher into the database
                PreparedStatement stmt = m_dbMicroAcConn.prepareStatement(
                    "INSERT INTO Voucher( UserID, MerchantID, ProductID, " +
                    "ExpiryDate, SignedVoucher ) VALUES (?, ?, ?, ?, " +
                    "empty_blob() ) " );

                stmt.setString( 1, m_userID );
                stmt.setString( 2, description.getIssuer() );
                stmt.setString( 3, description.getOfferID() );
                java.sql.Timestamp expiryDate = new java.sql.Timestamp(
                    description.getProductExpiry().getTime());
                stmt.setTimestamp(4, expiryDate);

                stmt.execute();

                String query = "SELECT * FROM Voucher WHERE userid = " +
                    "'" + m_userID + "' AND MerchantID = '" +
                    description.getIssuer() + "' AND ProductID = '" +
                    description.getOfferID() + "' for update of SignedVoucher";

                PreparedStatement updateStatement
                    = m_dbMicroAcConn.prepareStatement(query);

                OracleResultSet rSet
                    = (OracleResultSet)updateStatement.executeQuery();

                rSet.next();
                BLOB blob = rSet.getBLOB("SignedVoucher");

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream stream = new ObjectOutputStream(byteStream);
                stream.writeObject(signedVoucher);

                byte[] signedVoucherAsBytes = byteStream.toByteArray();

                OutputStream outstream=((BLOB)blob).getBinaryOutputStream();
                outstream.write(signedVoucherAsBytes);
                outstream.flush();
                outstream.close();
                m_dbMicroAcConn.commit();
            }
            catch( SQLException SQLE )
            {
                SQLE.printStackTrace();
            }
            catch( IOException  IOE )
            {
                IOE.printStackTrace();
            }
        }
    }

    public final String getPAN()
    {
        return m_PAN;
    }

    public final String getBIN()
    {
        return m_BIN;
    }

    public void close()
    {
        //The balance is update with each purchase
        //updateDatabase();
        m_parent.logOff(m_userID);
    }

    private void updateDatabase()
        throws AccountUnavailableException
    {
        try
        {
            PreparedStatement updateStatement
                = m_dbMicroAcConn.prepareStatement(
                "UPDATE Profile SET Balance = ? WHERE UserID = ?");
            updateStatement.setDouble(1, m_balance);
            updateStatement.setString(2, m_userID);
            updateStatement.executeUpdate();
            m_dbMicroAcConn.commit();

            updateStatement = m_dbMicroAcConn.prepareStatement(
                "UPDATE Cardholders SET open_to_buy = ? , cr_limit = ? " +
                "WHERE PAN = ? ");
            updateStatement.setDouble(1, m_balance );
            updateStatement.setDouble(2, m_balance );
            updateStatement.setString(3, m_PAN );
            updateStatement.executeUpdate();
            m_dbMicroAcConn.commit();
        }
        catch (java.sql.SQLException e)
        {
            e.printStackTrace();
            throw new AccountUnavailableException( e.getLocalizedMessage() );
        }
    }

    /* For testing purposes
     */
    public static void main( String[] args )
        throws SQLException, InvalidVoucherException,
        java.io.FileNotFoundException, java.io.IOException, Exception
    {
        //Create a fake voucher record.
        DatabaseConPool dbPool = DatabaseConPool.getDatabaseConPool();
        Connection conn = null;
        try
        {
            ConfigurationManager rsrcs = ConfigurationManager.Instance();
            conn = dbPool.getConnection( rsrcs.getString( "db.UserID" ), 
                                                        rsrcs.getString( "db.Password" ) );
        }
        catch( SQLException SQLE )
        {
            System.out.println(SQLE.getMessage());
            SQLE.printStackTrace();
        }
        conn.setAutoCommit(false);

        // Provide CryptoAgents with a key to use.

        ConfigurationManager rsrcs = ConfigurationManager.Instance();
        java.io.File keyFile = new java.io.File(rsrcs.getString("merchant.key.store.Path"));
        int keyLength = Integer.parseInt(rsrcs.getString("acquirer.key.Length"), 10);
        CryptoAgentFactory.InitWithKeyFile(keyFile, keyLength);

        SignedVoucher signedVoucher = CryptoAgentFactory.GetCryptoAgent().
            sign( new Voucher( new Product() ) );

        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Voucher( UserID, MerchantID, ProductID, "
            + "SignedVoucher ) VALUES ('Nick1','EMafia' ,'Elter', "
            + "empty_blob() ) " );

        stmt.execute();

        String query = "SELECT * FROM Voucher WHERE userid = 'Nick1' for "
            + "update of SignedVoucher";

        OracleResultSet rset = (OracleResultSet)stmt.executeQuery(query);
        rset.next();
        BLOB blob = rset.getBLOB("SignedVoucher");

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(byteStream);
        stream.writeObject(signedVoucher);

        byte[] signedVoucherAsBytes = byteStream.toByteArray();

        OutputStream outstream=((BLOB)blob).getBinaryOutputStream();
        outstream.write(signedVoucherAsBytes);
        outstream.flush();
        outstream.close();
        conn.commit();

        System.out.println( " Here is the signedVOucher that was written to " +
            "the database" );
        System.out.println(new String(Base64.Encode(signedVoucherAsBytes)));

        //Get the signed voucher out of the database
        query = "Select * from voucher where userid = 'Nick1'";
        rset = (OracleResultSet)stmt.executeQuery(query);

        while( rset.next() )
        {
            // Casting the resturn type to BLOB
            //get the LOB locator
            BLOB readBlob = rset.getBLOB( "SignedVoucher" );

            ObjectInputStream inStream = new ObjectInputStream(
                ((BLOB)readBlob).getBinaryStream());

            SignedVoucher sv2 = (SignedVoucher)inStream.readObject();

            ByteArrayOutputStream byteStream2 = new ByteArrayOutputStream();
            ObjectOutputStream stream2 = new ObjectOutputStream(byteStream2);
            stream2.writeObject(sv2);

            byte[] signedVoucherAsBytes2 = byteStream.toByteArray();

            System.out.println( "This is the signed object that was read " +
                "from the database" );
            System.out.println(new String(Base64.Encode(
                signedVoucherAsBytes2)));

            if( java.util.Arrays.equals(signedVoucherAsBytes2,
                signedVoucherAsBytes ) )
                System.out.println( "The byte arrays are equal" );
            else
                 System.out.println( "The byte arrays are not equal" );
        }
        rset.close();
        conn.close();
    }

}
