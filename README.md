
# Dereferencer

Java library to resolve `$ref` references in a given set of JSON schemas

---
## Table of Contents
* [Description](#description)<br/>
  * [Ref types](#ref-types)<br/>
  * [URI resolving](#uri-resolving)<br/>
  * [URN resolving](#urn-resolving)<br/>
  * [Merge allOf arrays](#merge-allof-arrays)<br/>
* [Using Dereferencer library](#using-dereferencer-library)<br/>
  * [Java 8 requirement](#java-8-requirement)<br/>
  * [Project Set Up](#project-set-up)<br/>
  * [Dependency](#dependency)<br/>
  * [Dereferencer class](#dereferencer-class)<br/>
  * [Usage examples](#usage-examples)<br/>
  * [Exceptions](#exceptions)<br/>

---
## Description
[comment]: # (some about dereferencing with example)
The dereferencer removes all properties with the key `$ref` from the init schema and instead sets the obtained schema by the `$ref` value. This process is recursive and stops when all references from init and dereferenced schemas have been removed. At the output, we get the single schema containing all the dereferenced schemas

For example, we have the document customer.json

**customer.json:**
```json
{
  "$schema": "https://json-schema.org/draft/2020-12",
  "$id": "https://example.com/schema/customer",

  "type": "object",
  "properties": {
    "name": { "type": "string" },
    "phone": { "$ref": "/schema/common#/$defs/phone" },
    "address": { "$ref": "/schema/address" }
  }
}
```

Also, we have the document common.json and addres.json

**common.json:**
```json
{
  "$schema": "https://json-schema.org/draft/2019-09",
  "$id": "https://example.com/schema/common",

  "$defs": {
    "phone": {
      "type": "string",
      "pattern": "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$"
    },
    "usaPostalCode": {
      "type": "string",
      "pattern": "^[0-9]{5}(?:-[0-9]{4})?$"
    },
    "unsignedInt": {
      "type": "integer",
      "minimum": 0
    }
  }
}
```

**address.json:**
```json
{
  "$schema": "https://json-schema.org/draft/2020-12",
  "$id": "https://example.com/schema/address",

  "type": "object",
  "properties": {
    "address": { "type": "string" },
    "city": { "type": "string" },
    "postalCode": { "$ref": "/schema/common#/$defs/usaPostalCode" },
    "state": { "$ref": "/$defs/states" }
  },

  "$defs": {
    "states": {
      "enum": [...]
    }
  }
}
```

The output document will be:

**output.json:**
```json
{
  "$schema": "https://json-schema.org/draft/2020-12",
  "$id": "https://example.com/schema/customer",

  "type": "object",
  "properties": {
    "name": { "type": "string" },
    "phone": {
      "type": "string",
      "pattern": "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$"
    }
    "address": {
      "$schema": "https://json-schema.org/draft/2020-12",
      "$id": "https://example.com/schema/address",

      "type": "object",
      "properties": {
        "address": { "type": "string" },
        "city": { "type": "string" },
        "postalCode": {
          "usaPostalCode": {
            "type": "string",
            "pattern": "^[0-9]{5}(?:-[0-9]{4})?$"
          },
        }
        "state": {
          "enum": [...]
        }
      },

      "$defs": {
        "states": {
          "enum": [...]
        }
      }
  }
}
```

### **Ref types**
[comment]: # (refs types from tg with example)
The URI used in `$ref` value must comply [RFC 3986](https://www.rfc-editor.org/rfc/rfc3986) and fall into one of the following categories:

- **Absolute URL [RFC 1738](https://www.rfc-editor.org/rfc/rfc1738)** - https://domain.name/path/to/file/#JsonPointer
- **Relative URL** - path/to/file#JsonPointer
- **URN [RFC 8141](https://www.rfc-editor.org/rfc/rfc8141.html) with 'tag' URI scheme [RFC 4151](https://datatracker.ietf.org/doc/html/rfc4151)** - urn:tag:authorityName,date:tagName#JsonPointer
- **JSONPointer [RFC 6901](https://www.rfc-editor.org/rfc/rfc6901)**
  -  **Default JSONPointer** - #path/to/json/node
  -  **Plain name (specified by `$anchor` key)** - #someFragmentName
- **Relative JSONPointer [Draft 2020-12](https://json-schema.org/draft/2020-12/relative-json-pointer.html)** - 2/some/path
 
### **URI resolving**
[comment]: # (uri resolving rules, example)
The relative URI resolves relative to the Base URI with according to [RFC 3986 Section 5.2](https://www.rfc-editor.org/rfc/rfc3986#section-5.2).

For the Base URI accepts with increasing priority:
- Defalut Base URI (specified in configuration or relative to project root)
- Retrieval URI
- Embedded in content URI (specified by `$id` key on the root of schema)

#### **URN resolution**
[comment]: # (urn resolving rules, file .origins, example)
URN resolution is performed according to the case described in [RFC 8141 Section 1.2.1](https://www.rfc-editor.org/rfc/rfc8141.html#section-1.2.1) when URNs mapped to their actual locators. 

To use URNs with the Dereferencer library, you must add a .origins.yaml file next to the schema that will be called for the dereference. For example, you dereference the schema with the actual URL - https://example.com/common.json, if the schema contains URNs, Dereferencer tries to find the .origins.yaml file at the URL - https://example.com/.origins.yaml

.origins.yaml file should have the following format:
**.origins.yaml:**
```yaml
someAuthorityName,2019:
  some.tag.name.*: "https://example.com/"
  some.tag.name.common*: "https://example.com/common"
  ...some other tags
  
anotherSomeAuthorityName,2019:
  another.some.tag.name: "https://example.com/address"
```

In accordance with above origins file, URNs resolves in the following way:
- **URN:** urn:tag:someAuthorityName,2019:some.tag.name.address -> **URI:** https://example.com/
- **URN:** urn:tag:someAuthorityName,2019:some.tag.name.common -> **URI:** https://example.com/common
- **URN:** urn:tag:anotherSomeAuthorityName,2019:another.some.tag.name -> **URI:** https://example.com/address
- **URN:** urn:tag:anotherSomeAuthorityName,2019:another.some.tag.name.addres -> **URI:** unspecified
### **Merge allOf arrays**
[comment]: # (allOf merge description with example)
If you set the merge flag [(See Dereferencer onfiguration)](#dereferencer-configuration) will merge the allOf array into a single object, by the next rules:
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
                    "type":"string"
                }
            },
            "prop-1":1
        },
        {
            "required":[age,first_name,last_name],
            "properties":{
                "age":{
                    "type":"number"
                }
            },
            "prop-2":2
        }
    ]
}
```

The result of the dereference with the merge flag will be:
```json
{
    "required":[age,first_name,last_name,age],
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

---

## Using Dereferencer library

### **Java 8 Requirement**
Java 8+ is required to use Dereferencer library

### **Project Set Up**
TODO

### **Dependency**
[comment]: # (Describe dependencyes)
Here are the dependencies. 

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>${version.jackson}</version>
</dependency>
```

---
### **Dereferencer class**
[comment]: # (Describe met  hods and ways to dereference)
Methods of class Dereferencer.
| Return Value | Modificator | Method                                    | Description |
|:------------:|:-----------:|-------------------------------------------|-------------|
| `JsonNode`   | `public`    | `static JsonNode dereference(String uri)` | Dereference |
TODO

#### **Input schemas**
TODO

#### **Output schemas**
TODO

#### **Dereferencer configuration**
[comment]: # (Describe available propertyes)
TODO

### **Usage examples**
[comment]: # (Code examples)
Standart dereferencing:
```java
Dereferencer dereferencer = new Dereferencer(DereferenceConfiguration.builder().build());
URI uri = URI.create("https://example.com/some.json")
JsonNode jsonNode = dereferencer.dereference(uri);
System.out.println(jsonNode)
```

### **Exceptions**
[comment]: # (types of exceptions, they short description)
TODO
