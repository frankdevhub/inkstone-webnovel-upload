package nyoibo.inkstone.upload;

import org.eclipse.swt.widgets.Display;

import nyoibo.inkstone.upload.gui.InkstoneUploadMainWindow;

public class NyoiboApp {
	public static void main(String[] args) {
		InkstoneUploadMainWindow main = new InkstoneUploadMainWindow(Display.getDefault().getActiveShell());
		main.open();
	}
}
