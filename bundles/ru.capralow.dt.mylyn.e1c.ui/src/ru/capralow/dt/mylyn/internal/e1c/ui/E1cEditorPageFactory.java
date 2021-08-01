/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c.ui;

import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

import ru.capralow.dt.mylyn.e1c.E1cConnector;

/**
 * @author Aleksandr Kapralov
 *
 */
public class E1cEditorPageFactory
    extends AbstractTaskEditorPageFactory
{

    @Override
    public boolean canCreatePageFor(TaskEditorInput input)
    {
        if (input.getTask().getConnectorKind().equals(E1cConnector.CONNECTOR_KIND))
        {
            return true;
        }

        else if (TasksUiUtil.isOutgoingNewTask(input.getTask(), E1cConnector.CONNECTOR_KIND))
        {
            return true;
        }

        return false;
    }

    @Override
    public IFormPage createPage(TaskEditor editor)
    {
        return new E1cEditorPage(editor, E1cConnector.CONNECTOR_KIND);
    }

    @Override
    public int getPriority()
    {
        return 0;
    }

    @Override
    public Image getPageImage()
    {
        return CommonImages.getImage(TasksUiImages.TASK);
    }

    @Override
    public String getPageText()
    {
        return "1С Ошибка";
    }

    @Override
    public String[] getConflictingIds(TaskEditorInput input)
    {
        return new String[] { ITasksUiConstants.ID_PAGE_PLANNING };
    }

}
