package nyoibo.inkstone.upload.web.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;
import nyoibo.inkstone.upload.utils.ExcelReaderUtils;

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
	
	private ConcurrentHashMap<String, Integer> total = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Integer> process = new ConcurrentHashMap<>();

	private InkstoneRawNovelService rawService = null;
	private InkstoneProgressNovelService progressNovelService = null;

	private List<Exception> execptions = new ArrayList();

	private Map<String, File> chapters = new HashMap<String, File>();
	private Map<String, String> bookListUrl = new HashMap<String, String>();
	private Map<String, String> bookCompareList = new HashMap<String, String>();

	private ExecutorService threadPool;

	private String bookListPath = "C:/Users/Administrator/AppData/Local/Google/booklist.xls";
	private String bookCompareListPath = "C:/Users/Administrator/AppData/Local/Google/comparelist.xls";
	
	private String transFilePath = "C:/Users/Administrator/AppData/Local/Google/Automation";
	
	private String bookName;
	
	public synchronized static Thread check(String thread) {
		Thread alive = null;
		ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
		int noThread = currentGroup.activeCount();

		Thread[] lstThreads = new Thread[noThread];
		currentGroup.enumerate(lstThreads);

		for (int i = 0; i < noThread; i++) {
			String currentThreadName = lstThreads[i].getName();
			if (currentThreadName.equals(thread)) {
				alive = lstThreads[i];
				break;
			}
		}
		return alive;
	}

	private void readBookList() throws Exception {
		File bookListFile = new File(bookListPath);
		this.bookListUrl = ExcelReaderUtils.readExcel(bookListFile);
	}

	private void readCompareList() throws Exception {
		File bookListFile = new File(bookCompareListPath);
		this.bookCompareList = ExcelReaderUtils.readExcel(bookListFile);
	}

	private void init() throws Exception {
		File folder = new File(transFilePath).listFiles()[0];
		
		this.bookName = folder.getName();
		
		File[] chapters = folder.listFiles();
		for (int i = 0; i < chapters.length; i++) {
			File chap = chapters[i];
			String chapName = chap.getName().substring(0, chap.getName().lastIndexOf("."));
			this.chapters.put(chapName, chap);
		}

		Integer count = new Integer(chapters.length);
		total.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW, count);
		total.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_INPROGRESS, count);
		total.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_TRANSLATEING, count);
		total.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_EDITING, count);
		total.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_READY_PUBLISH, count);

		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_RAW, 0);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_INPROGRESS, 0);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_TRANSLATEING, 0);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_EDITING, 0);
		process.put(SeleniumInkstone.INKSTONE_TRANS_STATUS_READY_PUBLISH, 0);

		process.put("init", 1);

		int nCPU = Runtime.getRuntime().availableProcessors();
		ExecutorService service = new ThreadPoolExecutor(3, 2 * nCPU, 0L, TimeUnit.MICROSECONDS,
				new LinkedBlockingQueue<Runnable>(300));
		this.threadPool = service;
		readBookList();
		//readCompareList();
		
		String url = bookListUrl.get(bookName);
		if (StringUtils.isEmpty(url))
			throw new Exception(String.format("cannot find book:[%s]", bookName));
		rawService = new InkstoneRawNovelService(false, url, this.bookName, process, this.chapters);

		Thread rawUpload = new Thread(rawService);
		rawUpload.setName(bookName);
		service.submit(rawUpload);

		service.shutdown();
	}

	public void start() throws Exception {
		init();
	}
	
	public static void main(String[] args) throws Exception {
		InkstoneUploadMainService service = new InkstoneUploadMainService();
		service.start();
	
	}
}
