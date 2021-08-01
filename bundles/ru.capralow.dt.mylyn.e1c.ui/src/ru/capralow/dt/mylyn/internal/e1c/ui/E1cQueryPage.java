/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c.ui;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Aleksandr Kapralov
 *
 */
public class E1cQueryPage
    extends AbstractRepositoryQueryPage
    implements IWizardPage
{

    private Button openButton;
    private Button closedButton;
    private Text titleText;
    private Text assigneeText;
    private Text newLabel;
    private Combo milestoneCombo;
    private TableViewer labelsViewer;

    private SelectionListener completeListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
            setPageComplete(isPageComplete());
        }
    };

    public E1cQueryPage(String pageName, TaskRepository taskRepository, IRepositoryQuery query)
    {
        super(pageName, taskRepository, query);
        setDescription("Specify your query");
        setPageComplete(false);
    }

    @Override
    public void createControl(Composite parent)
    {
        Composite displayArea = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).applyTo(displayArea);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(displayArea);

        if (!inSearchContainer())
        {
            Composite titleArea = new Composite(displayArea, SWT.NONE);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(titleArea);
            GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(titleArea);

            new Label(titleArea, SWT.NONE).setText("Query title");
            titleText = new Text(titleArea, SWT.SINGLE | SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(titleText);
            titleText.addModifyListener(new ModifyListener()
            {
                @Override
                public void modifyText(ModifyEvent e)
                {
                    setPageComplete(isPageComplete());
                }
            });
        }

        createOptionsArea(displayArea);

        createLabelsArea(displayArea);

        initialize();
        setControl(displayArea);
    }

    private void createOptionsArea(Composite parent)
    {
        Composite optionsArea = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(optionsArea);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(optionsArea);

        Composite statusArea = new Composite(optionsArea, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).applyTo(statusArea);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(statusArea);

        new Label(statusArea, SWT.NONE).setText("Query state");

        openButton = new Button(statusArea, SWT.CHECK);
        openButton.setSelection(true);
        openButton.setText("State opened");
        openButton.addSelectionListener(completeListener);

        closedButton = new Button(statusArea, SWT.CHECK);
        closedButton.setSelection(true);
        closedButton.setText("State closed");
        closedButton.addSelectionListener(completeListener);

        Label milestonesLabel = new Label(optionsArea, SWT.NONE);
        milestonesLabel.setText("Query milestone");

//        milestoneCombo = new Combo(optionsArea, SWT.DROP_DOWN | SWT.READ_ONLY);
//        GridDataFactory.fillDefaults().grab(true, false).applyTo(milestoneCombo);
//        IE1cConnection connection = ConnectionManager.getSafe(getTaskRepository());
//        if (connection != null)
//        {
//            milestoneCombo.add("");
//            for (GitlabMilestone s : connection.getMilestones())
//            {
//                milestoneCombo.add(s.getTitle());
//            }
//        }

        Label assigneeLabel = new Label(optionsArea, SWT.NONE);
        assigneeLabel.setText("Query asignee");

        assigneeText = new Text(optionsArea, SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(assigneeText);
    }

    private void createLabelsArea(Composite parent)
    {
        Group labelsArea = new Group(parent, SWT.NONE);
        labelsArea.setText("Query group labels");
        GridDataFactory.fillDefaults().grab(true, true).applyTo(labelsArea);
        GridLayoutFactory.swtDefaults().applyTo(labelsArea);

        labelsViewer = new TableViewer(labelsArea, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        GridDataFactory.fillDefaults().grab(true, true).hint(100, 80).applyTo(labelsViewer.getControl());
        labelsViewer.setContentProvider(ArrayContentProvider.getInstance());
        labelsViewer.setLabelProvider(new LabelProvider()
        {
            @Override
            public Image getImage(Object element)
            {
                return null; //GitlabImages.IMAGE_LABEL;
            }
        });

        newLabel = new Text(labelsArea, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(newLabel);

        Composite btnArea = new Composite(labelsArea, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(btnArea);
        GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(true).applyTo(btnArea);

        final Button btnAdd = new Button(btnArea, SWT.BORDER);
        btnAdd.setText("Query new label regex");
        GridDataFactory.fillDefaults().grab(true, false).applyTo(btnAdd);

        Button btnRemove = new Button(btnArea, SWT.BORDER);
        btnRemove.setText("Query remove label regex");
        GridDataFactory.fillDefaults().grab(true, false).applyTo(btnRemove);

        btnAdd.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                String[] split = newLabel.getText().split(",");
                for (String s : split)
                {
                    if (s.trim().length() > 0)
                    {
                        labelsViewer.add(s.trim());
                    }
                }
                newLabel.setText("");
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        });

        btnRemove.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                removeSelection();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        });

        newLabel.addFocusListener(new FocusListener()
        {

            @Override
            public void focusLost(FocusEvent e)
            {
                getShell().setDefaultButton(null);
                setPageComplete(isPageComplete());
            }

            @Override
            public void focusGained(FocusEvent e)
            {
                getShell().setDefaultButton(btnAdd);
            }
        });

        labelsViewer.getTable().addKeyListener(new KeyListener()
        {

            @Override
            public void keyReleased(KeyEvent e)
            {
                if (e.keyCode == SWT.DEL)
                {
                    removeSelection();
                }
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
            }
        });
    }

    private void initialize()
    {
        IRepositoryQuery query = getQuery();
        if (query == null)
        {
            return;
        }

        titleText.setText(query.getSummary());
        assigneeText.setText(query.getAttribute("assignee"));
        milestoneCombo.setText(query.getAttribute("milestone"));

        openButton.setSelection(Boolean.parseBoolean(query.getAttribute("opened")));
        closedButton.setSelection(Boolean.parseBoolean(query.getAttribute("closed")));

        for (String label : query.getAttribute("labels").split(","))
        {
            if (label.trim().length() > 0)
            {
                labelsViewer.add(label.trim());
            }
        }

    }

    private void removeSelection()
    {
        StructuredSelection selection = (StructuredSelection)labelsViewer.getSelection();
        if (!selection.isEmpty())
        {
            labelsViewer.remove(selection.toArray());
        }
    }

    @Override
    public String getQueryTitle()
    {
        return titleText != null ? titleText.getText() : null;
    }

    @Override
    public void applyTo(IRepositoryQuery query)
    {
        query.setSummary(titleText.getText());
        query.setAttribute("assignee", assigneeText.getText());
        query.setAttribute("milestone", milestoneCombo.getText());
        query.setAttribute("opened", "" + openButton.getSelection());
        query.setAttribute("closed", "" + closedButton.getSelection());

        ArrayList<String> labels = new ArrayList<>();

        for (TableItem i : labelsViewer.getTable().getItems())
        {
            labels.add(i.getText());
        }
        query.setAttribute("labels", String.join(",", labels));
    }

}
