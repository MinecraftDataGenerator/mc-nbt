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

import com.dietrichpaul.mcnbt.builder.NBTListBuilder;

import java.util.*;

/**
 * Represents an NBT List that stores multiple tags of the same type.
 * <p>
 * Enforces that all elements have the same {@link NBTTagType}. Provides
 * convenient methods for manipulation, iteration, and construction.
 * As long as no type is provided the first one sets it.
 *
 * @param <T> the type of the elements in the list
 */
public class NBTList<T extends NBTTag<?>> implements NBTIterable<T> {

    /**
     * Internal storage for list elements
     */
    private final List<T> list;

    /**
     * The enforced NBTTagType of elements in this list
     */
    private NBTTagType internType;

    private NBTList(List<T> list, NBTTagType internType) {
        this.list = list;
        this.internType = internType;
    }

    private NBTList(List<T> list) {
        this(list, list.isEmpty() ? NBTTagType.NULL : list.getFirst().getTagType());
    }

    /**
     * Creates a new empty {@link NBTList} with an optional enforced element type.
     *
     * @param type the NBTTagType to enforce for all elements, or {@code null} to allow first insert to define it
     */
    private NBTList(NBTTagType type) {
        this(new ArrayList<>(), type);
    }

    /**
     * Creates a new empty {@link NBTList} with type determined on first insertion.
     */
    private NBTList() {
        this(NBTTagType.NULL);
    }

    /**
     * Creates a new {@link NBTListBuilder} for building an {@link NBTList} fluently.
     *
     * @param <T> the element type of the list to build
     * @return a new list builder
     */
    public static <T extends NBTTag<?>> NBTListBuilder<T> builder() {
        return new NBTListBuilder<>();
    }

    /**
     * Wraps the given backing {@link List} as an {@link NBTList} with an explicitly defined
     * internal element {@link NBTTagType}.
     * <p>
     * The caller is responsible for ensuring that all elements in {@code list} match
     * the provided {@code internType}.
     *
     * @param list       the backing list to wrap (not copied)
     * @param internType the enforced element type for this list
     * @param <T>        the element type
     * @return a new NBTList view over the given list
     */
    public static <T extends NBTTag<?>> NBTList<T> of(List<T> list, NBTTagType internType) {
        return new NBTList<>(list, internType);
    }

    /**
     * Creates a new {@link NBTList} with the given elements.
     * The list type is automatically inferred from the first element.
     *
     * @param elements elements to add
     * @param <T>      type of the elements
     * @return a new NBTList containing the given elements
     */
    @SafeVarargs
    public static <T extends NBTTag<?>> NBTList<T> of(T... elements) {
        if (elements == null || elements.length == 0) {
            return new NBTList<>();
        }
        NBTList<T> list = new NBTList<>(elements[0].getTagType());
        for (T e : elements) {
            list.add(e);
        }
        return list;
    }

    /**
     * Create a new NBTList from the given {@link List} of NBT tag elements.
     *
     * @param elements The NBT tag elements to store into the newly created {@link NBTList}.
     * @param <T>      The type of the NBT tag elements to store into the newly created list.
     * @return A new {@link NBTList} which contains all the given NBT tag elements.
     */
    public static <T extends NBTTag<?>> NBTList<T> of(List<T> elements) {
        if (!elements.isEmpty()) {
            NBTTagType internType = elements.getFirst().getTagType();
            for (int i = 1; i < elements.size(); i++) {
                T t = elements.get(i);
                if (t.getTagType() != internType) {
                    throw new IllegalArgumentException("All tags must match intern type: " + internType);
                }
            }
        }
        return new NBTList<>(elements);
    }

    /**
     * Creates an empty {@link NBTList} whose element type is fixed to {@code type}.
     *
     * @param type the enforced {@link NBTTagType} for elements
     * @param <T>  the element type
     * @return an empty list with the given element type
     */
    public static <T extends NBTTag<T>> NBTList<T> of(NBTTagType type) {
        return new NBTList<>(type);
    }

    /**
     * Returns this instance as an {@link NBTList}.
     *
     * @return {@code this}
     */
    @Override
    public NBTList<?> asList() {
        return this;
    }

    /**
     * Adds a new tag to this list.
     * <p>
     * If the list already has a type, the new tag must match it.
     * If the list is empty, the type is inferred from the first added element.
     *
     * @param tag the tag to add
     * @return {@code true} if the tag was added successfully
     * @throws IllegalArgumentException if the tag type does not match the list type
     */
    public boolean add(T tag) {
        Objects.requireNonNull(tag, "tag cannot be null");

        if (internType != NBTTagType.NULL && internType != tag.getTagType()) {
            throw new IllegalArgumentException("Tag type " + tag.getTagType() + " does not match list type " + internType);
        }

        if (internType == null) {
            internType = tag.getTagType();
        }

        return list.add(tag);
    }

    /**
     * Removes the first occurrence of the specified tag from this list, if present.
     *
     * @param tag the tag to remove
     * @return {@code true} if the list contained the specified element
     */
    public boolean remove(T tag) {
        return list.remove(tag);
    }

    /**
     * Removes the element at the specified position in this list.
     *
     * @param index index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public T remove(int index) {
        return list.remove(index);
    }

    /**
     * Replaces the element at the specified position in this list with the specified tag.
     *
     * @param index index of the element to replace
     * @param tag   element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws IllegalArgumentException  if the tag type does not match this list's element type
     */
    public T set(int index, T tag) {
        Objects.requireNonNull(tag, "tag cannot be null");
        if (internType == NBTTagType.NULL) {
            internType = tag.getTagType();
        }
        else if (internType != tag.getTagType()) {
            throw new IllegalArgumentException("Tag type " + tag.getTagType() + " does not match list type " + internType);
        }
        return list.set(index, tag);
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public T get(int index) {
        return list.get(index);
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements
     */
    public int size() {
        return list.size();
    }

    /**
     * Removes all elements from this list.
     * The internal element type remains unchanged.
     */
    public void clear() {
        list.clear();
    }

    /**
     * Returns the enforced type of elements in this list.
     *
     * <p>Note: when the element type hasn't been determined yet, the sentinel
     * value {@link NBTTagType#NULL} is returned instead of {@code null}.</p>
     *
     * @return the {@link NBTTagType} of elements (or {@link NBTTagType#NULL} if not determined)
     */
    public NBTTagType getInternalType() {
        return internType;
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     */
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    /**
     * Creates a {@link Spliterator} over the elements in this list.
     */
    @Override
    public Spliterator<T> spliterator() {
        return list.spliterator();
    }

    /**
     * Returns this tag's own {@link NBTTagType} which is always {@link NBTTagType#LIST}.
     */
    @Override
    public NBTTagType getTagType() {
        return NBTTagType.LIST;
    }

    /**
     * Exposes the mutable backing {@link List}.
     * Changes to the returned list are reflected in this {@link NBTList} and vice versa.
     *
     * @return the backing list
     */
    public List<T> getList() {
        return list;
    }
}
