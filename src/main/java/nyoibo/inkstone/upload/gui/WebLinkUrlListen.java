package nyoibo.inkstone.upload.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * <p>Title:WebLinkTextThread.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-27 21:50
 */

public class WebLinkUrlListen implements Runnable {
	private Runnable runnable;
	public static String update = "";
	private Display display;
	private Text textarea;

	public WebLinkUrlListen(Display display, Text textarea) {
		this.display = display;
		this.textarea = textarea;
	}

	private class Time implements Runnable {
		Text textarea;

		public Time(Text textarea) {
			this.textarea = textarea;
		}

		@Override
		public void run() {
			textarea.setText(update);
		}
	}

	@Override
	public void run() {
		for (;;) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!this.display.isDisposed()) {
				runnable = new Time(this.textarea);
			}
			display.asyncExec(runnable);
		}
	}

}
