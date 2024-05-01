package ru.fusionsoft.dereferencer.core.pointers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class RelativeJsonPointer {
    private final String jsonPointer;
    private final boolean evaluationCompletesWithObjectMember;

    private RelativeJsonPointer(String jsonPointer, boolean evaluationCompletesWithObjectMember){
        this.jsonPointer = jsonPointer;
        this.evaluationCompletesWithObjectMember = evaluationCompletesWithObjectMember;
    }

    public static RelativeJsonPointer parseFromString(String requestPoint, String relativePointer){
        boolean endsWithHash = relativePointer.endsWith("#");
        String referencedValue = calculateReferencedValue(requestPoint, relativePointer);
        String jsonPointer;

        if(endsWithHash){
            jsonPointer = referencedValue;
        } else {
            jsonPointer = relativePointer.substring(relativePointer.indexOf("/"));
            jsonPointer = jsonPointer.length() > 1 ? referencedValue.concat(jsonPointer) : jsonPointer;
        }

        return new RelativeJsonPointer(jsonPointer, endsWithHash);
    }
    private static String calculateReferencedValue(String initialReferencedValue, String relativePointer){
        int nonNegativeInteger = getLongestDigitSequence(relativePointer);
        StringBuilder updatedReferencedValue = new StringBuilder(initialReferencedValue);

        for(int i = 0 ; i < nonNegativeInteger ; i++){
            if(updatedReferencedValue.isEmpty())
                throw new RuntimeException("errors while evaluation relative json pointer - " + relativePointer + ", non negative integer is too big");

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

    private static String getPropertyName(@NotNull String identifier) {
        return identifier.substring(identifier.lastIndexOf("/") + 1);
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
    public boolean isEvaluationCompletesWithObjectMember(){
        return evaluationCompletesWithObjectMember;
    }

    public String getJsonPointer(){
        return jsonPointer;
    }

    public JsonNode getObjectMember(){
        String memberName = getPropertyName(jsonPointer);
        return StringUtils.isNumeric(memberName) ? IntNode.valueOf(Integer.parseInt(memberName)) : TextNode.valueOf(memberName);
    }
}
