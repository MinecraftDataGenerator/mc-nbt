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
import com.dietrichpaul.mcnbt.primitive.NBTString;

import java.nio.ByteBuffer;

/**
 * Provides a conservative estimate of the maximum number of bytes required to serialize
 * an NBT structure into a {@link ByteBuffer}.
 *
 * <p>This is useful for efficient buffer allocation (e.g., with {@code ByteBuffer.allocate(maxSize)}).
 * The estimate intentionally errs on the high side to prevent buffer overflows.
 */
public class NBTSizeProvider {

    private NBTSizeProvider() {
    }

    /**
     * Calculates the maximum possible size of a serialized, named NBT tag.
     * This is the primary entry point for estimating the root tag size.
     *
     * @param tagIdentifiable the named tag (e.g., the root compound)
     * @return the maximum required number of bytes
     */
    public static int estimateMaxSize(NBTTagIdentifiable<?> tagIdentifiable) {
        int size = 0;
        NBTTag<?> tag = tagIdentifiable.tag();

        // 1. Tag ID (1 byte)
        size += Byte.BYTES;

        // 2. Tag name (2-byte length + up to 4 bytes per character for UTF-8)
        size += estimateStringPayloadSize(tagIdentifiable.name());

        // 3. Tag payload
        size += estimatePayloadSize(tag);

        return size;
    }

    /**
     * Calculates the maximum possible size of a serialized NBT tag payload (excluding name and ID).
     *
     * @param tag the tag to estimate
     * @return the maximum required number of bytes for the payload
     */
    public static int estimatePayloadSize(NBTTag<?> tag) {
        if (tag == null || tag.getTagType() == NBTTagType.NULL) {
            return 0;
        }

        return switch (tag.getTagType()) {
            // Primitive numeric types (fixed size)
            case BYTE -> Byte.BYTES;
            case SHORT -> Short.BYTES;
            case INT -> Integer.BYTES;
            case LONG -> Long.BYTES;
            case FLOAT -> Float.BYTES;
            case DOUBLE -> Double.BYTES;

            // Array types (length + content)
            case BYTE_ARRAY -> estimateByteArrayPayloadSize((NBTByteArray) tag);
            case INT_ARRAY -> estimateIntArrayPayloadSize((NBTIntArray) tag);
            case LONG_ARRAY -> estimateLongArrayPayloadSize((NBTLongArray) tag);

            // String (length + content)
            case STRING -> estimateStringPayloadSize(((NBTString) tag).asString());

            // Structured types (length + content)
            case LIST -> estimateListPayloadSize((NBTList<?>) tag);
            case COMPOUND -> estimateCompoundPayloadSize((NBTCompound) tag);

            default ->
                throw new IllegalArgumentException("Unknown tag type for size estimation: " + tag.getTagType().getName());
        };
    }

    /**
     * Calculates the maximum size for a string payload (2-byte length + UTF-8 content).
     * <p>The maximum length for an NBT string is 65,535 bytes; this estimate assumes up to
     * 4 bytes per Java character in UTF-8.</p>
     *
     * @param s the string value (may be {@code null})
     * @return the maximum required number of bytes for the serialized string payload
     */
    public static int estimateStringPayloadSize(String s) {
        if (s == null || s.isEmpty()) {
            return Short.BYTES; // 2 bytes for length=0
        }
        // 2 bytes for length + (worst case: 4 bytes per Java char for UTF-8)
        return Short.BYTES + s.length() * 4;
    }

    private static int estimateByteArrayPayloadSize(NBTByteArray tag) {
        // 4 bytes for length + byte array content
        return Integer.BYTES + tag.getContent().size();
    }

    private static int estimateIntArrayPayloadSize(NBTIntArray tag) {
        // 4 bytes for length + (count * 4 bytes per int)
        return Integer.BYTES + tag.getContent().size() * Integer.BYTES;
    }

    private static int estimateLongArrayPayloadSize(NBTLongArray tag) {
        // 4 bytes for length + (count * 8 bytes per long)
        return Integer.BYTES + tag.getContent().size() * Long.BYTES;
    }

    private static int estimateListPayloadSize(NBTList<?> list) {
        // 1 byte for internal tag ID + 4 bytes for list length (int)
        int size = Byte.BYTES + Integer.BYTES;

        // If the list is empty, the internal type is NULL (0x00), and size is 5.
        if (list.size() > 0) {
            // Sum of payloads of all elements
            for (NBTTag<?> tag : list) {
                size += estimatePayloadSize(tag);
            }
        }
        return size;
    }

    private static int estimateCompoundPayloadSize(NBTCompound compound) {
        int size = 0;
        // Sum of the maximum sizes of all contained named tags
        for (NBTTagIdentifiable<?> identifiable : compound) {
            size += estimateMaxSize(identifiable);
        }
        // 1 byte for the TAG_End (0x00) terminator
        size += Byte.BYTES;
        return size;
    }
}