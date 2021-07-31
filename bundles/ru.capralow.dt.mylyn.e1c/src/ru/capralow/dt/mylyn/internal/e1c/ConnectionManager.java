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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.AuthenticationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Aleksandr Kapralov
 *
 */
public final class ConnectionManager
{

    private static Pattern URLPattern = Pattern.compile("((?:http|https):\\/\\/(?:[^\\\\\\/]*))\\/((?:[^\\\\\\/]*))$"); //$NON-NLS-1$

    public static E1cConnection validate(TaskRepository repository) throws CoreException
    {
        try
        {
            String projectPath = null;
            String host = null;

            Matcher matcher = URLPattern.matcher(repository.getUrl());
            if (!matcher.find())
            {
                throw new NullPointerException("Invalid Project-URL!");
            }

            projectPath = matcher.group(2);
            host = matcher.group(1);

            String username = repository.getCredentials(AuthenticationType.REPOSITORY).getUserName();
            String password = repository.getCredentials(AuthenticationType.REPOSITORY).getPassword();

            String testString = "/odata/standard.odata/$metadata"; //$NON-NLS-1$

            String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(repository.getUrl() + testString))
                .header("Authorization", "Basic " + encoding)
                .build();
            HttpResponse<String> response;
            response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() != 200)
            {
                throw new AuthenticationException("Authentication problem: " + response.statusCode());
            }

            E1cConnection connection = new E1cConnection();
            return connection;
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
