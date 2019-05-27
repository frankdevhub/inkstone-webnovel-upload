package nyoibo.inkstone.upload.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.gui.ConsoleUtils;
import nyoibo.inkstone.upload.gui.SWTResourceManager;
import nyoibo.inkstone.upload.gui.WebLinkUtils;
import nyoibo.inkstone.upload.message.MessageMethod;
import nyoibo.inkstone.upload.selenium.AssignDriver;
import nyoibo.inkstone.upload.selenium.DriverBase;
import nyoibo.inkstone.upload.selenium.Query;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;
import nyoibo.inkstone.upload.utils.WebDriverUtils;

/**
 * <p>Title:InkstoneHomePage.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-08 11:00
 */

public class InkstoneHomePage implements Runnable{
	private String accountName;
	private String accountPwd;
  
	private Query signIntoBtn;
	private Query selectEmailLoginBtn;
	private Query accountNameInput;
	private Query accountPwdInput;
	private Query submitBtn;

	private WebDriver driver;
	private final Logger LOGGER = LoggerFactory.getLogger(InkstoneHomePage.class);
	
	public InkstoneHomePage(boolean foreign, WebDriver driver, String bookName) throws Exception {
		this.driver = driver;
		if (foreign) {
			this.accountName = SeleniumInkstone.INKSTONE_ACCOUNT_NAME_EN;
			this.accountPwd = SeleniumInkstone.INKSTONE_ACCOUNT_PWD_CN;
		} else {
			this.accountName = SeleniumInkstone.INKSTONE_ACCOUNT_NAME_CN;
			this.accountPwd = SeleniumInkstone.INKSTONE_ACCOUNT_PWD_CN;
		}

		signIntoBtn = new Query()
				.defaultLocator(By.cssSelector("[class='" + SeleniumInkstone.INKSTONE_SIGN_BTN_CLASS + "']"));
		accountNameInput = new Query().defaultLocator(By.name(SeleniumInkstone.INKSTONE_LOGIN_INPUT_EMAIL_NAME));
		accountPwdInput = new Query().defaultLocator(By.name(SeleniumInkstone.INKSTONE_LOGIN_INPUT_PWD_NAME));
		selectEmailLoginBtn = new Query()
				.defaultLocator(By.cssSelector("[class='" + SeleniumInkstone.INKSTONE_LOGIN_PANEL_EMAIL_CLASS + "']"));
		submitBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_LOGIN_SUBMIT_ID));

		AssignDriver.initQueryObjects(this, DriverBase.getDriver(bookName));
		ConsoleUtils
				.pushToConsole(LOGGER.begin().headerAction(MessageMethod.EVENT).info("Init InkstoneHomePage Thread"));
	}

	private void login() throws Exception {
		
		ConsoleUtils.pushToConsole(LOGGER.begin().headerMethod(MessageMethod.EVENT).info("navigate to homepage"));

		driver.get(SeleniumInkstone.INKSTONE);

		WebLinkUtils.pushToWebLink(SeleniumInkstone.INKSTONE);
		
		try {
			WebDriverWait wait = new WebDriverWait(driver, 5, 100);
			WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_HOME_TITLE, wait);

			signIntoBtn.findWebElement().click();
			driver.switchTo().frame(SeleniumInkstone.INKSTONE_MAIL_LOGIN_FRAME_ID);
			selectEmailLoginBtn.findWebElement().click();

			SWTResourceManager.condition.wait();
			ConsoleUtils.pushToConsole(LOGGER.begin().headerAction(MessageMethod.EVENT).info("switch to login iframe"));
			WebDriverUtils.findWebElement(accountNameInput).clear();
			WebDriverUtils.findWebElement(accountNameInput).sendKeys(this.accountName);

			WebDriverUtils.findWebElement(accountPwdInput).clear();
			WebDriverUtils.findWebElement(accountPwdInput).sendKeys(this.accountPwd);

			WebDriverUtils.findWebElement(submitBtn).click();

		} catch (Exception e) {
			// another login page

			JavascriptExecutor jsExec = (JavascriptExecutor) driver;
			String function = "return document.readyState";
			String code = (String) jsExec.executeScript(function);

			if (!code.isEmpty() && code.equals("complete")) {
				String title = driver.getTitle();
				if (title.equals(SeleniumInkstone.INKSTONE_DASHBOARD)) {
					return;
				}
			} else {
				throw e;
			}

		}
		return;
	}

	@Override
	public void run() {
		try {
			login();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
