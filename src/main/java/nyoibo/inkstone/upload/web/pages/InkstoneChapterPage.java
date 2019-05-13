package nyoibo.inkstone.upload.web.pages;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
    private final String bookName;
    
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
		this.bookName = bookName;
		wait = new WebDriverWait(driver, 10, 1000);

		AssignDriver.initQueryObjects(this, DriverBase.getDriver(bookName));
	}


	private void switchTransDialog() {
		WebDriverUtils.doWaitCss("[class='" + SeleniumInkstone.INKSTONE_TRANSLATE_ALERT_CLASS + "']", wait);
	}

	private void waitForSaveBtn() {
		WebDriverUtils.doWaitId(SeleniumInkstone.INKSTONE_TRANSLATE_SAVE_ID, wait);
	}

	public void editLatestRaw() {
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("navigate to inkstone dashboard");
		WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_DASHBOARD, wait);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("get to book chapters view");
		driver.get(bookUrl);
		System.out.println("=======" + this.bookName);
		WebDriverUtils.doWaitTitle(this.bookName, wait);

		WebDriverUtils.findWebElement(firstRawChapter).click();
		WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_TRANSLATION, wait);
		selectTranslate();
	}

	private void selectTranslate() {
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click translate button");
		WebDriverUtils.findWebElement(transBtn).click();
		
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("switch translate dialog");
		switchTransDialog();
		
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click yes to take this chapter");
		conFirmTransBtn.findWebElement().click();
		waitForSaveBtn();
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("proceed to translate status");
	}
	
	public void doTranslate() throws Exception {
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("doing translate");
		WebElement titleElement = WebDriverUtils.findWebElement(editTitle);
		WebElement contextElement = WebDriverUtils.findWebElement(editContext);
		
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
