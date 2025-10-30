package backend_for_react.backend_for_react.common.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class TextUtils {
    public static String removeVietnameseAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("")
                .replace("đ", "d")
                .replace("Đ", "D");
    }
}
