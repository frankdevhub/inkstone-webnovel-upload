package nyoibo.inkstone.upload.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class InProgressDialog extends Dialog {
    public InProgressDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE | SWT.MAX;
    }

    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        return null;
    }

    public final int SELECT_MANNUALLY = 101;
    public final int TERMINAT = 402;

    protected void initializeBounds() {
        Composite comp = (Composite) getButtonBar();
        super.createButton(comp, SELECT_MANNUALLY, "Select Mannually", true);
        super.createButton(comp, TERMINAT, "Terminate", false);
        super.initializeBounds();
    }

    protected Point getInitialSize() {
        return new Point(300, 400);
    }

}
