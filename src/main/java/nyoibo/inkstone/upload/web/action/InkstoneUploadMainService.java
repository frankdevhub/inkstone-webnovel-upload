package nyoibo.inkstone.upload.web.action;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;

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
	
	private Map<String, String> bookListUrl = new HashMap<String, String>();
	private ExecutorService threadPool;
	
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
		rawService  = new InkstoneRawNovelService(false,url ,"Badge in Azure ( BIA )" , process);
		service.submit(rawService);
	}
	
	public void start() throws Exception{
		init();	
	}
	
	public static void main(String[] args) throws Exception {
		InkstoneUploadMainService main = new InkstoneUploadMainService();
		main.start();
	}
}
