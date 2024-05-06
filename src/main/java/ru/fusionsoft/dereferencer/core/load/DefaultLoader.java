package ru.fusionsoft.dereferencer.core.load;

import org.apache.tika.Tika;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

public class DefaultLoader implements URLLoader {

    @Override
    public URLConnection load(URI uri) {
        try {
            URLConnection urlConnection = uri.toURL().openConnection();
            urlConnection.connect();
            return urlConnection;
        } catch (IOException e) {
            // TODO
            throw  new RuntimeException("");
//            throw new DereferenceException("errors when retrieving a source from " + uri + " by " + uri.getScheme()+" proto");
        }
    }
}
