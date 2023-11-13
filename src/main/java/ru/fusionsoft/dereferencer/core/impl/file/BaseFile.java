package ru.fusionsoft.dereferencer.core.impl.file;

import static ru.fusionsoft.dereferencer.core.impl.file.FileState.CREATED;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

import ru.fusionsoft.dereferencer.core.File;
import ru.fusionsoft.dereferencer.core.FileRegister;
import ru.fusionsoft.dereferencer.core.JsonPointer;
import ru.fusionsoft.dereferencer.core.Reference;

public class BaseFile implements File {
    private URI baseURI;
    private FileRegister fileRegister;
    private JsonNode source;
    private JsonNode derefedSource;
    private Map<Reference, JsonPointer> references;
    private FileState fileState;
    private ResponseManager responseManager;

    public BaseFile(FileRegister fileRegister, URI baseURI, JsonNode source){
        this.baseURI = baseURI;
        this.fileRegister = fileRegister;
        this.source = source;
        this.derefedSource = null;
        this.references = new TreeMap<>();
        this.fileState = CREATED;
        this.responseManager = new ResponseManager();
    }

    @Override
    public JsonNode getDerefedJson() {
        return derefedSource;
    }

    @Override
    public JsonNode getSourceNode() {
        return source;
    }

    @Override
    public Reference[] getReferences() {
        return (Reference[]) references.keySet().toArray();
    }

    @Override
    public void dereference() {
        // TODO
        exploreSourceJson();
    }

    private void exploreSourceJson(){
        JsonNode currentNode;
        String currentPath;
        Stack<JsonNode> nodeStack = new Stack<>();
        Stack<String> pathStack = new Stack<>();
        nodeStack.push(source);
        pathStack.push("");

        while(!nodeStack.empty()){
            currentNode = nodeStack.pop();
            currentPath = pathStack.pop();
            Iterator<Entry<String, JsonNode>> fields = currentNode.fields();
            while(fields.hasNext()){
                Entry<String, JsonNode> field = fields.next();
                String fieldKey = field.getKey();
                String fieldPath = currentPath + "/" + fieldKey;
                JsonNode fieldValue = field.getValue();

                resolveNode(fieldPath, fieldValue);
                decodeFieldKey(fieldKey);

                if (fieldValue.isArray()) {
                    Iterator<JsonNode> elements = field.getValue().elements();
                    int i = 0;
                    while (elements.hasNext()) {
                        nodeStack.push(elements.next());
                        pathStack.push(fieldPath + "/" + i++);
                    }
                } else {
                    nodeStack.push(fieldValue);
                    pathStack.push(fieldPath);
                }

            }
        }
    }

    private void decodeFieldKey(String fieldKey){
        if (fieldKey.contains("/")) {
            fieldKey = fieldKey.replaceAll("/", "~1");
        } else if (fieldKey.contains("~")) {
            fieldKey = fieldKey.replaceAll("~", "~0");
        }
    }

    @Override
    public Reference getFragment(JsonPointer jsonPointer) {
        // TODO
        return null;
    }

    @Override
    public void redirectReference(Reference.ReferenceProxy referenceProxy) {
        // TODO
    }

    @Override
    public void updateReferenceInfo(Reference reference) {
        // TODO
    }

    protected void resolveNode(String path, JsonNode node){
        // TODO
    }

    class ResponseManager{
        // TODO
    }
}
