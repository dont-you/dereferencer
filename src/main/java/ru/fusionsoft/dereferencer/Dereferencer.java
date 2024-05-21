package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.jetbrains.annotations.NotNull;
import ru.fusionsoft.dereferencer.core.DereferencedFile;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.cycles.LoopControl;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.pointers.RelativeJsonPointer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dereferencer {
    private final ExecutorService executorService;
    private final FileRegister fileRegister;
    private final LoopControl loopControl;
    private final URI defaultBaseURI;
    private final Map<URI, Future<JsonNode>> tasks;
    private final Logger logger;

    Dereferencer(@NotNull ExecutorService executorService, @NotNull FileRegister fileRegister, @NotNull URI defaultBaseURI, @NotNull Logger logger) {
        this.executorService = executorService;
        this.fileRegister = fileRegister;
        this.defaultBaseURI = defaultBaseURI;
        this.logger = logger;
        tasks = new ConcurrentHashMap<>();
        loopControl = new LoopControl();
    }

    public void exit() {
        executorService.shutdownNow();
    }

    public JsonNode dereference(URI uri) {
        logger.log(Level.INFO, "request to dereference file by uri - " + uri);
        JsonNode result = getResultFromFuture(executorService.submit(dereferenceCall(defaultBaseURI.resolve(uri), this)));
        logger.log(Level.INFO, "file by uri - " + uri + " successful dereferenced");

        return result;
    }

    public Map<String, JsonNode> dereference(URI fileBaseURI, Map<String, String> refMaps) {
        var response = new HashMap<String, JsonNode>();

        for (Map.Entry<String, Future<JsonNode>> futureRef : callDereferenceTasks(fileBaseURI, refMaps).entrySet()) {
            response.put(futureRef.getKey(), getResultFromFuture(futureRef.getValue()));
            logger.log(Level.INFO, "dereference of ref - " + futureRef.getKey() + " with consumer file " + fileBaseURI + " completed");
        }

        return response;
    }

    private Map<String, Future<JsonNode>> callDereferenceTasks(URI fileBaseURI, Map<String, String> refMaps) {
        Map<String, Future<JsonNode>> calls = new HashMap<>();

        for (Map.Entry<String, String> ref : refMaps.entrySet()) {
            logger.log(Level.INFO, "start dereference ref - " + ref.getValue() + " from consumer file - " + fileBaseURI);
            if (Character.isDigit(ref.getValue().charAt(0))) {
                Future<JsonNode> future = executorService.submit(dereferenceCall(fileBaseURI, RelativeJsonPointer.parseFromString(ref.getKey(), ref.getValue()), ref.getValue()));
                calls.put(ref.getKey(), future);
            } else {
                URI targetURI = fileBaseURI.resolve(ref.getValue());

                if (tasks.containsKey(targetURI)) {
                    calls.put(ref.getKey(), tasks.get(targetURI));
                } else {
                    Future<JsonNode> future = executorService.submit(dereferenceCall(fileBaseURI, targetURI, ref.getKey()));
                    tasks.put(targetURI, future);
                    calls.put(ref.getKey(), future);
                }
            }
        }

        return calls;
    }

    private DereferenceTask dereferenceCall(URI uri, Dereferencer dereferencer) {
        return () -> {
            DereferencedFile file = fileRegister.get(makeAbsoluteURI(uri));
            return file.getFragment("", dereferencer);
        };
    }

    private DereferenceTask dereferenceCall(URI consumerBaseURI, URI targetURI, String requestPoint) {
        return () -> evaluateJsonPointer(consumerBaseURI, targetURI, targetURI.getFragment(), requestPoint);
    }

    private DereferenceTask dereferenceCall(URI consumerBaseURI, RelativeJsonPointer relativeJsonPointer, String requestPoint) {
        return () -> evaluateRelativeJsonPointer(consumerBaseURI, relativeJsonPointer, requestPoint);
    }

    private JsonNode evaluateRelativeJsonPointer(URI consumerBaseURI, RelativeJsonPointer relativeJsonPointer, String requestPoint) {
        if (relativeJsonPointer.isEvaluationCompletesWithObjectMember())
            return relativeJsonPointer.getObjectMember();
        else
            return evaluateJsonPointer(consumerBaseURI, consumerBaseURI, relativeJsonPointer.getJsonPointer(), requestPoint);

    }

    private JsonNode evaluateJsonPointer(URI consumerBaseURI, URI targetURI, String fragment, String requestPoint) {
        try {
            fragment = fragment == null ? "" : fragment;
            DereferencedFile producerFile = fileRegister.get(makeAbsoluteURI(targetURI)), consumerFile = fileRegister.get(consumerBaseURI);
            boolean isThereLoop = loopControl.isThereLoop(consumerFile, producerFile, requestPoint, fragment);
            loopControl.addMapping(consumerFile, producerFile, requestPoint, fragment);
            JsonNode response = isThereLoop ? producerFile.getFragmentImmediately(fragment, this) : producerFile.getFragment(fragment, this);
            loopControl.removeMapping(consumerFile, producerFile, requestPoint, fragment);
            return response;
        } catch (DereferenceException e) {
            logger.log(Level.INFO, "error dereference ref - " + targetURI + " with msg - " + e.getMessage());
            return MissingNode.getInstance();
        }
    }

    private URI makeAbsoluteURI(URI uri) {
        try {
            return new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("unhandled error while trying make absolute uri by uri - " + uri);
        }
    }

    private <V> V getResultFromFuture(Future<V> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("unhandled error while trying get result from an asynchronous computation with msg - " + e.getMessage(), e);
        }
    }
}