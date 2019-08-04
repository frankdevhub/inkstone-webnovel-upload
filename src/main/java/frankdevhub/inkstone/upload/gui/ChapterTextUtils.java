package nyoibo.inkstone.upload.gui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class ChapterTextUtils {
    private static Display display;
    private static Text textarea;

    public ChapterTextUtils(Display display, Text textarea) {
        ChapterTextUtils.display = display;
        ChapterTextUtils.textarea = textarea;
    }

    public static void pushToChapterText(String message) {
        if (!StringUtils.isEmpty(message)) {
            display.syncExec(() -> textarea.append(message));
        }
    }
}
