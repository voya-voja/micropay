/*
 * TransactionQueue.java
 *
 * Created on November 8, 2001, 5:00 PM
 */

package com.inexum.MicroAc.Transaction;

import com.inexum.MicroAc.Exceptions.QueueSizeExceededException;
import com.inexum.IPC.ServiceRegistry;


/**
 *
 * @author  rgoold
 * @version 1.0
 */
public class TransactionQueue extends java.lang.Object
{
    private static TransactionQueue m_Singleton;
    private static final int        c_MaxQueueSize = 1;
    
    private TransactionDispatcher   m_dispatcher;
    private java.util.LinkedList    m_queue;
    
//----------------------------------------------------------------------------//
    
    public static TransactionQueue getDefaultInstance()
    {
        if (m_Singleton == null)
        {
            m_Singleton = new TransactionQueue();
        }
        return m_Singleton;
    }
    
//----------------------------------------------------------------------------//

    /** Creates new TransactionQueue */
    protected TransactionQueue()
    {
        m_queue = new java.util.LinkedList();
        ServiceRegistry registry = ServiceRegistry.getDefaultInstance();
        registry.setServiceForName(this, "TransactionQueue");
        m_dispatcher = new TransactionDispatcher();
        m_dispatcher.start();
    }
    
//----------------------------------------------------------------------------//
    
    public boolean isFull()
    {
        return m_queue.size() >= c_MaxQueueSize;
    }
    
//----------------------------------------------------------------------------//
    
    public synchronized void queueOffer(TransactionOffer offer,
        Object object, ITransactionCompletionListener observer)
        throws QueueSizeExceededException
    {
        if (m_queue.size() >= c_MaxQueueSize)
        {
            throw new QueueSizeExceededException();
        }
        m_queue.addLast(new QueueEntry(offer, object, observer));
    }
    
//----------------------------------------------------------------------------//
    
    public synchronized boolean hasNext()
    {
        return (m_queue.size() > 0);
    }
    
//----------------------------------------------------------------------------//
    
    public synchronized QueueEntry next()
        throws java.util.EmptyStackException
    {
        if (m_queue.size() <= 0)
        {
            throw new java.util.EmptyStackException();
        }
        QueueEntry entry = (QueueEntry)m_queue.removeFirst();
        return entry;
    }
    
//----------------------------------------------------------------------------//
    
    public class QueueEntry extends java.lang.Object
    {
        private TransactionOffer                m_offer;
        private java.lang.Object                m_object;
        private ITransactionCompletionListener  m_observer;
        
    //------------------------------------------------------------------------//
        
        public QueueEntry(TransactionOffer offer, Object object,
            ITransactionCompletionListener observer)
        {
            m_offer     = offer;
            m_object    = object;
            m_observer  = observer;
        }
        
    //------------------------------------------------------------------------//
        
        public TransactionOffer getOffer()
        {
            return m_offer;
        }
        
    //------------------------------------------------------------------------//
        
        public Object getObject()
        {
            return m_object;
        }
        
    //------------------------------------------------------------------------//
        
        public ITransactionCompletionListener getObserver()
        {
            return m_observer;
        }
        
    }

}

