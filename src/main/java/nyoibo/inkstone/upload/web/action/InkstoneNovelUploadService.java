package nyoibo.inkstone.upload.web.action;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.selenium.DriverBase;
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

public class InkstoneNovelUploadService {
	private final WebDriver driver;
	private final InkstoneHomePage inkstoneHomePage;
	private final InkstoneChapterPage inkstoneChapterPage;

	private WebDriverWait wait;
	
	public InkstoneNovelUploadService(boolean foreign) throws Exception {
		DriverBase.instantiateDriverObject();
		this.driver = DriverBase.getDriver();
		this.inkstoneHomePage = new InkstoneHomePage(foreign, driver);
		this.inkstoneChapterPage = new InkstoneChapterPage(driver);
	}

	private ExpectedCondition<Boolean> pageTitleStartsWith(final String header) {
		return driver -> driver.getTitle().toLowerCase().startsWith(header.toLowerCase());
	}
	
	public void start() throws Exception {
		inkstoneHomePage.login();
		Thread.sleep(2000);
		wait = new WebDriverWait(driver, 10, 100);
		wait.until(pageTitleStartsWith(SeleniumInkstone.INKSTONE_PRO_DASHBOARD));

		System.out.println("======go to azure");
		driver.get("https://inkstone.webnovel.com/book/detail/cbid/8628176105001205");

		inkstoneChapterPage.editLatestRaw();

	}
	
	public static void main(String[] args) throws Exception {
		InkstoneNovelUploadService test = new InkstoneNovelUploadService(false);
		test.start();
	}
}
