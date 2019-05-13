package nyoibo.inkstone.upload.web.pages;

import java.io.File;
import java.util.Map;
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
import nyoibo.inkstone.upload.utils.WordExtractorUtils;
import nyoibo.inkstone.upload.web.action.InkstoneRawNovelService;

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
	private final Query editBtn;
	private final Query conFirmTransBtn;
    private final Query firstRawChapter;
	
	private WebDriver driver;
	private final Logger LOGGER = LoggerFactory.getLogger(InkstoneChapterPage.class);
	private final String bookUrl;
    private final String bookName;
    
    private final Query editTitle;
    private final Query editContext;
    
    private final Query nextBtn;
    private final Query doneBtn;
    
    private final Map<String,File> chapters;
    
	private WebDriverWait wait;

	private ConcurrentHashMap<String, Integer> process;

	private final InkstoneRawNovelService parent;
	
	public InkstoneChapterPage(WebDriver driver, String bookUrl, String bookName,
			ConcurrentHashMap<String, Integer> process, Map<String, File> chapters, InkstoneRawNovelService parent)
			throws Exception {
		this.driver = driver;
		this.chapters = chapters;
		this.firstRawChapter = new Query().defaultLocator(By.xpath("//div[@class='"
				+ SeleniumInkstone.INKSTONE_PROJECT_RAW_DIV_CLASS + "']/child::node()[1]/child::node()[1]"));
		this.transBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_ID));
		this.conFirmTransBtn = new Query()
				.defaultLocator(By.cssSelector("[class='" + SeleniumInkstone.INKSTONE_TRANSLATE_TAKE_CLASS + "']"));
		this.bookUrl = bookUrl;
		this.editBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_EDIT_BTN_ID));

		this.editTitle = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_EDIT_TITLE_ID));
		this.editContext = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_EDIT_CONTENT_ID));
		this.nextBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_NEXT_BTN_ID));
		this.doneBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_DONE_BTN_ID));

		this.parent = parent;
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

	public void editLatestRaw() throws Exception {
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("navigate to inkstone dashboard");
		WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_DASHBOARD, wait);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("get to book chapters view");
		driver.get(bookUrl);
		WebDriverUtils.doWaitTitle(this.bookName, wait);

		try {
			WebDriverUtils.findWebElement(firstRawChapter).click();
		} catch (Exception e) {
			throw new Exception("time out or all chapters in raw have been uploaded");
		}
		WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_TRANSLATION, wait);

		Integer raw = process.get(SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW, raw + 1);

		selectTranslate();
		doTranslate();
	}

	private void selectTranslate() {
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click translate button");
		WebDriverUtils.findWebElement(transBtn).click();

		Integer inprogress = process.get(SeleniumInkstone.INKSTONE_TRANS_STATUS_TRANSLATEING);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_TRANSLATEING, inprogress + 1);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("switch translate dialog");
		switchTransDialog();

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click yes to take this chapter");
		WebDriverUtils.findWebElement(conFirmTransBtn).click();
		waitForSaveBtn();

		Integer translate = process.get(SeleniumInkstone.INKSTONE_TRANS_STATUS_TRANSLATEING);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_TRANSLATEING, translate + 1);

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

		File transFile = chapters.get(bookName);
		if (null == transFile)
			throw new Exception("cannot find chapter file please check title and rollback in inprogress item");
		WordExtractorUtils wordUtils = new WordExtractorUtils();
		wordUtils.extractFile(transFile);

		titleElement.sendKeys(wordUtils.getTitle());
		contextElement.sendKeys(wordUtils.getContext());

		WebDriverUtils.findWebElement(nextBtn).click();
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("switch translate dialog");
		switchTransDialog();

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click yes to submit work");
		WebDriverUtils.findWebElement(conFirmTransBtn).click();
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("sumbit translate");

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("proceed to edit status");

		Integer edit = process.get(SeleniumInkstone.INKSTONE_TRANS_STATUS_EDITING);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_EDITING, edit + 1);
	}

	private void doEdit() throws Exception {
		WebDriverUtils.doWaitQuery(editBtn, wait);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("doing edit");
		WebDriverUtils.findWebElement(editBtn).click();

		switchTransDialog();

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click yes to start edit");
		WebDriverUtils.findWebElement(conFirmTransBtn).click();
		WebDriverUtils.doWaitQuery(doneBtn, wait);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("sumbit edit");
		WebDriverUtils.findWebElement(doneBtn).click();

		switchTransDialog();
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click to submit edit");
		WebDriverUtils.findWebElement(conFirmTransBtn).click();

		try {
			WebDriverUtils.doWaitQuery(doneBtn, wait);
			throw new Exception("current chapter cannot be published, please check in in progress item");
		} catch (Exception e) {
			LOGGER.begin().headerAction(MessageMethod.EVENT).info("proceed to ready to publish");
			Integer ready = process.get(SeleniumInkstone.INKSTONE_TRANS_STATUS_READY_PUBLISH);
			process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_READY_PUBLISH, ready + 1);
			
			parent.doNextChaps();
		}
	}
}
