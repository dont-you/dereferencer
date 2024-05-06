package ru.fusionsoft.dereferencer.git;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.URLLoader;


public class GitHubURLLoader implements URLLoader {

    private GitHub gitHub;

    GitHubURLLoader() throws IOException {
        gitHub = new GitHubBuilder().build();
    }

    public void configureGitHubLoader(String token) throws IOException {
        gitHub = new GitHubBuilder().withOAuthToken(token).build();
    }

    public void configureGitHubLoader(GitHub gitHub) {
        this.gitHub = gitHub;
    }

    @Override
    public URLConnection load(URI uri) throws IOException {
        String[] segments = uri.getPath().split("/", 6);
        String projectPath = segments[1] + "/" + segments[2];
        String ref = segments[4];
        String filePath = segments[5];
        // TODO
//        return gitHub.getRepository(projectPath).getFileContent(filePath, ref).;
        return null;
    }
}
