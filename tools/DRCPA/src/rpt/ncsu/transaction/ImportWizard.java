package rpt.ncsu.transaction;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import rpt.ncsu.transaction.analysis.TraceAnalyzer;
import rpt.ncsu.transaction.util.TempInfo;
import rpt.ncsu.transaction.views.TransactionView;

public class ImportWizard extends Wizard implements IImportWizard {
	
	TraceDataSelectionPage tdPage;

	public ImportWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		final String tView_ID = "rpt.ncsu.transaction.views.TransactionView";
		String file = TempInfo.traceFile;
		TraceAnalyzer traceAnalyzer = new TraceAnalyzer(new String[] { file });
		file = file.substring(0, file.lastIndexOf(".xml"));
		file = file + file.substring(file.lastIndexOf("\\")) + ".txt";
		traceAnalyzer.identifyTransactions(file);

		TransactionView tView = (TransactionView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(tView_ID);
		if (tView != null)
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().hideView(tView);

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(tView_ID);
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		return true;
	}

	public void addPages() {
		tdPage = new TraceDataSelectionPage("Select Trace Data File");
		addPage(tdPage);		
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

}
