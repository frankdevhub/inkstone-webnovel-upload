package nyoibo.inkstone.upload.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InkstoneRawHeaderUtils {

    private static final String numRegx = "\\d+(\\.\\d+){0,1}";
    private static final String chapCNRegx = "第([\\s\\S]*?)章";
    private static final String braceRegx = "(?<=\\()[^\\)]+";

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

        int chapIndex;
        int partIndex = -1;
        String tail = null;
        Object res[] = getInnerPart(header);
        if (null != res) {
            tail = (String) res[1];
            partIndex = (int) res[0];
        }

        String convert = clearBrace(header);
        Matcher matcher = Pattern.compile(chapCNRegx).matcher(convert);
        if (matcher.find()) {
            chapIndex = matcher.start();
            convert = matcher.group(1).trim();
            convert = new Integer(StringNumberUtils.numberCN2Arab(convert)).toString();
        } else {
            matcher = Pattern.compile(numRegx).matcher(convert);
            if (matcher.find()) {
                chapIndex = matcher.start();
                if (chapIndex == partIndex && partIndex >= 0) {
                    convert = matcher.group(1);
                    chapIndex = matcher.start();
                } else {
                    convert = matcher.group();
                }
            } else {
                chapIndex = 1;
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

        if (tail == null) {
            return convert;
        } else {
            if (chapIndex < partIndex)
                return convert + tail;
            else
                return convert;
        }

    }

    public static String convertRawENeader(String header) throws Exception {
        if (header == null)
            return null;

        int chapIndex = -1;
        int partIndex = -1;
        String tail = null;
        Object res[] = getInnerPart(header);
        if (null != res) {
            tail = (String) res[1];
            partIndex = (int) res[0];
        }

        String convert = clearBrace(header);
        Matcher matcher = Pattern.compile(numRegx).matcher(convert);
        if (matcher.find()) {
            chapIndex = matcher.start();
            if (chapIndex == partIndex && partIndex >= 0) {
                convert = matcher.group(1);
                chapIndex = matcher.start();
            } else {
                convert = matcher.group();
            }

        }
        if (StringUtils.isEmpty(convert)) {
            throw new Exception(String.format("cannot recognize the raw header in format：[%s] if need help, "
                    + "please contact support for this bug.", header));
        }

        if (tail == null) {
            return convert;
        } else {
            if (chapIndex < partIndex)
                return convert + tail;
            else
                return convert;
        }
    }

    private static Object[] getInnerPart(String header) {
        //0:index 1:string
        Object[] res = new Object[2];
        String convert = clearBrace(header);
        convert = convert.toLowerCase();

        Matcher matcher = Pattern.compile(braceRegx).matcher(convert);
        if (matcher.find()) {
            res[0] = matcher.start();
            convert = matcher.group();
            matcher = Pattern.compile(numRegx).matcher(convert);
            if (matcher.find()) {
                convert = matcher.group();
            }
        } else {
            return null;
        }

        convert = convert.trim();
        convert = convert.replace(" ", "");
        convert = convert.replace("part", "");

        if (convert.contains("上")) {
            convert = "P1";
            res[1] = convert;
            return res;
        } else if (convert.contains("下")) {
            convert = "P2";
            res[1] = convert;
            return res;
        } else {
            try {
                Integer part = Integer.parseInt(convert);
                res[1] = "P" + part.toString();
                return res;
            } catch (Exception e) {
                res[1] = "P" + StringNumberUtils.numberCN2Arab(convert);
                return res;
            }
        }
    }

}
