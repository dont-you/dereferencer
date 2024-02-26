package ru.fusionsoft.dereferencer.git;

import ru.fusionsoft.dereferencer.core.load.*;

import java.io.IOException;

public class GitLoadersBuilder {
    private final GitHubLoader gitHubLoader;
    private final GitLabLoader gitLabLoader;

    private GitLoadersBuilder() throws IOException {
        gitHubLoader = new GitHubLoader(new URLLoader());
        gitLabLoader = new GitLabLoader(gitHubLoader);
    }

    public static GitLoadersBuilder getInstance() throws IOException {
        return new GitLoadersBuilder();
    }

    public GitLoadersBuilder configureGitHub(String token) throws IOException {
        gitHubLoader.configureGitHubLoader(token);
        return this;
    }

    public GitLoadersBuilder configureGitLab(String token, String host) {
        gitLabLoader.configureGitLabLoader(token, host);
        return this;
    }

    public LoaderDecorator build() {
        return gitLabLoader;
    }
}
