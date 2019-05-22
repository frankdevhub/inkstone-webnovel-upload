package nyoibo.inkstone.upload.gui;

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

	public ProgressThread(Display display, ProgressBar progressBar) {
		this.display = display;
		this.progressBar = progressBar;
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
