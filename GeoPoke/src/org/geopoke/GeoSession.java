package org.geopoke;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 *
 * @author Michael
 */
public class GeoSession {

    private static final Logger LOGGER = Logger.getLogger(GeoSession.class.getName());
    private String username;
    private String password;
    private HttpClient client;

    public GeoSession(String username, String password) {
        client = new HttpClient();
        this.username = username;
        this.password = password;
    }

    public Geocache getCache(String target) {
        if (target.trim().toLowerCase().startsWith("gc")) {
            return getCacheFromGC(target);
        } else {
            return getCacheFromURL(target);
        }
    }

    public Geocache getCacheFromGC(String gcCode) {
        return getCacheFromURL("http://www.geocaching.com/seek/cache_details.aspx?wp=" + gcCode);
    }

    public Geocache getCacheFromURL(String url) {
        GetMethod httpGet = new GetMethod(url);
        try {
            client.executeMethod(httpGet);
            return new CacheFactory().cacheFromPage(httpGet.getResponseBodyAsString());
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Exception getting page", ex);
            return null;
        }
    }

    public boolean login() {
        String strURL = "https://www.geocaching.com/login/default.aspx";
        HttpState initialState = new HttpState();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
        client.setState(initialState);
        client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        GetMethod httpget = new GetMethod(strURL);
        try {
            client.executeMethod(httpget);
            httpget.getResponseBodyAsString();
        } catch (IOException ex) {
            return false;
        }
        Cookie[] cookies = client.getState().getCookies();
        httpget.releaseConnection();

        PostMethod postMethod = new PostMethod("https://www.geocaching.com/login/default.aspx");
        NameValuePair[] postData = new NameValuePair[8];
        postData[0] = new NameValuePair("RESETCOMPLETE", "Y");
        postData[1] = new NameValuePair("ctl00$ContentBody$tbUsername", username);
        postData[2] = new NameValuePair("ctl00$ContentBody$tbPassword", password);
        postData[3] = new NameValuePair("ctl00$ContentBody$btnSignIn", "Sign In");
        postData[4] = new NameValuePair("ctl00$ContentBody$cbRememberMe", "On");
        postData[5] = new NameValuePair("__VIEWSTATE", "");
        postData[6] = new NameValuePair("__EVENTTARGET", "");
        postData[7] = new NameValuePair("__EVENTARGUMENT", "");
        postMethod.addParameters(postData);
        postMethod.addRequestHeader("Referer", strURL);
        postMethod.addRequestHeader("Origin", "https://www.geocaching.com");

        for (int i = 0; i < cookies.length; i++) {
            initialState.addCookie(cookies[i]);
        }
        client.setState(initialState);
        int responseCode;
        try {
            responseCode = client.executeMethod(postMethod);
        } catch (IOException ex) {
            return false;
        }
        postMethod.releaseConnection();
        return responseCode == 302;
    }
}
