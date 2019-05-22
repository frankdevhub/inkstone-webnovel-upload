package nyoibo.inkstone.upload.gui;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * <p>Title:ProgressThread.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-20 01:18
 */

public class ProgressThread extends Thread {
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

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
			display.asyncExec(new Runnable() {
				public void run() {
					if (progressBar.isDisposed())
						return;
					if (progressBar.getSelection() > 5) {
						progressBar.setSelection(0);
					}
					progressBar.setSelection(progressBar.getSelection() + 1);
				}
			});
		}
	}

}
