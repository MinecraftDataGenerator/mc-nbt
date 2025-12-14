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

package com.dietrichpaul.mcnbt.format.nio;

import com.dietrichpaul.mcnbt.*;
import com.dietrichpaul.mcnbt.exception.UnknownTagException;
import com.dietrichpaul.mcnbt.primitive.*;
import gnu.trove.list.TByteList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for serializing NBT tags into a {@link ByteBuffer}
 * using the standard NBT binary format (big-endian).
 * Note: Java edition uses big-endian, while Bedrock uses little-endian.
 *
 * <p>Functionally mirrors the deserialization performed by {@code NIOToNBT}
 * by writing NBT structures into the buffer.</p>
 */
public class NBTToNIO {
    /**
     * Maps each {@link NBTTagType} to the corresponding {@link NIOSerializer}.
     */
    private static final Map<NBTTagType, NIOSerializer<?>> SERIALIZERS;

    static {
        SERIALIZERS = new EnumMap<>(NBTTagType.class);

        // Primitive numeric types
        SERIALIZERS.put(NBTTagType.BYTE, (NIOSerializer<NBTByte>) (buffer, tag) -> buffer.put(tag.asByte()));
        SERIALIZERS.put(NBTTagType.SHORT, (NIOSerializer<NBTShort>) (buffer, tag) -> buffer.putShort(tag.asShort()));
        SERIALIZERS.put(NBTTagType.INT, (NIOSerializer<NBTInt>) (buffer, tag) -> buffer.putInt(tag.asInt()));
        SERIALIZERS.put(NBTTagType.LONG, (NIOSerializer<NBTLong>) (buffer, tag) -> buffer.putLong(tag.asLong()));
        SERIALIZERS.put(NBTTagType.FLOAT, (NIOSerializer<NBTFloat>) (buffer, tag) -> buffer.putFloat(tag.asFloat()));
        SERIALIZERS.put(NBTTagType.DOUBLE,
            (NIOSerializer<NBTDouble>) (buffer, tag) -> buffer.putDouble(tag.asDouble()));

        // String
        SERIALIZERS.put(NBTTagType.STRING,
            (NIOSerializer<NBTString>) (buffer, tag) -> writeStringPayload(buffer, tag.asString()));

        // Structured types
        SERIALIZERS.put(NBTTagType.LIST, (NIOSerializer<NBTList<?>>) NBTToNIO::writeListPayload);
        SERIALIZERS.put(NBTTagType.COMPOUND, (NIOSerializer<NBTCompound>) NBTToNIO::writeCompoundPayload);

        // Array types
        SERIALIZERS.put(NBTTagType.BYTE_ARRAY, (NIOSerializer<NBTByteArray>) NBTToNIO::writeByteArrayPayload);
        SERIALIZERS.put(NBTTagType.INT_ARRAY, (NIOSerializer<NBTIntArray>) NBTToNIO::writeIntArrayPayload);
        SERIALIZERS.put(NBTTagType.LONG_ARRAY, (NIOSerializer<NBTLongArray>) NBTToNIO::writeLongArrayPayload);

        // TAG_End (NULL) has no payload and is not mapped here.
    }

    private NBTToNIO() {
    }

    /**
     * Serializes a single, named NBT tag (e.g., the root tag or an entry inside a compound)
     * into the given {@link ByteBuffer}.
     *
     * @param buffer          the target byte buffer
     * @param identifiableTag the named NBT tag to serialize
     */
    public static void writeNamedTag(ByteBuffer buffer, NBTTagIdentifiable<?> identifiableTag) {
        Objects.requireNonNull(buffer, "buffer");
        Objects.requireNonNull(identifiableTag, "identifiableTag");

        NBTTag<?> tag = identifiableTag.tag();
        NBTTagType type = tag.getTagType();

        // 1. Write tag ID (1 byte)
        buffer.put((byte) type.getId());

        // TAG_End (0x00) has no name and no payload
        if (type == NBTTagType.NULL) {
            return;
        }

        // 2. Write tag name (2-byte length + UTF-8 bytes)
        writeStringPayload(buffer, identifiableTag.name());

        // 3. Write tag payload
        writeTagPayload(buffer, tag);
    }

    /**
     * Serializes the payload of a tag into the buffer.
     * Uses the {@link #SERIALIZERS} map to dispatch to the correct implementation.
     *
     * @param buffer the target byte buffer
     * @param tag    the NBT tag whose payload should be written
     */
    public static void writeTagPayload(ByteBuffer buffer, NBTTag<?> tag) {
        NBTTagType type = tag.getTagType();

        // TAG_End (NULL) has no payload
        if (type == NBTTagType.NULL) {
            return;
        }

        // Use the map for function dispatch
        @SuppressWarnings("unchecked") NIOSerializer<NBTTag<?>> serializer = (NIOSerializer<NBTTag<?>>) SERIALIZERS.get(
            type);

        if (serializer == null) {
            throw new UnknownTagException("Unknown tag type for serialization: " + type.getName(), type.getId());
        }

        try {
            serializer.serialize(buffer, tag);
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("Inconsistent NBT tag type: expected " + type.getName() + ", got " + tag.getClass().getSimpleName(),
                e);
        }
    }

    /**
     * Writes an NBT string payload: 2-byte unsigned length (short), followed by UTF-8 bytes.
     *
     * @param buffer the target byte buffer to write to
     * @param s      the string value to encode as an NBT STRING payload (UTF-8)
     * @throws IllegalArgumentException if the encoded UTF-8 length exceeds 65535 bytes
     */
    public static void writeStringPayload(ByteBuffer buffer, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

        if (bytes.length > 65535) {
            throw new IllegalArgumentException("String is too long for the NBT format (max 65535 bytes): " + bytes.length + " bytes");
        }

        // 2 bytes for length (unsigned short, big-endian)
        buffer.putShort((short) bytes.length);

        // UTF-8 content
        buffer.put(bytes);
    }

    /**
     * Writes a TAG_List payload: 1-byte element tag ID, 4-byte size (int), followed by the element payloads.
     *
     * @param buffer the target byte buffer to write to
     * @param list   the NBT list whose payload should be written
     */
    private static void writeListPayload(ByteBuffer buffer, NBTList<?> list) {
        NBTTagType internType = list.getInternalType();
        if (internType == null) {
            // An empty list must have TAG_End (0x00) as its internal type.
            internType = NBTTagType.NULL;
        }

        // 1. Write internal tag ID (1 byte)
        buffer.put((byte) internType.getId());

        // 2. Write list length (4 bytes, big-endian)
        buffer.putInt(list.size());

        // 3. Write element payloads
        if (internType != NBTTagType.NULL) {
            for (NBTTag<?> tag : list) {
                writeTagPayload(buffer, tag);
            }
        }
    }

    /**
     * Writes a TAG_Compound payload: a sequence of named tags, terminated by a TAG_End (0x00).
     *
     * @param buffer   the target byte buffer to write to
     * @param compound the NBT compound whose payload should be written
     */
    private static void writeCompoundPayload(ByteBuffer buffer, NBTCompound compound) {
        // Write all contained named tags
        for (NBTTagIdentifiable<?> identifiable : compound) {
            writeNamedTag(buffer, identifiable);
        }

        // Write TAG_End terminator (0x00)
        buffer.put((byte) NBTTagType.NULL.getId());
    }

    /**
     * Writes a TAG_Byte_Array payload: 4-byte size (int), followed by the bytes.
     *
     * @param buffer the target byte buffer to write to
     * @param tag    the NBT byte array whose payload should be written
     */
    private static void writeByteArrayPayload(ByteBuffer buffer, NBTByteArray tag) {
        TByteList content = tag.getContent();
        // 1. Write array length (4 bytes, big-endian)
        buffer.putInt(content.size());

        // 2. Write byte array content
        buffer.put(content.toArray());
    }

    /**
     * Writes a TAG_Int_Array payload: 4-byte size (int), followed by the 4-byte integers (big-endian).
     *
     * @param buffer the target byte buffer to write to
     * @param tag    the NBT int array whose payload should be written
     */
    private static void writeIntArrayPayload(ByteBuffer buffer, NBTIntArray tag) {
        TIntList content = tag.getContent();
        int size = content.size();

        // 1. Write array length (4 bytes, big-endian)
        buffer.putInt(size);

        // 2. Write int array content
        if (size > 0) {
            // asIntBuffer inherits the byte order of the parent buffer
            buffer.asIntBuffer().put(content.toArray());
            // Advance the parent buffer position manually
            buffer.position(buffer.position() + size * Integer.BYTES);
        }
    }

    /**
     * Writes a TAG_Long_Array payload: 4-byte size (int), followed by the 8-byte longs (big-endian).
     *
     * @param buffer the target byte buffer to write to
     * @param tag    the NBT long array whose payload should be written
     */
    private static void writeLongArrayPayload(ByteBuffer buffer, NBTLongArray tag) {
        TLongList content = tag.getContent();
        int size = content.size();

        // 1. Write array length (4 bytes, big-endian)
        buffer.putInt(size);

        // 2. Write long array content
        if (size > 0) {
            // asLongBuffer inherits the byte order of the parent buffer
            buffer.asLongBuffer().put(content.toArray());
            // Advance the parent buffer position manually
            buffer.position(buffer.position() + size * Long.BYTES);
        }
    }
}