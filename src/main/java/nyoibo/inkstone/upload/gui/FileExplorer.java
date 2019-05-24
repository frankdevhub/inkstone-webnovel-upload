package nyoibo.inkstone.upload.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

/**
 * <p>Title:FileExplorer.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-13 12:17
 */

public class FileExplorer{

	private SashForm sash;
	private TreeViewer tree;
	private TableViewer table;
	private SelectAction selectAction;
    private ChapterTable chapterTable;
	private Composite parent;
    
	public FileExplorer(Composite parent) {
		this.parent = parent;
		selectAction = new SelectAction();
		createContents(parent);
	}

	protected Control createContents(Composite parent) {
		sash = new SashForm(parent, SWT.SMOOTH);
		sash.setLayoutData(new GridData(GridData.FILL_BOTH));
		initTree();
		initTable();
		sash.setWeights(new int[] { 40, 60 });
		return parent;
	}

	private void initTable() {
		table = new TableViewer(sash);
		
		new TableColumn(table.getTable(), SWT.LEFT).setText("Name");
		new TableColumn(table.getTable(), SWT.LEFT).setText("Type");
		new TableColumn(table.getTable(), SWT.LEFT).setText("Size");
		new TableColumn(table.getTable(), SWT.LEFT).setText("Last Modify Date");
		for (int i = 0; i < table.getTable().getColumnCount(); i++) {
			table.getTable().getColumn(i).pack();
		}

		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);

		table.setContentProvider(new FileTableContentProvider());

		table.setLabelProvider(new FileTableLabelProvider());

		table.setSorter(new FileSorter());

		table.addDoubleClickListener(selectAction);

	}

	private void initTree() {
		tree = new TreeViewer(sash);
		tree.setContentProvider(new FileTreeContentProvider());
		tree.setLabelProvider(new FileTreeLabelProvider());
		tree.setInput("root");
		tree.addSelectionChangedListener(selectAction);
	};

	public class FileTreeContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object element) {
			return ((File) element).listFiles(new AllowOnlyFoldersFilter());
		}

		public Object[] getElements(Object element) {
			File[] roots = File.listRoots();
			List rootFolders = new ArrayList();
			for (int i = 0; i < roots.length; i++) {
				if (roots[i].isDirectory())
					rootFolders.add(roots[i]);
			}
			return rootFolders.toArray();
		}

		public boolean hasChildren(Object element) {
			Object[] obj = getChildren(element);
			return obj == null ? false : obj.length > 0;
		}

		public Object getParent(Object element) {
			return ((File) element).getParentFile();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	public class FileTreeLabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			File file = (File) element;
			if (file.isDirectory())
				return ImageFactory.loadImage(Display.getCurrent(), ImageFactory.FOLDER);
			return ImageFactory.loadImage(Display.getCurrent(), ImageFactory.FILE);
		}

		public String getText(Object element) {
			String text = ((File) element).getName();
			if (text.length() == 0) {
				text = ((File) element).getPath();
			}
			return text;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
			ImageFactory.dispose();
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

	}

	class FileTableContentProvider implements IStructuredContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			File file = (File) inputElement;
			return file.listFiles();
		}

	}

	class FileTableLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			File file = (File) element;
			if (columnIndex == 0) {
				if (file.isDirectory())
					return ImageFactory.loadImage(Display.getCurrent(), ImageFactory.FOLDER);
				else
					return ImageFactory.loadImage(Display.getCurrent(), ImageFactory.FILE);
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			File file = (File) element;
			if (columnIndex == 0)
				return file.getName();
			else if (columnIndex == 1) {
				if (file.isDirectory())
					return "folder";
				else
					return "file";
			} else if (columnIndex == 2) {
				if (file.isDirectory())
					return "";
				else
					return file.length() + " KB";
			} else if (columnIndex == 3) {
				Date date = new Date(file.lastModified());
				return date.toLocaleString();
			}
			return null;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
			ImageFactory.dispose();
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

	}

	public class AllowOnlyFoldersFilter implements FileFilter {
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}

	}

	public class FileSorter extends ViewerSorter {
		public int category(Object element) {
			return ((File) element).isDirectory() ? 0 : 1;
		}
	}

	public class SelectAction implements ISelectionChangedListener, IDoubleClickListener {

		public void selectionChanged(SelectionChangedEvent event) {
			table.setInput(getTreeSelection());
		}

		public void doubleClick(DoubleClickEvent event) {
			Object selection = getTableSelection();
			if (selection == null)
				return;
			File file = (File) selection;
			if (file.isFile()) {
				String selectFileName = file.getName();
				CompareChapterWindow.chapCacheName = selectFileName;
			} else if (file.isDirectory()) {
				table.setInput(selection);
			}

		}
	}

	public Object getTreeSelection() {
		IStructuredSelection selection = (IStructuredSelection) tree.getSelection();
		if (selection.size() != 1)
			return null;
		return selection.getFirstElement();
	}

	public Object getTableSelection() {
		IStructuredSelection selection = (IStructuredSelection) table.getSelection();
		if (selection.size() != 1)
			return null;
		return selection.getFirstElement();
	}
}
