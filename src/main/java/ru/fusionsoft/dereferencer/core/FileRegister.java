package ru.fusionsoft.dereferencer.core;

import org.jetbrains.annotations.NotNull;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.load.Resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileRegister {
    private final Map<URI, CachingFileProxy> cache;
    private final DereferencedFileFactory dereferencedFileFactory;
    private final ResourceCenter resourceCenter;
    private final Lock sameFileLock;
    private final Logger logger;

    FileRegister(@NotNull DereferencedFileFactory fileFactory, @NotNull ResourceCenter resourceCenter, @NotNull Logger logger) {
        this.dereferencedFileFactory = fileFactory;
        this.resourceCenter = resourceCenter;
        this.logger = logger;
        cache = new ConcurrentHashMap<>();
        sameFileLock = new ReentrantLock();
    }

    public DereferencedFile get(URI uri) throws DereferenceException {
        CachingFileProxy initialProxy = null;

        sameFileLock.lock();
        if (!cache.containsKey(uri))
            initialProxy = createAndPutToCache(uri);
        sameFileLock.unlock();

        return initialProxy == null ? cache.get(uri) : loadFile(uri, initialProxy);
    }

    private DereferencedFile loadFile(URI uri, CachingFileProxy initialProxy) throws DereferenceException {
        var proxies = new ArrayList<>(Collections.singletonList(initialProxy));

        logger.log(Level.INFO, "trying load file by uri - " + uri);
        Resource resource = getResource(uri);
        logger.log(Level.INFO, "file by uri - " + uri + " successful loaded");

        proxies.add(createAndPutToCache(resource.getRetrievalURI()));
        configureProxies(proxies, makeFile(resource, proxies));

        return initialProxy;
    }

    private Resource getResource(URI uri) throws DereferenceException {
        try {
            return resourceCenter.load(uri);
        } catch (Exception e) {
            throw new DereferenceException("couldn't load resource from uri - " + uri + ", with msg - " + e.getMessage());
        }
    }

    private DereferencedFile makeFile(Resource resource, List<CachingFileProxy> proxies) throws DereferenceException {
        DereferencedFile targetFile = null;
        try {
            targetFile = dereferencedFileFactory.makeFile(resource);
        } catch (IOException e) {
            throw new DereferenceException("couldn't make file with uri - " + resource.getRetrievalURI() + ", with msg - " + e.getMessage());
        }
        proxies.add(createAndPutToCache(targetFile.getBaseURI()));

        return targetFile;
    }

    private void configureProxies(List<CachingFileProxy> proxies, DereferencedFile wrapping) {
        for (CachingFileProxy cachingFileProxy : proxies) {
            cachingFileProxy.setFile(wrapping);
        }
    }

    private CachingFileProxy createAndPutToCache(URI uri) {
        CachingFileProxy cachingFileProxy = new CachingFileProxy();

        if (!cache.containsKey(uri))
            cache.put(uri, cachingFileProxy);

        return cachingFileProxy;
    }
}
