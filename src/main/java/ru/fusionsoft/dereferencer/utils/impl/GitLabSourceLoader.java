package ru.fusionsoft.dereferencer.utils.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;

import ru.fusionsoft.dereferencer.core.exceptions.LoadException;
import ru.fusionsoft.dereferencer.core.exceptions.UnknownException;
import ru.fusionsoft.dereferencer.core.load.SourceLoader;
import ru.fusionsoft.dereferencer.core.load.SupportedSourceTypes;

public class GitLabSourceLoader implements SourceLoader {

    private GitLabApi gitLabApi;
    private String locator;
    private String projectPath;
    private String filePath;
    private String ref;

    public GitLabSourceLoader(String token, URI uri) {
        String parts[] = uri.getPath().split("/");
        locator = uri.getScheme() + "://" + uri.getAuthority();
        projectPath = parts[1] + "/" + parts[2];
        ref = parts[5];
        filePath = parts[6];

        for (int i = 7; i < parts.length; i++) {
            filePath += "/" + parts[i];
        }

        setApiClient(token);
    }

    @Override
    public InputStream getSource() throws LoadException {
        try {
            return new ByteArrayInputStream(
                    gitLabApi.getRepositoryFileApi().getFile(projectPath, filePath, ref).getDecodedContentAsBytes());
        } catch (GitLabApiException e) {
            throw new UnknownException(
                    String.format("error while getting file from gitlab with: \n\tmessage - %s\n\thttp code - %s",
                            e.getMessage(), e.getHttpStatus()));
        }
    }

    @Override
    public SupportedSourceTypes getSourceType() throws LoadException {
        String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);
        return SupportedSourceTypes.resolveSourceTypeByMimeType("application/" + fileExtension);
    }

    public void setToken(String token) {
        setApiClient(token);
    }

    private void setApiClient(String token) {
        gitLabApi = new GitLabApi(locator, token);
    }
}
