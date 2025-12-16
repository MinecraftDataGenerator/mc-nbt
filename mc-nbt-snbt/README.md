# mc-nbt-snbt

**A powerful, version-aware SNBT (Stringified NBT) parser and serializer for the `mc-nbt` library.**

This module provides the functionality to convert Minecraft NBT tags into their string representation (SNBT) and parse
SNBT strings back into Java objects. It is designed to handle the subtle syntax differences across various Minecraft
versions, from the lenient legacy formats (1.7/1.8) to the strict modern standards (1.21+).

## Features

* **Bidirectional Conversion:** Parse SNBT strings to `NBTTag` and serialize `NBTTag` to Strings.
* **Version Aware:** Includes a robust `SNbtSyntax` configuration to handle version-specific quirks (e.g., quoting
  rules, array suffixes, legacy list formats).
* **Legacy Support:** Special handling for the relaxed parsing rules of Minecraft 1.7 - 1.12.
* **Modern Support:** strict parsing for 1.13+ including typed arrays (`[I; ...]`, `[L; ...]`).
* **Zero External Dependencies:** Built purely on top of the core `mc-nbt` module.

---

## Installation

Add the module to your project dependencies (adjust version accordingly):
![Maven Central Version](https://img.shields.io/maven-central/v/com.dietrichpaul.mcnbt/mc-nbt-snbt)

**Maven**

```xml

<dependency>
    <groupId>com.dietrichpaul.mcnbt</groupId>
    <artifactId>mc-nbt-snbt</artifactId>
    <version>VERSION</version>
</dependency>
```

**Gradle**

```groovy
implementation 'com.dietrichpaul.mcnbt:mc-nbt-snbt:VERSION'
```

---

## Usage

### 1. Deserialization (Parsing SNBT)

The entry point for parsing is the `SNbtToNBT` class.

#### Basic Parsing (Latest Version)

By default, the parser uses the latest supported syntax (currently 1.21.5).

```java
import com.dietrichpaul.mcnbt.format.snbt.SNbtToNBT;
import com.dietrichpaul.mcnbt.NBTTag;
import com.dietrichpaul.mcnbt.NBTCompound;

String snbt = "{id:\"minecraft:diamond_sword\", Count:1b, tag:{Damage:0}}";

// Returns an NBTTag<?> (usually NBTCompound for full items/entities)
NBTTag<?> tag = SNbtToNBT.parse(snbt);

if(tag instanceof
NBTCompound compound){
        System.out.

println(compound.getString("id")); // "minecraft:diamond_sword"
        }
```

#### Version-Specific Parsing

If you are processing user input or data from older Minecraft versions, you should specify the syntax version. Old
versions (like 1.8) allowed unquoted keys and had different error handling.

```java
import com.dietrichpaul.mcnbt.format.snbt.SNbtSyntax;

// 1.8 style string (might lack quotes or use loose formatting)
String legacySnbt = "{id:35, Damage:0s}";

        // Use the V1_8 syntax profile
        NBTTag<?> tag = SNbtToNBT.parse(legacySnbt, SNbtSyntax.V1_8);
```

### 2. Serialization (NBT to String)

To convert an NBT object back into a string (e.g., for use in a `/give` command or debugging), use `NBTToSNbt`.

```java
import com.dietrichpaul.mcnbt.format.snbt.NBTToSNbt;
import com.dietrichpaul.mcnbt.NBTCompound;

NBTCompound tag = new NBTCompoundBuilder().putString("id", "minecraft:stone").putByte("Count", (byte) 64).build();

// Serialize using modern 1.21 syntax (strict quoting, specific suffixes)
String output = NBTToSNbt.serialize(tag, SNbtSyntax.V1_21_5);

System.out.

println(output);
// Output: {id:"minecraft:stone",Count:64b}
```

---

## Deep Dive: Syntax Versions

Minecraft's NBT string format has evolved over time. This module uses the `SNbtSyntax` enum to determine how to parse or
write data.

| Syntax Profile      | Description                                                                                                                                                                               |
|:--------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`V1_7` / `V1_8`** | **Legacy Mode.** Very lenient. Allows unquoted keys/values in many places. Array syntax is simplified. Internally uses a different parser logic to match the old Notchian implementation. |
| **`V1_12`**         | Transitional. stricter than 1.8 but still allows some loose formatting. Introduced stricter checking for indices.                                                                         |
| **`V1_13`**         | **The Flattening.** stricter quoting rules. Keys usually require quotes if they contain special characters.                                                                               |
| **`V1_14`**         | Refined rules for text component parsing within NBT.                                                                                                                                      |
| **`V1_21_5`**       | **Modern Standard.** Strict typed arrays (e.g., `[I; 1, 2]`), strict suffix requirements for numbers, and consistent quoting.                                                             |

### Why does this matter?

* **Parsing:** If you try to parse a modern string (like `[I; 1, 2, 3]`) using the `V1_8` syntax, it may fail or produce
  incorrect results because 1.8 didn't support that array syntax.
* **Serialization:** If you are sending a command to a 1.12 server, you shouldn't send 1.21 formatted NBT, as the server
  might not understand it.

---

## Error Handling

If the input string is malformed, an `SNbtException` is thrown. This exception provides a detailed message indicating
exactly where the error occurred in the string.

```java
try{
        SNbtToNBT.parse("{id:\"incomplete");
}catch(
SNbtException e){
        // Prints: "Unclosed quote at: ...id:\"incomplete<--[HERE]"
        e.

printStackTrace();
}
```

---

## Credits

* **[MCStructs](https://github.com/Lenni0451/MCStructs)** by **Lenni0451**:
  The SNBT parsing logic in this project is heavily inspired by the implementation found in MCStructs.

---

## License

This project is licensed under the terms of the **Apache 2.0 License**.