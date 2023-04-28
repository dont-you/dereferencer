package ru.fusionsoft.dereferencer.core.reference.factories;

import java.net.URI;
import java.net.URISyntaxException;

import ru.fusionsoft.dereferencer.core.reference.Reference;
import ru.fusionsoft.dereferencer.exception.ReferenceException;
import ru.fusionsoft.dereferencer.core.reference.impl.LocalReference;
import ru.fusionsoft.dereferencer.core.reference.impl.RemoteReference;
import ru.fusionsoft.dereferencer.core.reference.impl.URLReference;
import ru.fusionsoft.dereferencer.enums.ReferenceType;

public class ReferenceFactory{
    public static Reference create(URI uri) throws ReferenceException{
        if(ReferenceType.isURLReference(uri))
            return new URLReference(uri);
        else if(ReferenceType.isRemoteReference(uri))
            return new RemoteReference(uri);
        else
            throw new ReferenceException("failed to recognize reference - " + uri);
    }

    public static Reference create(String uri) throws ReferenceException{
        try{
            URI createdUri = new URI(uri);
            return create(createdUri);
        } catch (URISyntaxException e){
            throw new ReferenceException("error making reference by uri - "+uri);
        }
    }

    public static Reference createRelative(Reference relativeReference, String targetPath) throws ReferenceException{
        try {
            URI uri = new URI(targetPath);
            if(ReferenceType.isLocalReference(uri)){
                return new LocalReference(relativeReference,uri.getFragment());
            } else if (uri.isAbsolute()){
                return create(uri);
            }
            URI relativeUri = relativeReference.getUri();
            String path =relativeUri.getPath();
            path = path.substring(0, path.lastIndexOf("/")+1) + uri.getPath();

            Reference createdReference = create(new URI(
                                                        relativeUri.getScheme(), relativeUri.getUserInfo(),
                                                        relativeUri.getHost(), relativeUri.getPort(),
                                                        path, relativeUri.getQuery(),
                                                        null
                                                        ));

            if (uri.getFragment()!=null && !uri.getFragment().equals("")) {
                return new LocalReference(createdReference, uri.getFragment());
            }

            return createdReference;
        } catch (URISyntaxException e) {
            throw new ReferenceException("failed to create relative reference with message: " + e.getMessage());
        }
    }
}
