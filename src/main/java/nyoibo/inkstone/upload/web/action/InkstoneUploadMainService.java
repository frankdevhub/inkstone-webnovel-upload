package nyoibo.inkstone.upload.web.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.gui.CompareChapterWindow;
import nyoibo.inkstone.upload.gui.ConsoleTextAreaListener;
import nyoibo.inkstone.upload.gui.InkstoneUploadConsole;
import nyoibo.inkstone.upload.message.MessageMethod;
import nyoibo.inkstone.upload.selenium.DriverBase;
import nyoibo.inkstone.upload.selenium.config.ChromeDataConfig;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;
import nyoibo.inkstone.upload.utils.CompareExcelReaderUtils;
import nyoibo.inkstone.upload.utils.ExcelReaderUtils;
import nyoibo.inkstone.upload.utils.InkstoneRawHeaderUtils;
import nyoibo.inkstone.upload.utils.ThreadUtils;

public class InkstoneUploadMainService implements ConsoleTextAreaListener {

	public static ConcurrentHashMap<String, Integer> process = new ConcurrentHashMap<String, Integer>();
	public static ArrayList<String> finishedChapters = new ArrayList<String>();
	public static String currentChapterName;
	public static int fileTotal = 0;
	public static int initFileCount = 0;

	private Map<String, String> bookListUrl = new HashMap<String, String>();
	private Map<String, String> bookCompareList = new HashMap<String, String>();
	private Map<String, String> chapterFileList = new HashMap<String, String>();

	private ExecutorService threadPool;
	private String path; // chrome cache path
	private String mirrorPath; // chrome chache mirror path

	private String bookListPath;
	private String bookCompareListPath;
	private String bookName;
	private WebDriver driver;

	private String dataFolderPath;
	private final Logger LOGGER = LoggerFactory.getLogger(InkstoneUploadMainService.class);

	public static LinkedList<Exception> exceptionList = new LinkedList<Exception>();

	public InkstoneUploadMainService(String bookListPath, String bookCompareListPath, String dataFolderPath,
			String cacheSourcePath, boolean foreign) throws Exception {

		File mirrorCacheFile = new File(ChromeDataConfig.WIN_TARGET);
		if (!mirrorCacheFile.exists())
			throw new Exception("Please create a file named Automation under root at disk D");

		this.bookListPath = bookListPath;
		this.bookCompareListPath = bookCompareListPath;
		this.dataFolderPath = dataFolderPath;
		this.path = cacheSourcePath;
	}

	private String configChromeData(String path) throws IOException, InterruptedException {
		ChromeDataConfig.cleanData();

		pushLog(LOGGER.begin().headerAction(MessageMethod.EVENT).info("local chrome cache source:" + path));
		String dataName = ChromeDataConfig.createDataName(SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW);
		String cacheMirror = ChromeDataConfig.config(path, dataName);
		pushLog(LOGGER.begin().headerAction(MessageMethod.EVENT).info(cacheMirror));

		return cacheMirror;
	}

	private void readBookList() throws Exception {
		File bookListFile = new File(bookListPath);
		this.bookListUrl = ExcelReaderUtils.readExcel(bookListFile);
	}

	private void readCompareList() throws Exception {
		if (!InkstoneUploadConsole.skipChapterCompareListExcel) {
			File compareFile = new File(bookCompareListPath);
			this.bookCompareList = CompareExcelReaderUtils.readExcel(compareFile);
		} else {
			this.bookCompareList = CompareChapterWindow.compareList;
		}
	}

	private void initRawUpload() throws Exception {
		this.mirrorPath = configChromeData(this.path);
		DriverBase.instantiateDriverObject();
		// DANGER :
		this.driver = DriverBase.getDriver(mirrorPath);

		File folder = new File(dataFolderPath);
		if (!InkstoneUploadConsole.skipChapterCompareListExcel)
			for (File f : folder.listFiles()) {
				chapterFileList.put(InkstoneRawHeaderUtils.convertRawENeader(f.getName()), f.getAbsolutePath());
			}

		this.bookName = folder.getName();
		// int nCPU = Runtime.getRuntime().availableProcessors();
		/*
		 * ExecutorService service = new ThreadPoolExecutor(3, 2 * nCPU, 0L,
		 * TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(300));
		 */
		this.threadPool = Executors.newSingleThreadExecutor();
		readBookList();
		readCompareList();

		String url = bookListUrl.get(bookName);
		System.out.println(String.format("Book url:[%s]", url));
		if (StringUtils.isEmpty(url))
			throw new Exception(String.format("cannot find book:[%s]", bookName));

		try {
			// ArrayList<InkstoneRawNovelService> waitList = new
			// ArrayList<InkstoneRawNovelService>();
			for (int i = 0; i < bookCompareList.size(); i++) {
				if (exceptionList.size() > 0) {
					LOGGER.begin().headerAction(MessageMethod.EVENT).info("Current Thread occured an error");
					throw exceptionList.get(0);
				}
				InkstoneRawNovelService rawService = null;
				if (i == 0) {
					rawService = new InkstoneRawNovelService(false, url, this.bookName, process, bookCompareList, true,
							driver, chapterFileList);
				} else {
					rawService = new InkstoneRawNovelService(false, url, this.bookName, process, bookCompareList, false,
							driver, chapterFileList);
				}
				Thread next = new Thread(rawService);
				next.setName("innkstone-novel-upload");
				this.threadPool.execute(next);

				// waitList.add(rawService);
			}

			this.threadPool.shutdown();
		} catch (Exception e) {
			Thread check = ThreadUtils.check("innkstone-novel-upload");
			if (check != null)
				check.interrupt();
			this.threadPool.shutdown();
		}
	}

	public void rawUploadStart() throws Exception {
		initRawUpload();
	}

	public WebDriver getDriver() {
		return this.driver;
	}

	@Override
	public void pushLog(String message) {
		InkstoneUploadConsole.consoleStr.add(message);
		InkstoneUploadConsole.flag = false;
	}

}
