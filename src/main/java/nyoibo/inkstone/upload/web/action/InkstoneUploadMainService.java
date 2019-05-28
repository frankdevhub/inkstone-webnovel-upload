package nyoibo.inkstone.upload.web.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;

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
	}

	private String configChromeData(String path) throws IOException, InterruptedException {
		ChromeDataConfig.cleanData();
		System.out.println("local chrome cache source:" + path);
		String dataName = ChromeDataConfig.createDataName(SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW);
		String cacheMirror = ChromeDataConfig.config(path, dataName);
		System.out.println(cacheMirror);
		return cacheMirror;
	}

	private void readBookList() throws Exception {
		File bookListFile = new File(bookListPath);
		this.bookListUrl = ExcelReaderUtils.readExcel(bookListFile);
	}

	private void readCompareList() throws Exception {
		File compareFile = new File(bookCompareListPath);
		this.bookCompareList = CompareExcelReaderUtils.readExcel(compareFile);
		
		Set<Entry<String,String>> set = bookCompareList.entrySet();
		for(Entry<String,String>entry:set){
			System.out.println(entry.getKey()+"======"+entry.getValue());
		}
	}

	private void initRawUpload() throws Exception {
		this.mirrorPath = configChromeData(this.path);
		//ConsoleUtils.pushToConsole(String.format("Chrome cache mirror path:[%s]", mirrorPath));

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

		Set<Entry<String,String>> entrySet = bookListUrl.entrySet();
		for(Entry<String,String> entry:entrySet){
			System.out.println(entry.getKey()+"==="+entry.getValue());
		}
		
		String url = bookListUrl.get(bookName);
		System.out.println(String.format("Book url:[%s]", url));
		if (StringUtils.isEmpty(url))
			throw new Exception(String.format("cannot find book:[%s]", bookName));

		try {
			ArrayList<InkstoneRawNovelService> waitList = new ArrayList<InkstoneRawNovelService>();
			for (int i = 0; i < bookCompareList.size(); i++) {
				InkstoneRawNovelService rawService = null;
				if (i == 0) {
					rawService = new InkstoneRawNovelService(false, url, this.bookName, process, bookCompareList, true,
							driver, chapterFileList);
				} else {
					rawService = new InkstoneRawNovelService(false, url, this.bookName, process, bookCompareList, false,
							driver, chapterFileList);
				}

				waitList.add(rawService);
			}
			for (InkstoneRawNovelService raw : waitList)
				this.threadPool.execute(new Thread(raw));
			this.threadPool.shutdown();
		} catch (Exception e) {
			this.threadPool.shutdown();
		}
	}

	public void rawUploadStart() throws Exception {
		initRawUpload();
	}

	public WebDriver getDriver() {
		return this.driver;
	}
	
	
	public static void main(String[] args) throws Exception {
		InkstoneUploadMainService service = new InkstoneUploadMainService("C:\\Users\\Administrator\\Desktop\\booklist.xls", "C:\\Users\\Administrator\\Desktop\\booklist.xls", "D:\\nyoibo_automation\\Sweet Love 1V1 Spoiled by The Executive", "C:\\Users\\Administrator\\AppData\\Local\\Google\\Chrome\\User Data", false);
		service.rawUploadStart();
	}
}
