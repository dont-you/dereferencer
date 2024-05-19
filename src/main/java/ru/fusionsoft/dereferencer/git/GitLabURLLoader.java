package ru.fusionsoft.dereferencer.git;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import ru.fusionsoft.dereferencer.core.load.DefaultLoader;
import ru.fusionsoft.dereferencer.core.load.Resource;
import ru.fusionsoft.dereferencer.core.load.URLLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class GitLabURLLoader implements URLLoader {

    private final DefaultLoader defaultLoader;
    private GitLabApi gitLabApi;
    private String host;

    GitLabURLLoader() throws URISyntaxException {
        this.defaultLoader = new DefaultLoader();
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

    public void configureGitLabLoader(String token, String host) throws URISyntaxException {
        configureGitLabLoader(new GitLabApi(host, token));
    }

    public void configureGitLabLoader(GitLabApi gitLabApi) throws URISyntaxException {
        this.gitLabApi = gitLabApi;
        this.host = new URI(gitLabApi.getGitLabServerUrl()).getHost();
    }

    @Override
    public Resource load(URI uri) throws IOException, URISyntaxException {
        if (!uri.getHost().equals(host))
            return defaultLoader.load(uri);

        return new Resource(uri, openStream(uri));
    }
}
