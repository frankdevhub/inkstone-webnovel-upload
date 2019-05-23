package nyoibo.inkstone.upload.gui;

import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class ChapterTable{
	private ViewForm viewForm = null;
	private ToolBar toolBar = null;
	private Composite composite = null;
	private Table table = null;
	private Menu menu = null;

	public ChapterTable(Composite parent) {
		this.composite = parent;
		createViewForm(parent);
		createToolBar();
		createMenu(parent);
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
		final ToolItem add = new ToolItem(toolBar, SWT.PUSH);
		add.setText("add");
		add.setImage(ImageFactory.loadImage(toolBar.getDisplay(), ImageFactory.ADD_OBJ));
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
				if (event.widget == add) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(new String[] { "第几章", "Chapter" });
					bindEditors();
				}

				else if (event.widget == del) {
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
				}
			}

		};
		add.addListener(SWT.Selection, listener);
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
						text.setText(CompareChapterWindow.chapCacheName);
					}
				});
			}
		}
	}

	private void getTableValues() {
		CompareChapterWindow.chapterList = new HashedMap<String, String>();
		TableItem[] items = table.getItems();

		if (items.length < 1)
			return;
		for (TableItem item : items) {
			CompareChapterWindow.chapterList.put(item.getText(0), item.getText(1));
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

		String[] tableHeader = { "CN_ChapterName", "EN_ChapterName" };
		for (int i = 0; i < tableHeader.length; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setText(tableHeader[i]);
			tableColumn.setMoveable(true);
		}
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "A1", "A1" });
		for (int i = 0; i < tableHeader.length; i++) {
			table.getColumn(i).pack();
		}

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

}
