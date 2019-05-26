package nyoibo.inkstone.upload.gui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;
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

public class CompareChapterWindow extends Dialog {

	private SashForm form;

	private Composite fileComposite;
	private Composite chapterComposite;
	private ScrolledComposite scrolledComposite;

	private String filePath;

	public static Map<String, String> chapterList = new HashMap<String, String>();
	public static Map<String, String> compareList = new HashMap<String, String>();
	public static String chapCacheName;
	public static String dataPath;
	public static String comaprePath;

	public static String runningChapterName;

	public CompareChapterWindow(Shell parentShell, String filePath) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.MIN);
		this.filePath = filePath;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dataPath = filePath;

		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));

		this.form = new SashForm(container, SWT.BEGINNING | SWT.BORDER);
		form.setLayout(new FillLayout());

		fileComposite = new Composite(form, SWT.BEGINNING | SWT.BORDER);
		fileComposite.setLayout(new GridLayout(1, true));
		new FileExplorer(fileComposite);

		scrolledComposite = new ScrolledComposite(form, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setTouchEnabled(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setAlwaysShowScrollBars(true);

		chapterComposite = new Composite(scrolledComposite, SWT.BORDER | SWT.H_SCROLL);
		try {
			new ChapterTable(chapterComposite, filePath);
			chapterComposite.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));
			form.setWeights(new int[] { 444, 429 });
			scrolledComposite.setMinSize(new Point(140, 680));
			scrolledComposite.setContent(chapterComposite);

			ScrollBar vb = scrolledComposite.getVerticalBar();
			vb.setIncrement(10);

			chapterComposite.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					chapterComposite.setFocus();
				}
			});

			chapterComposite.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(900, 300);
		newShell.setText("Config Chapter List");
		newShell.setImage(new Image(null, "src/main/resources/gui/favicon.ico"));
	}

}
