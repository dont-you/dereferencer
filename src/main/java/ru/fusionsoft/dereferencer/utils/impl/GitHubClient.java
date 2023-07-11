package ru.fusionsoft.dereferencer.utils.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.tika.Tika;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.URIException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.utils.ClientFactory;
import ru.fusionsoft.dereferencer.utils.SourceClient;
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.load.SupportedSourceTypes;

public class GitHubClient implements SourceLoader, SourceClient {

    private String token = null;

    @Override
    public InputStream getSource(URI uri) throws LoadException {
        URI apiUri = transformToGetReposContentCall(uri);
        Map<String, String> properties = new HashMap<>() {{put("Accept", "application/vnd.github.v3.raw");}};

        if (token != null)
            properties.put("Authorization", "token " + token);

        return apiCall(properties, apiUri);
    }

    @Override
    public SupportedSourceTypes getSourceType(URI uri) throws LoadException {
        Path path = Paths.get(uri);
        try {
            Tika tika = new Tika();
            return SupportedSourceTypes.resolveSourceTypeByMimeType(tika.detect(path));
        } catch (IOException e) {
            throw new UnknownException(
                    "unknown exception caused while getting mime type with msg - " + e.getMessage());
        }
    }

    @Override
    public List<String> directoryList(URI uri) throws LoadException{
        URI apiUri = transformToGetReposContentCall(uri);
        Map<String, String> properties = new HashMap<>() {{put("Accept", "application/vnd.github.+json");}};

        if (token != null)
            properties.put("Authorization", "Bearer " + token);

        ObjectMapper obj = new ObjectMapper();
        try {
            JsonNode response = obj.readTree(apiCall(properties, apiUri));
            List<String> result = new ArrayList<>();
            for(int i = 0 ; i < response.size() ; i++){
                JsonNode value = response.at("/"+i+"/html_url");
                result.add(value.asText().substring(value.asText().lastIndexOf("/") + 1));
            }
            return result;
        } catch (IOException e) {
            throw new UnknownException(
                    "unknown exception caused while reading response from api github call with msg - " + e.getMessage());
        }
    }

    private InputStream apiCall(Map<String, String> connectionProperties, URI uri) throws LoadException {
        try {
            HttpURLConnection conn;
            conn = (HttpURLConnection) uri.toURL().openConnection();
            connectionProperties.forEach((k, v) -> conn.setRequestProperty(k, v));
            return conn.getInputStream();
        } catch (IOException e) {
            throw new UnknownException(
                    "unknown exception caused while call to github api with msg - " + e.getMessage());
        }

    }

    private URI transformToGetReposContentCall(URI uri) throws URIException {
        String apiGithubHostName = ClientFactory.HOSTNAMES.getProperty("refs.hostname.api-github");

        if (uri.getHost().equals(apiGithubHostName))
            return uri;

        String[] uriPath = uri.getPath().split("/");
        String resultPath = "/repos/" + uriPath[1] + "/" + uriPath[2] + "/contents/";
        if(uriPath.length>3)
                resultPath+=String.join("/", Arrays.stream(uriPath).collect(Collectors.toList()).subList(5, uriPath.length));
        URI resultUri;

        try {
            resultUri = new URI(
                    uri.getScheme(), uri.getUserInfo(),
                    apiGithubHostName,
                    uri.getPort(), resultPath,
                    uriPath.length>3?"ref=" + uriPath[4]:"", uri.getFragment());
        } catch (URISyntaxException e) {
            throw new URIException(resultPath);
        }

        return resultUri;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
