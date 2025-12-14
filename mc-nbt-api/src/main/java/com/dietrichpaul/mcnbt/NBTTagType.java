/*
 * Copyright (C) 2025 Paul Dietrich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dietrichpaul.mcnbt;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Represents the full set of NBT tag types defined by Minecraft.
 *
 * <p>Each tag type provides an associated serializer and deserializer used
 * for reading and writing its payload according to the official NBT binary
 * format specification. All multibyte primitives use big-endian ordering.
 *
 * <p>See: <a href="https://minecraft.wiki/w/NBT_format">...</a>
 */
public enum NBTTagType {

    /**
     * <b>TAG_End (0x00)</b><br>
     * Used only to mark the end of a {@link NBTCompound}.
     * <p>
     * Also used as the type of empty {@link NBTList} structures.
     */
    NULL(null),

    /**
     * <b>TAG_Byte (0x01)</b><br>
     * Signed 1-byte integer (-128 to 127).
     * Often used as a boolean (0 indicates false, otherwise true), as done in Mojang code.
     */
    BYTE("byte"),

    /**
     * <b>TAG_Short (0x02)</b><br>
     * Signed 16-bit integer, big-endian.
     * Range: -32,768 to 32,767.
     */
    SHORT("short"),

    /**
     * <b>TAG_Int (0x03)</b><br>
     * Signed 32-bit integer, big-endian.
     * Range: -2,147,483,648 to 2,147,483,647.
     */
    INT("int"),

    /**
     * <b>TAG_Long (0x04)</b><br>
     * Signed 64-bit integer, big-endian.
     * Used extensively in timestamps, UUIDs, etc.
     */
    LONG("long"),

    /**
     * <b>TAG_Float (0x05)</b><br>
     * 32-bit IEEE-754 floating point number, big-endian.
     */
    FLOAT("float"),

    /**
     * <b>TAG_Double (0x06)</b><br>
     * 64-bit IEEE-754 floating point number, big-endian.
     */
    DOUBLE("double"),

    /**
     * <b>TAG_Byte_Array (0x07)</b><br>
     * 4-byte signed length (big-endian), followed by raw bytes.
     */
    BYTE_ARRAY("byte_array"),

    /**
     * <b>TAG_String (0x08)</b><br>
     * UTF-8 string with:
     * <ul>
     * <li>2-byte unsigned length (big-endian)</li>
     * <li>exactly that many UTF-8 bytes</li>
     * </ul>
     * <p>
     * Uses default {@link DataInput}/{@link DataOutput} codec behavior.
     */
    STRING("string"),

    /**
     * <b>TAG_List (0x09)</b><br>
     * Represents a typed list of elements:
     * <ul>
     * <li>1 byte: element tag type ID</li>
     * <li>4 bytes: number of elements (big-endian)</li>
     * <li>N payloads of the given type (no names inside)</li>
     * </ul>
     * All contained elements must have the same tag type.
     */
    LIST("list"),

    /**
     * <b>TAG_Compound (0x0A)</b><br>
     * An unordered set of named tags:
     * <ul>
     * <li>Each entry: 1 byte type ID</li>
     * <li>2-byte unsigned name length + UTF-8 name</li>
     * <li>Payload according to its type</li>
     * </ul>
     * Ends with a {@link #NULL} TAG_End marker.
     * <p>
     * Tag names inside a compound must be unique.
     */
    COMPOUND("compound"),

    /**
     * <b>TAG_Int_Array (0x0B)</b><br>
     * 4-byte signed length (big-endian), followed by 32-bit integers.
     */
    INT_ARRAY("int_array"),

    /**
     * <b>TAG_Long_Array (0x0C)</b><br>
     * 4-byte signed length (big-endian), followed by 64-bit integers.
     */
    LONG_ARRAY("long_array");

    private final String displayName;

    <T extends NBTTag<?>> NBTTagType(String displayName) {
        this.displayName = displayName;
    }

    public static NBTTagType getByName(String name) {
        for (NBTTagType type : values()) {
            if (type.displayName.equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return displayName;
    }

    /**
     * Returns the tag type corresponding to the numeric NBT ID,
     * or {@code null} if the ID is outside the valid range.
     */
    public static NBTTagType getTagById(int id) {
        System.out.println("GET " + id);
        if (id < 0 || id >= values().length) {
            return null;
        }
        return values()[id];
    }

    /**
     * Will also determine if this is applicable to boolean.
     * This is equivalent to {@code this != STRING && isPrimitive()}.
     *
     * @return true if this tag represents a number.
     */
    public boolean isNumber() {
        return switch (this) {
            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE -> true;
            default -> false;
        };
    }

    /**
     * Determines whether this tag type is a primitive value.
     * Primitive types include numeric values as well as strings.
     *
     * @return true if this tag represents a primitive type, false otherwise
     */
    public boolean isPrimitive() { // string counts too in this case
        return switch (this) {
            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, STRING -> true;
            default -> false;
        };
    }

    /**
     * Determines whether this tag type represents an array.
     * Array types include byte arrays, int arrays, and long arrays.
     *
     * @return true if this tag is an array type, false otherwise
     */
    public boolean isArray() {
        return switch (this) {
            case BYTE_ARRAY, INT_ARRAY, LONG_ARRAY -> true;
            default -> false;
        };
    }

    /**
     * Determines whether this tag type represents a compound structure.
     * A compound contains key-value pairs and behaves like a map.
     *
     * @return true if this tag is a compound, false otherwise
     */
    public boolean isCompound() {
        return this == COMPOUND;
    }

    /**
     * Determines whether this tag type represents a list.
     * A list contains an ordered sequence of elements of a uniform type.
     *
     * @return true if this tag is a list, false otherwise
     */
    public boolean isList() {
        return this == LIST;
    }

    /**
     * Determines whether this tag type can contain multiple elements.
     * Iterable types include lists, compounds, and all array types.
     *
     * @return true if this tag type is iterable, false otherwise
     */
    public boolean isIterable() {
        return isArray() || isList() || isCompound();
    }

    /**
     * Returns the NBT numeric ID for this tag type.
     * For vanilla NBT, this is the ordinal of the enum:
     * <ul>
     *     <li>0 = TAG_End</li>
     *     <li>1 = TAG_Byte</li>
     *     <li>...</li>
     *     <li>12 = TAG_Long_Array</li>
     * </ul>
     */
    public int getId() {
        return ordinal();
    }
}
