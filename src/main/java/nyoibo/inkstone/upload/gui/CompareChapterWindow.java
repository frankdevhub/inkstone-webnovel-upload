package nyoibo.inkstone.upload.gui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>Title:CompareChapterWindow.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-23 12:13
 */

public class CompareChapterWindow {
	
	private Shell shell;
	private Display display;
	private SashForm form;
	
	private Composite fileComposite;
	private Composite chapterComposite;
	
	public static Map<String, String> chapterList = new HashMap<String, String>();
	public static Map<String, String> compareList = new HashMap<String, String>();
	public static String chapCacheName;
	public static String dataPath;
	public static String comaprePath;
	
	public CompareChapterWindow(String filePath) throws Exception {
		dataPath = filePath;

		this.display = new Display();
		this.shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setImage(new Image(null, "src/main/resources/gui/favicon.ico"));
		shell.setText("Config Chapter List");

		this.form = new SashForm(shell, SWT.HORIZONTAL | SWT.BORDER);
		form.setLayout(new FillLayout());

		fileComposite = new Composite(form, SWT.BEGINNING);
		fileComposite.setLayout(new FillLayout());
		new FileExplorer(fileComposite);

		this.chapterComposite = new Composite(form, SWT.BEGINNING);
		chapterComposite.setLayout(new FillLayout());
		new ChapterTable(chapterComposite, filePath);

		form.setWeights(new int[] { 200, 150 });
		shell.setSize(800, 450);

	}

	public void open() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

	}

}
