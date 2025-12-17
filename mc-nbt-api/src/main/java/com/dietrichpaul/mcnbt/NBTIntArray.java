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

import com.dietrichpaul.mcnbt.builder.NBTIntArrayBuilder;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * Represents an NBT tag containing an array of 32-bit integers.
 * <p>
 * The array is internally stored inside a {@link TIntList} implementation
 * (Trove), which avoids object boxing and provides high-performance
 * operations on primitive values. This makes it significantly more
 * efficient than using {@code List<Integer>}.
 * <p>
 * Users are allowed to fully modify the underlying list, either through
 * manipulation methods provided by this class (such as {@link #add(int)},
 * {@link #set(int, int)}, {@link #remove(int)}), or by replacing the entire
 * content via {@link #setContent(TIntList)}.
 * <p>
 * For iteration, the preferred method is {@link #intIterator()} because it
 * returns a primitive iterator without boxing. The methods
 * {@link #iterator()} and {@link #spliterator()} are provided only for API
 * compatibility and are marked as deprecated, as they necessarily box
 * values into {@code Integer}.
 */
public class NBTIntArray implements NBTIterable<Integer> {

    private TIntList content;

    /**
     * Creates a new NBTIntArray instance.
     *
     * @param content the underlying primitive list (must not be null)
     */
    public NBTIntArray(TIntList content) {
        this.content = Objects.requireNonNull(content, "content");
    }

    /**
     * Creates a new {@link NBTIntArrayBuilder} to fluently construct an
     * {@link NBTIntArray} instance.
     *
     * @return a new builder for {@link NBTIntArray}
     */
    public static NBTIntArrayBuilder builder() {
        return new NBTIntArrayBuilder();
    }

    /**
     * Creates a new NBTIntArray from an int array.
     *
     * @param array initial values
     * @return new instance
     */
    public static NBTIntArray of(int... array) {
        return new NBTIntArray(array.length == 0 ? new TIntArrayList() : new TIntArrayList(array));
    }

    /**
     * Returns the underlying primitive list.
     * Modifications to this list directly affect the NBT data.
     *
     * @return primitive integer list
     */
    public TIntList getContent() {
        return content;
    }

    /**
     * Replaces the entire underlying list.
     * Useful when bulk-swapping structures or reusing existing Trove lists.
     *
     * @param content new list (must not be null)
     */
    public void setContent(TIntList content) {
        this.content = Objects.requireNonNull(content, "content");
    }

    /**
     * Adds an int value to the end of the array.
     *
     * @param value the value to add
     */
    public void add(int value) {
        content.add(value);
    }

    /**
     * Inserts an int value at the specified index.
     *
     * @param index position to insert at
     * @param value the value to insert
     */
    public void add(int index, int value) {
        content.insert(index, value);
    }

    /**
     * Removes and returns the int at the specified index.
     *
     * @param index index to remove
     * @return the removed value
     */
    public int remove(int index) {
        return content.removeAt(index);
    }

    /**
     * Retrieves the int value at the given index.
     *
     * @param index array index
     * @return the value at that index
     */
    public int get(int index) {
        return content.get(index);
    }

    /**
     * Sets the int value at the specified index.
     *
     * @param index array index
     * @param value new value to set
     */
    public void set(int index, int value) {
        content.set(index, value);
    }

    @Override
    public Integer getEntry(int i) {
        return get(i);
    }

    /**
     * Returns the number of ints currently stored in this array.
     *
     * @return size of the array
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
     * Ensures that the underlying list has enough capacity to store at least
     * {@code capacity} values without resizing (when backed by {@link TIntArrayList}).
     *
     * @param capacity minimum capacity to ensure
     */
    public void ensureCapacity(int capacity) {
        if (content instanceof TIntArrayList list) {
            list.ensureCapacity(capacity);
        }
    }

    /**
     * Returns a primitive iterator over the underlying list.
     * <p>
     * This avoids boxing and should be preferred for any performance-sensitive
     * iteration.
     *
     * @return primitive iterator
     */
    public TIntIterator intIterator() {
        return content.iterator();
    }

    /**
     * Returns a boxed iterator over the integers.
     * <p>
     * This method is slower than {@link #intIterator()} because each value is boxed.
     * It is provided for API completeness and integration with enhanced for-loops.
     */
    @Deprecated(forRemoval = false)
    @Override
    public Iterator<Integer> iterator() {
        TIntIterator it = content.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Integer next() {
                return it.next();
            }
        };
    }

    /**
     * Returns a new primitive int[] copying the current contents.
     *
     * @return int array
     */
    public int[] toIntArray() {
        return content.toArray();
    }

    /**
     * Returns a boxed spliterator.
     * <p>
     * As with {@link #iterator()}, this should generally be avoided
     * due to boxing costs.
     */
    @Deprecated(forRemoval = false)
    @Override
    public Spliterator<Integer> spliterator() {
        return Spliterators.spliterator(iterator(), size(), Spliterator.ORDERED | Spliterator.NONNULL);
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.INT_ARRAY;
    }
}
