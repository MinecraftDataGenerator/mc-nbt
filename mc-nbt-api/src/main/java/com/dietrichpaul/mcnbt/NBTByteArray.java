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

import com.dietrichpaul.mcnbt.builder.NBTByteArrayBuilder;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * Represents an NBT tag containing an array of bytes.
 * <p>
 * The array is internally stored in a {@link TByteList}, which avoids boxing
 * and provides extremely fast primitive operations. This class mirrors the
 * structure and behavior of {@code NBTIntArray} and {@code NBTLongArray}.
 * <p>
 * The underlying list can be manipulated directly through mutation methods
 * (e.g. {@link #add(byte)}, {@link #set(int, byte)}, {@link #remove(int)}),
 * or replaced entirely using {@link #setContent(TByteList)}. This design
 * gives full control and avoids unnecessary copying.
 * <p>
 * Primitive iteration through {@link #byteIterator()} is strongly preferred.
 * The boxed {@link #iterator()} and {@link #spliterator()} methods exist only
 * for API compatibility and are marked as deprecated due to boxing overhead.
 */
public class NBTByteArray implements NBTIterable<Byte> {

    private TByteList content;

    /**
     * Creates a new NBTByteArray using the provided primitive list.
     *
     * @param content the underlying byte list (must not be null)
     */
    public NBTByteArray(TByteList content) {
        this.content = Objects.requireNonNull(content, "content");
    }

    /**
     * Creates a new {@link NBTByteArrayBuilder} to fluently construct an
     * {@link NBTByteArray} instance.
     *
     * @return a new builder for {@link NBTByteArray}
     */
    public static NBTByteArrayBuilder builder() {
        return new NBTByteArrayBuilder();
    }

    /**
     * Creates a new NBTByteArray from a byte array.
     * If the array is empty, a new list with default capacity is created.
     *
     * @param array initial byte values
     * @return a new NBTByteArray instance
     */
    public static NBTByteArray of(byte... array) {
        return new NBTByteArray(array.length == 0 ? new TByteArrayList() : new TByteArrayList(array));
    }

    /**
     * Returns the underlying primitive byte list.
     * Direct modifications affect the NBT structure.
     *
     * @return the internal TByteList
     */
    public TByteList getContent() {
        return content;
    }

    /**
     * Replaces the underlying byte list with a new one.
     *
     * @param content the new byte list (must not be null)
     */
    public void setContent(TByteList content) {
        this.content = Objects.requireNonNull(content, "content");
    }

    /**
     * Adds a byte to the end of the array.
     *
     * @param value the byte to add
     */
    public void add(byte value) {
        content.add(value);
    }

    /**
     * Inserts a byte at the given index.
     *
     * @param index the position to insert at
     * @param value the byte to insert
     */
    public void add(int index, byte value) {
        content.insert(index, value);
    }

    /**
     * Removes the byte at the specified index.
     *
     * @param index index to remove
     * @return the removed byte
     */
    public byte remove(int index) {
        return content.removeAt(index);
    }

    /**
     * Retrieves the byte at the given index.
     *
     * @param index array index
     * @return the byte at that index
     */
    public byte get(int index) {
        return content.get(index);
    }

    /**
     * Sets the byte value at the specified index.
     *
     * @param index array index
     * @param value new byte value
     */
    public void set(int index, byte value) {
        content.set(index, value);
    }

    @Override
    public Byte getEntry(int i) {
        return get(i);
    }

    /**
     * Returns the number of bytes currently stored in this array.
     *
     * @return number of bytes stored
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
     * Ensures that the underlying list has enough capacity to store
     * at least {@code capacity} values without resizing.
     *
     * @param capacity minimum capacity to ensure
     */
    public void ensureCapacity(int capacity) {
        if (content instanceof TByteArrayList list) {
            list.ensureCapacity(capacity);
        }
    }

    /**
     * Returns a primitive iterator over the byte array.
     * <p>
     * This is the preferred way to iterate, as it avoids boxing overhead.
     *
     * @return primitive byte iterator
     */
    public TByteIterator byteIterator() {
        return content.iterator();
    }

    /**
     * Returns a boxed iterator over byte values.
     * <p>
     * This method allocates boxed {@link Byte} objects and should only be used
     * when interacting with APIs requiring {@link Iterator}.
     */
    @Deprecated(forRemoval = false)
    @Override
    public Iterator<Byte> iterator() {
        TByteIterator it = content.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Byte next() {
                return it.next();
            }
        };
    }

    /**
     * Returns a boxed spliterator for integration with Stream APIs.
     * <p>
     * This method boxes each value and should be avoided if performance matters.
     *
     * @return a {@link Spliterator} over boxed {@link Byte} values
     */
    @Deprecated(forRemoval = false)
    @Override
    public Spliterator<Byte> spliterator() {
        return Spliterators.spliterator(iterator(), size(), Spliterator.ORDERED | Spliterator.NONNULL);
    }

    /**
     * Creates a new primitive array containing all values of this NBTByteArray.
     *
     * @return a newly created byte[]
     */
    public byte[] toByteArray() {
        return content.toArray();
    }

    /**
     * Returns the {@link NBTTagType} that identifies this tag as a byte array.
     *
     * @return {@link NBTTagType#BYTE_ARRAY}
     */
    @Override
    public NBTTagType getTagType() {
        return NBTTagType.BYTE_ARRAY;
    }
}
