package nyoibo.inkstone.upload.gui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>Title:ErrorDialogUtils.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-24 02:44
 */

public class ErrorDialogUtils {

	private Display display;

	public ErrorDialogUtils(Display display) {
		super();
		this.display = display;
	}

	public void openErrorDialog(String text, Exception e) {
		Shell shell = display.getActiveShell();
		Status status = new Status(IStatus.ERROR, "dummyPlugin_id", 1, text, e);
		ErrorDialog dlg = new ErrorDialog(shell, "Error", e.getMessage(), status, IStatus.ERROR);
		dlg.open();
	}

}
