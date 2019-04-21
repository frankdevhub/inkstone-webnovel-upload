package nyoibo.inkstone.upload.web.action;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.selenium.DriverBase;
import nyoibo.inkstone.upload.web.pages.InkstoneHomePage;



/**
 * <p>Title:InkStoneWebPageAutomation.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-21 01:34
 */

public class InkStoneLoginExample {
	
	private ExpectedCondition<Boolean> pageTitleStartsWith(final String searchString) {
		return driver -> driver.getTitle().toLowerCase().startsWith(searchString.toLowerCase());
	}

	public void test() throws Exception{
	    DriverBase.instantiateDriverObject();
		WebDriver driver = DriverBase.getDriver();

        driver.get("https://github.com/login");
        InkstoneHomePage googleHomePage = new InkstoneHomePage();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());

        googleHomePage.enterCredential();

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        WebDriverWait wait = new WebDriverWait(driver, 10, 100);
        wait.until(pageTitleStartsWith("Cheese"));

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
	}

}
