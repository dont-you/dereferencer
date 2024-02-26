package ru.fusionsoft.dereferencer.git;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.fusionsoft.dereferencer.core.Resource;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.URLLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class GitHubLoaderTest {

    @Mock
    GitHub mockedGitHub;
    @Mock
    GHRepository mockedRepo;

    @Mock
    GHContent mockedContent;

    @Mock
    InputStream expectedStream;

    @Mock
    URLLoader urlLoader;

    private GitHubLoader gitHubLoader;

    @Before
    public void init() throws IOException {
        gitHubLoader = new GitHubLoader(urlLoader);
        gitHubLoader.configureGitHubLoader(mockedGitHub);
    }

    @Test
    public void Test_load_json_file() throws IOException, URISyntaxException, DereferenceException {
        testLoadMethod(new URI("https://github.com/dont-you/tests/blob/main/test.json"), "dont-you/tests", "test.json", "main");
        testLoadMethod(new URI("https://github.com/dont-you/tests/blob/main/path/to/test.json"), "dont-you/tests", "path/to/test.json", "main");
        testLoadMethod(new URI("https://github.com/ivan/dereferencer/raw/develop/test.json"), "ivan/dereferencer", "test.json", "develop");
    }

    private void testLoadMethod(URI uri, String projectPath, String filePath, String ref) throws IOException, DereferenceException {
        initGitHubMocs(projectPath, filePath, ref);

        Resource resource = gitHubLoader.load(uri);
        InputStream actualStream = resource.getStream();
        assertEquals(expectedStream, actualStream);
    }

    private void initGitHubMocs(String projectPath, String filePath, String ref) throws IOException {
        Mockito.when(mockedGitHub.getRepository(projectPath)).thenReturn(mockedRepo);
        Mockito.when(mockedRepo.getFileContent(filePath, ref)).thenReturn(mockedContent);
        Mockito.when(mockedContent.read()).thenReturn(expectedStream);
    }
}
