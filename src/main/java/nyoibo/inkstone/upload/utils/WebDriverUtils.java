package nyoibo.inkstone.upload.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.gui.ConsoleUtils;
import nyoibo.inkstone.upload.gui.WebLinkUtils;
import nyoibo.inkstone.upload.selenium.Query;

/**
 * <p>Title:WebDriverUtils.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-13 17:16
 */

public class WebDriverUtils {

	private static ExpectedCondition<Boolean> pageTitleStartsWith(final String header) {
		return driver -> driver.getTitle().toLowerCase().startsWith(header.toLowerCase());
	}

	public static ExpectedCondition<Boolean> waitPageLoadComplete(WebDriverWait webDriverWait) {
		String function = "return document.readyState";
		return driver -> ((String) ((JavascriptExecutor) driver).executeScript(function)).equals("complete");
	}

	public static synchronized WebElement findWebElement(Query query) {
		WebElement element = query.findWebElement();
		ConsoleUtils.pushToConsole(String.format("Find Query:[%s]", query.toString()));
		return element;
	}

	public static synchronized void doWaitTitle(String header, WebDriverWait wait) {
		wait.until(pageTitleStartsWith(header));
	}

	public static synchronized void doWaitQuery(Query query, WebDriverWait wait) {
		wait.until(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				return query.findWebElement();
			}
		});
		ConsoleUtils.pushToConsole(String.format("Find Query:[%s]", query.toString()));
	}

	public static synchronized void doWaitCss(String css, WebDriverWait wait) {
		wait.until(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.cssSelector(css));
			}
		});
		ConsoleUtils.pushToConsole(String.format("Find element by xPath:[%s]", css));
	}

	public static synchronized void doWaitId(String id, WebDriverWait wait) {
		wait.until(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.id(id));
			}
		});

		ConsoleUtils.pushToConsole(String.format("Find Element with id:[%s]", id));
	}
}
