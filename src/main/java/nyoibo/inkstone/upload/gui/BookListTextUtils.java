package nyoibo.inkstone.upload.gui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * <p>Title:WebLinkUtils.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-24 02:26
 */

public class BookListTextUtils {
	private static Display display;
	private static Text textarea;

	public BookListTextUtils(Display display, Text textarea) {
		this.display = display;
		this.textarea = textarea;
	}

	public static void pushToBookListLink(String message) {
		if (!StringUtils.isEmpty(message)) {
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					textarea.append(message);
				}
			});
		}
	}
}
