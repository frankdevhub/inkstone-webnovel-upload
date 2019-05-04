package nyoibo.inkstone.upload.utils;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.scheduling.config.Task;

/**
 * <p>Title:InkstoneCustomThreadPoolExecutor.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-04 23:17
 */

public class InkstoneCustomThreadPoolExecutor extends ThreadPoolExecutor {

	private static final Logger LOGGER = Logger.getLogger(InkstoneCustomThreadPoolExecutor.class);
	private BlockingQueue<FutureTask<Map>> workBlockingQueue = new LinkedBlockingQueue<FutureTask<Map>>();

	public InkstoneCustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public BlockingQueue<FutureTask<Map>> getWorkBlockingQueue() {
		return workBlockingQueue;
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		workBlockingQueue.add((FutureTask<Map>) r);
		LOGGER.info("Before the task execution");
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		workBlockingQueue.remove((FutureTask<Map>) r);
		LOGGER.info("After the task execution");
	}

	public void addToThreadPool(FutureTask<Map> task) {
		BlockingQueue<Runnable> waitThreadQueue = this.getQueue();
		BlockingQueue<FutureTask<Map>> workThreadQueue = this.getWorkBlockingQueue();

		if (!waitThreadQueue.contains(task) && !workThreadQueue.contains(task)) {
			Timestamp recordtime = new Timestamp(System.currentTimeMillis());
			// LOGGER.info("a_workThread:recordId="+downloadRecord.getId()+",name="+downloadRecord.getName()+"
			// add to workThreadQueue");
			// downloadThread.setName("th_"+downloadRecord.getName());
			this.execute(task);

		} else {
			// LOGGER.info("i_workThread:recordId="+downloadRecord.getId()+",name="+downloadRecord.getName()+"
			// in waitThreadQueue or workThreadQueue");
		}
	}

}
