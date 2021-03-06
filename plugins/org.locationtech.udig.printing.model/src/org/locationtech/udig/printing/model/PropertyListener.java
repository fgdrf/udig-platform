/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.printing.model;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * Provides easy access for listening to printing events.
 * Developers wishing to use this should extend it and override the *Changed()
 * methods that they are interested in.
 * 
 * @see PropertyListener#boxesChanged()
 * @see PropertyListener#locationChanged()
 * @see PropertyListener#mapChanged()
 * @see PropertyListener#sizeChanged()
 * @see PropertyListener#sourceConnectionsChanged()
 * @see PropertyListener#targetConnectionsChanged()
 * @see PropertyListener#textChanged()
 * 
 * 
 * @author Richard Gould
 * @since 0.3
 */
public class PropertyListener extends AdapterImpl {

	/**
	 * Maps an event to its particular *Changed method.
	 */
    public void notifyChanged( Notification msg ) {
        switch(msg.getFeatureID(Element.class)) {
        	case ModelPackage.ELEMENT__LOCATION:
        	    locationChanged();
        		break;
        	case ModelPackage.ELEMENT__SIZE:
        	    sizeChanged();
        		break;
        }
        switch(msg.getFeatureID(Page.class)) {
        	case ModelPackage.PAGE__BOXES:
        	    boxesChanged();
        		break;
        }
        super.notifyChanged(msg);
    }

    /**
     * Indicates that the Text property has been changed
     * Override me to perform an action when Text is changed
     */
    protected void textChanged() {
        //do nothing
    }

    /**
     * Indicates that the Boxes property has been changed
     * Override me to perform an action when Boxes are changed
     */
    protected void boxesChanged() {
        //do nothing
    }

    /**
     * Indicates that the Size property has been changed 
     * 
     * Override me to perform an action when Size is changed
     */
    protected void sizeChanged() {
        //do nothing
    }

    /**
     * Indicates that the Location property has been changed 
     * 
     * Override me to perform an action when Location is changed
     */
    protected void locationChanged() {
        //do nothing
    }
    
    
    /**
     * Indicates that the Target Connections property has been changed
     * 
     * Override me to perform an action when TargetConnections are changed
     */
    protected void targetConnectionsChanged() {
        //do nothing
    }
    
    /**
     * Indicates that the Source Connections property has been changed 
     * 
     * Override me to perform an action when SourceConnections are changed
     */
    protected void sourceConnectionsChanged() {
        //do nothing
    }
}
