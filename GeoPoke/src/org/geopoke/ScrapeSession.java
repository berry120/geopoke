/* 
 * This file is part of Geopoke.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.geopoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author Michael
 */
public class ScrapeSession implements GeoSession {

    private static final Logger LOGGER = Logger.getLogger(ScrapeSession.class.getName());
    private String username;
    private String password;
    private DefaultHttpClient client;

    public ScrapeSession(String username, String password) {
        client = new DefaultHttpClient();
        this.username = username;
        this.password = password;
    }

    @Override
    public Geocache getCache(String target) {
        if(target.trim().toLowerCase().startsWith("gc")) {
            return getCacheFromGC(target);
        }
        else {
            return getCacheFromURL(target);
        }
    }

    @Override
    public Geocache getCacheFromGC(String gcCode) {
        return getCacheFromURL("http://www.geocaching.com/seek/cache_details.aspx?wp=" + gcCode);
    }

    @Override
    public Geocache getCacheFromURL(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = client.execute(httpGet);
            return new CacheFactory().cacheFromPage(inputStreamToString(response.getEntity().getContent()));
        }
        catch(IOException | IllegalStateException ex) {
            LOGGER.log(Level.WARNING, "Exception getting page", ex);
            return null;
        }
    }

    @Override
    public boolean login() {
        HttpPost postMethod = new HttpPost("https://www.geocaching.com/login/default.aspx");
        List<NameValuePair> postData = new ArrayList<>();
        postData.add(new BasicNameValuePair("RESETCOMPLETE", "Y"));
        postData.add(new BasicNameValuePair("ctl00$ContentBody$tbUsername", username));
        postData.add(new BasicNameValuePair("ctl00$ContentBody$tbPassword", password));
        postData.add(new BasicNameValuePair("ctl00$ContentBody$btnSignIn", "Sign In"));
        postData.add(new BasicNameValuePair("ctl00$ContentBody$cbRememberMe", "On"));
        postData.add(new BasicNameValuePair("__VIEWSTATE", ""));
        postData.add(new BasicNameValuePair("__EVENTTARGET", ""));
        postData.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData);
            postMethod.setEntity(entity);
        }
        catch(UnsupportedEncodingException ex) {
            LOGGER.log(Level.SEVERE, "Unsupported encoding", ex);
        }
        postMethod.addHeader(new BasicHeader("Origin", "https://www.geocaching.com"));
        int responseCode;
        try {
            responseCode = client.execute(postMethod).getStatusLine().getStatusCode();
        }
        catch(IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't get response code", ex);
            return false;
        }
        postMethod.releaseConnection();
        return responseCode == 302;
    }

    private String inputStreamToString(InputStream stream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder content = new StringBuilder();
            while((line = reader.readLine()) != null) {
                content.append(line).append('\n');
            }
            return content.toString();
        }
        catch(IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't get page content", ex);
            return null;
        }
    }
}
