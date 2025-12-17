# MC-NBT API

The core module of the library. it provides a high-performance, type-safe object model for Minecraft's NBT format.

## Key Design Principles

### 1. Factory-Based Instantiation
To ensure consistency and allow for internal optimizations, all Tag classes have **private constructors**. You must use the static `.of()` methods to create instances.

```java
// Correct usage:
NBTInt health = NBTInt.of(20);
NBTString name = NBTString.of("Steve");
NBTByteArray data = NBTByteArray.of(1, 2, 3);
```

### 2. Primitive Optimization (Trove4j)
To avoid the massive memory overhead of Java's wrapper classes (like `ArrayList<Integer>`), this module uses **Trove4j** primitive collections for all array types:
- `NBTByteArray` uses `TByteArrayList`
- `NBTIntArray` uses `TIntArrayList`
- `NBTLongArray` uses `TLongArrayList`

### 3. Advanced Compound Access
The `NBTCompound` class provides a sophisticated API for data retrieval, allowing you to choose between safety and convenience:

* **Strict Access**: Throws a `IllegalStateException` if the key is missing or the type is incorrect.
    - `getCompound(String key)`
    - `getCompoundOrThrow(String key)`
* **Defaulting Access**: Returns a provided default value if the key is missing.
    - `getIntOrDefault(String key, int defaultValue)`
    - `getStringOrDefault(String key, String defaultValue)`
* **Optional Access**: Returns the raw `NBTTag` or `null`.
    - `get(String key)`

### 4. NBTTagIdentifiable
In this library, an `NBTTag` is anonymous. When a tag needs a name (e.g., in a file header or a Compound entry), it is wrapped in an `NBTTagIdentifiable<T>`, which couples a `String` name with the tag.

## Example: Building a Structure
```java
NBTCompound root = new NBTCompoundBuilder()
.putString("server-name", "Production")
.putInt("max-players", 100)
.putCompound("nested", sub -> sub.putBoolean("whitelisted", true))
.build();

// Access with default
int players = root.getIntOrDefault("max-players", 0);
```
