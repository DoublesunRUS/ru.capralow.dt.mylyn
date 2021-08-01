/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c.odata.modeling;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpException;
import org.eclipse.core.runtime.CoreException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.capralow.dt.mylyn.e1c.E1cError;
import ru.capralow.dt.mylyn.e1c.E1cProject;
import ru.capralow.dt.mylyn.e1c.E1cUser;
import ru.capralow.dt.mylyn.e1c.IE1cConnection;
import ru.capralow.dt.mylyn.internal.e1c.E1cAttributeMapper;
import ru.capralow.dt.mylyn.internal.e1c.E1cPlugin;
import ru.capralow.dt.mylyn.internal.e1c.odata.CatalogOData;

/**
 * @author Aleksandr Kapralov
 *
 */
public class ModelingConnection
    implements IE1cConnection
{

    private static Pattern URLPattern = Pattern.compile("((?:http|https):\\/\\/(?:[^\\\\\\/]*))\\/((?:[^\\\\\\/]*))$"); //$NON-NLS-1$

    private String url;
    private String authHeader;
    private E1cAttributeMapper mapper;

    public ModelingConnection(String url, String authHeader, E1cAttributeMapper mapper)
    {
        this.url = url;
        this.authHeader = authHeader;
        this.mapper = mapper;
    }

    @Override
    public E1cAttributeMapper getMapper()
    {
        return mapper;
    }

    @Override
    public List<E1cError> getErrors() throws CoreException
    {
        try
        {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper objectMapper = new ObjectMapper();

            String requestString = "/odata/standard.odata/Catalog_Ошибки?$format=json"; //$NON-NLS-1$
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

            JsonNode jsonTree = objectMapper.readTree(response.body());
            ModelingErrorOData[] odata =
                objectMapper.readValue(jsonTree.get("value").traverse(), ModelingErrorOData[].class); //$NON-NLS-1$

            List<E1cError> result = new ArrayList<>();
            for (ModelingErrorOData odataElement : odata)
            {
                result.add(createErrorFromOData(odataElement));
            }

            return result;

        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    @Override
    public E1cError getError(String refKey) throws CoreException
    {
        try
        {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper objectMapper = new ObjectMapper();

            String requestString = "/odata/standard.odata/Catalog_Ошибки(guid'" + refKey + "')?$format=json"; //$NON-NLS-1$ //$NON-NLS-2$
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + requestString))
                .header("Authorization", "Basic " + authHeader) //$NON-NLS-1$ //$NON-NLS-2$
                .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() != 200)
            {
                JsonNode jsonTree = objectMapper.readTree(response.body());
                String errorMessage = jsonTree.get("odata.error").get("message").get("value").asText(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                throw new HttpException(errorMessage);
            }

            ModelingErrorOData odata = objectMapper.readValue(response.body(), ModelingErrorOData.class);

            return createErrorFromOData(odata);

        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    private List<E1cProject> projects;
    private List<E1cUser> users;

    @Override
    public void update() throws CoreException
    {
        updateProjects();
        updateUsers();
    }

    private void updateProjects() throws CoreException
    {
        try
        {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper objectMapper = new ObjectMapper();

            String requestString = "/odata/standard.odata/Catalog_Проекты?$format=json"; //$NON-NLS-1$
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + requestString))
                .header("Authorization", "Basic " + authHeader) //$NON-NLS-1$ //$NON-NLS-2$
                .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            JsonNode jsonTree = objectMapper.readTree(response.body());

            if (response.statusCode() != 200)
            {
                String errorMessage = jsonTree.get("odata.error").get("message").get("value").asText(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                throw new HttpException(errorMessage);
            }

            ModelingCatalogOData[] odata =
                objectMapper.readValue(jsonTree.get("value").traverse(), ModelingCatalogOData[].class); //$NON-NLS-1$

            List<E1cProject> projectList = new ArrayList<>();
            for (ModelingCatalogOData odataElement : odata)
            {
                projectList.add(createProjectFromOData(odataElement));
            }
            projects = Collections.unmodifiableList(projectList);

        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    private void updateUsers() throws CoreException
    {
        try
        {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper objectMapper = new ObjectMapper();

            String requestString = "/odata/standard.odata/Catalog_Пользователи?$format=json"; //$NON-NLS-1$
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + requestString))
                .header("Authorization", "Basic " + authHeader) //$NON-NLS-1$ //$NON-NLS-2$
                .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            JsonNode jsonTree = objectMapper.readTree(response.body());

            if (response.statusCode() != 200)
            {
                String errorMessage = jsonTree.get("odata.error").get("message").get("value").asText(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                throw new HttpException(errorMessage);
            }

            ModelingCatalogOData[] odata =
                objectMapper.readValue(jsonTree.get("value").traverse(), ModelingCatalogOData[].class); //$NON-NLS-1$

            List<E1cUser> userList = new ArrayList<>();
            for (ModelingCatalogOData odataElement : odata)
            {
                userList.add(createUserFromOData(odataElement));
            }
            users = Collections.unmodifiableList(userList);

        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    private E1cError createErrorFromOData(ModelingErrorOData odata)
    {
        Map<String, E1cProject> projectsMap = projects.stream().collect(Collectors.toMap(o -> o.refKey, o -> o));
        Map<String, E1cUser> usersMap = users.stream().collect(Collectors.toMap(o -> o.refKey, o -> o));

        E1cError result = new E1cError();

        result.refKey = odata.refKey;
        result.code = odata.code;
        result.description = odata.description;
        result.createdAt = odata.createdAt;
        result.modifiedAt = odata.createdAt;
        result.project = projectsMap.get(odata.ownerKey);

        if (odata.protocol.length != 0)
        {
            result.modifiedAt = odata.protocol[0].date;
        }

        boolean errorShouldBeClosed = false;

        if (odata.closed || odata.status.equals("Закрыта"))
        {
            result.completedAt = odata.closedAt;
        }
        else if (odata.withdrawn || odata.status.equals("Отозвана"))
        {
            result.completedAt = odata.withdrawnAt;
        }
        else if (odata.fixed || odata.status.equals("ПроверенаИсправлена") && !errorShouldBeClosed)
        {
            result.completedAt = odata.fixedAt;
        }

        return result;
    }

    private E1cProject createProjectFromOData(CatalogOData odata)
    {
        E1cProject result = new E1cProject();

        result.refKey = odata.refKey;
        result.code = odata.code;
        result.description = odata.description;

        return result;
    }

    private E1cUser createUserFromOData(CatalogOData odata)
    {
        E1cUser result = new E1cUser();

        result.refKey = odata.refKey;
        result.code = odata.code;
        result.description = odata.description;

        return result;
    }

    public List<E1cProject> getProjects()
    {
        return Collections.unmodifiableList(projects);
    }

    public List<E1cUser> getUsers()
    {
        return Collections.unmodifiableList(users);
    }

}
