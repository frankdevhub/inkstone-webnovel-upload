package nyoibo.inkstone.upload.web.action;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.WebDriver;

import nyoibo.inkstone.upload.selenium.DriverBase;
import nyoibo.inkstone.upload.selenium.config.ChromeDataConfig;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;
import nyoibo.inkstone.upload.web.pages.InkstoneChapterPage;
import nyoibo.inkstone.upload.web.pages.InkstoneHomePage;

/**
 * <p>Title:InkstoneNovelUploadService.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-08 11:00
 */

public class InkstoneRawNovelService implements Runnable{
	private final WebDriver driver;
	private final InkstoneHomePage inkstoneHomePage;
	private final InkstoneChapterPage inkstoneChapterPage;

	private final String bookName;
	private final String thread;

	private String path;
	
	private final Map<String,File> chapters;
	private ConcurrentHashMap<String, Integer> process;

	private String configChromeData() throws IOException {
		String root = ChromeDataConfig.getLocal();
		String dataName = ChromeDataConfig.createDataName(this.thread);
		return ChromeDataConfig.config(root, dataName);
	}

	public InkstoneRawNovelService(boolean foreign, String bookUrl, String bookName,
			ConcurrentHashMap<String, Integer> process, Map<String, File> chapters) throws Exception {
		DriverBase.instantiateDriverObject();
		this.chapters = chapters;
		this.process = process;
		this.thread = SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW;
		String path = configChromeData();
		this.driver = DriverBase.getDriver(path);
		this.bookName = bookName;
		this.inkstoneHomePage = new InkstoneHomePage(foreign, driver, bookName, process);
		this.inkstoneChapterPage = new InkstoneChapterPage(driver, bookUrl, bookName, process, chapters, this);
	}

	public void doNextChaps() throws Exception {
		inkstoneChapterPage.editLatestRaw();
		inkstoneChapterPage.doTranslate();
		inkstoneChapterPage.doEdit();
	}

	@Override
	public void run() {
		try {
			inkstoneHomePage.login();
			inkstoneChapterPage.editLatestRaw();
			inkstoneChapterPage.doTranslate();
			inkstoneChapterPage.doEdit();
		} catch (Exception e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
}
