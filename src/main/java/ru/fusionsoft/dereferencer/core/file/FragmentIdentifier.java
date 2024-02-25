package ru.fusionsoft.dereferencer.core.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceException;
import ru.fusionsoft.dereferencer.core.exceptions.DereferenceRuntimeException;

import java.util.Objects;

public class FragmentIdentifier {
    private final String identifier;

    public FragmentIdentifier(String fragment) {
        this.identifier = Objects.requireNonNullElse(fragment, "");
    }

    public String getIdentifier() {
        return identifier;
    }

    public static String getPropertyName(@NotNull FragmentIdentifier fragmentIdentifier) {
        if (fragmentIdentifier.getType() != IdentifierType.JSON_POINTER)
            throw new DereferenceRuntimeException("the fragment identifier must be a json pointer");

        return getPropertyName(fragmentIdentifier.identifier);
    }

    public static String getPropertyName(@NotNull String identifier) {
        return identifier.substring(identifier.lastIndexOf("/") + 1);
    }

    public static String getParentPointer(@NotNull FragmentIdentifier fragmentIdentifier) {
        if (fragmentIdentifier.getType() != IdentifierType.JSON_POINTER)
            throw new DereferenceRuntimeException("the fragment identifier must be a json pointer");

        return getParentPointer(fragmentIdentifier.identifier);
    }

    public static String getParentPointer(@NotNull String identifier) {
        return identifier.substring(0, identifier.lastIndexOf("/"));
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        return ((FragmentIdentifier) obj).identifier.equals(identifier);
    }

    public static JsonNode evaluateRelativeJsonPointer(JsonNode jsonDocument, String referencedValue, String relativePointer) throws DereferenceException{
        if(getType(relativePointer) != IdentifierType.RELATIVE_JSON_POINTER)
            throw new DereferenceException("errors while evaluation relative json pointer - " + relativePointer);

        return completeEvaluation(jsonDocument, calculateReferencedValue(referencedValue, relativePointer), relativePointer);
    }

    private static JsonNode completeEvaluation(JsonNode jsonDocument, String referencedValue, String relativePointer){
        if(relativePointer.endsWith("#")){
            String memberName = getPropertyName(referencedValue);
            return StringUtils.isNumeric(memberName) ? IntNode.valueOf(Integer.parseInt(memberName)) : TextNode.valueOf(memberName);
        } else {
            String jsonPointer = relativePointer.substring(relativePointer.indexOf("/"));
            return jsonPointer.length() > 1 ? jsonDocument.at(referencedValue.concat(jsonPointer)) : jsonDocument.at(referencedValue);
        }
    }

    private static String calculateReferencedValue(String initialReferencedValue, String relativePointer) throws DereferenceException{
        int nonNegativeInteger = getLongestDigitSequence(relativePointer);
        StringBuilder updatedReferencedValue = new StringBuilder(initialReferencedValue);

        for(int i = 0 ; i < nonNegativeInteger ; i++){
            if(updatedReferencedValue.isEmpty())
                throw new DereferenceException("errors while evaluation relative json pointer - " + relativePointer + ", non negative integer is too big");

            updatedReferencedValue.delete(updatedReferencedValue.lastIndexOf("/"), updatedReferencedValue.length());
        }

        return performIndexManipulation(updatedReferencedValue, relativePointer, String.valueOf(nonNegativeInteger).length());
    }

    private static String performIndexManipulation(StringBuilder updatedReferencedValue, String relativePointer, int startOfIndexManipulation){
        char op = relativePointer.charAt(startOfIndexManipulation);

        if(op == '-' || op == '+'){
            int indexManipulation = getLongestDigitSequence(relativePointer.substring(startOfIndexManipulation+1));
            indexManipulation = op == '-' ? indexManipulation*-1 : indexManipulation;
            int updatedIndex = Integer.parseInt(getPropertyName(updatedReferencedValue.toString())) + indexManipulation;
            updatedReferencedValue.replace(updatedReferencedValue.lastIndexOf("/"), updatedReferencedValue.length(), String.valueOf(updatedIndex));
        }

        return updatedReferencedValue.toString();
    }

    private static int getLongestDigitSequence(String line){
        int num = 0;
        int factor = 1;

        for (char c: line.toCharArray()){
            if(Character.isDigit(c)){
                num = num*factor + Character.getNumericValue(c);
                factor*=10;
            } else {
                break;
            }
        }

        return num;
    }

    public IdentifierType getType() {
        return getType(identifier);
    }

    public static IdentifierType getType(String identifier) {
        if (identifier.isEmpty() || identifier.startsWith("/"))
            return IdentifierType.JSON_POINTER;
        else if (Character.isDigit(identifier.charAt(0)))
            return IdentifierType.RELATIVE_JSON_POINTER;
        else
            return IdentifierType.PLAIN_NAME;
    }

    public enum IdentifierType {
        JSON_POINTER,
        RELATIVE_JSON_POINTER,
        PLAIN_NAME
    }
}
