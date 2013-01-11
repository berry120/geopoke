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

import com.arcao.geocaching.api.GeocachingApi;
import com.arcao.geocaching.api.data.CacheLog;
import com.arcao.geocaching.api.data.DeviceInfo;
import com.arcao.geocaching.api.data.UserProfile;
import com.arcao.geocaching.api.data.type.CacheLogType;
import com.arcao.geocaching.api.data.type.MemberType;
import com.arcao.geocaching.api.exception.GeocachingApiException;
import com.arcao.geocaching.api.impl.LiveGeocachingApi;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import name.antonsmirnov.javafx.dialog.Dialog;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.geopoke.keys.keybash.KeyBash;

/**
 *
 * @author Michael
 */
public class APISession implements GeoSession {

    public static final String OAUTH_URL = "https://www.geocaching.com/OAuth/oauth.ashx";
    public static final String OAUTH_CALLBACK_URL = "http://localhost:20586/";
    private static final String CLOSE_WINDOW_JS = "HTTP/1.0 200 OK\nConnection: close\nServer: SimpleHTTPtutorial v0\nContent-Type: text/html\n\n<html><script language='javascript'>function happycode(){close();}</script><body><script>window.onload=happycode;</script><h1>Thanks!</h1><p>Authorisation complete, you can close this window now.</p></body></html>\n";
    private GeocachingApi api;
    private Boolean limited = null;

    public boolean login() {
        try {
            Logger.getRootLogger().setLevel(Level.WARN);
            String token = getToken();
            api = new LiveGeocachingApi("https://api.groundspeak.com/LiveV6/Geocaching.svc");
            api.openSession(token);
            return true;
        }
        catch(GeocachingApiException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private String getToken() {
        Preferences prefs = Preferences.userRoot().node("Geopoke");
        String token = prefs.get("token", null);
        if(token != null) {
            return token;
        }
        try {
            OAuthConsumer consumer = new CommonsHttpOAuthConsumer(KeyBash.key(), KeyBash.secret());
            OAuthProvider provider = new CommonsHttpOAuthProvider(OAUTH_URL, OAUTH_URL, OAUTH_URL);
            String authUrl = provider.retrieveRequestToken(consumer, OAUTH_CALLBACK_URL);
            Desktop.getDesktop().browse(new URL(authUrl).toURI());

            ServerSocket serverSocket = new ServerSocket(20586);
            Socket s = serverSocket.accept();
            String str = new BufferedReader(new InputStreamReader(s.getInputStream())).readLine();
            try(DataOutputStream output = new DataOutputStream(s.getOutputStream())) {
                output.writeBytes(CLOSE_WINDOW_JS);
            }
            String verifier = str.substring(str.indexOf("oauth_verifier=") + "oauth_verifier=".length());
            verifier = URLDecoder.decode(verifier.substring(0, verifier.indexOf('&')), "UTF-8");

            provider.retrieveAccessToken(consumer, verifier);
            prefs.put("token", consumer.getToken());
            return consumer.getToken();
        }
        catch(OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException | URISyntaxException | IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isLimited() {
        if(limited == null) {
            try {
                UserProfile profile = api.getYourUserProfile(false, false, false, false, false, false,
                        new DeviceInfo(2147483647, 2147483647, "String content", "String content", "String content", "String content", (float) 1.26743233E+15, "String content", "String content", "String content"));
                MemberType type = profile.getUser().getMemberType();
                limited = type == MemberType.Basic || type == MemberType.Guest;
            }
            catch(GeocachingApiException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return limited;
    }

    @Override
    public Geocache getCache(String target) {
        if(target.toUpperCase().startsWith("GC")) {
            return getCacheFromGC(target);
        }
        else {
            return getCacheFromURL(target);
        }
    }

    @Override
    public Geocache getCacheFromGC(String gcCode) {
        try {
            com.arcao.geocaching.api.data.Geocache apiCache = api.getCache(gcCode, 10, 0);
            if(apiCache == null || !apiCache.getCacheCode().trim().equalsIgnoreCase(gcCode.trim())) {
                return null;
            }
            if(isLimited()) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Dialog.showWarning("Limited member", "As a basic member of geocaching.com you can download full details of just 3 caches per 24 hour period. Upgrade to geocaching.com premium membership today for only US $30 per year to download the full details for up to 6000 caches per day.");
                    }
                });
            }
            Geocache ret = new Geocache(apiCache);
            ret.setDisabledWarning(apiCache.isArchived() || !apiCache.isAvailable());
            ret.setLogWarning(!logsok(apiCache));
            return ret;
        }
        catch(GeocachingApiException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private boolean logsok(com.arcao.geocaching.api.data.Geocache apiCache) {
        List<CacheLog> logs = apiCache.getCacheLogs();
        int max = 2;
        for(int i = 0; i < logs.size() && i < max; i++) {
            CacheLog log = logs.get(i);
            if(log.getLogType() == CacheLogType.FoundIt || log.getLogType() == CacheLogType.OwnerMaintenance) {
                return true;
            }
            else if(log.getLogType() != CacheLogType.DidntFindIt) {
                max++;
            }
        }
        return false;
    }

    @Override
    public Geocache getCacheFromURL(String url) {
        if(url.toLowerCase().contains("coord.info")) {
            String gc = url.substring(url.lastIndexOf('/') + 1);
            return getCacheFromGC(gc);
        }
        if(url.contains("wp=")) {
            String gc = url.substring(url.lastIndexOf("wp=") + 1);
            return getCacheFromGC(gc);
        }
        return null;
    }
}
