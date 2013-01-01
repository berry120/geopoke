/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geopoke;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 *
 * @author Michael
 */
public class CacheFactory {

    private static final Logger LOGGER = Logger.getLogger(CacheFactory.class.getName());

    @SuppressWarnings("unchecked")
    public Geocache cacheFromPage(String pageContent) {
        try {
            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode rootNode = cleaner.clean(new StringReader(pageContent));
            boolean logsok = logsok(pageContent);
            List<TagNode> detailsNodes = (List<TagNode>) rootNode.getElementListByAttValue("id", "cacheDetails", true, true);
            List<TagNode> oldWarningNodes = (List<TagNode>) rootNode.getElementListByAttValue("class", "OldWarning", true, true);
            List<TagNode> nameNodes = (List<TagNode>) rootNode.getElementListByAttValue("id", "ctl00_ContentBody_CacheName", true, true);
            List<TagNode> descriptionNodes = (List<TagNode>) rootNode.getElementListByAttValue("id", "ctl00_ContentBody_ShortDescription", true, true);
            List<TagNode> gcNodes = (List<TagNode>) rootNode.getElementListByAttValue("id", "ctl00_ContentBody_CoordInfoLinkControl1_uxCoordInfoCode", true, true);
            List<TagNode> hintNodes = (List<TagNode>) rootNode.getElementListByAttValue("id", "div_hint", true, true);
            CacheType type = getType(detailsNodes);
            if (type == null) {
                LOGGER.log(Level.WARNING, "Couldn't get cache type. Assuming traditional...");
                type = CacheType.TRADITIONAL;
            }
            String description = getDescription(descriptionNodes);
            String gc = getGC(gcNodes);
            String name = getName(nameNodes);
            String hint = getHint(hintNodes);
            Coords coords = getCoords(pageContent);
            int difficulty = 0;
            int terrain = 0;
            boolean isWarning = !oldWarningNodes.isEmpty();

            Geocache ret = new Geocache(type, gc, name, coords, description, difficulty, terrain, hint);
            ret.setDisabledWarning(isWarning);
            ret.setLogWarning(!logsok);
            return ret;
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Exception getting cache details", ex);
            return null;
        }
    }

    private Coords getCoords(String pageContent) {
        for (String line : pageContent.split("\n")) {
            if (line.startsWith("var userDefinedCoords")) {
                String initialCoords = getVal(line, "oldLatLngDisplay").get(0);
                String newCoords = null;
                String isNewCoords = getVal(line, "isUserDefined").get(0);
                if (isNewCoords.toLowerCase().equals("true")) {
                    String rawCoords = getVal(line, "newLatLng").get(0);
                    rawCoords = rawCoords.substring(1, rawCoords.length() - 1);
                    String lat = rawCoords.split(",")[0];
                    String lon = rawCoords.split(",")[1];
                    newCoords = getCoordAsString(Double.parseDouble(lat), Double.parseDouble(lon));
                }
                return new Coords(initialCoords, newCoords);
            }
        }
        LOGGER.log(Level.WARNING, "No coords found.");
        return null;
    }
    
    private boolean logsok(String pageContent) {
        for (String line : pageContent.split("\n")) {
            if (line.trim().startsWith("initalLogs =")) {
                List<String> result = getVal(pageContent, "LogType");
                int max = 2;
                for(int i=0 ; i<result.size()&&i<max ; i++) {
                    String val = result.get(i).toLowerCase().trim();
                    if(val.contains("found")||val.equalsIgnoreCase("Owner Maintenance")) {
                        return true;
                    }
                    else if(!val.contains("didn't")){
                        max++;
                    }
                }
                return false;
            }
        }
        return true; //Assume ok
    }

    private String getCoordAsString(double lat, double lon) {
        DecimalFormat roundFormat = new DecimalFormat("#.###");
        int latDegrees = (int) lat;
        int lonDegrees = (int) lon;
        double latMinutes = (lat - (int) lat) * 60;
        double lonMinutes = (lon - (int) lon) * 60;
        String latPrefix = "N";
        String lonPrefix = "E";
        if (latDegrees < 0) {
            latPrefix = "S";
            latDegrees *= -1;
        }
        if (lonDegrees < 0) {
            lonPrefix = "W";
            lonDegrees *= -1;
        }
        return latPrefix + " " + latDegrees + "° " + roundFormat.format(latMinutes) + "' "
                + lonPrefix + " " + String.format("%03d", lonDegrees) + "° " + roundFormat.format(lonMinutes) + "'";
    }

    private List<String> getVal(String line, String key) {
        String escapeKey = "\"" + key + "\":";
        if (!line.contains(escapeKey)) {
            return new ArrayList<>();
        }
        String cutString = line.substring(line.indexOf(escapeKey) + escapeKey.length());
        int end = cutString.indexOf(",\"");
        int end2 = cutString.indexOf("}");
        if (end == -1 && end2 == -1) {
            return new ArrayList<>();
        }
        if (end == -1) {
            end = Integer.MAX_VALUE;
        }
        if (end2 == -1) {
            end2 = Integer.MAX_VALUE;
        }
        String finalString = cutString.substring(0, end < end2 ? end : end2);
        ArrayList<String> ret = new ArrayList<>();
        ret.add(removeMarks(finalString, '"'));
        ret.addAll(getVal(cutString, key));
        return ret;
    }

    private String removeMarks(String orig, char c) {
        orig = orig.trim();
        if (orig.charAt(0) == c && orig.charAt(orig.length() - 1) == c) {
            return orig.substring(1, orig.length() - 1);
        }
        return orig;
    }

    @SuppressWarnings("unchecked")
    private CacheType getType(List<TagNode> detailsNodes) {
        final String cacheTypePreamble = "/images/WptTypes/";
        if (detailsNodes.isEmpty()) {
            LOGGER.log(Level.WARNING, "No details nodes");
            return null;
        } else {
            List<TagNode> nodes = (List<TagNode>) detailsNodes.get(0).getElementListByName("img", true);
            for (TagNode node : nodes) {
                String srcAttrib = node.getAttributeByName("src");
                if (srcAttrib != null && srcAttrib.trim().startsWith(cacheTypePreamble)) {
                    srcAttrib = srcAttrib.substring(cacheTypePreamble.length());
                    switch (srcAttrib) {
                        case "2.gif":
                            return CacheType.TRADITIONAL;
                        case "3.gif":
                            return CacheType.MULTI;
                        case "4.gif":
                            return CacheType.VIRTUAL;
                        case "5.gif":
                            return CacheType.HYBRID;
                        case "6.gif":
                            return CacheType.EVENT;
                        case "7.gif":
                            return CacheType.APE;
                        case "8.gif":
                            return CacheType.MYSTERY;
                        case "9.gif":
                            return CacheType.APE;
                        case "11.gif":
                            return CacheType.WEBCAM;
                        case "12.gif":
                            return CacheType.REVERSE;
                        case "13.gif":
                            return CacheType.CITO;
                        case "137.gif":
                            return CacheType.EARTH;
                        case "453.gif":
                            return CacheType.MEGAEVENT;
                        case "1858.gif":
                            return CacheType.WHERIGO;
                        case "3773.gif":
                            return CacheType.HQ;
                        default:
                            LOGGER.log(Level.WARNING, "Unimplemented cache type");
                            return null;
                    }
                }
            }
            LOGGER.log(Level.WARNING, "No images in detail node");
            return null;
        }
    }

    private String getName(List<TagNode> nameNodes) {
        return getContentOfFirstElement(nameNodes);
    }

    private String getDescription(List<TagNode> descriptionNodes) {
        return getContentOfFirstElement(descriptionNodes);
    }

    private String getGC(List<TagNode> gcNodes) {
        return getContentOfFirstElement(gcNodes);
    }

    private String getHint(List<TagNode> hintNodes) {
        String rawHint = getContentOfFirstElement(hintNodes);
        return HintScrambler.toggleScramble(rawHint);
    }

    private String getContentOfFirstElement(List<TagNode> nodes) {
        if (nodes.isEmpty()) {
            LOGGER.log(Level.WARNING, "No nodes in list");
            return null;
        } else {
            return nodes.get(0).getText().toString().trim();
        }
    }
}
