package ru.fusionsoft.dereferencer.core;

import org.junit.Test;
import static org.junit.Assert.*;

import java.net.URI;

public class BaseURITest {
    @Test
    public void Test_equals_method(){
        URI urnUri = URI.create("urn:tag:somelocation.ru,2026:some.tag");
        URI locatorUri = URI.create("www.example.com/somefile.json");
        URI duplicateUri = URI.create("www.anotherloacation.com/somefile.json");

        BaseURI baseURI = new BaseURI(duplicateUri);
        baseURI.updateCanonical(locatorUri);
        baseURI.updateCanonical(urnUri);

        assertTrue(baseURI.equals(new BaseURI(urnUri)));
        assertTrue(baseURI.equals(new BaseURI(locatorUri)));
        assertTrue(baseURI.equals(new BaseURI(duplicateUri)));
        assertFalse(baseURI.equals(new BaseURI(URI.create("www.example1.com/file.json"))));
    }
}
