package org.geopoke;

/**
 * 
 * @author Michael
 */
public class SessionFactory {
    
    public GeoSession newAPISession() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
    
    public GeoSession newScrapeSession() { 
       return new LoginStage().getSession();
    }
    
}
