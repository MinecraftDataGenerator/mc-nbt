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

import com.dietrichpaul.mcnbt.builder.NBTLongArrayBuilder;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * Represents an NBT tag containing an array of 64-bit integers.
 * <p>
 * The array is internally stored using a {@link TLongList} implementation,
 * which keeps values in a primitive form and therefore avoids any boxing
 * overhead. This provides much higher performance compared to an
 * implementation based on {@code List<Long>} or similar boxed collections.
 * <p>
 * The internal list can be freely manipulated. Users may insert, replace,
 * and remove values via methods such as {@link #add(long)},
 * {@link #set(int, long)}, and {@link #remove(int)}, or replace the entire
 * underlying list through {@link #setContent(TLongList)}.
 * <p>
 * For iteration, {@link #longIterator()} is the preferred mechanism as it
 * yields a primitive iterator with no allocation or boxing. The boxed
 * variants {@link #iterator()} and {@link #spliterator()} are provided for
 * compatibility with generic Java APIs and enhanced for-loops, but they
 * should be avoided in performance-sensitive scenarios.
 */
public class NBTLongArray implements NBTIterable<Long> {
    private TLongList content;

    /**
     * Constructs a new NBTLongArray using the provided primitive list.
     *
     * @param content the underlying long list (must not be null)
     */
    public NBTLongArray(TLongList content) {
        this.content = Objects.requireNonNull(content, "content");
    }

    /**
     * Creates a new {@link NBTLongArrayBuilder} instance.
     *
     * @return A new {@link NBTLongArrayBuilder} instance.
     */
    public static NBTLongArrayBuilder builder() {
        return new NBTLongArrayBuilder();
    }

    /**
     * Creates a new NBTLongArray from a primitive long array.
     *
     * @param array initial 64-bit values
     * @return a new NBTLongArray instance
     */
    public static NBTLongArray of(long... array) {
        return new NBTLongArray(array.length == 0 ? new TLongArrayList() : new TLongArrayList(array));
    }

    /**
     * Returns the underlying primitive long list.
     * Modifying this list directly affects the represented NBT structure.
     *
     * @return the internal TLongList
     */
    public TLongList getContent() {
        return content;
    }

    /**
     * Replaces the underlying long list entirely.
     * Useful when reusing pre-existing Trove lists or bulk-swapping values.
     *
     * @param content the new long list (must not be null)
     */
    public void setContent(TLongList content) {
        this.content = Objects.requireNonNull(content, "content");
    }

    /**
     * Appends a long value to the end of the array.
     *
     * @param value the long to add
     */
    public void add(long value) {
        content.add(value);
    }

    /**
     * Inserts a long value at the specified index.
     *
     * @param index insertion index
     * @param value value to insert
     */
    public void add(int index, long value) {
        content.insert(index, value);
    }

    /**
     * Removes the long value at the given index.
     *
     * @param index index to remove
     * @return the removed long value
     */
    public long remove(int index) {
        return content.removeAt(index);
    }

    /**
     * Returns the value stored at the given index.
     *
     * @param index array index
     * @return the long value at that position
     */
    public long get(int index) {
        return content.get(index);
    }

    @Override
    public Long getEntry(int i) {
        return get(i);
    }

    /**
     * Replaces the value at the given index with a new long.
     *
     * @param index array index
     * @param value new value
     */
    public void set(int index, long value) {
        content.set(index, value);
    }

    /**
     * Retrieves the number of values stored in this array.
     *
     * @return number of stored long values
     */
    public int size() {
        return content.size();
    }

    /**
     * Removes all values from the array.
     */
    public void clear() {
        content.clear();
    }

    /**
     * Ensures that the internal list has room for at least {@code capacity}
     * values without resizing.
     *
     * @param capacity minimum required storage capacity
     */
    public void ensureCapacity(int capacity) {
        if (content instanceof TLongArrayList list) {
            list.ensureCapacity(capacity);
        }
    }

    /**
     * Returns a primitive iterator over the long values.
     * <p>
     * This method is allocation-free and should be preferred whenever possible.
     *
     * @return a primitive long iterator
     */
    public TLongIterator longIterator() {
        return content.iterator();
    }

    /**
     * Returns a boxed iterator that yields {@link Long} instances.
     * <p>
     * This method allocates boxed values and should be avoided in hot loops.
     */
    @Deprecated(forRemoval = false)
    @Override
    public Iterator<Long> iterator() {
        TLongIterator it = content.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Long next() {
                return it.next();
            }
        };
    }

    /**
     * Returns a new primitive long array containing a copy of the stored values.
     *
     * @return a newly created long[]
     */
    public long[] toLongArray() {
        return content.toArray();
    }

    /**
     * Returns a boxed spliterator for integration with Stream APIs.
     * <p>
     * As with {@link #iterator()}, each value must be boxed and the result is
     * therefore slower than using {@link #longIterator()}.
     */
    @Deprecated(forRemoval = false)
    @Override
    public Spliterator<Long> spliterator() {
        return Spliterators.spliterator(iterator(), size(), Spliterator.ORDERED | Spliterator.NONNULL);
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.LONG_ARRAY;
    }
}
