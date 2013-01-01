/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

/**
 *
 * @author Michael
 */
public class Coords {
    
    private String oldCoords;
    private String newCoords;

    public Coords(String oldCoords, String newCoords) {
        this.oldCoords = oldCoords;
        this.newCoords = newCoords;
    }

    public String getOldCoords() {
        return oldCoords;
    }

    public String getNewCoords() {
        return newCoords;
    }
    
}
