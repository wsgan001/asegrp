package parsewebproject.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.util.TreeSet;

import java.util.logging.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;

import pw.code.analyzer.SequenceStore;
import pw.common.CommonConstants;
import pw.main.PARSEWeb;

public class PARSEWebResultView extends ViewPart {

	public static final String ID = "parsewebproject.actions.PARSEWebResultView";
	
	public Logger logger = Logger.getLogger("PARSEWebResultView");
	
	String directoryName;
	
    public static final Object[] EMPTY = new Object[0];

    private TableTreeViewer viewer;

    private Label label;
    private Table table;
    
    public TreeSet resultSet = null;
    
    class ViewContentProvider implements ITreeContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
        	
        	if(resultSet == null)
        	{
        		return EMPTY;
        	}
        	
        	if(resultSet.size() <= CommonConstants.MAX_NUM_SEQUENCE_OUTPUT)
        		return resultSet.toArray();
        	else
        	{
        		Object[] objArray = resultSet.toArray();
        		Object[] returnObjArr = new Object[CommonConstants.MAX_NUM_SEQUENCE_OUTPUT];
        		System.arraycopy(objArray, 0, returnObjArr, 0, CommonConstants.MAX_NUM_SEQUENCE_OUTPUT);
        		return returnObjArr;
        	}
        }

        public Object[] getChildren(Object parent) {
            
        	if(parent instanceof SequenceStore)
        	{
        		String result[];
        		SequenceStore ss = (SequenceStore) parent;
        		String sequence = ss.sequence;
        		
        		StringTokenizer st = new StringTokenizer(sequence, "\n");
        		result = new String[st.countTokens()];
        		int counter = 0;
        		while(st.hasMoreTokens())
        		{
        			result[counter++] = st.nextToken();
        		}
        		
        		return result;
        		
        	}
        	
        	return EMPTY;
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
        	
        	if(element instanceof SequenceStore)
        	{
        		return true;
        	}
        	
            return false;
        }
    }

    class ViewLabelProvider extends LabelProvider implements
            ITableLabelProvider {

        public String getColumnText(Object obj, int index) {
        	
        	if(obj instanceof SequenceStore)
        	{
        		SequenceStore ss = (SequenceStore) obj;
        		
        		switch (index) {
        			case 0: 
        				return "File: " + ss.javaFileName + " Method: " + ss.methodName;
        			case 1: 
        				return "" + ss.rank;
        			case 2: 
        				return "" + ss.numOfTimes;
        			case 3: 
        				return "" + ss.numOfMethodCalls;
        			case 5:
        				return "" + ss.numUndefinedVars;
        			case 4:
        				if(ss.confidenceLevel)
        					return "High";
        				else
        					return "Low";
        			default: 
        				return "";
        		}
        	}
        	else if(obj instanceof String)
        	{
        		switch (index) {
    			case 0: 
    				return (String) obj;
    			case 1: 
    				return "";
    			case 2: 
    				return "";
    			case 3: 
    				return "";
    			default: 
    				return "";
    		}
        	}
            return "None";
        }

        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
        }

    }
	

	public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout(3, false);
        parent.setLayout(layout);

        //ToolBar bar = new ToolBar(parent, SWT.FLAT);

        GridData sData = new GridData(SWT.LEFT);
        sData.widthHint = 300;

        label = new Label(parent, SWT.HORIZONTAL);
        label.setText("No active search");
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        viewer = new TableTreeViewer(parent, SWT.FULL_SELECTION | SWT.SINGLE
                | SWT.BALLOON);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 3;

        viewer.getTableTree().setLayoutData(data);
        table = viewer.getTableTree().getTable();
        table.setLayoutData(data);
        table.setLinesVisible(true);
        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.setText("File Name and Method Sequences");
        column.setWidth(800);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Rank");
        column.setWidth(45);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Frequency");
        column.setWidth(65);

        column = new TableColumn(table, SWT.LEFT);
        column.setText("Length");
        column.setWidth(50);
        
        column = new TableColumn(table, SWT.LEFT);
        column.setText("Confidence");
        column.setWidth(50);
        
        column = new TableColumn(table, SWT.LEFT);
        column.setText("Undefined");
        column.setWidth(30);
        
        
        table.setHeaderVisible(true);

        table.setHeaderVisible(true);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.setInput(getViewSite());
        
        hookDoubleClickAction();

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		viewer.getControl().setFocus();
	}

	public TableTreeViewer getViewer() {
		return viewer;
	}
	
	public void search()
	{
		//Executing the PARSEWeb tool in the background
		try {
            PlatformUI.getWorkbench().getProgressService().run(true, true,
                    new IRunnableWithProgress() {

                        public void run(IProgressMonitor monitor)
                                throws InvocationTargetException,
                                InterruptedException {
                            try {
                            	
                            	logger.info("Started the search...");
                            	
                                monitor.beginTask("Searching", 6);
                                
                                
                                directoryName = CommonConstants.baseDirectoryName + CommonConstants.FILE_SEP + System.currentTimeMillis();
                                
                                resultSet = new PARSEWeb().initiateProcess(directoryName, CommonConstants.sourceObject, CommonConstants.DestinationObject, monitor);
                                
                                monitor.done();
                                Display.getDefault().asyncExec(new Runnable() {
                                    public void run() {
                                    	
                                    	System.out.println("Inside the inner most run method");
                                    	
                                    	label.setText(CommonConstants.sourceObject + " -> " + CommonConstants.DestinationObject);
                                    	viewer.refresh(true);
                                    }
                                });
                            } catch (final Exception e) {
                                logger.info(e.getStackTrace().toString());
                                Display.getDefault().asyncExec(new Runnable() {
                                    public void run() {
                                        MessageDialog.openError(new Shell(),
                                                "Search Error", e.getMessage());
                                    }
                                });

                            }
                        }
                    });
            
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

	
    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
            	
            	try
            	{
	                IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
	                if (structuredSelection.getFirstElement() instanceof SequenceStore) {
	                    SequenceStore entry = (SequenceStore) (structuredSelection.getFirstElement());
	                    
	                    IWorkspace ws = ResourcesPlugin.getWorkspace();
	                    IProject project = ws.getRoot().getProject("External Files");
	                    if (!project.exists())
	                       project.create(null);
	                    if (!project.isOpen())
	                       project.open(null);
	                    File fileObj = new File(directoryName + "\\" + entry.javaFileName);
	                    IStorage storage = new LocalFileStorage(fileObj);
	            		IPath location = storage.getFullPath();
	                    IFile file = project.getFile(location.lastSegment());
	                    try
	                    {
	                    	file.createLink(location, IResource.NONE, null);
	                    }
	                    catch(Exception ex)
	                    {
	                    	if(ex.getMessage().indexOf("exists") == -1)
	                    	{
	                    		throw new Exception();
	                    	}
	                    }
	                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	                    FileEditorInput fei = new FileEditorInput(file);
	                    if (page != null)
	                       page.openEditor(fei,"org.eclipse.ui.editors.text.texteditor");
	                    
	                }
            	}
            	catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
            }
        });
    }

}
