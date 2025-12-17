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

import com.dietrichpaul.mcnbt.NBTCompound;
import com.dietrichpaul.mcnbt.NBTIntArray;
import com.dietrichpaul.mcnbt.NBTList;
import com.dietrichpaul.mcnbt.NBTTag;
import com.dietrichpaul.mcnbt.primitive.*;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class LegacyParser {

    private static final char QUOTE = '"';
    private static final char ESCAPE = '\\';
    private static final char COMMA = ',';
    private static final char COLON = ':';

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    private static final String REGEX_INT = "[-+]?[0-9]+";
    private static final String REGEX_FLOATING = "[-+]?[0-9]*\\.?[0-9]+";

    private final String content;

    public LegacyParser(String content) {
        this.content = content.trim();
    }

    public NBTTag<?> parse() {
        if (content.startsWith("{")) {
            return parseCompound(content);
        }
        // Fallback for non-compounds if necessary, usually legacy expects compound root
        return parsePrimitive(content);
    }

    private NBTCompound parseCompound(String input) {
        String s = input.trim();

        if (!s.startsWith("{") || !s.endsWith("}")) {
            throw new SNbtException("Invalid compound: " + s);
        }

        s = s.substring(1, s.length() - 1);
        NBTCompound compound = new NBTCompound();

        while (!s.isEmpty()) {
            String pair = findPair(s, false);

            if (!pair.isEmpty()) {
                String key = findKey(pair);
                String value = findValue(pair);

                compound.put(key, parseAny(value));

                if (s.length() <= pair.length()) {
                    break;
                }
                s = s.substring(pair.length() + 1);
            }
        }
        return compound;
    }

    private NBTTag<?> parseAny(String s) {
        String value = s.trim();

        if (value.startsWith("{")) {
            return parseCompound(value);
        }

        if (value.startsWith("[") && !value.matches("\\[[-\\d|,\\s]+]")) {
            return parseList(value);
        }

        return parsePrimitive(value);
    }

    private NBTList<?> parseList(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            throw new SNbtException("Invalid list: " + input);
        }

        String s = input.substring(1, input.length() - 1);
        List<NBTTag<?>> list = new ArrayList<>();

        while (!s.isEmpty()) {
            String pair = findPair(s, true);

            if (!pair.isEmpty()) {
                String value = pair.contains(":") ? findValue(pair) : pair;

                try {
                    list.add(parseAny(value));
                } catch (Exception ignored) {
                    // Legacy parsers were lenient
                }

                if (s.length() <= pair.length()) {
                    break;
                }
                s = s.substring(pair.length() + 1);
            }
        }
        return NBTList.of(list);
    }

    private NBTTag<?> parsePrimitive(String value) {
        try {
            char suffix = Character.toLowerCase(value.charAt(value.length() - 1));

            switch (suffix) {
                case 'd':
                    return NBTDouble.of(Double.parseDouble(trimSuffix(value)));
                case 'f':
                    return NBTFloat.of(Float.parseFloat(trimSuffix(value)));
                case 'b':
                    return NBTByte.of(Byte.parseByte(trimSuffix(value)));
                case 'l':
                    return NBTLong.of(Long.parseLong(trimSuffix(value)));
                case 's':
                    return NBTShort.of(Short.parseShort(trimSuffix(value)));
                default:
                    break;
            }

            if (value.matches(REGEX_INT)) {
                return NBTInt.of(Integer.parseInt(value));
            }

            if (value.matches(REGEX_FLOATING)) {
                return NBTDouble.of(Double.parseDouble(value));
            }

            if (value.equalsIgnoreCase(TRUE)) {
                return NBTByte.of((byte) 1);
            }

            if (value.equalsIgnoreCase(FALSE)) {
                return NBTByte.of((byte) 0);
            }

            // Legacy IntArray
            if (value.startsWith("[") && value.endsWith("]")) {
                return parseIntArray(value);
            }

        } catch (NumberFormatException ignored) {
        }

        return NBTString.of(unquote(value));
    }

    private NBTIntArray parseIntArray(String value) {
        String content = value.substring(1, value.length() - 1);
        String[] parts = content.split(",");
        TIntArrayList list = new TIntArrayList();

        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) {
                list.add(Integer.parseInt(trimmed));
            }
        }
        return new NBTIntArray(list);
    }

    private String findPair(String s, boolean isList) {
        int startIndex = 0;

        if (!isList) {
            int sep = s.indexOf(COLON);
            if (sep < 0) {
                throw new SNbtException("No key-value separator found");
            }
            startIndex = sep + 1;
        }

        boolean quoted = false;
        int depth = 0;

        for (int i = startIndex; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == QUOTE && (i == 0 || s.charAt(i - 1) != ESCAPE)) {
                quoted = !quoted;
                continue;
            }

            if (!quoted) {
                if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth--;
                } else if (c == COMMA && depth == 0) {
                    return s.substring(0, i);
                }
            }
        }
        return s;
    }

    private String findKey(String s) {
        int idx = s.indexOf(COLON);
        if (idx < 0) {
            return "";
        }
        return s.substring(0, idx).trim();
    }

    private String findValue(String s) {
        int idx = s.indexOf(COLON);
        if (idx < 0) {
            return s.trim();
        }
        return s.substring(idx + 1).trim();
    }

    private String trimSuffix(String value) {
        return value.substring(0, value.length() - 1);
    }

    private String unquote(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\\\"", "\"");
        }
        return value;
    }
}