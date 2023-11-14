/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mipropiomysql;
import java.util.Comparator;

public class AlphanumericComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
        return compareAlphanumeric(s1, s2);
    }

    private int compareAlphanumeric(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int i = 0;
        int j = 0;

        while (i < len1 && j < len2) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(j);

            if (Character.isDigit(c1) && Character.isDigit(c2)) {
                // Si ambos caracteres son dígitos, compara como números
                int num1 = 0;
                int num2 = 0;

                while (i < len1 && Character.isDigit(s1.charAt(i))) {
                    num1 = num1 * 10 + (s1.charAt(i) - '0');
                    i++;
                }

                while (j < len2 && Character.isDigit(s2.charAt(j))) {
                    num2 = num2 * 10 + (s2.charAt(j) - '0');
                    j++;
                }

                int numComparison = Integer.compare(num1, num2);
                if (numComparison != 0) {
                    return numComparison;
                }
            } else {
                // Si al menos un carácter no es un dígito, compara como texto
                int textComparison = Character.compare(c1, c2);
                if (textComparison != 0) {
                    return textComparison;
                }

                i++;
                j++;
            }
        }

        return len1 - len2;
    }
}
