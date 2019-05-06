package nyoibo.inkstone.upload.google.drive.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;

/**
 * <p>Title:InkstoneCustomThreadPoolExecutor.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-04 23:17
 */

public class InkstoneCustomThreadPoolExecutor {
	public static ExecutorService googleBackUpPool() {
		ExecutorService service = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MICROSECONDS,
				new LinkedBlockingQueue<Runnable>(10)){
					
					@Override
					protected void afterExecute(Runnable r, Throwable t) {
						super.afterExecute(r, t);
					}

					@Override
					protected void beforeExecute(Thread t, Runnable r) {
						super.beforeExecute(t, r);
					}

					@Override
					protected void terminated() {
						super.terminated();
					}
			
		};
		return service;
	}

}
