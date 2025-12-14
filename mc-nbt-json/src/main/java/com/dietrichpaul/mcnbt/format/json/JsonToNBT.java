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
import com.dietrichpaul.mcnbt.primitive.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for converting {@link JsonElement} objects to {@link NBTTag} instances.
 * <p>
 * This class implements conversion rules similar to Minecraft's NBT to JSON logic:
 * <ul>
 *     <li>JsonObject → NBTCompound</li>
 *     <li>JsonArray → ByteArray, IntArray, LongArray, or NBTList depending on element types</li>
 *     <li>JsonPrimitive → NBT primitive types (Byte, Short, Int, Long, Float, Double, String)</li>
 *     <li>JsonNull elements are ignored; {@link NBTTagType#NULL} is never returned</li>
 * </ul>
 * <p>
 * <b>Array rules:</b>
 * <ul>
 *     <li>All elements must have the same converted NBT type to form a typed array (ByteArray, IntArray, LongArray).</li>
 *     <li>If element types differ, a generic {@link NBTList} is used.</li>
 *     <li>Empty arrays produce an empty {@link NBTList}.</li>
 * </ul>
 *
 * <b>Primitive number rules:</b>
 * <ul>
 *     <li>Byte: number fits in [-128, 127] and equals its byte value</li>
 *     <li>Short: number fits in [-32_768, 32_767] and equals its short value</li>
 *     <li>Int: number fits in [-2_147_483_648, 2_147_483_647] and equals its int value</li>
 *     <li>Long: number fits in [-9_223_372_036_854_775_808, 9_223_372_036_854_775_807] and equals its long value</li>
 *     <li>Float: number can be exactly represented as 32-bit float</li>
 *     <li>Double: otherwise</li>
 * </ul>
 */
public class JsonToNBT {
    private JsonToNBT() {
    }

    /**
     * Converts a {@link JsonElement} into the appropriate {@link NBTTag}.
     *
     * @param element the JSON element to convert; must not be null
     * @return the corresponding {@link NBTTag}, never null
     * @throws IllegalArgumentException if the JSON element is unsupported
     */
    public static NBTTag<?> convertJSONToTag(JsonElement element) {
        if (element.isJsonObject()) {
            return convertObjectToCompound(element.getAsJsonObject());
        }
        else if (element.isJsonArray()) {
            return convertArrayToTag(element.getAsJsonArray());
        }
        else if (element.isJsonPrimitive()) {
            return convertPrimitive(element.getAsJsonPrimitive());
        }
        else {
            throw new IllegalArgumentException("Unknown JSON element: " + element);
        }
    }

    /**
     * Converts a {@link JsonObject} into an {@link NBTCompound}.
     *
     * @param object the JSON object
     * @return an {@link NBTCompound} with keys and values converted recursively
     */
    private static NBTCompound convertObjectToCompound(JsonObject object) {
        NBTCompound compound = new NBTCompound();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            compound.put(entry.getKey(), convertJSONToTag(entry.getValue()));
        }
        return compound;
    }

    /**
     * Converts a {@link JsonArray} into an appropriate NBT array or {@link NBTList}.
     * <p>
     * Typed arrays (ByteArray, IntArray, LongArray) are used if all elements have the same NBT type.
     * Mixed-type arrays produce an {@link NBTList}.
     *
     * @param array the JSON array
     * @return the corresponding NBT tag
     */
    private static NBTTag<?> convertArrayToTag(JsonArray array) {
        if (array.isEmpty())
            return NBTList.of();

        List<NBTTag<?>> elements = new ArrayList<>();
        NBTTagType firstType = null;

        for (JsonElement el : array) {
            NBTTag<?> tag = convertJSONToTag(el);
            elements.add(tag);
            if (firstType == null)
                firstType = tag.getTagType();
            else if (firstType != tag.getTagType())
                firstType = NBTTagType.LIST;
        }

        if (firstType == null)
            return NBTList.of();

        switch (firstType) {
            case BYTE -> {
                byte[] arr = new byte[elements.size()];
                for (int i = 0; i < elements.size(); i++)
                    arr[i] = ((NBTByte) elements.get(i)).getPrimitiveType();
                return NBTByteArray.of(arr);
            }
            case SHORT, INT -> {
                int[] arr = new int[elements.size()];
                for (int i = 0; i < elements.size(); i++)
                    arr[i] = ((NBTInt) elements.get(i)).getPrimitiveType();
                return NBTIntArray.of(arr);
            }
            case LONG -> {
                long[] arr = new long[elements.size()];
                for (int i = 0; i < elements.size(); i++)
                    arr[i] = ((NBTLong) elements.get(i)).getPrimitiveType();
                return NBTLongArray.of(arr);
            }
            default -> {
                return NBTList.of(elements);
            }
        }
    }

    /**
     * Converts a {@link JsonPrimitive} into an NBT primitive type.
     *
     * @param primitive the JSON primitive
     * @return the corresponding NBT primitive
     */
    private static NBTTag<?> convertPrimitive(JsonPrimitive primitive) {
        if (primitive.isString()) {
            return NBTString.of(primitive.getAsString());
        }
        else if (primitive.isNumber()) {
            return numberToNBT(primitive.getAsNumber());
        }
        else {
            throw new IllegalArgumentException("Unsupported JSON primitive: " + primitive);
        }
    }

    /**
     * Converts a {@link Number} to the smallest appropriate NBT numeric type that can hold it
     * without precision loss.
     * <p>
     * Type selection order: Byte → Short → Int → Long → Float → Double
     *
     * @param number the number to convert
     * @return the corresponding NBT numeric tag
     */
    private static NBTTag<?> numberToNBT(Number number) {
        double d = number.doubleValue();

        if (d >= Byte.MIN_VALUE && d <= Byte.MAX_VALUE && number.doubleValue() == number.byteValue()) {
            return NBTByte.of(number.byteValue());
        }
        else if (d >= Short.MIN_VALUE && d <= Short.MAX_VALUE && number.doubleValue() == number.shortValue()) {
            return NBTShort.of(number.shortValue());
        }
        else if (d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE && number.doubleValue() == number.intValue()) {
            return NBTInt.of(number.intValue());
        }
        else if (d >= Long.MIN_VALUE && d <= Long.MAX_VALUE && number.doubleValue() == number.longValue()) {
            return NBTLong.of(number.longValue());
        }
        else if ((float) d == d) {
            return NBTFloat.of((float) d);
        }
        else {
            return NBTDouble.of(d);
        }
    }
}
