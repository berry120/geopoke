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

/**
 *
 * @author Michael
 */
public enum Difficulty {
    
    D1(1), D1_5(1.5), D2(2), D2_5(2.5), D3(3), D3_5(3.5), D4(4), D4_5(4.5), D5(5);
    
    private double num;
    
    private Difficulty(double num) {
        this.num = num;
    }
    
    public double toDouble() {
        return num;
    }
    
    public static Difficulty fromDouble(double num) {
        if(num==1) {
            return D1;
        }
        else if(num == 1.5) {
            return D1_5;
        }
        else if(num == 2) {
            return D2;
        }
        else if(num == 2.5) {
            return D2_5;
        }
        else if(num == 3) {
            return D3;
        }
        else if(num == 3.5) {
            return D3_5;
        }
        else if(num == 4) {
            return D4;
        }
        else if(num == 4.5) {
            return D4_5;
        }
        else if(num == 5) {
            return D5;
        }
        else {
            throw new IllegalArgumentException("Invalid difficulty: " + num);
        }
    }
    
    @Override
    public String toString() {
        return Double.toString(toDouble());
    }
    
}
