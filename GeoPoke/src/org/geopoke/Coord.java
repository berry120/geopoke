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

import java.text.DecimalFormat;

/**
 *
 * @author Michael
 */
public class Coord {

    private double lat;
    private double lon;

    public Coord(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public static Coord newFromDegreesMinutes(String coord) {
        if(coord == null) {
            return null;
        }
        String[] parts = coord.split(" ");
        boolean isNorth = parts[0].equalsIgnoreCase("N");
        String northDegreesStr = removeLastCharsIfNotDigit(parts[1]);
        String northMinutesStr = removeLastCharsIfNotDigit(parts[2]);

        boolean isEast = parts[3].equalsIgnoreCase("E");
        String eastDegreesStr = removeLastCharsIfNotDigit(parts[4]);
        String eastMinutesStr = removeLastCharsIfNotDigit(parts[5]);


        try {
            int northDegrees = Integer.parseInt(northDegreesStr);
            double northMinutes = Double.parseDouble(northMinutesStr);
            if(!isNorth) {
                northDegrees *= -1;
                northMinutes *= -1;
            }

            int eastDegrees = Integer.parseInt(eastDegreesStr);
            double eastMinutes = Double.parseDouble(eastMinutesStr);
            if(!isEast) {
                eastDegrees *= -1;
                eastMinutes *= -1;
            }
            
            return new Coord(toDegrees(northDegrees, northMinutes), toDegrees(eastDegrees, eastMinutes));
        }
        catch(NumberFormatException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String removeLastCharsIfNotDigit(String input) {
        while(!Character.isDigit(input.charAt(input.length() - 1))) {
            input = input.substring(0, input.length() - 1);
        }
        return input;
    }

    private static double toDegrees(int degrees, double minutes) {
        return degrees + (minutes / 60);
    }

    public String toDegreesMinutes() {
        StringBuilder result = new StringBuilder();
        if(lat >= 0) {
            result.append("N");
        }
        else {
            result.append("S");
        }
        result.append(" ");
        result.append(Math.abs((int) lat));
        result.append("° ");
        result.append(getMinutes(lat));
        result.append("' ");

        if(lon >= 0) {
            result.append("E");
        }
        else {
            result.append("W");
        }
        result.append(" ");
        result.append(Math.abs((int) lon));
        result.append("° ");
        result.append(getMinutes(lon));
        result.append("'");

        return result.toString();
    }
    
    private String getMinutes(double degrees) {
        DecimalFormat roundFormat = new DecimalFormat("#.###");
        return roundFormat.format(Math.abs((degrees - ((int) degrees))*60));
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}