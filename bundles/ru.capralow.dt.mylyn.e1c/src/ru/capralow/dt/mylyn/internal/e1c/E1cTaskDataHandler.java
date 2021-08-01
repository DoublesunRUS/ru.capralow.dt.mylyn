/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import ru.capralow.dt.mylyn.e1c.E1cConnector;
import ru.capralow.dt.mylyn.e1c.E1cError;
import ru.capralow.dt.mylyn.e1c.IE1cConnection;

/**
 * @author Aleksandr Kapralov
 *
 */
public class E1cTaskDataHandler
    extends AbstractTaskDataHandler
{

    @Override
    public TaskAttributeMapper getAttributeMapper(TaskRepository repository)
    {
        try
        {
            return ConnectionManager.get(repository).getMapper();
        }
        catch (Exception e)
        {
            e.getStackTrace();
            return null;
        }
    }

    @Override
    public boolean initializeTaskData(TaskRepository repository, TaskData data, ITaskMapping initializationData,
        IProgressMonitor monitor) throws CoreException
    {
        createDefaultAttributes(data, false);

        IE1cConnection connection = ConnectionManager.get(repository);
        TaskAttribute root = data.getRoot();

        return true;
    }

    public TaskData downloadTaskData(TaskRepository repository, String taskId) throws CoreException
    {
        try
        {
            IE1cConnection connection = ConnectionManager.get(repository);
            E1cError error = connection.getError(getRefKey(taskId));

            return createTaskDataFromE1cError(error, repository);
        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    @Override
    public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
        Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException
    {
        return null;
    }

    public TaskData createTaskDataFromE1cError(E1cError error, TaskRepository repository) throws CoreException
    {
        IE1cConnection connection = ConnectionManager.get(repository);
        TaskData data = new TaskData(connection.getMapper(), E1cConnector.CONNECTOR_KIND, repository.getUrl(),
            error.refKey.replace("-", "")); //$NON-NLS-1$ //$NON-NLS-2$

        createDefaultAttributes(data, true);

        TaskAttribute root = data.getRoot();
        root.getAttribute(E1cAttribute.CODE.getTaskKey()).setValue(error.code);
        root.getAttribute(E1cAttribute.DESCRIPTION.getTaskKey()).setValue(error.description);
        root.getAttribute(E1cAttribute.PROJECT.getTaskKey()).setValue(error.project.description);
        root.getAttribute(E1cAttribute.CREATED.getTaskKey()).setValue(String.valueOf(error.createdAt.getTime()));
        root.getAttribute(E1cAttribute.UPDATED.getTaskKey()).setValue(String.valueOf(error.modifiedAt.getTime()));

        if (error.completedAt != null)
        {
            root.getAttribute(E1cAttribute.COMPLETED.getTaskKey())
                .setValue(String.valueOf(error.completedAt.getTime()));
        }

        return data;
    }

    private void createDefaultAttributes(TaskData data, boolean existingTask)
    {
        createAttribute(data, E1cAttribute.CODE);
        createAttribute(data, E1cAttribute.DESCRIPTION);
        createAttribute(data, E1cAttribute.PROJECT);
        createAttribute(data, E1cAttribute.CREATED);
        createAttribute(data, E1cAttribute.UPDATED);
        createAttribute(data, E1cAttribute.COMPLETED);
    }

    private void createAttribute(TaskData data, E1cAttribute attribute)
    {
        TaskAttribute attr = data.getRoot().createAttribute(attribute.getTaskKey());
        TaskAttributeMetaData metaData = attr.getMetaData();
        metaData.setType(attribute.getType());
        metaData.setKind(attribute.getKind());
        metaData.setLabel(attribute.toString());
        metaData.setReadOnly(attribute.isReadOnly());
    }

    private static String getRefKey(String taskId)
    {
        return String.join("-", taskId.substring(0, 8), taskId.substring(8, 12), taskId.substring(12, 16), //$NON-NLS-1$
            taskId.substring(16, 20), taskId.substring(20));
    }
}
