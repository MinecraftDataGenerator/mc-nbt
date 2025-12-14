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

public interface NBTTag<T> {
    /**
     * Retrieves the underlying type of this NBT tag.
     *
     * @return The underlying type of this NBT tag.
     */
    NBTTagType getTagType();

    /**
     * Retrieves this NBT tag's value as an {@link NBTIterable}.
     *
     * @return The value of this NBT tag as a {@link NBTIterable}.
     * @throws IllegalArgumentException if this NBT tag cannot be parsed into a {@link NBTIterable}.
     */
    default NBTIterable<T> asIterable() {
        throw new IllegalArgumentException("Cannot parse " + getTagType() + " as iterable");
    }

    /**
     * Retrieves this NBT tag's value as an {@link NBTCompound}.
     *
     * @return The value of this NBT tag as a {@link NBTCompound}.
     * @throws IllegalArgumentException if this NBT tag cannot be parsed into a {@link NBTCompound}.
     */
    default NBTCompound asCompound() {
        throw new IllegalArgumentException("Cannot parse " + getTagType() + " as compound");
    }

    /**
     * Retrieves this NBT tag's value as an {@link NBTList}.
     *
     * @return The value of this NBT tag as a {@link NBTList}.
     * @throws IllegalArgumentException if this NBT tag cannot be parsed into a {@link NBTList}.
     */
    default NBTList<?> asList() {
        throw new IllegalArgumentException("Cannot parse " + getTagType() + " as list");
    }
}
