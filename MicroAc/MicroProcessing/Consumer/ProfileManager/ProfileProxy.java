/*
 * ProfileProxy.java
 *
 * Created on July 6, 2001, 1:56 PM
 */

package com.inexum.MicroAc.MicroProcessing.Consumer.ProfileManager;

import com.inexum.MicroAc.Exceptions.*;
import com.inexum.MicroAc.MicroProcessing.Transaction.*;
import com.inexum.MicroAc.Transaction.TransactionDescription;
import com.inexum.MicroAc.Merchant.*;
import com.inexum.MicroAc.DomainTypes.IProfile;
import com.inexum.MicroAc.DomainTypes.IRemoteProfile;
import com.inexum.MicroAc.DomainTypes.VoucherInfo;
import com.inexum.MicroAc.DomainTypes.SignedVoucher;
import com.inexum.Types.Money;

import java.util.Collection;
import javax.crypto.SealedObject;
import java.rmi.RemoteException;

/**
 *
 * @author  Administrator
 * @version 1.0
 */
public final class ProfileProxy extends Object 
    implements java.io.Serializable, IProfile
{

    protected String            m_nativeCurrency;
    protected Money             m_xChangeRate;
    protected double            m_balance;
    protected Collection        m_vouchers;
    protected ISession          m_session; 
    protected IRemoteProfile    m_remoteProfile;

//----------------------------------------------------------------------------//

    /** Creates new ProfileProxy */
    protected ProfileProxy( )
    {
    }

//----------------------------------------------------------------------------//

    public final double getBalance()     { return m_balance;     }
    public final Collection getVouchers(){ return m_vouchers;    }
    public final Money getXchngRate()    { return m_xChangeRate; }
    public final String getXchngRateCur(){ return m_xChangeRate.getCurrency(); }
    public final String getNativeCur()   { return m_nativeCurrency; }

//----------------------------------------------------------------------------//

    public final Money getMoneyBalance()
    {
        return new Money(m_balance, m_nativeCurrency);
    }

//----------------------------------------------------------------------------//

    public final SignedVoucher purchase( TransactionDescription description, 
        SealedObject voucher )
        throws UnknownMerchantException, InvalidVoucherException,
        RestrictedProductException, InsufficientFundsException,
        AccountUnavailableException, InternalErrorException
    {
        SignedVoucher sv = null;
        double prePurchaseBalance = m_balance;
        try
        {
            sv = m_session.purchase( description, voucher );
            
            /* (2001-08-14 RTG) Ugly hack. */
            if (m_remoteProfile == null)
            {
                m_remoteProfile = (IRemoteProfile)m_session;
            }
            try
            {
                m_balance = m_remoteProfile.getBalance();
            }
            catch (java.rmi.RemoteException e)
            {
                // Probably means the CAMA had trouble converting balance
                // from a String to a Money. Ignore.
                e.printStackTrace();
                throw new InternalErrorException(false,
                    e.getLocalizedMessage());
            }
        }
        catch( RemoteException RE )
        {
            RE.printStackTrace ();
            throw new InternalErrorException (prePurchaseBalance == m_balance,
                RE.getLocalizedMessage());
        }
        catch( UnknownMerchantException UME )
        {
            throw UME;
        }
        catch( InvalidVoucherException IVE )
        {
            throw IVE;
        }
        catch( RestrictedProductException RPE )
        {
            throw RPE;
        }
        catch( InsufficientFundsException ISFE )
        {
            throw ISFE;
        }
        catch( AccountUnavailableException AUE )
        {
            throw AUE;
        }
        finally
        {
            return sv;
        }
    }

//----------------------------------------------------------------------------//

    public void close() 
    {
        try
        {
            m_session.close();
        }
        catch ( RemoteException RE ) 
        {
            RE.printStackTrace();
        }
        finally
        {
        }
    }

}
