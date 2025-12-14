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

package com.dietrichpaul.mcnbt.format.json;

import com.dietrichpaul.mcnbt.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Objects;

/**
 * Utility class for converting {@link NBTTag} instances into {@link JsonElement} objects.
 * <p>
 * This class implements a deep conversion of NBT tags to JSON representation:
 * <ul>
 *     <li>TAG_Compound → {@link JsonObject}</li>
 *     <li>TAG_List, TAG_Byte_Array, TAG_Int_Array, TAG_Long_Array → {@link JsonArray}</li>
 *     <li>Primitive types (Byte, Short, Int, Long, Float, Double, String) → {@link JsonPrimitive}</li>
 * </ul>
 *
 * <p>
 * <b>Conversion rules:</b>
 * <ul>
 *     <li>Compound tags are converted recursively, preserving key-value mapping.</li>
 *     <li>Iterable tags (lists and arrays) are converted element by element. Arrays are flattened into JsonArray.</li>
 *     <li>Primitive numbers are converted to JsonPrimitive, preserving numeric value.</li>
 *     <li>No NBT null/empty tags are converted; the conversion always produces a JSON representation.</li>
 * </ul>
 */
public class NBTToJson {
    private NBTToJson() {
    }

    /**
     * Converts a generic {@link NBTTag} to a {@link JsonElement}.
     *
     * @param tag the NBT tag to convert; must not be null
     * @return a {@link JsonElement} representing the NBT tag
     * @throws NullPointerException     if {@code tag} is null
     * @throws IllegalArgumentException if the tag type cannot be serialized
     */
    public static JsonElement convertTagToJSON(NBTTag<?> tag) {
        Objects.requireNonNull(tag, "tag");

        if (tag.getTagType().isCompound()) {
            return convertCompoundToJSON(tag.asCompound());
        }
        else if (tag.getTagType().isIterable()) {
            return convertIterableToJSON(tag.asIterable());
        }
        else if (tag.getTagType().isPrimitive()) {
            return convertPrimitiveToJSON(tag);
        }
        else {
            throw new IllegalArgumentException("Cannot serialize " + tag);
        }
    }

    /**
     * Converts a primitive NBT value or number into a {@link JsonPrimitive}.
     *
     * @param o the primitive object or NBTPrimitive; must not be null
     * @return a {@link JsonPrimitive} representing the value
     */
    public static JsonElement convertPrimitiveToJSON(Object o) {
        if (o instanceof NBTPrimitive<?> primitiveTag) {
            return convertPrimitiveToJSON(primitiveTag.getPrimitiveType());
        }
        else if (o instanceof Number num) {
            return new JsonPrimitive(num);
        }
        else {
            return new JsonPrimitive(String.valueOf(o));
        }
    }

    /**
     * Converts an {@link NBTIterable} (list or array) into a {@link JsonArray}.
     * <p>
     * Each element is recursively converted to its JSON representation.
     *
     * @param iterable the NBT iterable object
     * @return a {@link JsonArray} containing the converted elements
     */
    public static JsonArray convertIterableToJSON(NBTIterable<?> iterable) {
        JsonArray array = new JsonArray();
        iterable.forEach(o -> {
            if (o instanceof NBTTag<?> tag) {
                array.add(convertTagToJSON(tag));
            }
            else {
                array.add(convertPrimitiveToJSON(o));
            }
        });
        return array;
    }

    /**
     * Converts an {@link NBTCompound} (TAG_Compound) into a {@link JsonObject}.
     * <p>
     * Each named tag in the compound is converted recursively and stored as a key-value
     * pair in the resulting JSON object.
     *
     * @param compound the compound tag to convert; must not be null
     * @return a {@link JsonObject} representing the compound
     */
    public static JsonObject convertCompoundToJSON(NBTCompound compound) {
        JsonObject object = new JsonObject();
        for (NBTTagIdentifiable<?> namedTag : compound.getTagList()) {
            object.add(namedTag.name(), convertTagToJSON(namedTag.tag()));
        }
        return object;
    }
}
