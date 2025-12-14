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

import com.dietrichpaul.mcnbt.NBTIntArray;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * Builder for creating {@link NBTIntArray} instances efficiently.
 * <p>
 * Values are accumulated in a primitive {@link TIntList} to avoid boxing.
 * You can add values one-by-one with {@link #add(int)} or in bulk with
 * {@link #add(int[])}. Use {@link #build()} to produce an {@link NBTIntArray}
 * that owns the accumulated list.
 */
public class NBTIntArrayBuilder {
    private final TIntList content;

    /**
     * Creates a new builder with a default initial capacity.
     */
    public NBTIntArrayBuilder() {
        this.content = new TIntArrayList();
    }

    /**
     * Creates a new builder with the given expected capacity.
     *
     * @param initialCapacity the anticipated number of elements to reduce resizes
     */
    public NBTIntArrayBuilder(int initialCapacity) {
        this.content = new TIntArrayList(initialCapacity);
    }

    /**
     * Appends a single int value.
     *
     * @param value the value to add
     * @return this builder for chaining
     */
    public NBTIntArrayBuilder add(int value) {
        content.add(value);
        return this;
    }

    /**
     * Appends all values from the given array in order.
     *
     * @param values the values to add (must not be null)
     * @return this builder for chaining
     */
    public NBTIntArrayBuilder add(int[] values) {
        content.add(values);
        return this;
    }

    /**
     * Builds an {@link NBTIntArray} backed by the accumulated content.
     * The returned instance takes ownership of the internal list reference.
     *
     * @return a new NBTIntArray with all added values
     */
    public NBTIntArray build() {
        return new NBTIntArray(content);
    }
}