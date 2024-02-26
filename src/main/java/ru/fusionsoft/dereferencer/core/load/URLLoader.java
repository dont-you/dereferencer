package ru.fusionsoft.dereferencer.core.load;

import org.apache.tika.Tika;
import ru.fusionsoft.dereferencer.core.Resource;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

public class URLLoader implements Loader{
    private final Tika tika;

    public URLLoader() {
        tika = new Tika();
    }

    @Override
    public Resource load(URI uri) throws DereferenceException {
        try {
            Resource resource = new Resource(uri);
            URLConnection urlConnection = uri.toURL().openConnection();
            urlConnection.connect();
            resource.setStream(urlConnection.getInputStream());
            resource.updateRetrieval(urlConnection.getURL().toURI());
            resource.setMimetype(getContentType(urlConnection));
            return resource;
        } catch (IOException | URISyntaxException e) {
            throw new DereferenceException("errors when retrieving a source from " + uri + " by " + uri.getScheme()+" proto");
        }
    }

    private String getContentType(URLConnection urlConnection) throws IOException {
        String contentType = urlConnection.getContentType();

        if(contentType.equals("content/unknown")){
            return tika.detect(urlConnection.getURL());
        } else {
            return contentType;
        }
    }
}
