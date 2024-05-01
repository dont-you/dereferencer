package ru.fusionsoft.dereferencer;

import com.fasterxml.jackson.databind.JsonNode;
import ru.fusionsoft.dereferencer.core.DereferencedFile;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.cycles.LoopControl;
import ru.fusionsoft.dereferencer.core.pointers.RelativeJsonPointer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Dereferencer {
    private final ExecutorService executorService;
    private final FileRegister fileRegister;
    private final LoopControl loopControl;
    private final URI defaultBaseURI;

    public Dereferencer(ExecutorService executorService, FileRegister fileRegister, URI defaultBaseURI){
        this.executorService = executorService;
        this.fileRegister = fileRegister;
        this.defaultBaseURI = defaultBaseURI;
        loopControl = new LoopControl();
    }

    public int getMappingsSize(){
        return loopControl.size();
    }

    public void exit() {
        executorService.shutdownNow();
    }

    public JsonNode dereference(URI uri) throws ExecutionException, InterruptedException {
        return executorService.submit(dereferenceCall(defaultBaseURI.resolve(uri), this)).get();
    }

    public Map<String, JsonNode> dereference(URI fileBaseURI, Map<String, String> refMaps) throws ExecutionException, InterruptedException {
        var response = new HashMap<String, JsonNode>();

        for (Map.Entry<String, Future<JsonNode>> futureRef : callDereferenceTasks(fileBaseURI, refMaps).entrySet()) {
            response.put(futureRef.getKey(), futureRef.getValue().get());
        }

        return response;
    }

    private Map<String, Future<JsonNode>> callDereferenceTasks(URI fileBaseURI, Map<String, String> refMaps) {
        return refMaps.entrySet()
                .stream()
                .map(ref -> Map.entry(ref.getKey(), executorService.submit(dereferenceCall(fileBaseURI, ref.getKey(), ref.getValue()))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private DereferenceTask dereferenceCall(URI uri, Dereferencer dereferencer) {
        return new DereferenceTask() {
            @Override
            public JsonNode call() throws URISyntaxException, IOException, ExecutionException, InterruptedException {
                DereferencedFile file = fileRegister.get(makeAbsoluteURI(uri));
                return file.getFragment("", dereferencer);
            }
        };
    }

    private DereferenceTask dereferenceCall(URI consumerBaseURI, String requestPoint, String reference) {
        return new DereferenceTask() {
            @Override
            public JsonNode call() throws URISyntaxException, IOException, ExecutionException, InterruptedException {
                if (Character.isDigit(reference.charAt(0))) {
                    return evaluateRelativeJsonPointer(consumerBaseURI, reference, requestPoint);
                } else {
                    URI targetURI = consumerBaseURI.resolve(reference);
                    return evaluateJsonPointer(consumerBaseURI, targetURI, targetURI.getFragment(), requestPoint);
                }
            }
        };
    }

    private JsonNode evaluateRelativeJsonPointer(URI consumerBaseURI, String reference, String requestPoint) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        RelativeJsonPointer relativeJsonPointer = RelativeJsonPointer.parseFromString(requestPoint, reference);

        if (relativeJsonPointer.isEvaluationCompletesWithObjectMember())
            return relativeJsonPointer.getObjectMember();
        else
            return evaluateJsonPointer(consumerBaseURI, consumerBaseURI, relativeJsonPointer.getJsonPointer(), requestPoint);

    }

    private JsonNode evaluateJsonPointer(URI consumerBaseURI, URI targetURI, String fragment, String requestPoint) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        fragment = fragment == null ? "" : fragment;
        DereferencedFile producerFile = fileRegister.get(makeAbsoluteURI(targetURI)), consumerFile = fileRegister.get(consumerBaseURI);
        boolean isThereLoop = loopControl.isThereLoop(consumerFile, producerFile, requestPoint, fragment);
        loopControl.addMapping(consumerFile, producerFile, requestPoint, fragment);
        JsonNode response = isThereLoop ? producerFile.getFragmentImmediately(fragment, this) : producerFile.getFragment(fragment, this);
        loopControl.removeMapping(consumerFile, producerFile, requestPoint, fragment);
        return response;
    }

    private URI makeAbsoluteURI(URI uri) {
        try {
            return new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}