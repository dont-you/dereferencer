package ru.fusionsoft.dereferencer.git;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import ru.fusionsoft.dereferencer.core.load.Loader;
import ru.fusionsoft.dereferencer.core.load.LoaderDecorator;


public class GitHubLoader extends LoaderDecorator {

    private GitHub gitHub;

    GitHubLoader(Loader loader) throws IOException {
        super(loader);
        gitHub = new GitHubBuilder().build();
    }

    @Override
    protected InputStream openStream(URI retrieval) throws IOException {
        String[] segments = retrieval.getPath().split("/", 6);
        String projectPath = segments[1] + "/" + segments[2];
        String ref = segments[4];
        String filePath = segments[5];
        return gitHub.getRepository(projectPath).getFileContent(filePath, ref).read();
    }

    @Override
    protected String getMimeType(URI retrieval) {
        String path = retrieval.getPath();
        return path.substring(path.lastIndexOf("."));
    }

    @Override
    protected boolean canLoad(URI retrieval) {
        return retrieval.getHost().equals("github.com");
    }

    public void configureGitHubLoader(String token) throws IOException {
        gitHub = new GitHubBuilder().withOAuthToken(token).build();
    }

    public void configureGitHubLoader(GitHub gitHub) {
        this.gitHub = gitHub;
    }
}
