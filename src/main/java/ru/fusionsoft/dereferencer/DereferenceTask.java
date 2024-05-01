package ru.fusionsoft.dereferencer;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.Callable;

public interface DereferenceTask extends Callable<JsonNode> {
}
