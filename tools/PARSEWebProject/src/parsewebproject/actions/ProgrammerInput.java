package parsewebproject.actions;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import pw.common.CommonConstants;



public class ProgrammerInput extends InputDialog {
	
	public ProgrammerInput(Shell shell, String selectedText) {
		super(shell, "Input", "GiveInput", selectedText, null);
		CommonConstants.bStartAction = false;
	}
	
	
	public void buttonPressed(int buttonId) 
	{
		
		
		if(buttonId == IDialogConstants.OK_ID)
		{
			String userQuery = getText().getText();
			
			if(userQuery == null || userQuery.equals(""))
				return;
			
			
			
			int indexOfArrow = userQuery.indexOf("->");
			if(indexOfArrow == -1)
			{
				CommonConstants.DestinationObject = userQuery.trim();
				CommonConstants.sourceObject = "";
			}
			else
			{
				CommonConstants.sourceObject = userQuery.substring(0, indexOfArrow).trim();
				CommonConstants.DestinationObject = userQuery.substring(indexOfArrow + 2, userQuery.length()).trim();
			}	
					
			CommonConstants.bStartAction = true;
		}
		super.buttonPressed(buttonId);
	}
}
