package ru.fusionsoft.dereferencer.core.impl.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceRuntimeException;

import java.util.Objects;
import java.util.stream.IntStream;

public class FragmentIdentifier {
    private final String identifier;

    public FragmentIdentifier(String fragment) {
        this.identifier = Objects.requireNonNullElse(fragment, "");
    }

    public String getIdentifier() {
        return identifier;
    }

    public static String getPropertyName(@NotNull FragmentIdentifier fragmentIdentifier){
        if(fragmentIdentifier.getType()!=IdentifierType.JSON_POINTER)
            throw new DereferenceRuntimeException("the fragment identifier must be a json pointer");

        return getPropertyName(fragmentIdentifier.identifier);
    }
    public static String getPropertyName(@NotNull String identifier){
        return identifier.substring(identifier.lastIndexOf("/") + 1);
    }
    public static String getParentPointer(@NotNull FragmentIdentifier fragmentIdentifier){
        if(fragmentIdentifier.getType()!=IdentifierType.JSON_POINTER)
            throw new DereferenceRuntimeException("the fragment identifier must be a json pointer");

        return getParentPointer(fragmentIdentifier.identifier);
    }

    public static String getParentPointer(@NotNull String identifier){
        return identifier.substring(0, identifier.lastIndexOf("/"));
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        return ((FragmentIdentifier) obj).identifier.equals(identifier);
    }

    public static JsonNode evaluateRelativeJsonPointer(JsonNode jsonDocument, String pathToReferencedValue, String referencedValue) throws DereferenceException{
        if(getType(referencedValue)!=IdentifierType.RELATIVE_JSON_POINTER)
            throw new DereferenceRuntimeException("the fragment identifier must be a relative json pointer");

        boolean endsWithHash = referencedValue.endsWith("#");
        referencedValue = endsWithHash ? referencedValue.substring(0, referencedValue.length() - 1) : referencedValue;

        String[] parts = referencedValue.split("/",2);
        String calculatedReferencedValue=calculateReferencedValue(parts[0], pathToReferencedValue);
        String jsonPointer = parts.length > 1 ? "/".concat(parts[1]) : "";

        if(endsWithHash){
            String objectMember = getPropertyName(calculatedReferencedValue);
            return StringUtils.isNumeric(objectMember) ? IntNode.valueOf(Integer.parseInt(objectMember)) : TextNode.valueOf(objectMember);
        } else {
            return jsonDocument.at(calculatedReferencedValue.concat(jsonPointer));
        }
    }

    private static String calculateReferencedValue(String prefix, String pathToReferencedValue) throws DereferenceException{
        int integerPrefix = 0;
        String indexManipulation = "";

        for(int i = 0; i < prefix.length() ; i++){
            if(prefix.charAt(i) == '-' || prefix.charAt(i) == '+'){
                indexManipulation = pathToReferencedValue.substring(i,prefix.length());
                break;
            } else {
                integerPrefix+=Character.getNumericValue(prefix.charAt(i));
            }
        }

        StringBuilder pathBuilder = new StringBuilder(pathToReferencedValue);
        IntStream.range(0, integerPrefix).forEach(i -> pathBuilder.delete(pathBuilder.lastIndexOf("/"),pathBuilder.length()));

        return indexManipulation.isEmpty() ? pathBuilder.toString() :  performIndexManipulation(pathBuilder, indexManipulation);
    }


    private static String performIndexManipulation(StringBuilder pathBuilder, String indexManipulation) throws DereferenceException {
        String index = getPropertyName(pathBuilder.toString());

        if(!StringUtils.isNumeric(index))
            throw new DereferenceException("current referenced value is not an item of an array");

        int updatedIndex = Integer.parseInt(index) + Integer.parseInt(indexManipulation);

        if(updatedIndex < 0)
            throw  new DereferenceException("updated index of an referenced value less then zero");

        return pathBuilder
                .delete(pathBuilder.lastIndexOf("/"),pathBuilder.length())
                .append("/")
                .append(updatedIndex)
                .toString();
    }

    public IdentifierType getType(){
        return getType(identifier);
    }

    public static IdentifierType getType(String identifier){
        if(identifier.isEmpty() || identifier.startsWith("/"))
            return IdentifierType.JSON_POINTER;
        else if(Character.isDigit(identifier.charAt(0)))
            return IdentifierType.RELATIVE_JSON_POINTER;
        else
            return IdentifierType.PLAIN_NAME;
    }

    public enum IdentifierType{
        JSON_POINTER,
        RELATIVE_JSON_POINTER,
        PLAIN_NAME
    }
}
