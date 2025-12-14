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

    public static <T extends NBTTag<?>> NBTListBuilder<T> builder() {
        return new NBTListBuilder<>();
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

    public static <T extends NBTTag<T>> NBTList<T> of(NBTTagType type) {
        return new NBTList<>(type);
    }

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
            throw new IllegalArgumentException(
                    "Tag type " + tag.getTagType() + " does not match list type " + internType);
        }

        if (internType == null) {
            internType = tag.getTagType();
        }

        return list.add(tag);
    }

    /**
     * Removes the first occurrence of the specified element from this list, if present.
     *
     * @param tag the tag to remove
     * @return {@code true} if the tag was removed, {@code false} otherwise
     */
    public boolean remove(T tag) {
        return list.remove(tag);
    }

    /**
     * Removes the element at the specified index.
     *
     * @param index the index of the element to remove
     * @return the removed element
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public T remove(int index) {
        return list.remove(index);
    }

    /**
     * Replaces the element at the specified index with a new tag.
     *
     * @param index the index to replace
     * @param tag   the new tag
     * @return the previous tag at the index
     * @throws IndexOutOfBoundsException if the index is invalid
     * @throws IllegalArgumentException  if the tag type does not match the list type
     */
    public T set(int index, T tag) {
        Objects.requireNonNull(tag, "tag cannot be null");
        if (internType == NBTTagType.NULL) {
            internType = tag.getTagType();
        } else if (internType != tag.getTagType()) {
            throw new IllegalArgumentException(
                    "Tag type " + tag.getTagType() + " does not match list type " + internType);
        }
        return list.set(index, tag);
    }

    /**
     * Returns the element at the specified index.
     *
     * @param index the index of the element
     * @return the tag at the given index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public T get(int index) {
        return list.get(index);
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the size of the list
     */
    public int size() {
        return list.size();
    }

    /**
     * Clears all elements from the list.
     * It doesn't mutate the intern type.
     */
    public void clear() {
        list.clear();
    }

    /**
     * Returns the enforced type of elements in this list.
     *
     * @return the {@link NBTTagType} of the list elements, or {@code null} if empty
     */
    public NBTTagType getInternalType() {
        return internType;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return list.spliterator();
    }

    @Override
    public NBTTagType getTagType() {
        return NBTTagType.LIST;
    }

    public List<T> getList() {
        return list;
    }
}
