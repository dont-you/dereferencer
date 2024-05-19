package ru.fusionsoft.dereferencer.core.cycles;

import ru.fusionsoft.dereferencer.core.DereferencedFile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoopControl {

    private final Set<Reference> references;

    public LoopControl() {
        references = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public boolean isThereLoop(DereferencedFile consumerFile, DereferencedFile producerFile, String requestPoint, String endPoint) {
        Reference targetReference = new Reference(consumerFile, producerFile, requestPoint + "/", endPoint + "/");
        return isThemselfLoop(targetReference) || findLoopForReference(targetReference, getStackForReference(targetReference).collect(Collectors.toSet()), new HashSet<>());
    }

    private boolean isThemselfLoop(Reference target) {
        return isSup(target, target);
    }

    private boolean findLoopForReference(Reference target, Set<Reference> referenceStack, Set<Reference> visited) {
        for (Reference currentReference : referenceStack) {
            visited.add(currentReference);

            if (isSup(currentReference, target) || findLoopForReference(target, getStackForReference(currentReference).collect(Collectors.toSet()), visited))
                return true;
        }

        return false;
    }

    private boolean isSup(Reference verifiable, Reference target) {
        return target.getConsumerFile().equals(verifiable.getProducerFile()) && target.getRequestPoint().startsWith(verifiable.getEndPoint());
    }

    private Stream<Reference> getStackForReference(Reference reference) {
        return references.stream().filter(currentReference -> isSup(reference, currentReference));
    }

    public void addMapping(DereferencedFile consumerFile, DereferencedFile producerFile, String requestPoint, String endPoint) {
        references.add(new Reference(consumerFile, producerFile, requestPoint + "/", endPoint + "/"));
    }

    public void removeMapping(DereferencedFile consumerFile, DereferencedFile producerFile, String requestPoint, String endPoint) {
        references.remove(new Reference(consumerFile, producerFile, requestPoint + "/", endPoint + "/"));
    }

    public int size() {
        return references.size();
    }
}
