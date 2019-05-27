package nyoibo.inkstone.upload.gui;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import nyoibo.inkstone.upload.web.action.InkstoneUploadMainService;

/**
 * <p>Title:ProgressThread.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-20 01:18
 */

public class ProgressThread implements Runnable{
	private Display display;
	private ProgressBar progressBar;
	private ConcurrentHashMap<String, Integer> process = new ConcurrentHashMap<>();

	public ProgressThread(Display display, ProgressBar progressBar, ConcurrentHashMap<String, Integer> process) {
		this.display = display;
		this.progressBar = progressBar;
		this.process = process;
	}

	public ConcurrentHashMap<String, Integer> getProcess() {
		return process;
	}

	public void setProcess(ConcurrentHashMap<String, Integer> process) {
		this.process = process;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName());
		for (int i = 1; i < 10; i++) {
			display.asyncExec(new Runnable() {
				public void run() {
					if (progressBar.isDisposed())
						return;
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String currentChapterName = InkstoneUploadMainService.currentChapterName;
					if (!StringUtils.isEmpty(currentChapterName))
						if (process.get(currentChapterName) != null) {
							int selection = process.get(currentChapterName);
							progressBar.setSelection(selection);
						}
				}
			});
			//i--;
		}
	}

}
