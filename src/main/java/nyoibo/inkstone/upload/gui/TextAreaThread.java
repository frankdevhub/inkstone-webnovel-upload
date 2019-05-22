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

public class TextAreaThread extends Thread {

	private Display display;
	private Text textarea;
	private String text;

	public TextAreaThread(Display display, Text textarea, String text) {
		super();
		this.display = display;
		this.textarea = textarea;
		this.text = text;
	}

	@Override
	public void run() {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				System.out.println("=====");
				textarea.append("\n");
				textarea.append(text);
			}
		});

	}

}
