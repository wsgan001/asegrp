package rpt.ncsu.transaction;

import java.io.OutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class MyConsole extends ViewPart {
	public static final String CONSOLE_ID = "rpt.ncsu.transaction.myConsole";
	private Text text;

	public MyConsole() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		text = new Text(parent, SWT.READ_ONLY | SWT.MULTI);
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				if (text.isDisposed())
					return;
				text.append(String.valueOf((char) b));
			}
		};
		final PrintStream oldOut = System.out;
		System.setOut(new PrintStream(out));
		text.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				System.setOut(oldOut);
			}
		});
	}

	@Override
	public void setFocus() {
		text.setFocus();
	}
}
