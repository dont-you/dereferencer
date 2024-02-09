package ru.fusionsoft.dereferencer.git;

import ru.fusionsoft.dereferencer.core.SourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;


public class GitHubLoader implements SourceLoader {

    private GitHub gitHub;

    GitHubLoader() throws IOException {
        gitHub = new GitHubBuilder().build();
    }

    @Override
    public boolean canLoad(URI uri) {
        return uri.getHost().equals("github.com");
    }

    @Override
    public InputStream loadSource(URI uri) throws IOException {
        String[] segments = uri.getPath().split("/", 6);
        String projectPath = segments[1] + "/" + segments[2];
        String ref = segments[4];
        String filePath = segments[5];
        return gitHub.getRepository(projectPath).getFileContent(filePath, ref).read();
    }

    @Override
    public SourceType getSourceType(URI uri) {
        return SourceType.resolveSourceTypeByPath(uri.getPath());
    }

    public void configureGitHubLoader(String token) throws IOException {
        gitHub = new GitHubBuilder().withOAuthToken(token).build();
    }

    public void configureGitHubLoader(GitHub gitHub) {
        this.gitHub = gitHub;
    }
}
