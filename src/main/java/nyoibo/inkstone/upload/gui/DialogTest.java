package nyoibo.inkstone.upload.gui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FillLayout;

/**
 * <p>Title:DialogTest.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-24 15:48
 */

public class DialogTest extends Dialog {
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
    public static String runningChapterName;

	public DialogTest(Shell parentShell, String filePath) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.MIN);
		this.shell = parentShell;
		this.filePath = filePath;
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		dataPath = filePath;

		Composite container = (Composite) super.createDialogArea(parent);
		composite = container;
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		// composite.setLayout(layout);
		this.form = new SashForm(container, SWT.BEGINNING | SWT.BORDER);
		form.setLayout(new GridLayout(1, true));

		fileComposite = new Composite(form, SWT.BEGINNING | SWT.BORDER);
		fileComposite.setLayout(new GridLayout(1, true));
		new FileExplorer(fileComposite);
		this.chapterComposite = new Composite(form, SWT.BEGINNING | SWT.BORDER);
		chapterComposite.setLayout(new GridLayout(1, true));
		try {
			new ChapterTable(chapterComposite, filePath);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		form.setWeights(new int[] { 200, 150 });
		display = container.getDisplay();

		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
	
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		this.shell = newShell;
		newShell.setSize(900, 300);
		newShell.setText("Config Chapter List");
		newShell.setImage(new Image(null, "src/main/resources/gui/favicon.ico"));
	}

	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		DialogTest test = new DialogTest(shell, "D:\\nyoibo");
		test.open();
	}
}
