package ru.fusionsoft.dereferencer.git;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.Resource;
import ru.fusionsoft.dereferencer.core.load.URLLoader;

public class GitLabURLLoader implements URLLoader{

    private GitLabApi gitLabApi;

    GitLabURLLoader() {
        configureGitLabLoader("", "https://gitlab.com");
    }

    private InputStream openStream(URI retrieval) throws IOException {
        String[] segments = retrieval.getPath().split("/", 7);
        String projectPath = segments[1] + "/" + segments[2];
        String ref = segments[5];
        String filePath = segments[6];
        return getFile(projectPath, filePath, ref);
    }

    private InputStream getFile(String projectPath, String filePath, String ref) throws IOException {
        try {
            return new ByteArrayInputStream(
                    gitLabApi.getRepositoryFileApi().getFile(projectPath, filePath, ref).getDecodedContentAsBytes());
        } catch (GitLabApiException e) {
            throw new IOException(
                    String.format("error while getting file from gitlab with: \n\tmessage - %s\n\thttp code - %s",
                            e.getMessage(), e.getHttpStatus()));
        }
    }

    public void configureGitLabLoader(String token, String host) {
        gitLabApi = new GitLabApi(host, token);
    }

    public void configureGitLabLoader(GitLabApi gitLabApi) {
        this.gitLabApi = gitLabApi;
    }

    @Override
    public Resource load(URI uri) throws IOException {
        return new Resource(uri, openStream(uri));
    }
}
