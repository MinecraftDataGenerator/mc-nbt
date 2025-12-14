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

/**
 * Represents a **named NBT tag**, pairing a {@link NBTTag} with its associated name.
 * <p>
 * This is used for all tags that have a name in NBT files, including:
 * <ul>
 *     <li>The root tag of an NBT file (whose name is stored in the file header)</li>
 *     <li>Tags inside a {@link NBTCompound}</li>
 * </ul>
 *
 * @param <T> the type of the underlying {@link NBTTag}
 * @param name The name of the NBT tag
 * @param tag The tag value associated with the name of this identifiable
 */
public record NBTTagIdentifiable<T extends NBTTag<?>>(String name, T tag) {
}
