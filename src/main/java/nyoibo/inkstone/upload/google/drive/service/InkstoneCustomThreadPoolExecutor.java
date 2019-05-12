package nyoibo.inkstone.upload.google.drive.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;

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

	// Nthreads=Ncpu*(1+w/c)
	public static ExecutorService googleBackUpPool() {

		int nCPU = Runtime.getRuntime().availableProcessors();
		ExecutorService service = new ThreadPoolExecutor(3, 2 * nCPU, 0L, TimeUnit.MICROSECONDS,
				new LinkedBlockingQueue<Runnable>(300)) {
			private final Logger LOGGER = LoggerFactory.getLogger(InkstoneCustomThreadPoolExecutor.class);

			@Override
			protected void beforeExecute(Thread t, Runnable r) {
				LOGGER.begin().headerAction(MessageMethod.EVENT)
						.info(String.format("File download start:[%s]", t.getName()));
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
