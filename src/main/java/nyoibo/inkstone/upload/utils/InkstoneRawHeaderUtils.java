package nyoibo.inkstone.upload.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.util.StringUtils;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;

/**
 * <p>Title:InkstoneRawHeaderUtils.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-19 17:35
 */

public class InkstoneRawHeaderUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(InkstoneRawHeaderUtils.class);
	private static final String numRegx = "\\d+(\\.\\d+){0,1}";
	private static final String chapCNRegx = "第([\\s\\S]*?)章";
	private static final String selectENRegx = "(?<=\\()[^\\)]+";
	
	
	public static String convertRawCNHeader(String header) throws Exception {
		header = header.replaceAll("（", "(");
		header = header.replaceAll("）", ")");
		
		String convert = null;
		Matcher matcher = Pattern.compile(chapCNRegx).matcher(header);
		if (matcher.find()) {
			convert = matcher.group(1).trim();
			LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("Catch raw header key:[%s]", convert));

			int number = StringNumberUtils.numberCN2Arab(convert);
			convert = Integer.toString(number);

		} else {
			matcher = Pattern.compile(numRegx).matcher(header);
			if (matcher.find()) {
				convert = matcher.group();
				LOGGER.begin().headerAction(MessageMethod.EVENT)
						.info(String.format("Catch raw header key:[%s]", convert));
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

	public static String getRawExelChap(String header) throws Exception {
		header = header.replaceAll("（", "(");
		header = header.replaceAll("）", ")");
		
		String convert = null;
		convert = header.toLowerCase();
		Matcher matcher = Pattern.compile(numRegx).matcher(header);
		if (matcher.find()) {
			convert = matcher.group();
			LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("Catch raw header key:[%s]", convert));
		}

		if (StringUtils.isEmpty(convert)) {
			throw new Exception(String.format("Cannot recognize the raw header in format：[%s] if need help, "
					+ "please contact support for this bug.", header));
		}
		
		System.out.println(header);
		String tail = getInnerPart(header);
		if (tail == null) {
			return convert;
		} else {
			return convert + tail;
		}

	}

	public static String getInnerPart(String header) {
		String convert = null;
		header = header.toLowerCase();
		
		Matcher matcher = Pattern.compile(selectENRegx).matcher(header);
		if (matcher.find()) {
			convert = matcher.group();
			LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("Catch raw header key:[%s]", convert));
		} else {
			matcher = Pattern.compile(selectENRegx).matcher(header);
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
/*
         public static void main(String[] args) throws Exception {
             String test = "Chapter2212 (上)";
             System.out.println(getRawExelChap(test));
		}*/
}
