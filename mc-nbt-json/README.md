# MC-NBT JSON

Converts NBT to Google Gson `JsonElement` structures.

## Usage
```java
NBTCompound nbt = ...;

// To JSON
JsonElement json = NBTToJson.convertTagToJSON(nbt);

// From JSON
NBTTag<?> back = JsonToNBT.convertJSONToTag(json);
```