package ru.fusionsoft.dereferencer.git;

import ru.fusionsoft.dereferencer.core.SourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class GitHubLoader implements SourceLoader {

    private GitHub gitHub;

    GitHubLoader() throws IOException{
        gitHub = new GitHubBuilder().build();
    }

    @Override
    public boolean canLoad(URL url) {
        return url.getHost().equals("github.com");
    }

    @Override
    public InputStream loadSource(URL url) throws IOException {
        String[] segments = url.getPath().split("/", 6);
        String projectPath=segments[1] + "/" + segments[2];
        String ref=segments[4];
        String filePath=segments[5];
        return gitHub.getRepository(projectPath).getFileContent(filePath, ref).read();
    }

    @Override
    public SourceType getSourceType(URL url) {
        return SourceType.resolveSourceTypeByPath(url.getPath());
    }

    public void configureGitHubLoader(String token) throws IOException {
        gitHub = new GitHubBuilder().withOAuthToken(token).build();
    }
}
