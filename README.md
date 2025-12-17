# MC-NBT

A modular, high-performance Java library for Minecraft's NBT format.

## Project Overview

| Module                            | Core Responsibility |
|:----------------------------------| :--- |
| **[api](mc-nbt-api/README.md)**   | Memory-efficient model using Trove4j and Factory patterns. |
| **[nio](mc-nbt-nio/README.md)**   | Binary serialization, Compression support, and Netty integration. |
| **[snbt](mc-nbt-snbt/README.md)** | Advanced SNBT Parser (Modern & Legacy support). |
| **[json](mc-nbt-json/README.md)** | Gson interoperability. |
| **[xml](mc-nbt-xml/README.md)**   | XNBTEdit format support. |

## Dependencies
- Java 21+
- Trove4j (Internalized/shaded in API)