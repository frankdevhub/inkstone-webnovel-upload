package nyoibo.inkstone.upload.web.action;

import org.openqa.selenium.WebDriver;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.selenium.DriverBase;
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

	private final Logger LOGGER = LoggerFactory.getLogger(InkstoneNovelUploadService.class);

	public InkstoneNovelUploadService(boolean foreign) throws Exception {
		DriverBase.instantiateDriverObject();
		this.driver = DriverBase.getDriver();
		this.inkstoneHomePage = new InkstoneHomePage(foreign, driver);
	}

	public void start() throws Exception {
		inkstoneHomePage.login().toDashBoard();
	}

	public static void main(String[] args) throws Exception {
		InkstoneNovelUploadService service = new InkstoneNovelUploadService(false);
		service.start();
	}
}
