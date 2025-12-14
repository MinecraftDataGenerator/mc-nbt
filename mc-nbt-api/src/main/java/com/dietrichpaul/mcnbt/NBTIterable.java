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

import java.util.Iterator;
import java.util.Spliterator;

/**
 * Base interface for NBT tags that expose iterable contents (e.g., arrays, lists).
 * <p>
 * Extends both {@link NBTTag} and {@link Iterable} to integrate with Java's
 * iteration constructs. Implementations may provide primitive-specialized
 * iteration methods to avoid boxing and should document their performance
 * characteristics accordingly.
 *
 * @param <T> the element type presented by the iterable view
 */
public interface NBTIterable<T> extends NBTTag<T>, Iterable<T> {

    /**
     * Returns an iterator over the elements. Implementations may choose to
     * return a boxed iterator even if a primitive iterator exists separately.
     */
    @Override
    Iterator<T> iterator();

    /**
     * Returns a spliterator over the elements. Implementations commonly return
     * a boxed spliterator for API compatibility.
     */
    @Override
    Spliterator<T> spliterator();

    @Override
    default NBTIterable<T> asIterable() {
        return this;
    }
}
