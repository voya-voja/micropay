/*
 * ProcessingQueue.java
 *
 * Created on March 27, 2002, 9:11 PM
 */

package com.inexum.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

/** A thread-safe queue implementation based on a LinkedList.
 *
 * @author  nvojinov
 * @version 2.0.2
 */
public class ProcessingQueue
{
    /** A new instance of of a processing queue. */
    public ProcessingQueue()
    {
        m_queue = Collections.synchronizedList( new LinkedList() );
    }

    /** A method for a client thread to add elements to a queue.
     *
     * @param  element - an ellement to be added to the processing queue
     * @throws IllegalMonitorStateException if there is synchronization error.
     * @throws ClassCastException if the class of the specified element prevents 
     * it from being added to this queue. 
     * @throws IllegalArgumentException if some aspect of this element prevents 
     * it from being added to this queue. 
     */
    public void add( Object element ) throws IllegalMonitorStateException 
    {
        try
        {
            synchronized( this )
            {
                m_queue.add( element );
                notifyAll();
            }
        }
        catch( UnsupportedOperationException e ) 
        { 
            e.printStackTrace(); 
        }
    }

    /** A method for a processing thread to remove an element from a queue. 
     * If there is no more elements to be processed the processing thread is 
     * in wait state.
     *
     * @return the first element from the queue.
     * @throws IllegalMonitorStateException if there is synchronization error.
     * @throws InterruptedException if the processing thread is interapted.
     */
    public Object waitElement() throws IllegalMonitorStateException, InterruptedException
    {
        try
        {
            synchronized( this )
            {
                while( m_queue.size() == 0 )
                    wait();
                return( m_queue.remove(0) );
            }
        }
        catch( UnsupportedOperationException e ) 
        { 
            e.printStackTrace();
            return null; //ntk this shoudl throw an exception
        }
    }

    /** A method for a processing thread to remove an element from a queue. 
     * If there is no more elements to be processed the processing thread is 
     * in wait state.
     *
     * @return the first element from the queue.
     */
    public Object removeElement()
    {
        try
        {
            return( m_queue.remove(0) );
        }
        catch( UnsupportedOperationException e ) 
        { 
            e.printStackTrace();
            return null;
        }
    }

    /** Return the number of ellements in the queue.
     *
     * @return the number of elements in the queue.
     */
    public int size() { return( m_queue.size() ); }

    private static List m_queue = null; 
}
