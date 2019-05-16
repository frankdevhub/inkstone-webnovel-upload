package nyoibo.inkstone.upload.web.pages;

import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
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

	private ConcurrentHashMap<String, Integer> process;
	
	public InkstoneHomePage(boolean foreign, WebDriver driver, String bookName,
			ConcurrentHashMap<String, Integer> process) throws Exception {
		this.process = process;
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
	}

	private void login() throws Exception {
		LOGGER.begin().headerMethod(MessageMethod.EVENT).info("navigate to homepage");
		driver.get(SeleniumInkstone.INKSTONE);

		process.put("start to login", 1);
		try {
			WebDriverWait wait = new WebDriverWait(driver, 5, 100);
			WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_HOME_TITLE, wait);

			signIntoBtn.findWebElement().click();
			driver.switchTo().frame(SeleniumInkstone.INKSTONE_MAIL_LOGIN_FRAME_ID);
			selectEmailLoginBtn.findWebElement().click();

			LOGGER.begin().headerAction(MessageMethod.EVENT).info("switch to login iframe");
			WebDriverUtils.findWebElement(accountNameInput).clear();
			WebDriverUtils.findWebElement(accountNameInput).sendKeys(this.accountName);

			WebDriverUtils.findWebElement(accountPwdInput).clear();
			WebDriverUtils.findWebElement(accountPwdInput).sendKeys(this.accountPwd);

			WebDriverUtils.findWebElement(submitBtn).click();
			
		} catch (Exception e) {
			JavascriptExecutor jsExec = (JavascriptExecutor) driver;
			String function = "return document.readyState";
			String code = (String) jsExec.executeScript(function);

			if (!code.isEmpty() && code.equals("complete")) {
				String title = driver.getTitle();
				if (title.equals(SeleniumInkstone.INKSTONE_DASHBOARD)) {
					process.put("login", 1);
					return;
				}
			} else {
				process.put("login", -1);
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
