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
//        createDefaultAttributes(data, false);

        E1cConnection connection = ConnectionManager.get(repository);
        TaskAttribute root = data.getRoot();

//        root.getAttribute(GitlabAttribute.PROJECT.getTaskKey()).setValue(connection.project.getName());
//        root.getAttribute(GitlabAttribute.LABELS.getTaskKey()).setValue("");
//        root.getAttribute(GitlabAttribute.STATUS.getTaskKey()).setValue("open");
//        root.getAttribute(GitlabAttribute.MILESTONE.getTaskKey()).setValue("");

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
        TaskData data = new TaskData(connection.mapper, E1cConnector.CONNECTOR_KIND, repository.getUrl(), "Id");

//        createDefaultAttributes(data, true);
//
//        TaskAttribute root = data.getRoot();
//        root.getAttribute(GitlabAttribute.AUTHOR.getTaskKey()).setValue(issue.getAuthor().getName());
//        root.getAttribute(GitlabAttribute.CREATED.getTaskKey()).setValue("" + issue.getCreatedAt().getTime());
//        root.getAttribute(GitlabAttribute.BODY.getTaskKey())
//            .setValue(issue.getDescription() == null ? "" : issue.getDescription());
//        root.getAttribute(GitlabAttribute.LABELS.getTaskKey()).setValue(labels);
//        root.getAttribute(GitlabAttribute.PROJECT.getTaskKey()).setValue(connection.project.getName());
//        root.getAttribute(GitlabAttribute.STATUS.getTaskKey()).setValue(issue.getState());
//        root.getAttribute(GitlabAttribute.TITLE.getTaskKey()).setValue(issue.getTitle());
//
//        root.getAttribute(GitlabAttribute.IID.getTaskKey()).setValue("" + issue.getIid());
//        root.getAttribute(GitlabAttribute.PRIORITY.getTaskKey()).setValue(getPriority(labels));
//        root.getAttribute(GitlabAttribute.TYPE.getTaskKey()).setValue(getType(labels));
//
//        if (issue.getMilestone() != null)
//        {
//            root.getAttribute(GitlabAttribute.MILESTONE.getTaskKey()).setValue(issue.getMilestone().getTitle());
//        }
//
//        if (issue.getUpdatedAt() != null)
//        {
//            root.getAttribute(GitlabAttribute.UPDATED.getTaskKey()).setValue("" + issue.getUpdatedAt().getTime());
//        }
//
//        if (issue.getState().equals(GitlabIssue.STATE_CLOSED))
//        {
//            root.getAttribute(GitlabAttribute.COMPLETED.getTaskKey()).setValue("" + issue.getUpdatedAt().getTime());
//        }
//
//        if (issue.getAssignee() != null)
//        {
//            root.getAttribute(GitlabAttribute.ASSIGNEE.getTaskKey()).setValue(issue.getAssignee().getName());
//        }
//
//        Collections.sort(notes, new Comparator<GitlabNote>()
//        {
//            @Override
//            public int compare(GitlabNote o1, GitlabNote o2)
//            {
//                return o1.getCreatedAt().compareTo(o2.getCreatedAt());
//            }
//        });
//
//        for (int i = 0; i < notes.size(); i++)
//        {
//            TaskCommentMapper cmapper = new TaskCommentMapper();
//            cmapper.setAuthor(repository.createPerson(notes.get(i).getAuthor().getName()));
//            cmapper.setCreationDate(notes.get(i).getCreatedAt());
//            cmapper.setText(notes.get(i).getBody());
//            cmapper.setNumber(i + 1);
//            TaskAttribute attribute = data.getRoot().createAttribute(TaskAttribute.PREFIX_COMMENT + (i + 1));
//            cmapper.applyTo(attribute);
//        }
//
//        GitlabAction[] actions = GitlabAction.getActions(issue);
//        for (int i = 0; i < actions.length; ++i)
//        {
//            GitlabAction action = actions[i];
//            TaskAttribute attribute = data.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + action.label);
//            TaskOperation.applyTo(attribute, action.label, action.label);
//        }

        return data;
    }

}
