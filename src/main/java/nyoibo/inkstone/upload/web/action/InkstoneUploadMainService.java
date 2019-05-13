package nyoibo.inkstone.upload.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.openqa.selenium.WebDriver;

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

	private Map<String, String> bookListUrl = new HashMap<String, String>();
	private ExecutorService threadPool;

	private List<WebDriver> drivers = new ArrayList<>();
	
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

	private void init() throws Exception {
		int nCPU = Runtime.getRuntime().availableProcessors();
		ExecutorService service = new ThreadPoolExecutor(3, 2 * nCPU, 0L, TimeUnit.MICROSECONDS,
				new LinkedBlockingQueue<Runnable>(300));
		this.threadPool = service;
		String url = "https://inkstone.webnovel.com/book/detail/cbid/8628176105001205";
		rawService = new InkstoneRawNovelService(false, url, "Badge in Azure-Inkstone", process);
		service.submit(rawService);
		String url2 = "https://inkstone.webnovel.com/book/detail/cbid/10866196606193805";
		InkstoneRawNovelService rawService1 = new InkstoneRawNovelService(false, url2,
				"Back Then, I Adored You-Inkstone", process);
		service.submit(rawService1);
		
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
