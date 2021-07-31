/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.e1c;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

import ru.capralow.dt.mylyn.internal.e1c.ConnectionManager;
import ru.capralow.dt.mylyn.internal.e1c.E1cConnection;
import ru.capralow.dt.mylyn.internal.e1c.E1cError;
import ru.capralow.dt.mylyn.internal.e1c.E1cPlugin;
import ru.capralow.dt.mylyn.internal.e1c.E1cTaskDataHandler;
import ru.capralow.dt.mylyn.internal.e1c.E1cTaskMapper;

/**
 * @author Aleksandr Kapralov
 *
 */
public class E1cConnector
    extends AbstractRepositoryConnector
{

    public static final String CONNECTOR_KIND = "e1c.connector"; //$NON-NLS-1$

    private static final String LABEL = "1C Connector"; //$NON-NLS-1$

    private E1cTaskDataHandler handler = new E1cTaskDataHandler();

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
        try
        {
            monitor.beginTask("Task Download", IProgressMonitor.UNKNOWN);
            return null;
//            return handler.downloadTaskData(repository, GitlabConnector.getTicketId(id));
        }
        finally
        {
            monitor.done();
        }
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
        TaskMapper mapper = new E1cTaskMapper(data);

        if (data.isPartial())
        {
            return mapper.hasChanges(task);
        }

        else
        {
            Date repositoryDate = mapper.getModificationDate();
            Date localDate = task.getModificationDate();
            if (repositoryDate != null && repositoryDate.equals(localDate))
            {
                return false;
            }

            return true;
        }
    }

    @Override
    public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
        ISynchronizationSession session, IProgressMonitor monitor)
    {
        try
        {
            monitor.beginTask("Tasks querying", IProgressMonitor.UNKNOWN);
            E1cConnection connection = ConnectionManager.get(repository);
            List<E1cError> errors = connection.getErrors();

            for (E1cError i : errors)
            {
                collector.accept(handler.createTaskDataFromE1cError(i, repository));
            }

            return Status.OK_STATUS;
        }
        catch (Exception e)
        {
            return E1cPlugin.createErrorStatus(e.getMessage(), e);
        }
        finally
        {
            monitor.done();
        }
    }

    @Override
    public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException
    {
        try
        {
            monitor.beginTask("Updating repository configuration", IProgressMonitor.UNKNOWN);
            ConnectionManager.get(repository, true);
        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
        finally
        {
            monitor.done();
        }
    }

    @Override
    public void updateTaskFromTaskData(TaskRepository repository, ITask task, TaskData data)
    {
        E1cTaskMapper mapper = new E1cTaskMapper(data);
        mapper.applyTo(task);
    }

    @Override
    public AbstractTaskDataHandler getTaskDataHandler()
    {
        return handler;
    }

    public static void validate(TaskRepository repository) throws CoreException
    {
        ConnectionManager.validate(repository);
    }
}
