package ru.fusionsoft.dereferencer.utils;

public class Tokens {
    private String gitHubToken;
    private String gitLabToken;

    public Tokens() {
        gitHubToken = null;
        gitLabToken = null;
    }

    public String getGitLabToken() {
        return gitLabToken;
    }

    public String getGitHubToken() {
        return gitHubToken;
    }

    public void setGitHubToken(String gitHubToken) {
        this.gitHubToken = gitHubToken;
    }

    public void setGitLabToken(String gitLabToken) {
        this.gitLabToken = gitLabToken;
    }
}
