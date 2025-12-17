# MC-NBT XML

Serializes NBT to the **XNBTEdit** XML format.

## Usage
```java
NBTTag<?> tag = ...;
Document doc = NBTToXML.convert(tag); // javax.xml.Document
```

The output includes metadata attributes:
```xml
<int name="age" value="25" />
<list name="tags" of="string">
<string value="player" />
</list>
```