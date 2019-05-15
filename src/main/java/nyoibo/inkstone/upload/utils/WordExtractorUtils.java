package nyoibo.inkstone.upload.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Document;

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

	private String charLCN ="“";
	private String charRCN ="”";
	
	private final Logger LOGGER = LoggerFactory.getLogger(WordExtractorUtils.class);

	private String title = null;
	private StringBuilder context = new StringBuilder();
	private String content = null;
	private int titleHit = 0;
	private static String getSuffix(File file) {
		String fileName = file.getName();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		return suffix;
	}
	
	public static String Word2003ToHtml(File file) throws Exception {

		if (null == file) {
			return null;
		} else {
			String suffix = getSuffix(file);
			if (suffix.equals("doc") || suffix.equals("DOC")) {
				InputStream input = new FileInputStream(file);
				HWPFDocument wordDocument = new HWPFDocument(input);
				WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
						DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());

				wordToHtmlConverter.processDocument(wordDocument);
				Document htmlDocument = wordToHtmlConverter.getDocument();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DOMSource domSource = new DOMSource(htmlDocument);
				StreamResult streamResult = new StreamResult(baos);

				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer serializer = factory.newTransformer();
				serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
				serializer.setOutputProperty(OutputKeys.INDENT, "yes");
				serializer.setOutputProperty(OutputKeys.METHOD, "html");
				serializer.transform(domSource, streamResult);

                String content = new String(baos.toByteArray());
				baos.close();
				return content;
			} else {
				throw new Exception("Enter only MS Office 2003 files");
			}
		}

	}

	public static String Word2007ToHtml(File file) throws Exception {

		if (null == file) {
			return null;
		} else {
			String suffix = getSuffix(file);
			if (suffix.equals("docx") || suffix.equals("DOCX")) {
				InputStream in = new FileInputStream(file);
				XWPFDocument document = new XWPFDocument(in);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				XHTMLConverter.getInstance().convert(document, baos, null);
				String content = baos.toString();
				baos.close();
				return content;
			} else {
				throw new Exception("Enter only MS Office 2007+ files");
			}
		}
	}

	public void extractFile(File file) {
		InputStream is = null;
		XWPFDocument doc = null;
		try {
			is = new FileInputStream(file);
			doc = new XWPFDocument(is);

			List<XWPFParagraph> paras = doc.getParagraphs();

			int index = 0;
			for (XWPFParagraph para : paras) {
				String paraText = para.getText();
				if (!StringUtils.isEmpty(paraText) && !paraText.equals("\n")) {
					index++;
					if (index == 1) {
						title = paraText;
						continue;
					}
				}
				
				String titleLevel = getTitleLvl(doc, para);
				if ("a5".equals(titleLevel) || "HTML".equals(titleLevel) || "".equals(titleLevel)
						|| null == titleLevel) {
					titleLevel = "8";
					// System.out.println(titleLevel + "==" +
					// para.getParagraphText());
					 paraText = para.getParagraphText();
					if (paraText.isEmpty()) {
						//paraText = "<br/>";
					} else {
						paraText = "<p>" + paraText + "<p>";
					}
					paraText = paraText.replaceAll(charLCN, "&quot;");
					paraText = paraText.replaceAll(charRCN, "");
					paraText = paraText.replaceAll("\"", "&quot;");
					paraText = paraText.replaceAll("\n", "\\n");
					context.append(paraText);
				}
				if (!"8".equals(titleLevel)) {
					paraText = para.getParagraphText();
					paraText = paraText.replaceAll("”", "&quot;");
					paraText = paraText.replaceAll("”", "&quot;");
					paraText = paraText.replaceAll("\"", "&quot;");
					paraText = paraText.replaceAll("\n", "\\n");
					context.append("<p>" + paraText + "</p>");
					// System.out.println(titleLevel + "==" +
					// para.getParagraphText());
				/*	titleHit = titleHit + 1;
					if (titleHit > 1)
						break;*/
				}
			}

			if (null == title) {
				title = paras.get(0).getParagraphText();
			}else{
				content = context.toString();
			}
			content = context.toString().replaceFirst("<p>"+title+"<p>", "");
			/*if (titleHit > 1) {
				throw new Exception(SeleniumInkstone.INKSTONE_FILE_UPLOAD_MULTI_TITLE);
			}*/
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

	public String getContent() {
		return content;
	}

	/*public static void main(String[] args) {
		WordExtractorUtils utils = new WordExtractorUtils();
		utils.extractFile(new File("D:/a.docx"));
		// System.out.println(utils.getContent());
		System.out.println(utils.getTitle());
		System.out.println(utils.getTitle().split("—")[1]);
	}*/
}
