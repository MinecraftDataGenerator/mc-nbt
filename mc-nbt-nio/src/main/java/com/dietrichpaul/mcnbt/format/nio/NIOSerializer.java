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
 * A function which transform a given {@link NBTTag} of some kind
 * into a sequence of bytes, which are stored into a {@link ByteBuffer}.
 *
 * @param <T> The type of {@link NBTTag} being deserialized.
 */
@FunctionalInterface
public interface NIOSerializer<T extends NBTTag<?>> {
    /**
     * Writes the payload of an NBT tag into the provided {@link ByteBuffer}.
     * Implementations must respect the byte order of the buffer.
     *
     * @param buffer the target byte buffer (byte order defines endianness)
     * @param tag    the NBT tag whose payload should be written
     */
    void serialize(ByteBuffer buffer, T tag);
}