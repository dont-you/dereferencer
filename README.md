# Dereferencer

Java library to resolve `$ref` references in a given set of JSON schemas

---
## Description
[comment]: # (some about dereferencing with example)
The dereferencer removes all properties with the key `$ref` from the init schema and instead sets the obtained schema by the `$ref` value. This process is recursive and stops when all references from init and dereferenced schemas have been removed. At the output, we get the single schema containing all the dereferenced schemas

For example we have the document customer.json

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

Also we have the document common.json and addres.json

**common.json:**
```json
{
  "$schema": "https://json-schema.org/draft/2019-09",
  "$id": "https://example.com/schema/common",

  "$defs": {
    "phone": {
      "type": "string",
      "pattern": "^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$"
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
      "pattern": "^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$"
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
TODO

### **URI resolving**
[comment]: # (uri resolving rules, example)
TODO

#### **URN resolving**
[comment]: # (urn resolving rules, file .origins, example)
TODO

### **Fragments**
[comment]: # (describe two types of fragment(json pointer, plain name fragment))
TODO

### **Merge allOf arrays**
[comment]: # (allOf merge description with example)
TODO

---
## Table of Contents
* [Description](#description)<br/>
  * [Ref types](#ref-types)<br/>
  * [URI resolving](#uri-resolving)<br/>
  * [URN resolving](#urn-resolving)<br/>
  * [Fragments](#fragments)<br/>
  * [Merge allOf arrays](#merge-allof-arrays)<br/>
* [Using Dereferencer library](#using-dereferencer-library)<br/>
  * [Java 8 requirement](#java-8-requirement)<br/>
  * [Project Set Up](#project-set-up)<br/>
  * [Dependency](#dependency)<br/>
  * [Dereferencer class](#dereferencer-class)<br/>
  * [Usage examples](#usage-examples)<br/>
  * [Exceptions](#exceptions)<br/>

---

## Using Dereferencer library

### **Java 8 Requirement**
Java 8+ is required to use Dereferencer library

### **Project Set Up**
TODO

### **Dependency**
[comment]: # (Describe dependencyes)
TODO

---
### **Dereferencer class**
[comment]: # (Describe methods and ways to dereference)
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
TODO

### **Exceptions**
[comment]: # (types of exceptions, they short description)
TODO
