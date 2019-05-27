package nyoibo.inkstone.upload.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * <p>Title:TextAreaThread.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-22 20:36
 */

public class ConsoleUtils {

	private static Display display;
	private static Text textarea;

	public ConsoleUtils(Display display, Text textarea) {
		this.display = display;
		this.textarea = textarea;
	}

	public static void pushToConsole(String message) {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				textarea.append("\n");
				textarea.append(message);
				textarea.pack();
			}
		});
	}

}
