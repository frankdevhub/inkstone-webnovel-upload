package nyoibo.inkstone.upload.web.action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;
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
	private final boolean needLogin;

	private final Map<String, String> bookCompareList;

	private ConcurrentHashMap<String, Integer> process;

	private final Logger LOGGER = LoggerFactory.getLogger(InkstoneRawNovelService.class);

	public InkstoneRawNovelService(boolean foreign, String bookUrl, String bookName,
			ConcurrentHashMap<String, Integer> process, Map<String, String> bookCompareList, boolean needLogin,WebDriver driver)
			throws Exception {
		this.driver = driver;
		this.needLogin = needLogin;
		this.process = process;
		this.bookName = bookName;
		this.inkstoneHomePage = new InkstoneHomePage(foreign, driver, bookName, process);
		this.inkstoneChapterPage = new InkstoneChapterPage(driver, bookUrl, bookName, process, bookCompareList,
				foreign);
		this.bookCompareList = bookCompareList;
	}

	private void doNextChaps() throws Exception {
		driver.get(SeleniumInkstone.INKSTONE_PRO_DASHBOARD);
		Thread.sleep(2000);
		
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("start to do next chapter, loop to dashboard");
		
		WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_DASHBOARD, new WebDriverWait(driver, 10, 1000));
		inkstoneChapterPage.editLatestRaw();
		inkstoneChapterPage.doTranslate();
		inkstoneChapterPage.doEdit();
	}

	@Override
	public void run() {
		
		if (needLogin) {
			try {
				inkstoneHomePage.login();
				Thread.sleep(3000);
				inkstoneChapterPage.editLatestRaw();
				Thread.sleep(700);
				inkstoneChapterPage.doTranslate();
				Thread.sleep(700);
				inkstoneChapterPage.doEdit();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			try {
				doNextChaps();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
