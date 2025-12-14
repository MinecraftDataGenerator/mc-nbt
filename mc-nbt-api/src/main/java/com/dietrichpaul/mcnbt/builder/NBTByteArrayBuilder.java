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

import com.dietrichpaul.mcnbt.NBTByteArray;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

/**
 * Builder for creating {@link NBTByteArray} instances efficiently.
 * <p>
 * Internally this builder accumulates values in a {@link TByteList} to avoid
 * boxing overhead. Values can be appended one-by-one via {@link #add(byte)} or
 * in bulk via {@link #add(byte[])}. The builder supports fluent chaining and
 * can be reused until {@link #build()} is called to create an immutable
 * snapshot in the form of an {@link NBTByteArray} that owns the accumulated
 * list.
 * <p>
 * If you already have a primitive byte array, consider using
 * {@link NBTByteArray#of(byte...)} for a direct one-liner. Use this builder
 * when you progressively assemble the contents or when you want to control the
 * initial capacity to reduce reallocations.
 */
public class NBTByteArrayBuilder {
    private final TByteList content;

    /**
     * Creates a new builder with a default initial capacity.
     */
    public NBTByteArrayBuilder() {
        this.content = new TByteArrayList();
    }

    /**
     * Creates a new builder with a specified initial capacity.
     *
     * @param initialCapacity the anticipated number of elements to minimize
     *                        internal resizes
     */
    public NBTByteArrayBuilder(int initialCapacity) {
        this.content = new TByteArrayList(initialCapacity);
    }

    /**
     * Appends a single byte value.
     *
     * @param value the value to add
     * @return this builder for chaining
     */
    public NBTByteArrayBuilder add(byte value) {
        content.add(value);
        return this;
    }

    /**
     * Appends all values from the given array in order.
     *
     * @param values the values to add (must not be null)
     * @return this builder for chaining
     */
    public NBTByteArrayBuilder add(byte[] values) {
        content.add(values);
        return this;
    }

    /**
     * Builds a new {@link NBTByteArray} backed by the accumulated content.
     * <p>
     * The returned instance takes ownership of the internal {@link TByteList}
     * reference used by this builder.
     *
     * @return a new NBTByteArray containing all added values
     */
    public NBTByteArray build() {
        return new NBTByteArray(content);
    }
}