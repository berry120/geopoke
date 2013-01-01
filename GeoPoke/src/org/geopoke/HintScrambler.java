package org.geopoke;

/**
 *
 * @author Michael
 */
public class HintScrambler {

    public static String toggleScramble(String hint) {
        if(hint==null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hint.length(); i++) {
            char c = hint.charAt(i);
            if (c >= 'a' && c <= 'm') {
                c += 13;
            } else if (c >= 'A' && c <= 'M') {
                c += 13;
            } else if (c >= 'n' && c <= 'z') {
                c -= 13;
            } else if (c >= 'N' && c <= 'Z') {
                c -= 13;
            }
            result.append(c);
        }
        return result.toString();
    }
}
