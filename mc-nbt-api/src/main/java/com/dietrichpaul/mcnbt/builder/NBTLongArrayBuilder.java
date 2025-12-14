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

package com.dietrichpaul.mcnbt.builder;

import com.dietrichpaul.mcnbt.NBTLongArray;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

/**
 * Builder for efficiently creating {@link NBTLongArray} instances.
 * <p>
 * Uses a primitive {@link TLongList} internally to avoid boxing overhead.
 * Add values incrementally via {@link #add(long)} or in bulk via
 * {@link #add(long[])}. Call {@link #build()} to obtain an {@link NBTLongArray}
 * that takes ownership of the accumulated list.
 */
public class NBTLongArrayBuilder {
    private final TLongList content;

    /**
     * Creates a new builder with a default initial capacity.
     */
    public NBTLongArrayBuilder() {
        this.content = new TLongArrayList();
    }

    /**
     * Creates a new builder with the specified expected capacity.
     *
     * @param initialCapacity anticipated number of elements to reduce resizes
     */
    public NBTLongArrayBuilder(int initialCapacity) {
        this.content = new TLongArrayList(initialCapacity);
    }

    /**
     * Appends a single long value.
     *
     * @param value the value to add
     * @return this builder for chaining
     */
    public NBTLongArrayBuilder add(long value) {
        content.add(value);
        return this;
    }

    /**
     * Appends all values from the given array in order.
     *
     * @param values the values to add (must not be null)
     * @return this builder for chaining
     */
    public NBTLongArrayBuilder add(long[] values) {
        content.add(values);
        return this;
    }

    /**
     * Builds an {@link NBTLongArray} backed by the accumulated content.
     * The returned instance takes ownership of the internal list reference.
     *
     * @return a new NBTLongArray with all added values
     */
    public NBTLongArray build() {
        return new NBTLongArray(content);
    }
}