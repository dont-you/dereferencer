package ru.fusionsoft.dereferencer.git;

import ru.fusionsoft.dereferencer.core.SourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;

public class GitLabLoader implements SourceLoader {

    private GitLabApi gitLabApi;

    GitLabLoader() {
        configureGitLabLoader("", "https://gitlab.com");
    }

    @Override
    public boolean canLoad(URL url) {
        return url.getProtocol().concat("://").concat(url.getHost()).equals(gitLabApi.getGitLabServerUrl());
    }

    @Override
    public InputStream loadSource(URL url) throws IOException {
        String[] segments = url.getPath().split("/", 7);
        String projectPath = segments[1] + "/" + segments[2];
        String ref = segments[5];
        String filePath = segments[6];
        try {
            return new ByteArrayInputStream(
                    gitLabApi.getRepositoryFileApi().getFile(projectPath, filePath, ref).getDecodedContentAsBytes());
        } catch (GitLabApiException e) {
            throw new IOException(
                    String.format("error while getting file from gitlab with: \n\tmessage - %s\n\thttp code - %s",
                            e.getMessage(), e.getHttpStatus()));
        }
    }

    @Override
    public SourceType getSourceType(URL url) {
        return SourceType.resolveSourceTypeByPath(url.getPath());
    }

    public void configureGitLabLoader(String token, String host) {
        gitLabApi = new GitLabApi(host, token);
    }
}
