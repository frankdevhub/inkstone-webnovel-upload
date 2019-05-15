package nyoibo.inkstone.upload.web.action;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.selenium.DriverBase;
import nyoibo.inkstone.upload.selenium.config.ChromeDataConfig;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;
import nyoibo.inkstone.upload.utils.WebDriverUtils;
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

	private final Map<String, String> bookCompareList;
	
	private String path;
	
	private ConcurrentHashMap<String, Integer> process;

	private String configChromeData() throws IOException {
		String root = ChromeDataConfig.getLocal();
		String dataName = ChromeDataConfig.createDataName(this.thread);
		return ChromeDataConfig.config(root, dataName);
	}

	public InkstoneRawNovelService(boolean foreign, String bookUrl, String bookName,
			ConcurrentHashMap<String, Integer> process, Map<String, String> bookCompareList) throws Exception {
		DriverBase.instantiateDriverObject();
		this.process = process;
		this.thread = SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW;
		String path = configChromeData();
		this.driver = DriverBase.getDriver(path);
		this.bookName = bookName;
		this.inkstoneHomePage = new InkstoneHomePage(foreign, driver, bookName, process);
		this.inkstoneChapterPage = new InkstoneChapterPage(driver, bookUrl, bookName, process, bookCompareList,
				foreign);
		this.bookCompareList = bookCompareList;
	}

	public void doNextChaps() throws Exception {
		driver.get(SeleniumInkstone.INKSTONE_PRO_DASHBOARD);
		WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_DASHBOARD, new WebDriverWait(driver, 10, 1000));
		inkstoneChapterPage.editLatestRaw();
		inkstoneChapterPage.doTranslate();
		inkstoneChapterPage.doEdit();
	}

	@Override
	public void run() {
		System.out.println(bookCompareList.size()+"==============================");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			for (int i = 0; i < bookCompareList.size(); i++) {
				if (i == 0) {
					inkstoneHomePage.login();
					Thread.sleep(3000);
					inkstoneChapterPage.editLatestRaw();
					Thread.sleep(700);
					inkstoneChapterPage.doTranslate();
					Thread.sleep(700);
					inkstoneChapterPage.doEdit();
				} else {
					System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa");
					doNextChaps();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
}
