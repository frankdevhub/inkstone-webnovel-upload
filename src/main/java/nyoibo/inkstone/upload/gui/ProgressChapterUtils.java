package nyoibo.inkstone.upload.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * <p>Title:ProgressChapterUtils.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-24 02:26
 */

public class ProgressChapterUtils {
	private static Display display;
	private static Text textarea;

	public ProgressChapterUtils(Display display, Text textarea) {
		this.display = display;
		this.textarea = textarea;
	}

	public synchronized static void pushToProgressChapterText(String message) {
		SWTResourceManager.lock.lock();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				textarea.append(message);
			}
		});
		SWTResourceManager.lock.unlock();
		SWTResourceManager.condition.signal();
	}
}
