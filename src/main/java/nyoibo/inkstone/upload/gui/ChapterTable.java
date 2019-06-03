package nyoibo.inkstone.upload.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.springframework.util.StringUtils;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;
import nyoibo.inkstone.upload.utils.FileZipUtils;
import nyoibo.inkstone.upload.utils.InkstoneRawHeaderUtils;

public class ChapterTable {
	private ViewForm viewForm = null;
	private ToolBar toolBar = null;
	private Composite composite = null;
	private Table table = null;
	private Menu menu = null;
	private Composite parent = null;
	private String chapterFilePath = null;
	private String saveExcelPath;

	private final Logger LOGGER = LoggerFactory.getLogger(ChapterTable.class);

	public ChapterTable(Composite parent, String filePath) throws Exception {
		this.composite = parent;
		this.parent = parent;
		this.chapterFilePath = filePath;
		createViewForm(parent);
		createToolBar();
		createMenu(parent);
		createData(filePath);
	}

	private void cleanExclusiveZip(String filePath) throws IOException {
		File file = new File(filePath);
		File[] fileList = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf('.') > 0) {
					int lastIndex = name.lastIndexOf('.');
					String str = name.substring(lastIndex);
					if (!str.equals(".zip")) {
						return true;
					}
				}
				return false;
			}

		});

		final int filterCount = fileList.length;

		class DeleteProgressMonitorDialog {
			private void showDialog() {
				try {
					ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(
							Display.getCurrent().getActiveShell());
					IRunnableWithProgress runnalble = new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							monitor.beginTask("Delete history files ...", filterCount);
							double step = 0;
							boolean groupStep = false;
							int group = 0;
							System.out.println("ready to delete file count:" + filterCount);
							if (filterCount <= 100) {
								step = 100 / filterCount;
							} else {
								step = ((double) 1) / (filterCount / 100);
								groupStep = true;
								group = (int) ((double) 1 / step);
							}
							System.out.println("delete-step:" + step);
							for (int i = 0; i < fileList.length; i++) {
								File file = fileList[i];
								System.out.println("delete file:" + file.getName());
								try {
									FileUtils.forceDelete(file);
									if (groupStep) {
										if (i % group == 0)
											monitor.worked(1);
									} else {
										monitor.worked((int) step);
									}
									monitor.subTask(String.format("File deleted:[%s]", file.getAbsolutePath()));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

							if (monitor.isCanceled())
								throw new InterruptedException("Delete has been canceled mannually.");
						}
					};
					progressDialog.run(true, false, runnalble);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (null != fileList && fileList.length > 0) {
			DeleteProgressMonitorDialog dialog = new DeleteProgressMonitorDialog();
			dialog.showDialog();
		}

	}

	private void createData(String filePath) throws Exception {
		cleanExclusiveZip(filePath);
		new FileZipUtils().unZipDriveZip(filePath);
		regroupFolder(filePath);

		File[] fileList = new File(filePath).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf('.') > 0) {
					return false;
				}
				return true;
			}
		});

		for (File f : fileList) {
			System.out.println("delete-folder:" + f.getAbsolutePath());
			FileUtils.forceDelete(f);
		}

		wrapDataMap(filePath);
	}

	private void wrapDataMap(String filePath) throws Exception {
		File dataFile = new File(filePath);
		File[] dataFileList = dataFile.listFiles();
		class CompareProgressMonitorDialog {

			private void showDialog() {
				try {
					int fileCount = dataFileList.length;
					Map<String, String> compareList = new HashMap<String, String>();
					Map<String, String> chapterList = new HashMap<String, String>();

					ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
					IRunnableWithProgress runnalble = new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							monitor.beginTask("Scanning Chapter Files ...", fileCount);
							double step = 0;
							boolean groupStep = false;
							int group = 0;
							if (fileCount <= 100) {
								step = 100 / dataFileList.length;
							} else {
								step = ((double) 1) / (dataFileList.length / 100);
								groupStep = true;
								group = (int) (((double) 1) / (step));
							}
							for (int i = 0; i < fileCount && !monitor.isCanceled(); i++) {
								File current = dataFileList[i];
								String fileName = current.getName();
								String guessCHName;
								try {
									guessCHName = InkstoneRawHeaderUtils.convertRawCNHeader(fileName);
									compareList.put(guessCHName, fileName);
									chapterList.put(fileName, current.getAbsolutePath());
									if (groupStep) {
										if (i % group == 0)
											monitor.worked(1);
									} else {
										monitor.worked((int) step);
									}
									monitor.subTask(
											String.format("Scanning complete:[%s]", dataFileList[i].getAbsolutePath()));
									parent.getDisplay().asyncExec(new Runnable() {
										@Override
										public void run() {
											TableItem item = new TableItem(table, SWT.NONE);
											item.setText(new String[] { guessCHName, fileName });
										}
									});
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							CompareChapterWindow.compareList = compareList;
							CompareChapterWindow.chapterFileList = chapterList;

							monitor.done();
							if (monitor.isCanceled())
								throw new InterruptedException("Scanning has been canceled mannually.");
						}
					};
					dialog.run(true, false, runnalble);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		CompareProgressMonitorDialog monitorDialog = new CompareProgressMonitorDialog();
		System.out.println("show scanning zipped file moinitor");
		monitorDialog.showDialog();
	}

	private void createViewForm(Composite parent) {
		viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setTopCenterSeparate(true);
		createToolBar();
		viewForm.setTopLeft(toolBar);
		createComposite();
		viewForm.setContent(composite);
	}

	private void createToolBar() {
		toolBar = new ToolBar(viewForm, SWT.FLAT);
		final ToolItem edit = new ToolItem(toolBar, SWT.PUSH);
		edit.setText("edit");
		edit.setImage(ImageFactory.loadImage(toolBar.getDisplay(), ImageFactory.ADD_OBJ));
		final ToolItem del = new ToolItem(toolBar, SWT.PUSH);
		del.setText("delete");
		del.setImage(ImageFactory.loadImage(toolBar.getDisplay(), ImageFactory.DELETE_OBJ));
		final ToolItem back = new ToolItem(toolBar, SWT.PUSH);
		back.setText("back");
		back.setImage(ImageFactory.loadImage(toolBar.getDisplay(), ImageFactory.BACKWARD_NAV));
		final ToolItem forward = new ToolItem(toolBar, SWT.PUSH);
		forward.setText("forward");
		forward.setImage(ImageFactory.loadImage(toolBar.getDisplay(), ImageFactory.FORWARD_NAV));
		final ToolItem save = new ToolItem(toolBar, SWT.PUSH);
		save.setText("save");
		save.setImage(ImageFactory.loadImage(toolBar.getDisplay(), ImageFactory.SAVE_EDIT));

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == edit) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(new String[] { "", "" });
					bindEditors();
				} else if (event.widget == del) {
					TableItem[] items = table.getItems();
					for (int i = 0; i < items.length; i++) {
						if (!items[i].getChecked())
							continue;
						int index = table.indexOf(items[i]);
						if (index < 0)
							continue;
						table.remove(index);
					}
				} else if (event.widget == back) {
					int selectedRow = table.getSelectionIndex();
					if (selectedRow > 0)
						table.setSelection(selectedRow - 1);
				} else if (event.widget == forward) {
					int selectedRow = table.getSelectionIndex();
					if (selectedRow > -1 && selectedRow < table.getItemCount() - 1)
						table.setSelection(selectedRow + 1);
				} else if (event.widget == save) {
					getTableValues();
					try {
						saveExcelFile();
						FileOutputStream fos = new FileOutputStream(InkstoneUploadConsole.configPropertiesPath, false);
						Properties usrConfigPro = new Properties();
						usrConfigPro.setProperty(InkstoneUploadMainWindow.CHAPTER_PATH, chapterFilePath);
						usrConfigPro.setProperty(InkstoneUploadMainWindow.CHAPTER_EXCEL, saveExcelPath);

						usrConfigPro.store(fos, "usr");
						fos.flush();
						fos.close();

						CompareChapterWindow.useSaved = true;

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		};
		edit.addListener(SWT.Selection, listener);
		del.addListener(SWT.Selection, listener);
		back.addListener(SWT.Selection, listener);
		forward.addListener(SWT.Selection, listener);
		save.addListener(SWT.Selection, listener);
	}

	private void createComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite = new Composite(viewForm, SWT.NONE);
		composite.setLayout(gridLayout);
		createTable();
		bindEditors();
	}

	private void bindEditors() {
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < 2; j++) {
				final TableEditor editor = new TableEditor(table);
				final Text text = new Text(table, SWT.NONE);
				final int index = j;
				text.setText(items[i].getText(index));
				editor.grabHorizontal = true;
				editor.setEditor(text, items[i], index);
				text.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						editor.getItem().setText(index, text.getText());
					}

				});

				text.addMouseListener(new MouseListener() {
					@Override
					public void mouseUp(MouseEvent e) {
					}

					@Override
					public void mouseDown(MouseEvent e) {
					}

					@Override
					public void mouseDoubleClick(MouseEvent e) {
						String tempChapter = CompareChapterWindow.chapCacheName;
						if (tempChapter != null) {
							text.setText(CompareChapterWindow.chapCacheName);
						}
					}
				});
			}
		}
	}

	private void saveExcelFile() throws IOException {
		String saveFileName = null;
		String savePath = null;
		DirectoryDialog folderdlg = new DirectoryDialog(new Shell());
		folderdlg.setText("Select Save Path");
		folderdlg.setFilterPath("SystemDrive");
		folderdlg.setMessage("Please select your save excel path");
		String selecteddir = folderdlg.open();
		if (selecteddir == null) {
			return;
		} else {
			savePath = selecteddir + "\\";
			saveFileName = Long.toString(System.currentTimeMillis()) + "ChapAutoList.xls";
			savePath = savePath + saveFileName;

			LOGGER.begin().headerAction(MessageMethod.EVENT).info(savePath);
			CompareChapterWindow.comaprePath = savePath;
		}
		if (savePath != null) {
			getTableValues();
			getHSSFWorkbook(savePath, CompareChapterWindow.compareList);
		}
		this.saveExcelPath = savePath;
	}

	private void getTableValues() {
		CompareChapterWindow.compareList = new HashedMap<String, String>();
		TableItem[] items = table.getItems();

		if (items.length < 1)
			return;
		for (TableItem item : items) {
			CompareChapterWindow.compareList.put(item.getText(0), item.getText(1));
		}
	}

	private void createTable() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = SWT.FILL;

		table = new Table(composite, SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLayoutData(gridData);
		table.setLinesVisible(true);

		String[] tableHeader = { "RAW_ChapterName", "EN_ChapterName" };
		for (int i = 0; i < tableHeader.length; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setText(tableHeader[i]);
			tableColumn.setMoveable(true);
		}

		for (int i = 0; i < tableHeader.length; i++) {
			table.getColumn(i).pack();
		}

		final TableCursor cursor = new TableCursor(table, SWT.NONE);
		final ControlEditor editor = new ControlEditor(cursor);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		cursor.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				final Text text = new Text(cursor, SWT.NONE);
				TableItem row = cursor.getRow();
				int column = cursor.getColumn();
				text.setText(row.getText(column));
				text.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if (e.character == SWT.CR) {
							TableItem row = cursor.getRow();
							int column = cursor.getColumn();
							row.setText(column, text.getText());
							text.dispose();
						}
						if (e.character == SWT.ESC) {
							text.dispose();
						}
					}
				});

				text.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						text.dispose();
					}
				});
				editor.setEditor(text);
				text.setFocus();
			}

			public void widgetSelected(SelectionEvent e) {
				table.setSelection(new TableItem[] { cursor.getRow() });
			}

		});

	}

	private void createMenu(Composite parent) {
		menu = new Menu(parent.getShell(), SWT.POP_UP);
		table.setMenu(menu);

		MenuItem del = new MenuItem(menu, SWT.PUSH);
		del.setText("delete");
		del.setImage(ImageFactory.loadImage(parent.getShell().getDisplay(), ImageFactory.DELETE_EDIT));

		del.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				table.remove(table.getSelectionIndices());
			}
		});
	}

	private HSSFWorkbook getHSSFWorkbook(String path, Map<String, String> values) throws IOException {
		String[] title = new String[] { "CN_chapterName", "EN_chapterName" };
		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFSheet sheet = wb.createSheet("compareSheet");

		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		HSSFCell cell = null;

		for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}

		int rowNum = 1;
		for (Entry<String, String> entry : values.entrySet()) {
			String cnName = entry.getKey();
			String enName = entry.getValue();
			if (!StringUtils.isEmpty(cnName) && !StringUtils.isEmpty(enName)) {
				row = sheet.createRow(rowNum);
				row.createCell(0).setCellValue(cnName);
				row.createCell(1).setCellValue(enName);
			}
			rowNum++;
		}

		FileOutputStream fos = new FileOutputStream(new File(path));
		wb.write(fos);
		return wb;
	}

	public static void regroupFolder(String path) throws IOException {
		regroupFolder(new File(path), path);
	}

	private static void regroupFolder(File root, String destPath) throws IOException {
		File file = root;
		File[] listFiles = file.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			File current = listFiles[i];
			if (current.isDirectory()) {
				regroupFolder(current, destPath);
			} else {
				FileUtils.copyFileToDirectory(current, new File(destPath));
				FileUtils.forceDelete(current);
			}
		}
	}
}
