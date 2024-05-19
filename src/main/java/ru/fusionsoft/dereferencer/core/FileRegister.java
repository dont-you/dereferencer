package ru.fusionsoft.dereferencer.core;

import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.Resource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileRegister {
    private final Map<URI, CachingFileProxy> cache;
    private final DereferencedFileFactory dereferencedFileFactory;
    private final ResourceCenter resourceCenter;
    private final Lock sameFileLock;

    public FileRegister(DereferencedFileFactory fileFactory, ResourceCenter resourceCenter){
        this.dereferencedFileFactory = fileFactory;
        this.resourceCenter = resourceCenter;
        cache = new ConcurrentHashMap<>();
        sameFileLock = new ReentrantLock();
    }
    public DereferencedFile get(URI uri) throws URISyntaxException, IOException {
        CachingFileProxy initialProxy = null;

        sameFileLock.lock();
        if(!cache.containsKey(uri))
            initialProxy = createAndPutToCache(uri);
        sameFileLock.unlock();

        return initialProxy==null ? cache.get(uri) : loadFile(uri, initialProxy);
    }

    private DereferencedFile loadFile(URI uri, CachingFileProxy initialProxy) throws URISyntaxException, IOException{
        var proxies = new ArrayList<>(Collections.singletonList(initialProxy));
        Resource resource =  resourceCenter.load(uri);

        proxies.add(createAndPutToCache(resource.getRetrievalURI()));
        configureProxies(proxies, makeFile(resource, proxies));

        return initialProxy;
    }

    private DereferencedFile makeFile(Resource resource, List<CachingFileProxy> proxies) throws IOException, URISyntaxException {
        DereferencedFile targetFile = dereferencedFileFactory.makeFile(resource);
        proxies.add(createAndPutToCache(targetFile.getBaseURI()));

        return targetFile;
    }

    private void configureProxies(List<CachingFileProxy> proxies, DereferencedFile wrapping){
        for(CachingFileProxy cachingFileProxy: proxies){
            cachingFileProxy.setFile(wrapping);
        }
    }

    private CachingFileProxy createAndPutToCache(URI uri){
        CachingFileProxy cachingFileProxy = new CachingFileProxy();

        if(!cache.containsKey(uri))
            cache.put(uri, cachingFileProxy);

        return cachingFileProxy;
    }
}
