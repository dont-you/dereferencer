package ru.fusionsoft.dereferencer.core;

import org.junit.Test;
import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

public class BaseURITest {
    @Test
    public void Test_equals_method() throws URISyntaxException {
        URI defaultUri = URI.create("www.some.com");
        URI urnUri = URI.create("urn:tag:somelocation.ru,2026:some.tag");
        URI locatorUri = URI.create("www.example.com/somefile.json");
        URI duplicateUri = URI.create("www.anotherloacation.com/somefile.json");

        BaseURI baseURI = new BaseURI(defaultUri,duplicateUri);
        baseURI.updateCanonical(locatorUri);
        baseURI.updateCanonical(urnUri);

        assertTrue(baseURI.equals(new BaseURI(defaultUri,urnUri)));
        assertTrue(baseURI.equals(new BaseURI(defaultUri,locatorUri)));
        assertTrue(baseURI.equals(new BaseURI(defaultUri,duplicateUri)));
        assertFalse(baseURI.equals(new BaseURI(defaultUri,URI.create("www.example1.com/file.json"))));
    }
}
