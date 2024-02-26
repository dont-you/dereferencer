package ru.fusionsoft.dereferencer.git;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.RepositoryFileApi;
import org.gitlab4j.api.models.RepositoryFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.URLLoader;

import java.net.URI;
import java.net.URISyntaxException;


@RunWith(MockitoJUnitRunner.class)
public class GitLabLoaderTest {

    @Mock
    GitLabApi mockedGitLab;
    @Mock
    RepositoryFileApi mockedRepo;
    @Mock
    RepositoryFile mockedFile;

    @Mock
    URLLoader urlLoader;
    private GitLabLoader gitLabLoader;

    @Before
    public void init() {
        gitLabLoader = new GitLabLoader(urlLoader);
        gitLabLoader.configureGitLabLoader(mockedGitLab);
    }

    @Test
    public void Test_load_json_file() throws GitLabApiException, URISyntaxException, DereferenceException {
        testLoadMethod(new URI("https://gitlab.com/dont-you/deref-test/-/blob/main/test.json"), "dont-you/deref-test", "test.json", "main");
        testLoadMethod(new URI("https://gitlab.com/dont-you/git-tests/-/blob/master/src/test.json"), "dont-you/git-tests", "src/test.json", "master");
    }

    private void testLoadMethod(URI uri, String projectPath, String filePath, String ref) throws GitLabApiException, DereferenceException {
        initGitLabMocs(projectPath, filePath, ref);
        gitLabLoader.load(uri);

        Mockito.verify(mockedGitLab, Mockito.times(1)).getRepositoryFileApi();
        Mockito.verify(mockedRepo, Mockito.times(1)).getFile(projectPath, filePath, ref);
        Mockito.verify(mockedFile, Mockito.times(1)).getDecodedContentAsBytes();
        Mockito.clearInvocations(mockedGitLab, mockedRepo, mockedFile);
    }

    private void initGitLabMocs(String projectPath, String filePath, String ref) throws GitLabApiException {
        Mockito.when(mockedGitLab.getRepositoryFileApi()).thenReturn(mockedRepo);
        Mockito.when(mockedGitLab.getGitLabServerUrl()).thenReturn("https://gitlab.com");
        Mockito.when(mockedRepo.getFile(projectPath, filePath, ref)).thenReturn(mockedFile);
        Mockito.when(mockedFile.getDecodedContentAsBytes()).thenReturn(new byte[]{});
    }
}
