package ru.fusionsoft.dereferencer.git;

import ru.fusionsoft.dereferencer.core.SourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;

public class GitLabLoader implements SourceLoader {

    private GitLabApi gitLabApi;

    GitLabLoader() {
        configureGitLabLoader("", "https://gitlab.com");
    }

    @Override
    public boolean canLoad(URI uri) {
        return uri.getScheme().concat("://").concat(uri.getHost()).equals(gitLabApi.getGitLabServerUrl());
    }

    @Override
    public InputStream loadSource(URI uri) throws IOException {
        String[] segments = uri.getPath().split("/", 7);
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
    public String getMimeType(URI uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf(".") + 1);

    }

    public void configureGitLabLoader(String token, String host) {
        gitLabApi = new GitLabApi(host, token);
    }

    public void configureGitLabLoader(GitLabApi gitLabApi) {
        this.gitLabApi = gitLabApi;
    }
}
