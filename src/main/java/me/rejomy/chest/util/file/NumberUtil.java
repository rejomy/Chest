package me.rejomy.chest.util.file;

public class NumberUtil {
    public static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return -999;
        }
    }
}
