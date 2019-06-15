package nyoibo.inkstone.upload.gui;

import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * <p>Title:ConsoleStream.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>
 *
 * @author frankdevhub
 * @date:2019-05-20 01:02
 */

public class ConsoleStream extends PrintStream {

    private Text text;

    public ConsoleStream(OutputStream out, Text text) {
        super(out);
        this.text = text;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        buf = new byte[5000];
        final String log = new String(buf, off, len);
        Display.getDefault().syncExec(new Thread(() -> text.append(log)));
    }

}
