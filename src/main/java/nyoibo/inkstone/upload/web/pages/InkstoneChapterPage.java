package nyoibo.inkstone.upload.web.pages;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
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
 * <p>Title:InkstoneProjectPage.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-09 14:49
 */

public class InkstoneChapterPage {

	private final Query transBtn;
	private final Query saveBtn;
	private final Query conFirmTransBtn;
    private final Query firstRawChapter;
	
	private WebDriver driver;
	private final Logger LOGGER = LoggerFactory.getLogger(InkstoneChapterPage.class);
	private final String bookUrl;
    
    private final Query editTitle;
    private final Query editContext;
    
	private WebDriverWait wait;

	private ConcurrentHashMap<String,Integer> process;

	public InkstoneChapterPage(WebDriver driver, String bookUrl, String bookName,
			ConcurrentHashMap<String, Integer> process) throws Exception {
		this.driver = driver;
		this.firstRawChapter = new Query().defaultLocator(By.xpath("//div[@class='"
				+ SeleniumInkstone.INKSTONE_PROJECT_RAW_DIV_CLASS + "']/child::node()[1]/child::node()[1]"));
		this.transBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_ID));
		this.conFirmTransBtn = new Query()
				.defaultLocator(By.cssSelector("[class='" + SeleniumInkstone.INKSTONE_TRANSLATE_TAKE_CLASS + "']"));
		this.bookUrl = bookUrl;
		this.saveBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_SAVE_ID));

		this.editTitle = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_EDIT_TITLE_ID));
		this.editContext = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_EDIT_CONTENT_ID));

		this.process = process;
		wait = new WebDriverWait(driver, 10, 1000);

		AssignDriver.initQueryObjects(this, DriverBase.getDriver(bookName));
	}

	private ExpectedCondition<Boolean> pageTitleStartsWith(final String header) {
		return driver -> driver.getTitle().toLowerCase().startsWith(header.toLowerCase());
	}

	private void switchTransDialog() {
		wait.until(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				return driver.findElement(
						By.cssSelector("[class='" + SeleniumInkstone.INKSTONE_TRANSLATE_ALERT_CLASS + "']"));
			}
		});
	}

	private void waitForSaveBtn() {
		wait.until(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_SAVE_ID));
			}
		});
	}

	public void editLatestRaw() {
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("navigate to inkstone dashboard");
		wait.until(pageTitleStartsWith(SeleniumInkstone.INKSTONE_DASHBOARD));

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("get to book chapters view");
		driver.get(bookUrl);
		wait.until(pageTitleStartsWith(SeleniumInkstone.INKSTONE_CHAPTERS));

		firstRawChapter.findWebElement().click();
		wait.until(pageTitleStartsWith(SeleniumInkstone.INKSTONE_TRANSLATION));
		selectTranslate();
	}

	private void selectTranslate() {
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click translate button");
		transBtn.findWebElement().click();
		
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("switch translate dialog");
		switchTransDialog();
		
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click yes to take this chapter");
		conFirmTransBtn.findWebElement().click();
		waitForSaveBtn();
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("proceed to translate status");
	}
	
	public void doTranslate() throws Exception {
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("doing translate");
		WebElement titleElement = editTitle.findWebElement();
		WebElement contextElement = editContext.findWebElement();
		
		String sourceChapName = titleElement.getAttribute("value");
		if (StringUtils.isEmpty(sourceChapName))
			throw new Exception("chapter name is empty");
		LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("translating:[%s]", sourceChapName));
		titleElement.clear();
		contextElement.clear();
		
		titleElement.sendKeys("test");
		contextElement.sendKeys("1111");
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("sumbit translate");

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("proceed to edit status");
	}

}
