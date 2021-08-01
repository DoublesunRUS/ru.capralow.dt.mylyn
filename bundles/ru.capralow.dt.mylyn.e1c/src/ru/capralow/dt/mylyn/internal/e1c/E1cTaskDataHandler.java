/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c;

import java.util.Date;
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
import ru.capralow.dt.mylyn.internal.e1c.model.E1cError;

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
            return ConnectionManager.get(repository).mapper;
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

        E1cConnection connection = ConnectionManager.get(repository);
        TaskAttribute root = data.getRoot();

        return true;
    }

    public TaskData downloadTaskData(TaskRepository repository, String taskId) throws CoreException
    {
        try
        {
            E1cConnection connection = ConnectionManager.get(repository);
            E1cError error = connection.getError(E1cConnector.getRefKey(taskId));

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
        E1cConnection connection = ConnectionManager.get(repository);
        TaskData data = new TaskData(connection.mapper, E1cConnector.CONNECTOR_KIND, repository.getUrl(),
            error.refKey.replace("-", "")); //$NON-NLS-1$ //$NON-NLS-2$

        createDefaultAttributes(data, true);

        Date modifiedAt = error.createdAt;
        if (error.protocol.length != 0)
        {
            modifiedAt = error.protocol[0].date;
        }

        TaskAttribute root = data.getRoot();
        root.getAttribute(E1cAttribute.CODE.getTaskKey()).setValue(error.code);
        root.getAttribute(E1cAttribute.DESCRIPTION.getTaskKey()).setValue(error.description);
        root.getAttribute(E1cAttribute.CREATED.getTaskKey()).setValue(String.valueOf(error.createdAt.getTime()));
        root.getAttribute(E1cAttribute.UPDATED.getTaskKey()).setValue(String.valueOf(modifiedAt.getTime()));

        return data;
    }

    private void createDefaultAttributes(TaskData data, boolean existingTask)
    {
        createAttribute(data, E1cAttribute.CODE);
        createAttribute(data, E1cAttribute.DESCRIPTION);
        createAttribute(data, E1cAttribute.CREATED);
        createAttribute(data, E1cAttribute.UPDATED);
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

}
