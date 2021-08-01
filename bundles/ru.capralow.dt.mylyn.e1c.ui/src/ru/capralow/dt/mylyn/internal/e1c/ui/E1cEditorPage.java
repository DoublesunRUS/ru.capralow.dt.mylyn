/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c.ui;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * @author Aleksandr Kapralov
 *
 */
public class E1cEditorPage
    extends AbstractTaskEditorPage
{
    public E1cEditorPage(TaskEditor editor, String connectorKind)
    {
        super(editor, connectorKind);
        setNeedsPrivateSection(false);
        setNeedsSubmitButton(false);
        setNeedsAddToCategory(false);
    }

    @Override
    protected Set<TaskEditorPartDescriptor> createPartDescriptors()
    {
        Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();

        // remove unnecessary default editor parts
        for (Iterator<TaskEditorPartDescriptor> it = descriptors.iterator(); it.hasNext();)
        {
            TaskEditorPartDescriptor descriptor = it.next();
            if (descriptor.getId().equals(ID_PART_PLANNING))
            {
                it.remove();
            }
        }
        return descriptors;
    }

}
