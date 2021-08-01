/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import ru.capralow.dt.mylyn.e1c.IE1cConnection;
import ru.capralow.dt.mylyn.internal.e1c.odata.modeling.ModelingConnection;

/**
 * @author Aleksandr Kapralov
 *
 */
public final class ConnectionManager
{

    private static Pattern URLPattern = Pattern.compile("((?:http|https):\\/\\/(?:[^\\\\\\/]*))\\/((?:[^\\\\\\/]*))$"); //$NON-NLS-1$

    private static HashMap<String, IE1cConnection> connections = new HashMap<>();

    public static IE1cConnection get(TaskRepository repository) throws CoreException
    {
        return get(repository, false);
    }

    public static IE1cConnection get(TaskRepository repository, boolean forceUpdate) throws CoreException
    {
        try
        {
            String auth = constructAuth(repository);
            if (connections.containsKey(auth) && !forceUpdate)
            {
                return connections.get(auth);
            }
            else
            {
                validate(repository);

                IE1cConnection connection =
                    new ModelingConnection(repository.getUrl(), auth, new E1cAttributeMapper(repository));

                connections.put(auth, connection);
                connection.update();

                return connection;
            }
        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    public static IE1cConnection getSafe(TaskRepository repository)
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

    private static String constructAuth(TaskRepository repository)
    {
        String username = repository.getCredentials(AuthenticationType.REPOSITORY).getUserName();
        String password = repository.getCredentials(AuthenticationType.REPOSITORY).getPassword();
        return Base64.getEncoder().encodeToString((username + ":" + password).getBytes()); //$NON-NLS-1$
    }

    public static void validate(TaskRepository repository) throws CoreException
    {
        try
        {
            Matcher matcher = URLPattern.matcher(repository.getUrl());
            if (!matcher.find())
            {
                throw new NullPointerException("Неверный URL базы");
            }

            String requestString = "/odata/standard.odata/$metadata"; //$NON-NLS-1$

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(repository.getUrl() + requestString))
                .header("Authorization", "Basic " + constructAuth(repository)) //$NON-NLS-1$ //$NON-NLS-2$
                .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() != 200)
            {
                throw new HttpException(
                    "Исключительная ситуация при получении списка доступных метаданных: " + response.statusCode());
            }
        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    private ConnectionManager()
    {

    }
}
