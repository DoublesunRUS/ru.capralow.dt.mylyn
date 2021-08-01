/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.capralow.dt.mylyn.internal.e1c.model.E1cError;

/**
 * @author Aleksandr Kapralov
 *
 */
public class E1cConnection
{

    private static Pattern URLPattern = Pattern.compile("((?:http|https):\\/\\/(?:[^\\\\\\/]*))\\/((?:[^\\\\\\/]*))$"); //$NON-NLS-1$

    public final E1cAttributeMapper mapper;

    private String url;
    private String authHeader;

    public E1cConnection(String url, String authHeader, E1cAttributeMapper mapper)
    {
        this.url = url;
        this.authHeader = authHeader;
        this.mapper = mapper;
    }

    public List<E1cError> getErrors() throws CoreException
    {
        try
        {
            String requestString = "/odata/standard.odata/Catalog_Ошибки?$format=json"; //$NON-NLS-1$

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + requestString))
                .header("Authorization", "Basic " + authHeader) //$NON-NLS-1$ //$NON-NLS-2$
                .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() != 200)
            {
                throw new HttpException(
                    "Исключительная ситуация при получении списка ошибок: " + response.statusCode());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonTree = objectMapper.readTree(response.body());
            E1cError[] odata = objectMapper.readValue(jsonTree.get("value").traverse(), E1cError[].class); //$NON-NLS-1$
            return Arrays.asList(odata);

        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    public E1cError getError(String refKey) throws CoreException
    {
        try
        {
            String requestString = "/odata/standard.odata/Catalog_Ошибки(guid'" + refKey + "')?$format=json"; //$NON-NLS-1$ //$NON-NLS-2$

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + requestString))
                .header("Authorization", "Basic " + authHeader) //$NON-NLS-1$ //$NON-NLS-2$
                .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();

            if (response.statusCode() != 200)
            {
                JsonNode jsonTree = objectMapper.readTree(response.body());
                String errorMessage = jsonTree.get("odata.error").get("message").get("value").asText(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                throw new HttpException(errorMessage);
            }

            E1cError odata = objectMapper.readValue(response.body(), E1cError.class);
            return odata;

        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    public void update() throws IOException
    {
//        ArrayList<GitlabProjectMember> memberList = new ArrayList<GitlabProjectMember>();
//
//        milestones = api().getMilestones(project);
//        memberList.addAll(api().getProjectMembers(project));
//        try
//        {
//            memberList.addAll(api().getNamespaceMembers(project.getNamespace()));
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        members = Collections.unmodifiableList(memberList);
    }

    public static E1cConnection validate(TaskRepository repository) throws CoreException
    {
        try
        {
            Matcher matcher = URLPattern.matcher(repository.getUrl());
            if (!matcher.find())
            {
                throw new NullPointerException("Invalid Project-URL!");
            }

            String username = repository.getCredentials(AuthenticationType.REPOSITORY).getUserName();
            String password = repository.getCredentials(AuthenticationType.REPOSITORY).getPassword();
            String authHeader = Base64.getEncoder().encodeToString((username + ":" + password).getBytes()); //$NON-NLS-1$

            String requestString = "/odata/standard.odata/$metadata"; //$NON-NLS-1$

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(repository.getUrl() + requestString))
                .header("Authorization", "Basic " + authHeader) //$NON-NLS-1$ //$NON-NLS-2$
                .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() != 200)
            {
                throw new HttpException(
                    "Исключительная ситуация при получении списка доступных метаданных: " + response.statusCode());
            }

            E1cConnection connection =
                new E1cConnection(repository.getUrl(), authHeader, new E1cAttributeMapper(repository));
            return connection;
        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

}
