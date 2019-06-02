package nyoibo.inkstone.upload.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;
import nyoibo.inkstone.upload.utils.ThreadUtils;
import nyoibo.inkstone.upload.web.action.InkstoneUploadMainService;

public class InkstoneUploadConsole extends Dialog implements ConsoleTextAreaListener {
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Display display;
	private Text bookListText;
	private Text chapterListText;
	private Text chromeCacheText;

	private Text consoleTextArea;
	private Text webLinkText;
	private Text progressText;
	private Text weblinkUrl;
	private Text compareListText;
	private Button chromeCacheButton;

	private String chromeCachePath = "";
	private String bookListPath = "";
	private String chapterListPath = "";
	private String compareListPath = "";

	private ProgressBar progressBar;
	private Composite composite;
	private final int CONSOLE_OK_ID = 10;
	private final int CONSOLE_CANCEL_ID = 20;
	private InkstoneUploadMainService mainService;
	private Properties proHistory = new Properties();

	public static LinkedList<String> consoleStr = new LinkedList<String>();
	private static final String configPropertiesPath = "src/main/resources/usr.properties";
	public static volatile int num = 0;
	public static volatile boolean flag = false;
	public static boolean skipReadingExcel = true;

	public static final Logger LOGGER = LoggerFactory.getLogger(InkstoneUploadConsole.class);

	private void startToRunUploadService() throws Exception {
		LOGGER.begin().headerMethod(MessageMethod.EVENT).info("check configuration and start to upload novels");
		saveProperties();
		int textIsEmpty = 0;
		if (StringUtils.isEmpty(chromeCachePath))
			textIsEmpty++;
		System.out.println(chromeCachePath);
		if (StringUtils.isEmpty(bookListPath))
			textIsEmpty++;
		System.out.println(bookListPath);
		if (StringUtils.isEmpty(chapterListPath))
			textIsEmpty++;
		System.out.println(chapterListPath);
		if (StringUtils.isEmpty(compareListPath))
			textIsEmpty++;
		System.out.println(compareListPath);
		if (textIsEmpty > 0)
			throw new Exception("Please input all configuration.");

		this.mainService = new InkstoneUploadMainService(bookListPath, compareListPath, chapterListPath,
				chromeCachePath, false);
		mainService.rawUploadStart();

	}

	private void saveProperties() throws IOException {
		int change = 0;

		FileOutputStream fos = new FileOutputStream(configPropertiesPath, false);
		Properties usrConfigPro = new Properties();

		usrConfigPro.setProperty(InkstoneUploadMainWindow.BOOK_LIST_PATH, bookListPath);
		if ((null != proHistory.get(InkstoneUploadMainWindow.BOOK_LIST_PATH)
				&& !proHistory.get(InkstoneUploadMainWindow.BOOK_LIST_PATH).equals(bookListPath))
				|| null != proHistory.get(InkstoneUploadMainWindow.BOOK_LIST_MODIFY) && !proHistory
						.get(InkstoneUploadMainWindow.BOOK_LIST_MODIFY).equals(new File(bookListPath).lastModified()))
			change++;

		usrConfigPro.setProperty(InkstoneUploadMainWindow.CHAPTER_PATH, chapterListPath);
		if ((null != proHistory.get(InkstoneUploadMainWindow.CHAPTER_PATH)
				&& !proHistory.get(InkstoneUploadMainWindow.CHAPTER_PATH).equals(bookListPath))
				|| null != proHistory.get(InkstoneUploadMainWindow.CHAPTER_PATH_DATE)
						&& !proHistory.get(InkstoneUploadMainWindow.CHAPTER_PATH_DATE)
								.equals(new File(chapterListPath).lastModified()))
			change++;

		usrConfigPro.setProperty(InkstoneUploadMainWindow.CHAPTER_EXCEL, compareListPath);
		if ((null != proHistory.get(InkstoneUploadMainWindow.CHAPTER_EXCEL)
				&& !proHistory.get(InkstoneUploadMainWindow.CHAPTER_EXCEL).equals(compareListPath))
				|| null != proHistory.get(InkstoneUploadMainWindow.COMPARE_LIST_DATE)
						&& !proHistory.get(InkstoneUploadMainWindow.COMPARE_LIST_DATE)
								.equals(new File(compareListPath).lastModified()))
			change++;

		usrConfigPro.setProperty(InkstoneUploadMainWindow.CHROME_CACHE_PATH, chromeCachePath);

		if (change > 0)
			skipReadingExcel = false;

		usrConfigPro.store(fos, "usr");
		fos.flush();
		fos.close();

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("local usr configuration saved");
	}

	private void readProperties() throws IOException {

		Properties usrConfigPro = new Properties();
		InputStream in = new BufferedInputStream(new FileInputStream(configPropertiesPath));
		usrConfigPro.load(in);
		in.close();
		this.proHistory = usrConfigPro;
	}

	private void setTextFromConfiguration() {

		if (!StringUtils.isEmpty(proHistory.getProperty(InkstoneUploadMainWindow.BOOK_LIST_PATH))) {
			bookListText.setText(proHistory.getProperty(InkstoneUploadMainWindow.BOOK_LIST_PATH));
			this.bookListPath = bookListText.getText();
		}
		if (!StringUtils.isEmpty(proHistory.getProperty(InkstoneUploadMainWindow.CHAPTER_PATH))) {
			chapterListText.setText(proHistory.getProperty(InkstoneUploadMainWindow.CHAPTER_PATH));
			this.chapterListPath = chapterListText.getText();
		}

		if (!StringUtils.isEmpty(proHistory.getProperty(InkstoneUploadMainWindow.CHAPTER_EXCEL))) {
			compareListText.setText(proHistory.getProperty(InkstoneUploadMainWindow.CHAPTER_EXCEL));
			this.compareListPath = compareListText.getText();
		}

		if (!StringUtils.isEmpty(proHistory.getProperty(InkstoneUploadMainWindow.CHROME_CACHE_PATH))) {
			chromeCacheText.setText(proHistory.getProperty(InkstoneUploadMainWindow.CHROME_CACHE_PATH));
			this.chromeCachePath = chromeCacheText.getText();
		}

		LOGGER.begin().headerAction(MessageMethod.EVENT).info("read usr local configuration");
	}

	public InkstoneUploadConsole(Shell parentShell, Display display) throws IOException {
		super(parentShell);
		this.display = display;
		readProperties();

		new ChromCachTextUtils(display, chromeCacheText);
		ChromCachTextUtils.pushToChromCacheText(chromeCachePath);
		new ChapterTextUtils(display, chapterListText);
		ChapterTextUtils.pushToChapterText(chapterListPath);
		new CompareTextUtils(display, compareListText);
		CompareTextUtils.pushToCompareText(compareListPath);
		new BookListTextUtils(display, bookListText);
		BookListTextUtils.pushToBookListLink(bookListPath);

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		composite = container;
		display = composite.getDisplay();

		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 4;

		chromeCacheButton = new Button(container, SWT.NONE);
		GridData gdChromeCacheButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gdChromeCacheButton.widthHint = 423;
		chromeCacheButton.setLayoutData(gdChromeCacheButton);
		formToolkit.adapt(chromeCacheButton, true, true);
		chromeCacheButton.setText("Config Chrome Cache");
		chromeCacheButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog folderdlg = new DirectoryDialog(new Shell());
				folderdlg.setText("Config Chrome Broswer");
				folderdlg.setFilterPath("SystemDrive");
				folderdlg.setMessage("Please select your chrome cache folder");
				String selecteddir = folderdlg.open();
				if (selecteddir == null) {
					return;
				} else {
					chromeCachePath = selecteddir;
					chromeCacheText.setText(selecteddir);
				}

			}
		});

		chromeCacheText = new Text(container, SWT.BORDER);
		chromeCacheText.setEditable(false);
		chromeCacheText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		formToolkit.adapt(chromeCacheText, true, true);

		Button bookListButton = formToolkit.createButton(container, "Config Book List", SWT.NONE);
		GridData gdBookListButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gdBookListButton.widthHint = 425;
		bookListButton.setLayoutData(gdBookListButton);
		bookListButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = Display.getCurrent().getActiveShell();
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterPath(System.getProperty("user.dir"));

				dialog.setText("Please select bookList excel");
				dialog.setFilterExtensions(new String[] { "*.xls", "*.xlsx" });
				bookListPath = dialog.open();
				if (bookListPath == null) {
					return;
				} else {
					bookListText.setText(bookListPath);
				}
			}
		});

		bookListText = formToolkit.createText(container, "bookListText", SWT.NONE);
		bookListText.setEditable(false);
		bookListText.setText("");
		GridData gdBookListText = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
		gdBookListText.widthHint = 422;
		bookListText.setLayoutData(gdBookListText);

		Button compareListButton = formToolkit.createButton(container, "Config Raw->Trans Excel", SWT.NONE);
		GridData gdCompareListButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gdCompareListButton.widthHint = 425;
		compareListButton.setLayoutData(gdCompareListButton);
		compareListButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = Display.getCurrent().getActiveShell();
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterPath(System.getProperty("user.dir"));

				dialog.setText("Please select raw->trans excel");
				dialog.setFilterExtensions(new String[] { "*.xls", "*.xlsx" });
				compareListPath = dialog.open();
				if (compareListPath == null) {
					return;
				} else {
					compareListText.setText(compareListPath);
				}
			}
		});

		compareListText = formToolkit.createText(container, "compareListText", SWT.NONE);
		compareListText.setEditable(false);
		compareListText.setText("");
		GridData gdCompareListText = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
		gdCompareListText.widthHint = 422;
		compareListText.setLayoutData(gdCompareListText);

		Button chapterListButton = formToolkit.createButton(container, "Config Chapters ", SWT.NONE);
		GridData gdChapterListButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gdChapterListButton.widthHint = 423;
		chapterListButton.setLayoutData(gdChapterListButton);
		chapterListButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog folderdlg = new DirectoryDialog(new Shell());
				folderdlg.setText("Select Novel Chapters");
				folderdlg.setFilterPath("SystemDrive");
				folderdlg.setMessage("Please select novel chapter folder");
				String selecteddir = folderdlg.open();
				if (selecteddir == null) {
					return;
				} else {
					chapterListPath = selecteddir;
					chapterListText.setText(selecteddir);
				}
			}
		});

		chapterListText = formToolkit.createText(container, "chapterListText", SWT.NONE);
		chapterListText.setEditable(false);
		chapterListText.setText("");
		chapterListText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		webLinkText = formToolkit.createText(container, "webLinkText", SWT.NONE);
		webLinkText.setEnabled(false);
		webLinkText.setToolTipText("");
		webLinkText.setEditable(false);
		webLinkText.setText("Web Link ");
		webLinkText.setBackground(container.getBackground());
		webLinkText.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1));

		weblinkUrl = new Text(container, SWT.BORDER);
		weblinkUrl.setText("https://inkstone.webnovel.com/book");
		weblinkUrl.setEditable(false);
		GridData gdWeblinkUrl = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gdWeblinkUrl.widthHint = 186;
		weblinkUrl.setLayoutData(gdWeblinkUrl);

		formToolkit.adapt(weblinkUrl, true, true);

		progressText = formToolkit.createText(container, "progressText", SWT.NONE);
		progressText.setEnabled(false);
		progressText.setEditable(false);
		progressText.setText("Progress ");
		progressText.setBackground(container.getBackground());
		progressText.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1));

		progressBar = new ProgressBar(container, SWT.HORIZONTAL | SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_CYAN));
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		GridData gdProgressBar = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
		gdProgressBar.widthHint = 425;
		progressBar.setLayoutData(gdProgressBar);
		formToolkit.adapt(progressBar, true, true);

		ScrolledComposite scrolledComposite = new ScrolledComposite(container,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1);
		gridData.widthHint = 367;
		gridData.heightHint = 218;
		scrolledComposite.setLayoutData(gridData);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setAlwaysShowScrollBars(true);

		consoleTextArea = formToolkit.createText(scrolledComposite, "InkstoneConsoleTextArea", SWT.BORDER | SWT.MULTI);
		consoleTextArea.setEditable(false);
		consoleTextArea.setText("Waiting ......");
		consoleTextArea.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		consoleTextArea.setForeground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));

		scrolledComposite.setContent(consoleTextArea);
		scrolledComposite.setMinSize(consoleTextArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setTextFromConfiguration();

		return container;
	}

	private void handleError(Exception e, Display parent) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				new ErrorDialogUtils(parent).openErrorDialog("InkstoneUploadMainService Error", e);
			}
		});
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, CONSOLE_OK_ID, IDialogConstants.OK_LABEL, false);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				okButton.setEnabled(false);
				try {
					Runnable progress = new Runnable() {
						public void run() {
							for (; num < Integer.MAX_VALUE;) {
								if (!flag && (num == 0 || ++num % 2 == 0)) {
									display.asyncExec(new Runnable() {
										public void run() {
											if (progressBar.isDisposed())
												return;
											/*
											 * if (InkstoneUploadMainService.
											 * process
											 * .get(InkstoneUploadMainService.
											 * currentChapterName).equals(null))
											 * { progressBar.setSelection(0); }
											 * else { progressBar.setSelection(
											 * InkstoneUploadMainService.process
											 * .get(InkstoneUploadMainService.
											 * currentChapterName)); }
											 */
											if (consoleStr.size() != 0) {
												consoleTextArea.append("\n");
												String next = consoleStr.get(0);
												if (!StringUtils.isEmpty(next))
													consoleTextArea.append(next);
												consoleStr.remove(0);
											}

											flag = true;
										}
									});
								}
							}
						}
					};
					Thread progressThread = new Thread(progress);
					progressThread.setDaemon(true);
					progressThread.start();

					Runnable service = new Runnable() {
						@Override
						public void run() {
							try {
								display.asyncExec(new Runnable() {
									public void run() {
										chapterListPath = chapterListText.getText();
										bookListPath = bookListText.getText();
										chromeCachePath = chromeCacheText.getText();
										compareListPath = compareListText.getText();
									}
								});
								startToRunUploadService();
							} catch (Exception e) {
								e.printStackTrace();
								handleError(e, parent.getDisplay());
							}
						}
					};
					Thread serviceThread = new Thread(service);
					serviceThread.setDaemon(true);
					serviceThread.start();

					okButton.setEnabled(true);
				} catch (Exception e1) {
					e1.printStackTrace();
					okButton.setEnabled(true);
					new ErrorDialogUtils(parent.getDisplay()).openErrorDialog("InkstoneUploadMainService Error", e1);
				}

			}
		});
		Button cancelButton = createButton(parent, CONSOLE_CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean call = MessageDialog.openConfirm(parent.getDisplay().getActiveShell(), "Stop Upload",
						"Are you sure to stop upload ?");
				boolean exist = true;
				if (call) {
					Thread check = ThreadUtils.check("innkstone-novel-upload");
					if (check != null)
						check.interrupt();
					else {
						MessageDialog.openInformation(parent.getDisplay().getActiveShell(), "Stop Upload",
								"Upload Service has been shutdown mannually, there may have some novels been pushed"
										+ " to \"In Progress\" for the current shutdown.");
						exist = false;
					}
				}
				LOGGER.begin().headerAction(MessageMethod.EVENT)
						.info(String.format("Upload Thread exists:[%s]", exist));
			}

		});

	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 632);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("WebNovel Upload Console");
		newShell.setImage(new Image(null, "src/main/resources/gui/favicon.ico"));
	}

	@Override
	public void pushLog(String message) {
		InkstoneUploadConsole.consoleStr.add(message);
		InkstoneUploadConsole.flag = false;
	}

}
