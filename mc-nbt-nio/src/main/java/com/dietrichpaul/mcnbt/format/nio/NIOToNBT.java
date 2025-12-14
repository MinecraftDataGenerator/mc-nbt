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
import com.dietrichpaul.mcnbt.primitive.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for deserializing NBT tags from a {@link java.nio.ByteBuffer}
 * using the standard NBT binary format (big-endian).
 * <p>
 * This class performs the inverse operation of {@link NBTToNIO} by reading
 * NBT structures from the buffer and constructing the corresponding tag
 * objects. All methods advance the buffer position by the number of bytes
 * consumed.
 * </p>
 */
public class NIOToNBT {
    private NIOToNBT() {
    }

    /**
     * Returns the {@link NIODeserializer} that can read the payload for the given
     * {@link NBTTagType} from a {@link ByteBuffer}.
     *
     * @param type the tag type to look up
     * @return a deserializer for the given type
     * @throws IllegalArgumentException if there is no deserializer mapped for the type
     */
    public static NIODeserializer<?> getDeserializerForType(NBTTagType type) {
        NIODeserializer<?> deserializer = DESERIALIZER_MAP.get(type);
        if (deserializer != null) {
            return deserializer;
        }

        throw new IllegalArgumentException("No deserializer for type: " + type.getName());
    }    private static final Map<NBTTagType, NIODeserializer<?>> DESERIALIZER_MAP = new HashMap<>() {{
        put(NBTTagType.BYTE, NIOToNBT::readByte);
        put(NBTTagType.SHORT, NIOToNBT::readShort);
        put(NBTTagType.INT, NIOToNBT::readInt);
        put(NBTTagType.LONG, NIOToNBT::readLong);
        put(NBTTagType.FLOAT, NIOToNBT::readFloat);
        put(NBTTagType.DOUBLE, NIOToNBT::readDouble);
        put(NBTTagType.BYTE_ARRAY, NIOToNBT::readByteArray);
        put(NBTTagType.INT_ARRAY, NIOToNBT::readIntArray);
        put(NBTTagType.LONG_ARRAY, NIOToNBT::readLongArray);
        put(NBTTagType.STRING, NIOToNBT::readString);
        put(NBTTagType.LIST, NIOToNBT::readList);
        put(NBTTagType.COMPOUND, NIOToNBT::readCompound);
    }};

    /**
     * Reads a single named NBT tag from the given {@link ByteBuffer}.
     * <p>
     * The method expects the buffer to be positioned at the beginning of a named tag:
     * first the tag ID (1 byte), then the tag name (2-byte unsigned length + UTF-8 bytes),
     * and finally the tag payload. If the tag ID is {@code TAG_End} ({@link NBTTagType#NULL}),
     * this method returns an {@link NBTTagIdentifiable} with {@code null} name and {@code null} tag.
     * </p>
     *
     * @param buffer the source byte buffer (big-endian)
     * @return the read named tag; returns {@code (null, null)} for {@code TAG_End}
     * @throws IllegalArgumentException if the tag type is unknown
     */
    public static NBTTagIdentifiable<?> readNamedTag(ByteBuffer buffer) {
        int tagTypeId = Byte.toUnsignedInt(buffer.get());
        NBTTagType tagType = NBTTagType.getTagById(tagTypeId);

        if (tagType == NBTTagType.NULL) {
            return new NBTTagIdentifiable<>(null, null);
        }

        NBTString nameTag = readString(buffer);
        String name = nameTag.asString();

        NIODeserializer<?> elementDeserializer = getDeserializerForType(tagType);
        NBTTag<?> tag = elementDeserializer.deserialize(buffer);

        return new NBTTagIdentifiable<>(name, tag);
    }

    /**
     * Reads a {@link NBTCompound} payload from the given buffer.
     * <p>
     * The buffer must be positioned at the first named entry of the compound.
     * Entries are read as named tags until a {@code TAG_End} is encountered.
     * </p>
     *
     * @param buffer the source buffer
     * @return the populated compound tag
     */
    public static NBTCompound readCompound(ByteBuffer buffer) {
        NBTCompound compound = new NBTCompound();
        NBTTagIdentifiable<?> identifiable;

        while ((identifiable = readNamedTag(buffer)).tag() != null) {
            compound.put(identifiable);
        }

        return compound;
    }

    /**
     * Reads an {@link NBTList} payload from the buffer.
     * <p>
     * Format: element type ID (1 byte), length (int), followed by that many
     * element payloads of the given type. If the element type is {@code TAG_End},
     * an empty list is returned.
     * </p>
     *
     * @param buffer the source buffer
     * @return the list tag containing all read elements
     * @throws IllegalArgumentException if the element type is unknown
     */
    public static NBTList<?> readList(ByteBuffer buffer) {
        int tagTypeId = Byte.toUnsignedInt(buffer.get());
        NBTTagType internType = NBTTagType.getTagById(tagTypeId);

        int len = buffer.getInt();

        List<NBTTag<?>> tags = new ArrayList<>(len);

        if (internType == NBTTagType.NULL) {
            return NBTList.of();
        }

        NIODeserializer<?> elementDeserializer = getDeserializerForType(internType);

        for (int i = 0; i < len; i++) {
            tags.add(elementDeserializer.deserialize(buffer));
        }

        return NBTList.of(tags, internType);
    }

    /**
     * Reads an {@link NBTString} payload from the buffer.
     * <p>
     * Format: 2-byte unsigned length (big-endian) followed by UTF-8 bytes.
     * </p>
     *
     * @param buffer the source buffer
     * @return the string tag; never {@code null}
     */
    public static NBTString readString(ByteBuffer buffer) {
        int len = Short.toUnsignedInt(buffer.getShort());
        if (len == 0) {
            return NBTString.of("");
        }
        byte[] buf = new byte[len];
        buffer.get(buf);
        return NBTString.of(new String(buf, StandardCharsets.UTF_8));
    }

    /**
     * Reads an {@link NBTLongArray} payload from the buffer.
     * <p>
     * Format: length (int) followed by that many 64-bit signed integers.
     * </p>
     *
     * @param buffer the source buffer
     * @return the long array tag
     */
    public static NBTLongArray readLongArray(ByteBuffer buffer) {
        int len = buffer.getInt();
        long[] array = new long[len];
        LongBuffer longBuffer = buffer.asLongBuffer();
        longBuffer.get(array);
        buffer.position(buffer.position() + len * 8);
        return NBTLongArray.of(array);
    }

    /**
     * Reads an {@link NBTIntArray} payload from the buffer.
     * <p>
     * Format: length (int) followed by that many 32-bit signed integers.
     * </p>
     *
     * @param buffer the source buffer
     * @return the int array tag
     */
    public static NBTIntArray readIntArray(ByteBuffer buffer) {
        int len = buffer.getInt();
        int[] array = new int[len];
        IntBuffer intBuffer = buffer.asIntBuffer();
        intBuffer.get(array);
        buffer.position(buffer.position() + len * 4);
        return NBTIntArray.of(array);
    }

    /**
     * Reads an {@link NBTByteArray} payload from the buffer.
     * <p>
     * Format: length (int) followed by that many bytes.
     * </p>
     *
     * @param buffer the source buffer
     * @return the byte array tag
     */
    public static NBTByteArray readByteArray(ByteBuffer buffer) {
        int len = buffer.getInt();
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return NBTByteArray.of(bytes);
    }

    /**
     * Reads an {@link NBTDouble} payload (IEEE 754 double, 8 bytes) from the buffer.
     *
     * @param buffer the source buffer
     * @return the double tag
     */
    public static NBTDouble readDouble(ByteBuffer buffer) {
        return NBTDouble.of(buffer.getDouble());
    }

    /**
     * Reads an {@link NBTFloat} payload (IEEE 754 float, 4 bytes) from the buffer.
     *
     * @param buffer the source buffer
     * @return the float tag
     */
    public static NBTFloat readFloat(ByteBuffer buffer) {
        return NBTFloat.of(buffer.getFloat());
    }

    /**
     * Reads an {@link NBTLong} payload (64-bit signed) from the buffer.
     *
     * @param buffer the source buffer
     * @return the long tag
     */
    public static NBTLong readLong(ByteBuffer buffer) {
        return NBTLong.of(buffer.getLong());
    }

    /**
     * Reads an {@link NBTInt} payload (32-bit signed) from the buffer.
     *
     * @param buffer the source buffer
     * @return the int tag
     */
    public static NBTInt readInt(ByteBuffer buffer) {
        return NBTInt.of(buffer.getInt());
    }

    /**
     * Reads an {@link NBTShort} payload (16-bit signed) from the buffer.
     *
     * @param buffer the source buffer
     * @return the short tag
     */
    public static NBTShort readShort(ByteBuffer buffer) {
        return NBTShort.of(buffer.getShort());
    }

    /**
     * Reads an {@link NBTByte} payload (8-bit signed) from the buffer.
     *
     * @param buffer the source buffer
     * @return the byte tag
     */
    public static NBTByte readByte(ByteBuffer buffer) {
        return NBTByte.of(buffer.get());
    }



}
