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

import com.dietrichpaul.mcnbt.builder.NBTCompoundBuilder;

import java.util.*;

/**
 * Represents a **named compound NBT tag** (TAG_Compound) that stores multiple
 * {@link NBTTagIdentifiable} elements in insertion order.
 * <p>
 * This implementation provides:
 * <ul>
 * <li>O(1) lookup by name via a {@link Map}</li>
 * <li>O(1) access by index via a {@link List}</li>
 * <li>Efficient removal in O(1) amortized time using a swap-last trick</li>
 * <li>Iteration preserving insertion order without creating additional wrapper objects</li>
 * </ul>
 *
 * <p>
 * This class is enhanced with a Fluent DSL for both creation (via the nested {@link NBTCompoundBuilder})
 * and safe read access (via {@code get...()} methods), providing a clean, readable, and type-safe API.
 * </p>
 */
public class NBTCompound implements NBTIterable<NBTTagIdentifiable<?>> {

    /**
     * Map for O(1) lookup of tags by name
     */
    private final Map<String, IndexedTag> tagMap;
    /**
     * List for O(1) access by index and iteration in insertion order
     */
    private final List<NBTTagIdentifiable<?>> tagList;

    private NBTCompound(Map<String, IndexedTag> tagMap, List<NBTTagIdentifiable<?>> tagList) {
        this.tagMap = tagMap;
        this.tagList = tagList;
    }

    /**
     * Constructs an empty NBT Compound with specified initial capacities.
     *
     * @param mapCapacity the initial capacity of the backing map
     * @param loadFactor  the load factor of the backing map
     * @param listSize    the initial capacity of the backing list
     */
    public NBTCompound(int mapCapacity, float loadFactor, int listSize) {
        this(new LinkedHashMap<>(mapCapacity, loadFactor), new ArrayList<>(listSize));
    }

    /**
     * Constructs an empty NBT Compound using default capacities.
     */
    public NBTCompound() {
        this(new LinkedHashMap<>(), new ArrayList<>());
    }

    /**
     * Returns a new {@link NBTCompoundBuilder} instance to fluently construct an
     * {@link NBTCompound}.
     *
     * @return a new builder for {@code NBTCompound}
     * @see NBTCompoundBuilder
     */
    public static NBTCompoundBuilder builder() {
        return new NBTCompoundBuilder();
    }

    /**
     * Returns this instance as an {@link NBTCompound}.
     * <p>
     * Convenience override to support fluent usage through the {@link NBTIterable}
     * hierarchy without additional casts.
     *
     * @return this compound instance
     */
    @Override
    public NBTCompound asCompound() {
        return this;
    }

    /**
     * Adds or replaces a tag in this compound.
     * <p>
     * If a tag with the same name already exists, it will be replaced in-place
     * while maintaining its original index in the list.
     * If it is a new tag, it is appended to the end of the list.
     *
     * @param tag the {@link NBTTagIdentifiable} to store
     */
    public void put(NBTTagIdentifiable<?> tag) {
        IndexedTag existing = tagMap.get(tag.name());
        if (existing != null) {
            // Replace existing tag at the same index
            tagMap.put(tag.name(), new IndexedTag(tag, existing.index));
            tagList.set(existing.index, tag);
        }
        else {
            // New tag: append to the end
            int index = tagList.size();
            tagList.add(tag);
            tagMap.put(tag.name(), new IndexedTag(tag, index));
        }
    }

    /**
     * Adds or replaces a tag in this compound by providing a name and an NBT tag.
     * <p>
     * Convenience method that wraps the name and tag into a {@link NBTTagIdentifiable}.
     *
     * @param name the name of the tag
     * @param tag  the {@link NBTTag} to store
     */
    public void put(String name, NBTTag<?> tag) {
        put(new NBTTagIdentifiable<>(name, tag));
    }

    /**
     * Retrieves a tag by its name.
     *
     * @param name the name of the tag
     * @return the {@link NBTTag} associated with the given name, or {@code null} if not present
     */
    public NBTTag<?> get(String name) {
        IndexedTag indexed = tagMap.get(name);
        return indexed != null ? indexed.getTag().tag() : null;
    }

    /**
     * Retrieves a tag by its index in insertion order.
     *
     * @param index the zero-based index of the tag
     * @return the {@link NBTTag} at the given index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public NBTTag<?> get(int index) {
        return tagList.get(index).tag();
    }

    /**
     * Removes a tag by its name.
     * <p>
     * Removal is performed in O(1) amortized time using the swap-last trick:
     * the last element in the list is swapped into the removed element's position,
     * and its index in the map is updated accordingly.
     *
     * @param name the name of the tag to remove
     * @return {@code true} if a tag was removed, {@code false} if no tag existed with the given name
     */
    public boolean remove(String name) {
        IndexedTag indexed = tagMap.remove(name);
        if (indexed == null)
            return false;

        int lastIndex = tagList.size() - 1;
        if (indexed.index != lastIndex) {
            // Swap last element into the removed slot
            NBTTagIdentifiable<?> lastTag = tagList.get(lastIndex);
            tagList.set(indexed.index, lastTag);

            // Update index in Map for swapped element
            tagMap.get(lastTag.name()).index = indexed.index;
        }
        tagList.remove(lastIndex);
        return true;
    }

    /**
     * Returns an iterator over the {@link NBTTagIdentifiable} elements
     * in insertion order.
     * <p>
     * Iteration is efficient and does not create new wrapper objects.
     *
     * @return an {@link Iterator} over the tags
     */
    @Override
    public Iterator<NBTTagIdentifiable<?>> iterator() {
        return tagList.iterator();
    }

    /**
     * Returns a {@link Spliterator} over the {@link NBTTagIdentifiable} elements
     * in insertion order.
     * <p>
     * Useful for parallel streaming operations.
     *
     * @return a {@link Spliterator} over the tags
     */
    @Override
    public Spliterator<NBTTagIdentifiable<?>> spliterator() {
        return tagList.spliterator();
    }

    /**
     * Returns the {@link NBTTagType} of this compound.
     *
     * @return {@link NBTTagType#COMPOUND}
     */
    @Override
    public NBTTagType getTagType() {
        return NBTTagType.COMPOUND;
    }

    /**
     * Returns the internal map used for O(1) lookups of tags by name.
     * <p>
     * The map preserves insertion order (backed by a {@link LinkedHashMap}).
     * Modifying the returned map will not affect this compound and is not
     * supported. Treat the returned map as read-only.
     *
     * @return a view of the internal name-to-indexed-tag map
     */
    public Map<String, IndexedTag> getTagMap() {
        return tagMap;
    }

    // --- Utility Methods (Original) ---

    /**
     * Returns the internal list used for indexed access and iteration in
     * insertion order.
     * <p>
     * Modifying the returned list will not affect this compound and is not
     * supported. Treat the returned list as read-only.
     *
     * @return the internal list of identifiable tags
     */
    public List<NBTTagIdentifiable<?>> getTagList() {
        return tagList;
    }

    /**
     * Returns the number of tags contained in this compound.
     *
     * @return the size of the compound
     */
    public int size() {
        return tagList.size();
    }

    @Override
    public NBTTagIdentifiable<?> getEntry(int i) {
        return tagList.get(i);
    }

    /**
     * Retrieves a nested NBT Compound tag.
     *
     * @param key the name of the tag to retrieve
     * @return the nested {@code NBTCompound} instance
     * @throws IllegalStateException if the tag is missing or is not a Compound tag
     *
     *                               <h4>Example</h4>
     *                               <pre>{@code
     *                                                             int health = root.getCompound("Stats") // returns NBTCompound
     *                                                                             .getInteger("Health"); // returns primitive int
     *                                                             }</pre>
     */
    public NBTCompound getCompound(String key) {
        NBTTag<?> tag = get(key);
        if (tag == null) {
            throw new IllegalStateException("Tag '" + key + "' is missing in compound.");
        }
        if (!(tag instanceof NBTCompound)) {
            throw new IllegalStateException("Tag '" + key + "' is not a Compound tag (Type: " + tag.getClass().getSimpleName() + ").");
        }
        return (NBTCompound) tag;
    }

    /**
     * Retrieves the {@code int} value from an NBT Int tag.
     *
     * @param key The name of the tag to retrieve.
     * @return The primitive integer value.
     * @throws IllegalStateException If the tag is missing or is not an Int tag.
     */
    public int getInteger(String key) {
        NBTTag<?> tag = get(key);
        if (!(tag instanceof NBTNumberPrimitive)) {
            throw new IllegalStateException("Tag '" + key + "' is not an Integer tag.");
        }
        return ((NBTNumberPrimitive<?>) tag).asInt();
    }

    /**
     * Retrieves the {@code int} value from an NBT Int tag, or returns a default value
     * if the tag is missing or has the wrong type.
     *
     * @param key          The name of the tag to retrieve.
     * @param defaultValue The value to return if the tag is not found or is of the wrong type.
     * @return The integer value or the default value.
     */
    public int getIntegerOrDefault(String key, int defaultValue) {
        NBTTag<?> tag = get(key);
        if (tag instanceof NBTNumberPrimitive<?> nbtInt) {
            return nbtInt.asInt();
        }
        return defaultValue;
    }

    /**
     * Retrieves the {@code String} value from an NBT String tag.
     *
     * @param key The name of the tag to retrieve.
     * @return The string value.
     * @throws IllegalStateException If the tag is missing or is not a String tag.
     */
    public String getString(String key) {
        NBTTag<?> tag = get(key);
        if (!(tag instanceof NBTPrimitive)) {
            throw new IllegalStateException("Tag '" + key + "' is not a String tag.");
        }
        return ((NBTPrimitive<?>) tag).asString();
    }

    /**
     * Retrieves the {@code String} value from an NBT String tag, or returns a default value
     * if the tag is missing or has the wrong type.
     *
     * @param key          The name of the tag to retrieve.
     * @param defaultValue The value to return if the tag is not found or is of the wrong type.
     * @return The string value or the default value.
     */
    public String getStringOrDefault(String key, String defaultValue) {
        NBTTag<?> tag = get(key);
        if (tag instanceof NBTPrimitive<?> nbtString) {
            return nbtString.asString();
        }
        return defaultValue;
    }

    /**
     * Retrieves the {@code float} value from an NBT Float tag.
     *
     * @param key The name of the tag to retrieve.
     * @return The primitive float value.
     * @throws IllegalStateException If the tag is missing or is not a Float tag.
     */
    public float getFloat(String key) {
        NBTTag<?> tag = get(key);
        if (!(tag instanceof NBTNumberPrimitive)) {
            throw new IllegalStateException("Tag '" + key + "' is not a Float tag.");
        }
        return ((NBTNumberPrimitive<?>) tag).asFloat();
    }

    /**
     * Retrieves the {@code byte} value from an NBT Byte tag.
     *
     * @param key The name of the tag to retrieve.
     * @return The primitive byte value.
     * @throws IllegalStateException If the tag is missing or is not a Byte tag.
     */
    public byte getByte(String key) {
        NBTTag<?> tag = get(key);
        if (!(tag instanceof NBTNumberPrimitive)) {
            throw new IllegalStateException("Tag '" + key + "' is not a Byte tag.");
        }
        return ((NBTNumberPrimitive<?>) tag).asByte();
    }

    /**
     * Retrieves the {@code short} value from an NBT Short tag.
     *
     * @param key The name of the tag to retrieve.
     * @return The primitive short value.
     * @throws IllegalStateException If the tag is missing or is not a Short tag.
     */
    public short getShort(String key) {
        NBTTag<?> tag = get(key);
        if (!(tag instanceof NBTNumberPrimitive)) {
            throw new IllegalStateException("Tag '" + key + "' is not a Short tag.");
        }
        return ((NBTNumberPrimitive<?>) tag).asShort();
    }

    /**
     * Retrieves the {@code long} value from an NBT Long tag.
     *
     * @param key The name of the tag to retrieve.
     * @return The primitive long value.
     * @throws IllegalStateException If the tag is missing or is not a Long tag.
     */
    public long getLong(String key) {
        NBTTag<?> tag = get(key);
        if (!(tag instanceof NBTNumberPrimitive)) {
            throw new IllegalStateException("Tag '" + key + "' is not a Long tag.");
        }
        return ((NBTNumberPrimitive<?>) tag).asLong();
    }

    /**
     * Retrieves the {@code double} value from an NBT Double tag.
     *
     * @param key The name of the tag to retrieve.
     * @return The primitive double value.
     * @throws IllegalStateException If the tag is missing or is not a Double tag.
     */
    public double getDouble(String key) {
        NBTTag<?> tag = get(key);
        if (!(tag instanceof NBTNumberPrimitive)) {
            throw new IllegalStateException("Tag '" + key + "' is not a Double tag.");
        }
        return ((NBTNumberPrimitive<?>) tag).asDouble();
    }

    /**
     * Internal wrapper class that stores a reference to an {@link NBTTagIdentifiable}
     * along with its index in the {@link #tagList}.
     * <p>
     * Used for efficient removal and index updates without scanning the list.
     */
    public static class IndexedTag {
        /**
         * The actual identifiable tag stored in the compound.
         */
        final NBTTagIdentifiable<?> tag;
        /**
         * The index of this tag in the {@link NBTCompound#tagList}.
         */
        int index;

        IndexedTag(NBTTagIdentifiable<?> tag, int index) {
            this.tag = tag;
            this.index = index;
        }

        /**
         * Returns the wrapped {@link NBTTagIdentifiable}.
         *
         * @return the identifiable tag
         */
        public NBTTagIdentifiable<?> getTag() {
            return tag;
        }

        /**
         * Returns the current index of this tag within the owning compound's
         * internal list. The index may change after removals that reorder
         * elements (swap-last removal strategy).
         *
         * @return the zero-based index
         */
        public int getIndex() {
            return index;
        }
    }
}