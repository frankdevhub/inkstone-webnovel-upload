package nyoibo.inkstone.upload.web.pages;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.gui.InkstoneUploadMainWindow;
import nyoibo.inkstone.upload.message.MessageMethod;
import nyoibo.inkstone.upload.selenium.AssignDriver;
import nyoibo.inkstone.upload.selenium.Query;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;
import nyoibo.inkstone.upload.utils.InkstoneRawHeaderUtils;
import nyoibo.inkstone.upload.utils.WebDriverUtils;
import nyoibo.inkstone.upload.utils.WordExtractorUtils;
import nyoibo.inkstone.upload.web.action.InkstoneUploadMainService;

public class InkstoneChapterPage implements Runnable {

	private long start = 0L;
	private long end = 0L;

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
	private final Query publishBtn;
	private final Query reditBtn;

	private final boolean foreign;

	private final Map<String, String> bookCompareList;
	private final Map<String, String> chapterFileList;

	private String filePath;

	private WebDriverWait wait;

	private ExpectedCondition<Boolean> pageTitleStartsWith(final String searchString) {
		System.out.println("Driver:" + driver.getTitle());
		System.out.println("FolderName" + searchString);
		return driver -> driver.getTitle().toLowerCase().contains(searchString.toLowerCase().split(" ")[0]);
	}

	public InkstoneChapterPage(WebDriver driver, String bookUrl, String bookName, Map<String, String> bookCompareList,
			boolean foreign, Map<String, String> chapterFileList) throws Exception {
		this.driver = driver;
		this.foreign = foreign;
		this.bookCompareList = bookCompareList;
		this.firstRawChapter = new Query().defaultLocator(By.xpath("//div[@class='"
				+ SeleniumInkstone.INKSTONE_PROJECT_RAW_DIV_CLASS + "']/child::node()[1]/child::node()[1]"));
		this.transBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_ID));
		this.conFirmTransBtn = new Query()
				.defaultLocator(By.cssSelector("[class='" + SeleniumInkstone.INKSTONE_TRANSLATE_TAKE_CLASS + "']"));
		this.bookUrl = bookUrl;
		this.editBtn = new Query()
				.defaultLocator(By.cssSelector("[class='" + SeleniumInkstone.INKSTONE_EDIT_BTN_ID + "']"));
		this.editTitle = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_EDIT_TITLE_ID));
		this.editContext = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_TRANSLATE_EDIT_CONTENT_ID));
		this.nextBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_NEXT_BTN_ID));
		this.doneBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_DONE_BTN_ID));
		this.publishBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_PUBLISH_BTN_ID));
		this.reditBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_REDIT_BTN_ID));

		this.bookName = bookName;

		this.chapterFileList = chapterFileList;

		wait = new WebDriverWait(driver, 10, 1000);

		AssignDriver.initQueryObjects(this, (RemoteWebDriver) driver);
	}

	private void switchTransDialog() {
		WebDriverUtils.doWaitCss("[class='" + SeleniumInkstone.INKSTONE_TRANSLATE_ALERT_CLASS + "']", wait);
	}

	private void waitForSaveBtn() {
		WebDriverUtils.doWaitId(SeleniumInkstone.INKSTONE_TRANSLATE_SAVE_ID, wait);
	}

	public void editLatestRaw() throws Exception {
		start = System.currentTimeMillis();

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("navigate to inkstone dashboard");
		Thread.sleep(2000);

		WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_DASHBOARD, wait);

		Thread.sleep(3000);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("get to book chapters view");
		driver.get(bookUrl);

		Thread.sleep(2000);

		wait.until(pageTitleStartsWith(this.bookName));

		LOGGER.begin().headerAction(MessageMethod.EVENT)
				.info(String.format("Into Raw Page Under Book:[%s]", this.bookName));
		WebElement firstChapter = firstRawChapter.findWebElement();

		String currentChapterName = null;
		InkstoneUploadMainService.currentChapterName = currentChapterName = firstChapter.getText();

		System.out.println("currentChapterName:" + firstChapter.getText());

		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 5);

		System.out.println("CN_CHAP_NAME" + InkstoneRawHeaderUtils.convertRawCNHeader(currentChapterName));
		String enChapName = bookCompareList.get(InkstoneRawHeaderUtils.convertRawCNHeader(currentChapterName));
		System.out.println("EN_CHAP_NAME" + enChapName);

		if (enChapName == null) {
			while (StringUtils.isEmpty(filePath)) {
				Shell shell = new Shell(new Display());
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterPath(System.getProperty(InkstoneUploadMainWindow.CHAPTER_PATH));

				dialog.setText(String.format("Canot match [] current raw file, please select mannually.",
						firstChapter.getText()));
				dialog.setFilterExtensions(new String[] { "*.doc", "*.docx" });
				filePath = dialog.open();

				MessageBox confirm = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				confirm.setText("Confirm");
				confirm.setMessage(String.format("Are you sure using [%s] ?", new File(filePath).getName()));
				int choice = confirm.open();
				if (choice == SWT.NO)
					filePath = null;
			}

		} else {
			this.filePath = chapterFileList.get(enChapName);
		}

		if (filePath == null)
			throw new Exception(String.format(
					"Cannot find related translated file with raw:[%s] please check mannually", currentChapterName));

		LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("Using file path: %s", filePath));

		firstChapter.click();
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click first raw button");
		WebDriverUtils.doWaitTitle(SeleniumInkstone.INKSTONE_TRANSLATION, wait);

		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 13);

		selectTranslate();
		doTranslate();
		doEdit();

		return;
	}

	private void selectTranslate() throws InterruptedException {
		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 20);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click translate button");
		WebDriverUtils.findWebElement(transBtn).click();

		start = System.currentTimeMillis();
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("switch translate dialog");

		switchTransDialog();
		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 37);
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click yes to take this chapter");
		WebDriverUtils.findWebElement(conFirmTransBtn).click();
		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 42);

		Thread.sleep(3000);

		waitForSaveBtn();

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("proceed to translate status");
	}

	private void doTranslate() throws Exception {
		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 52);

		WebElement titleElement = WebDriverUtils.findWebElement(editTitle);
		WebElement contextElement = WebDriverUtils.findWebElement(editContext);

		String sourceChapName = titleElement.getAttribute("value");
		if (StringUtils.isEmpty(sourceChapName))
			throw new Exception("chapter name is empty");
		LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("translating:[%s]", sourceChapName));

		titleElement.clear();
		contextElement.clear();

		File transFile = new File(filePath);
		WordExtractorUtils wordUtils = new WordExtractorUtils();
		wordUtils.extractFile(transFile);

		JavascriptExecutor executor = (JavascriptExecutor) driver;
		String titleSource = wordUtils.getTitle();

		LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("using title source: %s", titleSource));

		String titleString = null;

		try {
			titleString = titleSource.split("-")[1];
		} catch (Exception e) {
			try {
				titleString = titleSource.split("—")[1];
			} catch (Exception e1) {
				try {
					titleString = titleSource.split("–")[1];
				} catch (Exception e2) {
					titleString = titleSource.split(" ")[1];
				}
			}
		}

		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 56);

		executor.executeScript("document.getElementById(\"editTitle\").value = \"" + titleString + "\"");
		executor.executeScript(
				"document.getElementById(\"editContent\").innerHTML = \"" + wordUtils.getContent() + "\"");

		WebDriverUtils.findWebElement(nextBtn).click();
		LOGGER.begin().headerAction(MessageMethod.EVENT).info("switch translate dialog");
		switchTransDialog();

		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 59);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click yes to submit work");
		WebElement confirmBtn = WebDriverUtils.findWebElement(conFirmTransBtn);
		confirmBtn.click();

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("sumbit translate");

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("proceed to edit status");
		Thread.sleep(4000);
	}

	private void doEdit() throws Exception {
		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 64);
		WebDriverUtils.doWaitQuery(editBtn, wait);

		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 75);
		Thread.sleep(1000);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("doing edit");
		WebDriverUtils.findWebElement(editBtn).click();

		switchTransDialog();
		Thread.sleep(2000);
		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 82);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click yes to start edit");
		WebDriverUtils.findWebElement(conFirmTransBtn).click();
		Thread.sleep(2000);
		WebDriverUtils.doWaitQuery(doneBtn, wait);

		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 89);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("sumbit edit");
		WebDriverUtils.findWebElement(doneBtn).click();

		switchTransDialog();
		Thread.sleep(2000);

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("click to submit edit");
		WebDriverUtils.findWebElement(conFirmTransBtn).click();

		Thread.sleep(4000);
		InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 96);
		try {
			WebDriverUtils.findWebElement(publishBtn);
			Thread.sleep(2000);
			InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 98);

			InkstoneUploadMainService.finishedChapters.add(InkstoneUploadMainService.currentChapterName);

			Thread.currentThread().interrupt();

			InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 100);
		} catch (Exception e) {

			try {
				WebDriverUtils.findWebElement(reditBtn);
				Thread.sleep(2000);
				InkstoneUploadMainService.process.put(InkstoneUploadMainService.currentChapterName, 100);
				end = System.currentTimeMillis();

				Thread.currentThread().interrupt();
			} catch (Exception e1) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
				throw new Exception(
						String.format("book [%s] publish failed , please check inprogress item", this.bookName));
			}
		}

	}

	@Override
	public void run() {
		try {
			editLatestRaw();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.begin().headerAction(MessageMethod.ERROR)
					.error(String.format("Error at page:[%s]", driver.getTitle()));
			LOGGER.begin().headerAction(MessageMethod.ERROR).error(e.getMessage());

			InkstoneUploadMainService.exceptionList.add(e);
		}
	}

	class InProgressDialog extends Dialog {
		public InProgressDialog(Shell parentShell) {
			super(parentShell);
		}

		@Override
		protected int getShellStyle() {
			return super.getShellStyle() | SWT.RESIZE | SWT.MAX;
		}

		protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
			return null;
		}

		public final int SELECT_MANNUALLY = 101;
		public final int TERMINAT = 402;

		protected void initializeBounds() {
			Composite comp = (Composite) getButtonBar();
			super.createButton(comp, SELECT_MANNUALLY, "Select Mannually", true);
			super.createButton(comp, TERMINAT, "Terminate", false);
			super.initializeBounds();
		}

		protected Point getInitialSize() {
			return new Point(300, 400);
		}

	}
}
