package org.geopoke;

/**
 * 
 * @author Michael
 */
public class SessionFactory {
    
    public GeoSession newAPISession() {
        return new APISession();
    }
    
    public GeoSession newScrapeSession() { 
       return new LoginStage().getSession();
    }
    
}
