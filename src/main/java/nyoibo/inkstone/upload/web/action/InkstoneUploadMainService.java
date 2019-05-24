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
	
	private ConcurrentHashMap<String, Integer> process = new ConcurrentHashMap<>();
	public static ArrayList<String> finishedChapters = new ArrayList<String>();
	public static String currentChapterName = null;

	private Map<String, String> bookListUrl = new HashMap<String, String>();
	private Map<String, String> bookCompareList = new HashMap<String, String>();
    private Map<String, String> chapterFileList = new HashMap<String,String>();
	
	private ExecutorService threadPool;
	private String path; //chrome cache path
    private String mirrorPath; // chrome chache mirror path
	
	private String bookListPath;
	private String bookCompareListPath;

	private String bookName;
	private WebDriver driver;
	
	private String dataFolderPath;
	
	
	public InkstoneUploadMainService(){
		
	}
	
	private String configChromeData(String path) throws IOException {
		// String root = ChromeDataConfig.getLocal();
		String dataName = ChromeDataConfig.createDataName(SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW);
		return ChromeDataConfig.config(path, dataName);
	}

	private void readBookList(String listPath) throws Exception {
		File bookListFile = new File(listPath);
		this.bookListUrl = ExcelReaderUtils.readExcel(bookListFile);
	}
	
	private void readCompareList() throws Exception {
		File compareFile = new File(bookCompareListPath);
		this.bookCompareList = CompareExcelReaderUtils.readExcel(compareFile);
	}

	private void initRawUpload() throws Exception {
		this.mirrorPath = configChromeData(this.path);
		DriverBase.instantiateDriverObject();
		// DANGER :
		this.driver = DriverBase.getDriver(mirrorPath);

		File folder = new File(dataFolderPath);

		for (File f : folder.listFiles()) {
			chapterFileList.put(InkstoneRawHeaderUtils.convertRawENeader(f.getName()), f.getAbsolutePath());
		}

		this.bookName = folder.getName();

		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW, 0);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_INPROGRESS, 0);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_TRANSLATEING, 0);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_EDITING, 0);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_READY_PUBLISH, 0);

		process.put("init", 1);

		//int nCPU = Runtime.getRuntime().availableProcessors();
		/*ExecutorService service = new ThreadPoolExecutor(3, 2 * nCPU, 0L, TimeUnit.MICROSECONDS,
				new LinkedBlockingQueue<Runnable>(300));*/

		this.threadPool = Executors.newSingleThreadExecutor();
		readBookList(bookListPath);
		readCompareList();

		//String url = bookListUrl.get(bookName);
		String url = "https://inkstone.webnovel.com/book/detail/cbid/8094160805005105";
		if (StringUtils.isEmpty(url))
			throw new Exception(String.format("cannot find book:[%s]", bookName));

		for (int i = 0; i < bookListUrl.size(); i++) {
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

	public ConcurrentHashMap<String, Integer> getProcess() {
		return process;
	}

	public void setProcess(ConcurrentHashMap<String, Integer> process) {
		this.process = process;
	}

	/*public static void main(String[] args) throws Exception {
		//InkstoneUploadMainService service = new InkstoneUploadMainService();
		//service.rawUploadStart();

		File folder = new File("C:/Users/Administrator/AppData/Local/Google/data").listFiles()[0];
		Map<String, String> map = new HashMap<String, String>();
		for (File f : folder.listFiles()) {
			map.put(InkstoneRawHeaderUtils.convertRawENeader(f.getName()), f.getAbsolutePath());
		}

		Set<Entry<String, String>> bookSet = map.entrySet();
		for (Entry<String, String> entry : bookSet) {
			System.out.println(entry.getKey() + "========" + entry.getValue());
		}
		
		System.out.println("ssssssssssssssssss");
		//
		File bookListFile = new File("C:/Users/Administrator/AppData/Local/Google/compare.xls");
		Map<String, String> compareMap = CompareExcelReaderUtils.readExcel(bookListFile);
		Set<Entry<String, String>> compareSet = compareMap.entrySet();
		for (Entry<String, String> entry : compareSet) {
			System.out.println(entry.getKey() + "========" + entry.getValue());
		}
		
		String res = map.get("1496");
		System.out.println(res);
		
		
	}*/
}
