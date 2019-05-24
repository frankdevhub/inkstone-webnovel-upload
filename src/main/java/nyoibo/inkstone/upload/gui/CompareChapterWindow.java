package nyoibo.inkstone.upload.gui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

public class CompareChapterWindow extends Dialog{
	
	private Shell shell;
	private SashForm form;

	private Composite fileComposite;
	private Composite chapterComposite;
	private Display display;
	private String filePath;

	public static Map<String, String> chapterList = new HashMap<String, String>();
	public static Map<String, String> compareList = new HashMap<String, String>();
	public static String chapCacheName;
	public static String dataPath;
    public static String comaprePath;
    private Composite composite;
    
    
	public CompareChapterWindow(Shell parentShell, String filePath) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.MIN);
		this.shell = parentShell;
		this.filePath = filePath;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dataPath = filePath;
		Composite container = (Composite) super.createDialogArea(parent);
		composite = container;
		display = container.getDisplay();

		GridLayout layout = new GridLayout(3, false);
		// composite.setLayout(layout);
		this.form = new SashForm(shell, SWT.HORIZONTAL | SWT.BORDER);
		form.setLayout(new FillLayout());
		form.setLayout(layout);

		fileComposite = new Composite(form, SWT.BEGINNING);
		fileComposite.setLayout(layout);
		new FileExplorer(fileComposite);
		this.chapterComposite = new Composite(form, SWT.BEGINNING);
		chapterComposite.setLayout(layout);
		try {
			new ChapterTable(chapterComposite, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		form.setWeights(new int[] { 100, 300 });
		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		this.shell = newShell;
		newShell.setSize(900, 300);
		newShell.setText("Config Chapter List");
		newShell.setImage(new Image(null, "src/main/resources/gui/favicon.ico"));
	}
	
/*	public CompareChapterWindow(String filePath,Shell shell) throws Exception {
		dataPath = filePath;
		
		
		this.shell = new Shell(new Display());
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

	}*/

/*	public void open() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

	}
*/
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		CompareChapterWindow test = new CompareChapterWindow(shell, "D:\\snyoibo");
		test.open();
	}
}
