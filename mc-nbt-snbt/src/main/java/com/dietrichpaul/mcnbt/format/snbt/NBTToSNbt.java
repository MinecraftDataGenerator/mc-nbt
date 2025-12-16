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

package com.dietrichpaul.mcnbt.format.snbt;

import com.dietrichpaul.mcnbt.*;
import com.dietrichpaul.mcnbt.primitive.*;

import java.util.regex.Pattern;

/**
 * Utility for serializing {@link NBTTag} trees into SNBT (stringified NBT).
 * <p>
 * Use {@link #serialize(NBTTag, SNbtSyntax)} to convert a tag to its textual
 * representation for a specific Minecraft syntax profile.
 */
public class NBTToSNbt {
    private static final Pattern NO_QUOTE_PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");

    private NBTToSNbt() {
    }

    /**
     * Serializes an NBT tag to SNBT using the provided syntax profile.
     *
     * @param tag    the root tag to serialize
     * @param syntax the syntax profile controlling quoting, suffixes, and arrays
     * @return the SNBT string
     * @throws IllegalArgumentException if an unknown tag type is encountered
     */
    public static String serialize(NBTTag<?> tag, SNbtSyntax syntax) {
        StringBuilder sb = new StringBuilder();
        serialize(tag, sb, syntax);
        return sb.toString();
    }

    private static void serialize(NBTTag<?> tag, StringBuilder sb, SNbtSyntax syntax) {
        switch (tag.getTagType()) {
            case BYTE -> sb.append(((NBTByte) tag).asByte()).append("b");
            case SHORT -> sb.append(((NBTShort) tag).asShort()).append("s");
            case INT -> sb.append(((NBTInt) tag).asInt());
            case LONG -> sb.append(((NBTLong) tag).asLong()).append("L");
            case FLOAT -> sb.append(((NBTFloat) tag).asFloat()).append("f");
            case DOUBLE -> sb.append(((NBTDouble) tag).asDouble()).append("d");
            case BYTE_ARRAY -> {
                sb.append("[B;");
                NBTByteArray arr = (NBTByteArray) tag;
                for (int i = 0; i < arr.size(); i++) {
                    if (i > 0)
                        sb.append(",");
                    sb.append(arr.get(i)).append("B");
                }
                sb.append("]");
            }
            case INT_ARRAY -> {
                sb.append("[I;");
                NBTIntArray arr = (NBTIntArray) tag;
                for (int i = 0; i < arr.size(); i++) {
                    if (i > 0)
                        sb.append(",");
                    sb.append(arr.get(i));
                }
                sb.append("]");
            }
            case LONG_ARRAY -> {
                sb.append("[L;");
                NBTLongArray arr = (NBTLongArray) tag;
                for (int i = 0; i < arr.size(); i++) {
                    if (i > 0)
                        sb.append(",");
                    sb.append(arr.get(i)).append("L");
                }
                sb.append("]");
            }
            case STRING -> sb.append(escape(((NBTString) tag).asString(), syntax));
            case LIST -> {
                sb.append("[");
                NBTList<?> list = (NBTList<?>) tag;
                int i = 0;
                for (NBTTag<?> element : list) {
                    if (i++ > 0)
                        sb.append(",");
                    // Legacy formats (1.7) technically used index:value, but simplified serialization usually omits it
                    serialize(element, sb, syntax);
                }
                sb.append("]");
            }
            case COMPOUND -> {
                sb.append("{");
                NBTCompound compound = (NBTCompound) tag;
                int i = 0;
                for (NBTTagIdentifiable<?> entry : compound) {
                    if (i++ > 0)
                        sb.append(",");
                    sb.append(handleKey(entry.name(), syntax)).append(":");
                    serialize(entry.tag(), sb, syntax);
                }
                sb.append("}");
            }
            default -> throw new IllegalArgumentException("Unknown tag type: " + tag.getTagType());
        }
    }

    private static String handleKey(String key, SNbtSyntax syntax) {
        if (syntax.isLegacyParser())
            return key; // 1.7/1.8 didn't strict quote keys usually
        return escape(key, syntax);
    }

    private static String escape(String s, SNbtSyntax syntax) {
        if (syntax == SNbtSyntax.V1_12 || syntax == SNbtSyntax.V1_13 || syntax == SNbtSyntax.V1_14) {
            if (NO_QUOTE_PATTERN.matcher(s).matches())
                return s;
        }

        StringBuilder sb = new StringBuilder();
        char quote = '"';
        if (syntax.isAllowSingleQuotes() && s.indexOf('"') != -1 && s.indexOf('\'') == -1) {
            quote = '\'';
        }

        sb.append(quote);
        for (char c : s.toCharArray()) {
            if (c == '\\' || c == quote)
                sb.append('\\');
            sb.append(c);
        }
        sb.append(quote);
        return sb.toString();
    }
}