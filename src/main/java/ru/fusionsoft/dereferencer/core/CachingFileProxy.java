package ru.fusionsoft.dereferencer.core;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.Dereferencer;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CachingFileProxy implements DereferencedFile {
    Map<String, JsonNode> requests;
    DereferencedFile dereferencedFile;
    Lock waitingLock;
    Lock notifyingLock;

    public CachingFileProxy() {
        this.requests = new ConcurrentHashMap<>();
        this.dereferencedFile = null;
        this.waitingLock = new ReentrantLock();
        this.notifyingLock = new ReentrantLock();
    }

    void setFile(DereferencedFile dereferencedFile) {
        synchronized (this) {
            this.dereferencedFile = dereferencedFile;
            this.notify();
        }
    }

    @Override
    public JsonNode getFragment(String path, Dereferencer dereferencer) {
        waitFileResolution();

        if (requests.containsKey(path)) {
            return requests.get(path);
        } else {
            JsonNode fragment = dereferencedFile.getFragment(path, dereferencer);
            requests.put(path, fragment);
            return fragment;
        }
    }

    @Override
    public JsonNode getFragmentImmediately(String path, Dereferencer dereferencer) {
        waitFileResolution();

        if (requests.containsKey(path)) {
            return requests.get(path);
        } else {
            JsonNode fragment = dereferencedFile.getFragmentImmediately(path, dereferencer);
            requests.put(path, fragment);
            return fragment;
        }
    }

    public void waitFileResolution() {
        waitingLock.lock();
        synchronized (this) {
            while (dereferencedFile == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        waitingLock.unlock();
    }

    @Override
    public URI getBaseURI() {
        waitFileResolution();
        return dereferencedFile.getBaseURI();
    }

    @Override
    public int hashCode() {
        waitFileResolution();
        return dereferencedFile.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        waitFileResolution();
        return dereferencedFile.equals(o);
    }
}
