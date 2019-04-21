package nyoibo.inkstone.upload.web.action;

import java.util.HashSet;

import org.openqa.selenium.Cookie;
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

        
        System.out.println("Page title is: " + driver.getTitle());

        googleHomePage.enterCredential();

        WebDriverWait wait = new WebDriverWait(driver, 5, 100);
        wait.until(pageTitleStartsWith("Github"));

		HashSet<Cookie> cookies = (HashSet<Cookie>) driver.manage().getCookies();
		for (Cookie cookie : cookies) {
			System.out.println(cookie.getName());
			System.out.println(cookie.getValue());
		}  
        
        
        System.out.println("Page title is: " + driver.getTitle());
        
        driver.manage().deleteAllCookies();
        driver.quit();
	}

}
