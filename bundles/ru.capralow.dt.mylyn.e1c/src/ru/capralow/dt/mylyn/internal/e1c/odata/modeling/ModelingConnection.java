/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c.odata.modeling;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpException;
import org.eclipse.core.runtime.CoreException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.capralow.dt.mylyn.e1c.E1cError;
import ru.capralow.dt.mylyn.e1c.IE1cConnection;
import ru.capralow.dt.mylyn.internal.e1c.E1cAttributeMapper;
import ru.capralow.dt.mylyn.internal.e1c.E1cPlugin;

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

            ModelingErrorOData odata = objectMapper.readValue(response.body(), ModelingErrorOData.class);

            return createErrorFromOData(odata);

        }
        catch (Exception e)
        {
            throw new CoreException(E1cPlugin.createErrorStatus(e.getMessage(), e));
        }
    }

    @Override
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

    private E1cError createErrorFromOData(ModelingErrorOData odata)
    {
        E1cError result = new E1cError();

        result.refKey = odata.refKey;
        result.code = odata.code;
        result.description = odata.description;
        result.createdAt = odata.createdAt;
        result.modifiedAt = odata.createdAt;

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

}
