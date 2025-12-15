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
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class LegacyParser {
    private final String content;

    public LegacyParser(String content) {
        this.content = content.trim();
    }

    public NBTTag<?> parse() {
        if (content.startsWith("{")) return parseCompound(content);
        // Fallback for non-compounds if necessary, usually legacy expects compound root
        return parsePrimitive(content);
    }

    private NBTCompound parseCompound(String s) {
        s = s.trim();
        if (!s.startsWith("{") || !s.endsWith("}"))
            throw new SNbtException("Invalid compound: " + s);

        s = s.substring(1, s.length() - 1);
        NBTCompound compound = new NBTCompound();

        while (!s.isEmpty()) {
            String pair = findPair(s, false);
            if (!pair.isEmpty()) {
                String name = findKey(pair);
                String value = findValue(pair);
                compound.put(name, parseAny(value));

                if (s.length() < pair.length() + 1) break;
                s = s.substring(pair.length() + 1);
            }
        }
        return compound;
    }

    private NBTTag<?> parseAny(String s) {
        s = s.trim();
        if (s.startsWith("{")) return parseCompound(s);
        if (s.startsWith("[") && !s.matches("\\[[-\\d|,\\s]+]")) return parseList(s);
        return parsePrimitive(s);
    }

    private NBTList<?> parseList(String s) {
        if (!s.startsWith("[") || !s.endsWith("]")) throw new SNbtException("Invalid list: " + s);
        s = s.substring(1, s.length() - 1);
        List<NBTTag<?>> list = new ArrayList<>();

        while (!s.isEmpty()) {
            String pair = findPair(s, true);
            if (!pair.isEmpty()) {
                // Legacy lists often had index:value format, we ignore index
                String valueStr = pair.contains(":") ? findValue(pair) : pair;
                try {
                    list.add(parseAny(valueStr));
                } catch (Exception ignored) {
                    // Legacy parsers were lenient
                }
                if (s.length() < pair.length() + 1) break;
                s = s.substring(pair.length() + 1);
            }
        }
        return NBTList.of(list);
    }

    private NBTTag<?> parsePrimitive(String value) {
        try {
            if (value.matches("[-+]?[0-9]*\\.?[0-9]+[d|D]"))
                return NBTDouble.of(Double.parseDouble(value.substring(0, value.length() - 1)));
            if (value.matches("[-+]?[0-9]*\\.?[0-9]+[f|F]"))
                return NBTFloat.of(Float.parseFloat(value.substring(0, value.length() - 1)));
            if (value.matches("[-+]?[0-9]+[b|B]"))
                return NBTByte.of(Byte.parseByte(value.substring(0, value.length() - 1)));
            if (value.matches("[-+]?[0-9]+[l|L]"))
                return NBTLong.of(Long.parseLong(value.substring(0, value.length() - 1)));
            if (value.matches("[-+]?[0-9]+[s|S]"))
                return NBTShort.of(Short.parseShort(value.substring(0, value.length() - 1)));
            if (value.matches("[-+]?[0-9]+")) return NBTInt.of(Integer.parseInt(value));
            if (value.matches("[-+]?[0-9]*\\.?[0-9]+")) return NBTDouble.of(Double.parseDouble(value));
            if (value.equalsIgnoreCase("true")) return NBTByte.of((byte) 1);
            if (value.equalsIgnoreCase("false")) return NBTByte.of((byte) 0);

            // Legacy IntArray detection
            if (value.startsWith("[") && value.endsWith("]")) {
                String content = value.substring(1, value.length() - 1);
                String[] parts = content.split(",");
                TIntArrayList ints = new TIntArrayList();
                for (String p : parts) {
                    if (!p.trim().isEmpty()) ints.add(Integer.parseInt(p.trim()));
                }
                return new NBTIntArray(ints);
            }
        } catch (NumberFormatException ignored) {
        }

        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1).replace("\\\"", "\"");
        }
        return NBTString.of(value);
    }

    private String findPair(String s, boolean isList) {
        int sep = s.indexOf(':');
        if (sep < 0 && !isList) throw new SNbtException("No separator found");
        // Logic simplified: Iterate chars to find split point based on brackets balance
        int i = (sep < 0) ? 0 : sep + 1;
        boolean quoted = false;
        Stack<Character> stack = new Stack<>();

        for (; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"') {
                if (i == 0 || s.charAt(i - 1) != '\\') quoted = !quoted;
            } else if (!quoted) {
                if (c == '{' || c == '[') stack.push(c);
                if (c == '}' && (stack.isEmpty() || stack.pop() != '{')) throw new SNbtException("Unbalanced {}");
                if (c == ']' && (stack.isEmpty() || stack.pop() != '[')) throw new SNbtException("Unbalanced []");
                if (c == ',' && stack.isEmpty()) return s.substring(0, i);
            }
        }
        return s.substring(0, i);
    }

    private String findKey(String s) {
        int idx = s.indexOf(':');
        if (idx < 0) return "";
        return s.substring(0, idx).trim();
    }

    private String findValue(String s) {
        int idx = s.indexOf(':');
        if (idx < 0) return s.trim();
        return s.substring(idx + 1).trim();
    }
}