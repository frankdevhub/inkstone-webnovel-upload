package nyoibo.inkstone.upload.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class InkstoneRawHeaderUtils {

    private static final String numRegx = "\\d+(\\.\\d+){0,1}";
    private static final String chapCNRegx = "第([\\s\\S]*?)章";
    private static final String selectENRegx = "(?<=\\()[^\\)]+";

    private static String clearBrace(String source) {
        String header = source;
        if (header == null)
            return null;
        header = header.replaceAll("（", "(");
        header = header.replaceAll("）", ")");
        return header;
    }

    public static String convertRawCNHeader(String header) throws Exception {
        if (header == null)
            return null;

        String convert = clearBrace(header);

        Matcher matcher = Pattern.compile(chapCNRegx).matcher(convert);
        if (matcher.find()) {
            convert = matcher.group(1).trim();
            convert = new Integer(StringNumberUtils.numberCN2Arab(convert)).toString();

        } else {
            matcher = Pattern.compile(numRegx).matcher(convert);
            if (matcher.find()) {
                convert = matcher.group();
                return convert;
            } else {
                char[] chars = convert.toCharArray();
                StringBuilder builder = new StringBuilder();
                for (char c : chars) {
                    builder.append(StringNumberUtils.numberCN2Arab(new Character(c).toString()));
                }
                convert = builder.toString();
            }
        }

        if (StringUtils.isEmpty(convert)) {
            throw new Exception(String.format("Cannot recognize the raw header in format：[%s] if need help, "
                    + "please contact support for this bug.", header));
        }
        String tail = getInnerPart(header);
        if (tail == null) {
            return convert;
        } else {
            return convert + tail;
        }

    }

    public static String convertRawENeader(String header) throws Exception {
        if (header == null)
            return null;

        String convert = clearBrace(header);
        convert = header.toLowerCase();
        Matcher matcher = Pattern.compile(numRegx).matcher(convert);
        if (matcher.find()) {
            convert = matcher.group();
        }
        if (StringUtils.isEmpty(convert)) {
            throw new Exception(String.format("Cannot recognize the raw header in format：[%s] if need help, "
                    + "please contact support for this bug.", convert));
        }
        String tail = getInnerPart(convert);
        if (tail == null) {
            return convert;
        } else {
            return convert + tail;
        }
    }

    private static String getInnerPart(String header) {
        String convert = clearBrace(header);
        convert = convert.toLowerCase();

        Matcher matcher = Pattern.compile(selectENRegx).matcher(header);
        if (matcher.find()) {
            convert = matcher.group();
        } else {
            matcher = Pattern.compile(numRegx).matcher(header);
            if (matcher.find()) {
                convert = matcher.group();
            } else {
                return null;
            }
        }

        convert = convert.trim();
        convert = convert.replace(" ", "");
        convert = convert.replace("part", "");

        if (convert.contains("上")) {
            convert = "P1";
            return convert;
        } else if (convert.contains("下")) {
            convert = "P2";
            return convert;
        } else {
            try {
                Integer part = Integer.parseInt(convert);
                return "P" + part.toString();
            } catch (Exception e) {
                return "P" + StringNumberUtils.numberCN2Arab(convert);
            }
        }
    }

}
