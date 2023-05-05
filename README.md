# **Dereferencer**

Dereferencer is a library designed to dereference **JSON/YAML** Schema references, checked **$ref** keys within a document:

## How to use:

### Library main class
Definition: `public class Dereferencer`

The returned `JsonNode` is the result of dereference all references from the schema. The input is an instance of the class `String` (path to the file, it can be url or local). Also we can set github token for github references

    
| Return Value | Modificator | Method                                           |
|:------------:|:-----------:|--------------------------------------------------|
| `JsonNode`   | `public`    | `static JsonNode dereference(String uri)`        |
| `void`       | `public`    | `static void setGitHubToken(String gitHubToken)` |
| `String`     | `public`    | `static String getGitHubToken()`                 |

### Exception class
Definition: `public class ReferenceException`

If any of the references contains a syntax error or refers to a missing node, the a ReferenceException is thrown with a description of the error.

## Examples:
### $ref syntax
According to RFC3986, the $ref string value (JSON Reference) should contain a URI, which identifies the location of the JSON value you are referencing to. If the string value does not conform URI syntax rules, it causes an error during the resolving. Supported refs formats: 
- **Local Reference** - `"$ref":"#/components/myElement"`
- **Remote Reference** - `"$ref":"../schemes/cat.json"`
  - **Remote Reference with Local Reference** - `"$ref":"../schemes/cat.json#/name"`
- **URL Reference** - `"$ref":"https://some/url/path"`
  - **URL Reference with Local Reference** - `"$ref":"https://some/url/path#/someComponent"`

For example we have some json document containing:
```json
{
    "remote-ref":{
       "$ref":"ref-example.yaml"
    },
    "remote-with-local-ref":{
       "$ref":"another-ref-example.json#/prop-1"
    },
    "local-ref":{
        "$ref": "#/remote-ref/prop-1"
    }
}
```

Document “`ref-example.yaml`” contains:
```yaml
prop-1: 1
prop-2: 2
prop-3: 3
```

Document “`another-ref-example.json`” contains:
```json
{
    "prop-1":4,
    "prop-2":5,
    "prop-3":6
}
```

The result of the dereference will be:
```json
{
    "remote-ref":{
        "prop-1":1,
        "prop-2":2,
        "prop-3":3
    },
    "remote-with-local-ref":{
        "prop-1":4,
        "prop-2":5,
        "prop-3":6
    },
    "local-ref":1
}
```
### allOf merge
Also method `dereferencer` perfoms the merging of the `allOf` array into whole object. The merge is performed according to whe following rules:
1. All nodes with different names and at the same level are added to the resulting schem
2. If we have nodes with the same name and at the same level, but with a **different data type**, then the last arriving node is entered in the resulting scheme
3. If we have nodes with the same name and at the same level with **primitive data type**, then the last arriving node is entered in the resulting scheme
4. If we have nodes with the same name and at the same level with **array data type**, then set unique values from these arrays are added to the resulting scheme
5. If we have nodes with the same name and at the same level with **object data type**, then value of that nodes merged by the rules described above and added to the resulting scheme


For example we have some json document containing:
```json
{
    "allOf":[
        {
        
            "required":[first_name,last_name],
            "properties":{
                "full_name":{
                   "type":"number"
                },
                "last_name":{
                   "type":"string"
                },
                "age":{
                    "type":"number"
                }
            },
            "prop-1":1
        },
        {
            "required":[age,first_name,last_name],
            "properties":{
                "$ref":"./schemes/person.json"
            },
            "prop-2":2
        }
    ]
}
```

Document “`person.json`” contains:
```json
{
    "full_name":{
        "type":"string"
    },
    "last_name":{
        "type":"string"
    }
}
```


The result of the dereference will be:
```json
{
    "required":[first_name,last_name,age],
    "properties":{
        "full_name":{
            "type":"string"
        },
        "last_name":{
            "type":"string"
        },
        "age":{
            "type":"number"
        }
    },
    "prop-2":2
}
```
