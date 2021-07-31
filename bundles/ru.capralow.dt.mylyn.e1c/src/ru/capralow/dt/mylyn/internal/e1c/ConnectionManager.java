/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Aleksandr Kapralov
 *
 */
public final class ConnectionManager
{

    private static HashMap<String, E1cConnection> connections = new HashMap<>();

    public static E1cConnection get(TaskRepository repository) throws CoreException
    {
        return get(repository, false);
    }

    public static E1cConnection get(TaskRepository repository, boolean forceUpdate) throws CoreException
    {
        try
        {
            String hash = constructUrl(repository);
            if (connections.containsKey(hash) && !forceUpdate)
            {
                return connections.get(hash);
            }
            else
            {
                E1cConnection connection = validate(repository);

                connections.put(hash, connection);
                connection.update();

                return connection;
            }
        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    public static E1cConnection getSafe(TaskRepository repository)
    {
        try
        {
            return get(repository);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static String constructUrl(TaskRepository repository)
    {
        String username = repository.getCredentials(AuthenticationType.REPOSITORY).getUserName();
        String password = repository.getCredentials(AuthenticationType.REPOSITORY).getPassword();
        return repository.getUrl() + "?username=" + username + "&password=" + password.hashCode();
    }

    public static E1cConnection validate(TaskRepository repository) throws CoreException
    {
        return E1cConnection.validate(repository);
    }

    private ConnectionManager()
    {

    }
}
