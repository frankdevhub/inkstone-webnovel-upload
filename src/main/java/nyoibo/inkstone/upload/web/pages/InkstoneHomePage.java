package nyoibo.inkstone.upload.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;
import nyoibo.inkstone.upload.selenium.AssignDriver;
import nyoibo.inkstone.upload.selenium.DriverBase;
import nyoibo.inkstone.upload.selenium.Query;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;
/**
 * <p>Title:InkstoneHomePage.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-08 11:00
 */

public class InkstoneHomePage {
	private String accountName;
	private String accountPwd;

	private Query accountIcon;
	private WebElement selectEmailLoginBtn;
	private Query accountNameInput;
	private Query accountPwdInput;
	private Query submitBtn;

	private WebDriver driver;

	private final Logger LOGGER = LoggerFactory.getLogger(InkstoneHomePage.class);

	private ExpectedCondition<Boolean> pageTitleStartsWith(final String header) {
		return driver -> driver.getTitle().toLowerCase().startsWith(header.toLowerCase());
	}

	public InkstoneHomePage(boolean foreign, WebDriver driver) throws Exception {
		this.driver = driver;
		if (foreign) {
			this.accountName = SeleniumInkstone.INKSTONE_ACCOUNT_NAME_EN;
			this.accountPwd = SeleniumInkstone.INKSTONE_ACCOUNT_PWD_CN;
		} else {
			this.accountName = SeleniumInkstone.INKSTONE_ACCOUNT_NAME_CN;
			this.accountPwd = SeleniumInkstone.INKSTONE_ACCOUNT_PWD_CN;
		}

		accountIcon = new Query().defaultLocator(By.className("g_user"));
		accountNameInput = new Query().defaultLocator(By.name(SeleniumInkstone.INKSTONE_LOGIN_INPUT_EMAIL_NAME));
		accountPwdInput = new Query().defaultLocator(By.name(SeleniumInkstone.INKSTONE_LOGIN_INPUT_PWD_NAME));
		submitBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_LOGIN_SUBMIT_ID));

		AssignDriver.initQueryObjects(this, DriverBase.getDriver());
	}

	public void login() {
		driver.get(SeleniumInkstone.INKSTONE_HOME_PAGE_URL);
		WebDriverWait wait = new WebDriverWait(driver, 10, 100);
		wait.until(pageTitleStartsWith(SeleniumInkstone.INKSTONE_HOME_HEADER));

		try {
			LOGGER.begin().headerAction(MessageMethod.EVENT).info("click account icon to login");
			accountIcon.findWebElement().click();

			wait.until(new ExpectedCondition<WebElement>() {

				@Override
				public WebElement apply(WebDriver arg0) {
					return driver.findElement(By.cssSelector(SeleniumInkstone.INKSTONE_LOGIN_PANEL_EMAIL_CLASS));
				}
			}).click();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			selectEmailLoginBtn = driver.findElement(By.cssSelector("a[accesskey=e]"));
			LOGGER.begin().headerAction(MessageMethod.EVENT).info("click login with email");
			selectEmailLoginBtn.click();
		} catch (Exception e) {
			e.printStackTrace();
		}

	/*	accountNameInput.findWebElement().clear();
		accountNameInput.findWebElement().sendKeys(this.accountName);
		accountPwdInput.findWebElement().sendKeys(this.accountPwd);*/

	}

}
