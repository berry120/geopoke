/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import javafx.scene.control.Label;

/**
 *
 * @author Michael
 */
public class StatusLabel extends Label {
    
    public StatusLabel() {
        setIdle();
    }
    
    public void setIdle() {
        setText("Idle :-)");
    }
    
    public void setGettingCache() {
        setText("Getting cache details...");
    }
    
}
