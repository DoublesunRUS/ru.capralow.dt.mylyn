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

    @Override
    public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
        Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException
    {
        // TODO Автоматически созданная заглушка метода
        return null;
    }

    public TaskData createTaskDataFromE1cError(E1cError error, TaskRepository repository) throws CoreException
    {
        E1cConnection connection = ConnectionManager.get(repository);
        TaskData data = new TaskData(connection.mapper, E1cConnector.CONNECTOR_KIND, repository.getUrl(), error.code);

        createDefaultAttributes(data, true);

        TaskAttribute root = data.getRoot();
        root.getAttribute(E1cAttribute.IID.getTaskKey()).setValue(error.code);
        root.getAttribute(E1cAttribute.TITLE.getTaskKey()).setValue(error.description);

        return data;
    }

    private void createDefaultAttributes(TaskData data, boolean existingTask)
    {
        createAttribute(data, E1cAttribute.IID);
        createAttribute(data, E1cAttribute.TITLE);
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
