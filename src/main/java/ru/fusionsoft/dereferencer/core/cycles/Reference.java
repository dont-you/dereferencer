package ru.fusionsoft.dereferencer.core.cycles;

import ru.fusionsoft.dereferencer.core.DereferencedFile;

import java.util.Objects;

public class Reference {
    private final DereferencedFile consumerFile;
    private final DereferencedFile producerFile;
    private final String requestPoint;
    private final String endPoint;
    private final int hash;

    public Reference(DereferencedFile consumerFile, DereferencedFile producerFile, String requestPoint, String endPoint) {
        this.consumerFile = consumerFile;
        this.producerFile = producerFile;
        this.requestPoint = requestPoint;
        this.endPoint = endPoint;
        hash = Objects.hash(consumerFile, producerFile, requestPoint, endPoint);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        return this.hash == o.hashCode();
    }

    public DereferencedFile getConsumerFile() {
        return consumerFile;
    }

    public DereferencedFile getProducerFile() {
        return producerFile;
    }

    public String getRequestPoint() {
        return requestPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }
}
