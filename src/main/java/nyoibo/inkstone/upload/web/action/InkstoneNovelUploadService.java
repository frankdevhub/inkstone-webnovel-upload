package nyoibo.inkstone.upload.web.action;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.selenium.DriverBase;
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

	private final String bookName;
	
	private WebDriverWait wait;

	public InkstoneNovelUploadService(boolean foreign, String bookUrl, String bookName) throws Exception {
		DriverBase.instantiateDriverObject();
		this.driver = DriverBase.getDriver(bookName);
		this.bookName = bookName;
		this.inkstoneHomePage = new InkstoneHomePage(foreign, driver, bookName);
		this.inkstoneChapterPage = new InkstoneChapterPage(driver, bookUrl, bookName);
	}

	public void start() throws Exception {
		inkstoneHomePage.login();
		inkstoneChapterPage.editLatestRaw();
		inkstoneChapterPage.doTranslate();
	}

	public static void main(String[] args) {
		InkstoneNovelUploadService test = null;
		InkstoneNovelUploadService test2 = null;
		try {
			String bookUrl = "https://inkstone.webnovel.com/book/detail/cbid/8628176105001205";
			String bookName = "Azure";
			test = new InkstoneNovelUploadService(false, bookUrl,bookName);
			test2 = new InkstoneNovelUploadService(false, bookUrl,bookName);
			System.out.println("=========");
			System.out.println(test.driver==test2.driver);
			
			test.start();
			test2.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			test.driver.quit();
		}
	}
}
