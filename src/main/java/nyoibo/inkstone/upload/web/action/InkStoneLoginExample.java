package nyoibo.inkstone.upload.web.action;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

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

@Component
public class InkStoneLoginExample {
	private ExpectedCondition<Boolean> pageTitleStartsWith(final String searchString) {
		return driver -> driver.getTitle().toLowerCase().startsWith(searchString.toLowerCase());
	}

	public void test() throws Exception{
	    DriverBase.instantiateDriverObject();
		WebDriver driver = DriverBase.getDriver();

        // And now use this to visit Google
        driver.get("http://www.baidu.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        InkstoneHomePage googleHomePage = new InkstoneHomePage();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());

        googleHomePage.enterSearchTerm("Cheese")
                .submitSearch();

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        WebDriverWait wait = new WebDriverWait(driver, 10, 100);
        wait.until(pageTitleStartsWith("Cheese"));

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
	}

}
