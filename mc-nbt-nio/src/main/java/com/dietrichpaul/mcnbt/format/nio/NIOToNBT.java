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

public class NIOToNBT {

    private static final Map<NBTTagType, NIODeserializer<?>> DESERIALIZER_MAP = new HashMap<>() {{
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

    public static NIODeserializer<?> getDeserializerForType(NBTTagType type) {
        NIODeserializer<?> deserializer = DESERIALIZER_MAP.get(type);
        if (deserializer != null) {
            return deserializer;
        }

        throw new IllegalArgumentException("No deserializer for type: " + type.getName());
    }

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

    public static NBTCompound readCompound(ByteBuffer buffer) {
        NBTCompound compound = new NBTCompound();
        NBTTagIdentifiable<?> identifiable;

        while ((identifiable = readNamedTag(buffer)).tag() != null) {
            compound.put(identifiable);
        }

        return compound;
    }

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

    public static NBTString readString(ByteBuffer buffer) {
        int len = Short.toUnsignedInt(buffer.getShort());
        if (len == 0) {
            return NBTString.of("");
        }
        byte[] buf = new byte[len];
        buffer.get(buf);
        return NBTString.of(new String(buf, StandardCharsets.UTF_8));
    }

    public static NBTLongArray readLongArray(ByteBuffer buffer) {
        int len = buffer.getInt();
        long[] array = new long[len];
        LongBuffer longBuffer = buffer.asLongBuffer();
        longBuffer.get(array);
        buffer.position(buffer.position() + len * 8);
        return NBTLongArray.of(array);
    }

    public static NBTIntArray readIntArray(ByteBuffer buffer) {
        int len = buffer.getInt();
        int[] array = new int[len];
        IntBuffer intBuffer = buffer.asIntBuffer();
        intBuffer.get(array);
        buffer.position(buffer.position() + len * 4);
        return NBTIntArray.of(array);
    }

    public static NBTByteArray readByteArray(ByteBuffer buffer) {
        int len = buffer.getInt();
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return NBTByteArray.of(bytes);
    }

    public static NBTDouble readDouble(ByteBuffer buffer) {
        return NBTDouble.of(buffer.getDouble());
    }

    public static NBTFloat readFloat(ByteBuffer buffer) {
        return NBTFloat.of(buffer.getFloat());
    }

    public static NBTLong readLong(ByteBuffer buffer) {
        return NBTLong.of(buffer.getLong());
    }

    public static NBTInt readInt(ByteBuffer buffer) {
        return NBTInt.of(buffer.getInt());
    }

    public static NBTShort readShort(ByteBuffer buffer) {
        return NBTShort.of(buffer.getShort());
    }

    public static NBTByte readByte(ByteBuffer buffer) {
        return NBTByte.of(buffer.get());
    }

}
