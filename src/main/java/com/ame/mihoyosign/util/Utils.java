package com.ame.mihoyosign.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class Utils {

    private static final Character[] charSource = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    private Utils() {
    }

    public static Map<String, String> getCookieByStr(String str) {
        String[] cookieStr = str.split(";");
        Map<String, String> cookie = new HashMap<>(cookieStr.length);
        for (String s : cookieStr) {
            String[] oneCookie = s.split("=");
            cookie.put(oneCookie[0], oneCookie[1]);
        }
        return cookie;
    }

    public static String getChinaTime(String format) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        java.util.Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * 随机数
     */
    public static String getRandomFromArray(Character[] array, int count) {
        if (array == null) {
            array = charSource;
        }
        StringBuilder re = new StringBuilder();
        Random random = new Random();
        List<Character> list = Arrays.asList(array);
        List<Character> arrList = new ArrayList<>(list);
        for (int i = 0; i < count; i++) {
            int t = random.nextInt(arrList.size());
            re.append(arrList.get(t));
            arrList.remove(t);
        }
        return re.toString();
    }
}
