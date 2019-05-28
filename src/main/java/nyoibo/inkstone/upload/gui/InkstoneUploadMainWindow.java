package nyoibo.inkstone.upload.gui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.shared.utils.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import nyoibo.inkstone.upload.utils.InkstoneRawHeaderUtils;

/**
 * <p>Title:InkstoneUploaddMainWindow.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-19 18:05
 */

public class InkstoneUploadMainWindow extends TitleAreaDialog {

	private static final String START_UPLOAD = "Start";
	private static final String HELP_DIALOG_TITLE = "Help";
	private static final String HELP_INFORMATION = "If you need help, please contact support at frank.fang@jianlaiglobal.com";

	private InkstoneUploadConsole uploadConsole;
	private CompareChapterWindow compareChapterWindow;

	public static final String CHAPTERS_PATH_ROOT = "D:\\nyoibo_automation";
	public static String CHAPTER_PATH = "chapters_file_path";
	public static String CHAPTER_EXCEL = "chapters_excel_path";
	public static String BOOK_LIST_PATH = "book_list_path";
	public static String CHROME_CACHE_PATH = "chrome_cache_path";

	public static final int START_RUN_ID = 280;
	
	public InkstoneUploadConsole getUplaodConsole() {
		return uploadConsole;
	}

	public CompareChapterWindow getCompareChapterWindow() {
		return compareChapterWindow;
	}

	private class CustomComposite extends Composite {
		public CustomComposite(Composite parent, int style, String imagePath) {
			super(parent, style);
			setLayout(new GridLayout(1, true));
			Image img = new Image(Display.getDefault(), imagePath);

			this.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					Point size = CustomComposite.this.getSize();
					Point p = CustomComposite.this.getLocation();
					e.gc.drawImage(img, 0, 0, img.getBounds().width, img.getBounds().height, p.x, p.y, size.x, size.y);

				}
			});
		}
	}

	public InkstoneUploadMainWindow(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		parent.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		parent.setToolTipText("");

		setMessage("Inkstone Web Novel Automation");
		setTitle("Nyoibo Studio\r\n");
		Composite area = (Composite) super.createDialogArea(parent);

		CustomComposite container = new CustomComposite(area, SWT.None, "src/main/resources/gui/nyoibo.png");
		container.setTouchEnabled(true);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		return area;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button helpButton = createButton(parent, IDialogConstants.HELP_ID, IDialogConstants.HELP_LABEL, false);
		helpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), HELP_DIALOG_TITLE,
						HELP_INFORMATION);
			}
		});
		Button startButton = createButton(parent, START_RUN_ID, START_UPLOAD, false);
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Shell shell = new Shell();
					Display display = Display.getDefault();
					uploadConsole = new InkstoneUploadConsole(shell, display);
					uploadConsole.open();

					while (!shell.isDisposed()) {
						if (!display.readAndDispatch())
							display.sleep();
					}

				} catch (IOException e1) {
					new ErrorDialogUtils(parent.getDisplay()).openErrorDialog("Can not initalize upload console.", e1);
					e1.printStackTrace();
				}
			}
		});
		Button setExcelButton = createButton(parent, 1001, "Set Upload Chapters", false);
		setExcelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setExcelButton.setEnabled(false);
				DirectoryDialog folderdlg = new DirectoryDialog(new Shell());
				folderdlg.setText("Select Chapter Path");
				folderdlg.setFilterPath("SystemDrive");
				folderdlg.setMessage("Please select your chapter stored path");
				String selecteddir = folderdlg.open();

				if (!StringUtils.isEmpty(selecteddir))
					if (!selecteddir.startsWith(CHAPTERS_PATH_ROOT)) {
						new ErrorDialogUtils(parent.getDisplay()).openErrorDialog(
								"Please select a folder named \"nyoibo_automation\" under root of disk D.",
								new FileNotFoundException());
						setExcelButton.setEnabled(true);

						return;
					}

				if (selecteddir != null) {
					try {
						compareChapterWindow = new CompareChapterWindow(Display.getCurrent().getActiveShell(),
								selecteddir);
						compareChapterWindow.open();

						setExcelButton.setEnabled(true);
					} catch (Exception e1) {
						new ErrorDialogUtils(Display.getDefault()).openErrorDialog("Failed to package chapter files",
								e1);
						setExcelButton.setEnabled(true);
					}
				}
				setExcelButton.setEnabled(true);
			}
		});
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Inkstone-QiDian International's novel translation platform");
		newShell.setImage(new Image(null, "src/main/resources/gui/favicon.ico"));
	}

	public static void main(String[] args) throws Exception {
		InkstoneUploadMainWindow main = new InkstoneUploadMainWindow(Display.getDefault().getActiveShell());
		main.open();
	}
}
