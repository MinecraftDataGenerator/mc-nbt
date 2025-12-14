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

import com.dietrichpaul.mcnbt.NBTTag;

import java.nio.ByteBuffer;

/**
 * A function which transform a given sequence of bytes
 * provided by a {@link ByteBuffer} into an {@link NBTTag} of some kind.
 *
 * @param <T> The type of {@link NBTTag} being deserialized.
 */
@FunctionalInterface
public interface NIODeserializer<T extends NBTTag<?>> {
    /**
     * Reads the payload of an NBT tag from the provided {@link ByteBuffer} and
     * returns a new tag instance. Implementations must respect the byte order of
     * the buffer.
     *
     * @param buffer the source byte buffer (byte order defines endianness)
     * @return the deserialized NBT tag
     */
    T deserialize(ByteBuffer buffer);

}
