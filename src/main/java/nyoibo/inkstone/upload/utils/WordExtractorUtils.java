package nyoibo.inkstone.upload.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;

/**
 * <p>Title:WordExtractorUtils.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-07 10:33
 */

public class WordExtractorUtils {

	private final Logger LOGGER = LoggerFactory.getLogger(WordExtractorUtils.class);

	private String title = null;
	private StringBuilder context = new StringBuilder();
	private int titleHit = 0;

	public void extractFile(String sourcePath) {
		InputStream is = null;
		XWPFDocument doc = null;
		try {
			is = new FileInputStream(sourcePath);
			doc = new XWPFDocument(is);

			List<XWPFParagraph> paras = doc.getParagraphs();

			for (XWPFParagraph para : paras) {
				String titleLevel = getTitleLvl(doc, para);
				if ("a5".equals(titleLevel) || "HTML".equals(titleLevel) || "".equals(titleLevel)
						|| null == titleLevel) {
					titleLevel = "8";
					context.append(para.getParagraphText()).append("\n");
				}
				if (!"8".equals(titleLevel)) {
					title = para.getParagraphText();
					titleHit = titleHit + 1;
					if (titleHit > 1)
						break;
				}
			}

			if (null == title)
				throw new Exception(SeleniumInkstone.INKSTONE_FILE_TITLE_NOT_FOUND);
			if (titleHit > 1) {
				throw new Exception(SeleniumInkstone.INKSTONE_FILE_UPLOAD_MULTI_TITLE);
			}
		} catch (Exception e) {
			LOGGER.begin().headerAction(MessageMethod.ERROR).error(e.getMessage());
		} finally {
			try {
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				LOGGER.begin().headerAction(MessageMethod.ERROR).error(e.getMessage());
			}
		}
	}

	private String getParagraphCTP(XWPFDocument doc, XWPFParagraph para, String titleLevel) {
		try {
			if (para.getCTP().getPPr().getOutlineLvl() != null) {
				return String.valueOf(para.getCTP().getPPr().getOutlineLvl().getVal());
			}
		} catch (Exception e) {

		}
		return titleLevel;
	}

	private String getParagraphStyles(XWPFDocument doc, XWPFParagraph para, String titleLevel) {
		try {
			if (doc.getStyles().getStyle(para.getStyle()).getCTStyle().getPPr().getOutlineLvl() != null) {

				return String.valueOf(
						doc.getStyles().getStyle(para.getStyle()).getCTStyle().getPPr().getOutlineLvl().getVal());
			}
		} catch (Exception e) {

		}
		return titleLevel;
	}

	private String getParagraphBasedOn(XWPFDocument doc, XWPFParagraph para, String titleLevel) {
		try {
			if (doc.getStyles().getStyle(para.getStyle()).getCTStyle().getPPr().getOutlineLvl() != null) {
				return String.valueOf(
						doc.getStyles().getStyle(para.getStyle()).getCTStyle().getPPr().getOutlineLvl().getVal());
			}
		} catch (Exception e) {

		}
		return titleLevel;
	}

	private String getTitleLvl(XWPFDocument doc, XWPFParagraph para) {
		String titleLevel = "";
		titleLevel = getParagraphCTP(doc, para, titleLevel);
		titleLevel = getParagraphStyles(doc, para, titleLevel);
		titleLevel = getParagraphBasedOn(doc, para, titleLevel);
		return titleLevel;
	}

	public String getTitle() {
		return title;
	}

	public StringBuilder getContext() {
		return context;
	}

}
