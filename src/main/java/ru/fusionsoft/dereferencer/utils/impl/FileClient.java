package ru.fusionsoft.dereferencer.utils.impl;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import ru.fusionsoft.dereferencer.core.load.impl.FileLoader;
import ru.fusionsoft.dereferencer.utils.SourceClient;

public class FileClient extends FileLoader implements SourceClient{

    @Override
    public List<String> directoryList(URI uri) {
        return Stream.of(Paths.get(uri.toString()).toAbsolutePath().toFile().listFiles())
            .filter(file -> !file.isDirectory())
            .map(File::getName)
            .toList();
    }
}
