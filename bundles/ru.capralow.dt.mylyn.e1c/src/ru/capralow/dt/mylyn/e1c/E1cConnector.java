/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.e1c;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

import ru.capralow.dt.mylyn.internal.e1c.ConnectionManager;

/**
 * @author Aleksandr Kapralov
 *
 */
public class E1cConnector
    extends AbstractRepositoryConnector
{

    public static final String CONNECTOR_KIND = "e1c.connector"; //$NON-NLS-1$

    private static final String LABEL = "1C Connector"; //$NON-NLS-1$

    @Override
    public boolean canCreateNewTask(TaskRepository repository)
    {
        return false;
    }

    @Override
    public boolean canCreateTaskFromKey(TaskRepository repository)
    {
        return false;
    }

    @Override
    public String getConnectorKind()
    {
        return CONNECTOR_KIND;
    }

    @Override
    public String getLabel()
    {
        return LABEL;
    }

    @Override
    public String getRepositoryUrlFromTaskUrl(String taskUrl)
    {
        // TODO Автоматически созданная заглушка метода
        return null;
    }

    @Override
    public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor) throws CoreException
    {
        // TODO Автоматически созданная заглушка метода
        return null;
    }

    @Override
    public String getTaskIdFromTaskUrl(String taskUrl)
    {
        // TODO Автоматически созданная заглушка метода
        return null;
    }

    @Override
    public String getTaskUrl(String repositoryUrl, String taskIdOrKey)
    {
        // TODO Автоматически созданная заглушка метода
        return null;
    }

    @Override
    public boolean hasTaskChanged(TaskRepository repository, ITask task, TaskData data)
    {
        // TODO Автоматически созданная заглушка метода
        return false;
    }

    @Override
    public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
        ISynchronizationSession session, IProgressMonitor monitor)
    {
        // TODO Автоматически созданная заглушка метода
        return null;
    }

    @Override
    public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException
    {
        // TODO Автоматически созданная заглушка метода

    }

    @Override
    public void updateTaskFromTaskData(TaskRepository repository, ITask task, TaskData data)
    {
        // TODO Автоматически созданная заглушка метода

    }

    public static void validate(TaskRepository taskRepo) throws CoreException
    {
        ConnectionManager.validate(taskRepo);
    }
}
