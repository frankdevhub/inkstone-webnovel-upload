package nyoibo.inkstone.upload.web.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.gui.ConsoleUtils;
import nyoibo.inkstone.upload.selenium.DriverBase;
import nyoibo.inkstone.upload.selenium.config.ChromeDataConfig;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;
import nyoibo.inkstone.upload.utils.CompareExcelReaderUtils;
import nyoibo.inkstone.upload.utils.ExcelReaderUtils;
import nyoibo.inkstone.upload.utils.InkstoneRawHeaderUtils;

/**
 * <p>Title:InkstoneNovelUploadThread.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-12 22:00
 */

public class InkstoneUploadMainService {

	public static ConcurrentHashMap<String, Integer> process = new ConcurrentHashMap<String, Integer>();
	public static ArrayList<String> finishedChapters = new ArrayList<String>();
	public static String currentChapterName = null;

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
	private final boolean foreign;

	private final Logger LOGGER = LoggerFactory.getLogger(InkstoneUploadMainService.class);

	public InkstoneUploadMainService(String bookListPath, String bookCompareListPath, String dataFolderPath,
			String cacheSourcePath, boolean foreign) throws Exception {
		File mirrorCacheFile = new File(ChromeDataConfig.WIN_TARGET);
		if (!mirrorCacheFile.exists())
			throw new Exception("Please create a file named Automation under root at disk D");

		this.bookListPath = bookListPath;
		this.bookCompareListPath = bookCompareListPath;
		this.dataFolderPath = dataFolderPath;
		this.path = cacheSourcePath;
		this.foreign = foreign;

		ConsoleUtils.pushToConsole("Init InkstoneUploadMainService");
	}

	private String configChromeData(String path) throws IOException {
		System.out.println("local chrome cache source:" + path);
		String dataName = ChromeDataConfig.createDataName(SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW);
		String cacheMirror = ChromeDataConfig.config(path, dataName);
		System.out.println(cacheMirror);
		return cacheMirror;
	}

	private void readBookList() throws Exception {
		ConsoleUtils.pushToConsole("do readBookList()");
		File bookListFile = new File(bookListPath);
		this.bookListUrl = ExcelReaderUtils.readExcel(bookListFile);
	}

	private void readCompareList() throws Exception {
		ConsoleUtils.pushToConsole("do readCompareList()");
		File compareFile = new File(bookCompareListPath);
		this.bookCompareList = CompareExcelReaderUtils.readExcel(compareFile);
	}

	private void initRawUpload() throws Exception {
		this.mirrorPath = configChromeData(this.path);
		ConsoleUtils.pushToConsole(String.format("Chrome cache mirror path:[%s]", mirrorPath));

		DriverBase.instantiateDriverObject();
		// DANGER :
		this.driver = DriverBase.getDriver(mirrorPath);

		File folder = new File(dataFolderPath);

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
		ConsoleUtils.pushToConsole(String.format("Book url:[%s]", url));
		if (StringUtils.isEmpty(url))
			throw new Exception(String.format("cannot find book:[%s]", bookName));

		for (int i = 0; i < chapterFileList.size(); i++) {
			InkstoneRawNovelService rawService = null;
			if (i == 0) {
				rawService = new InkstoneRawNovelService(false, url, this.bookName, process, bookCompareList, true,
						driver, chapterFileList);
			} else {
				rawService = new InkstoneRawNovelService(false, url, this.bookName, process, bookCompareList, false,
						driver, chapterFileList);
			}

			Thread uploadThread = new Thread(rawService);
			this.threadPool.execute(uploadThread);
		}
		this.threadPool.shutdown();
	}

	public void rawUploadStart() throws Exception {
		initRawUpload();
	}

}
