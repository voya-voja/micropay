/*
 * InexumBundle.java
 *
 * Created on March 27, 2002, 9:11 PM
 */

package com.inexum.util;

import java.util.ResourceBundle;
import java.util.ListResourceBundle;

/**
 *
 * @author  nvojinov
 * @version 
 */

public abstract class ConstantsBundle extends ListResourceBundle 
{
    public final void parent( ResourceBundle parent)
    {
        setParent( parent );
    }
}
