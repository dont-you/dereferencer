package ru.fusionsoft.dereferencer.git;


import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import ru.fusionsoft.dereferencer.core.load.DefaultLoader;
import ru.fusionsoft.dereferencer.core.load.Resource;
import ru.fusionsoft.dereferencer.core.load.URLLoader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class GitHubURLLoader implements URLLoader {

    private final DefaultLoader defaultLoader;
    private GitHub gitHub;

    GitHubURLLoader() throws IOException {
        gitHub = new GitHubBuilder().build();
        defaultLoader = new DefaultLoader();
    }

    public void configureGitHubLoader(String token) throws IOException {
        gitHub = new GitHubBuilder().withOAuthToken(token).build();
    }

    public void configureGitHubLoader(GitHub gitHub) {
        this.gitHub = gitHub;
    }

    @Override
    public Resource load(URI uri) throws IOException, URISyntaxException {
        if (!uri.getHost().equals("github.com"))
            return defaultLoader.load(uri);

        String[] segments = uri.getPath().split("/", 6);
        String projectPath = segments[1] + "/" + segments[2];
        String ref = segments[4];
        String filePath = segments[5];

        return new Resource(uri, gitHub.getRepository(projectPath).getFileContent(filePath, ref).read());
    }
}
