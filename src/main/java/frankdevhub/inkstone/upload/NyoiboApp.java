package nyoibo.inkstone.upload;

import nyoibo.inkstone.upload.gui.InkstoneUploadMainWindow;
import org.eclipse.swt.widgets.Display;

public class NyoiboApp {
    public static void main(String[] args) {
        InkstoneUploadMainWindow main = new InkstoneUploadMainWindow(Display.getDefault().getActiveShell());
        main.open();
    }
}
