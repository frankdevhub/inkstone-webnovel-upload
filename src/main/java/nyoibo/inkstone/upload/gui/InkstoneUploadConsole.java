package nyoibo.inkstone.upload.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * <p>Title:InkstoneUploadConsole.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-19 18:06
 */

public class InkstoneUploadConsole extends Dialog {
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text txtNewText;
	private Text txtNewText_1;
	private Text txtNewText_2;

	public InkstoneUploadConsole(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;

		Button btnNewButton = formToolkit.createButton(container, "New Button", SWT.NONE);

		txtNewText = formToolkit.createText(container, "New Text", SWT.NONE);
		txtNewText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnNewButton_1 = formToolkit.createButton(container, "New Button", SWT.NONE);

		txtNewText_1 = formToolkit.createText(container, "New Text", SWT.NONE);
		txtNewText_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel = new Label(container, SWT.NONE);
		formToolkit.adapt(lblNewLabel, true, true);
		lblNewLabel.setText("New Label");

		Link link = new Link(container, SWT.NONE);
		formToolkit.adapt(link, true, true);
		link.setText("<a>New Link</a>");

		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		formToolkit.adapt(lblNewLabel_1, true, true);
		lblNewLabel_1.setText("New Label");

		ProgressBar progressBar = new ProgressBar(container, SWT.NONE);
		GridData gd_progressBar = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_progressBar.widthHint = 344;
		progressBar.setLayoutData(gd_progressBar);
		formToolkit.adapt(progressBar, true, true);

		ScrolledComposite scrolledComposite = new ScrolledComposite(container,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
		gridData.widthHint = 367;
		gridData.heightHint = 205;
		scrolledComposite.setLayoutData(gridData);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setAlwaysShowScrollBars(true);

		txtNewText_2 = formToolkit.createText(scrolledComposite, "New Text", SWT.NONE);
		txtNewText_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		txtNewText_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		txtNewText_2.append("ssss");
		scrolledComposite.setContent(txtNewText_2);
		scrolledComposite.setMinSize(txtNewText_2.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 456);
	}
}
