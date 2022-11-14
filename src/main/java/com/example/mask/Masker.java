package com.example.mask;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Masker {

    private enum PatternType {
        PHONE_NUMBER,
        SSN,
        IP,
        EMAIL
    }
    private static final Pattern phoneNumberPattern = Pattern.compile("(\\d+)-?(\\d+)\\D*?(\\d*)");
    private static final Pattern ssnPattern = Pattern.compile("(?!666|000|9\\d{2})\\d{3}-(?!00)\\d{2}-(?!0{4})\\d{4}");
    private static final Pattern ipPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)");
    private static final Pattern emailPattern  = Pattern.compile("(\\w+@\\w+\\.\\w+)");

    public static void main(String[] arg) {
        String phoneNumberSource = "######### starting ######### \n1st: +1(123)-234-3456 \n2nd: 345 457 6789 \n3rd: (425)-345-3456 \n######### ending #########";
        String ssnSource = "######### starting ######### \n1st: 123-23-3456 \n2nd: 345-45-6789 \n3rd: 657-23-1234 \n######### ending #########";
        String ipSource = "######### starting ######### \n1st: 11.23.45.38 \n2nd: 102.234.32.11 \n3rd: 11.234.90.12 \n######### ending #########";
        String emailSource = "######### starting ######### \n1st: hello@gmail.com \n2nd: world@gmail.com \n3rd: wow@gmail.com \n######### ending #########";
        Map<PatternType, String> map = new HashMap<>(4);
        map.put(PatternType.PHONE_NUMBER, phoneNumberSource);
        map.put(PatternType.SSN, ssnSource);
        map.put(PatternType.IP, ipSource);
        map.put(PatternType.EMAIL, emailSource);

        for (PatternType patternType : map.keySet()) {
            System.out.println(mask(getPattern(patternType), map.get(patternType)));
        }
    }

    private static Pattern getPattern(PatternType patternType) {
        switch (patternType) {
            case PHONE_NUMBER:
                return phoneNumberPattern;
            case SSN:
                return ssnPattern;
            case IP:
                return ipPattern;
            case EMAIL:
                return emailPattern;
            default:
                return null;
        }
    }

    private static String mask(Pattern pattern, String source) {
        if (pattern == null || source == null) {
            return null;
        }
        Matcher m = pattern.matcher(source);
        StringBuilder sb = new StringBuilder(source);
        while(m.find()) {
            System.out.println("found group: " + m.group());
            for (int i = m.start(); i < m.end(); i++) {
                if (pattern == emailPattern) {
                    if (source.charAt(i) != '@') {
                        sb.setCharAt(i, '*');
                    }
                }
                if (Character.isDigit(source.charAt(i))) {
                    sb.setCharAt(i, '*');
                }
            }
        }
        return sb.toString();
    }
}
