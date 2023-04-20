# **Dereferencer**

Dereferencer is a library designed to dereference **JSON/YAML** Schema references, checked **$ref** keys within a document:

## How to use:

### Library main class
Definition: `public class Dereferencer`

The returned `JsonNode` is the result of dereference all references from the schema  . 
The input is an instance of the class `String` (path to the file, it can be url or local).



|Return Value| Modificator |Method |
|:-----------------------:|:-----------------------:| ----------------------- |
|`JsonNode`| `public` | `static JsonNode dereference(String uri)` | 

