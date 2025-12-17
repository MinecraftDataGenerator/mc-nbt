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
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class ModernParser {

    private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile(
        "[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern DOUBLE_PATTERN = Pattern.compile(
        "[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern FLOAT_PATTERN = Pattern.compile(
        "[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern BYTE_PATTERN  = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", Pattern.CASE_INSENSITIVE);
    private static final Pattern LONG_PATTERN  = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", Pattern.CASE_INSENSITIVE);
    private static final Pattern INT_PATTERN   = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");

    private final SNbtStringReader reader;
    private final SNbtSyntax syntax;

    public ModernParser(String string, SNbtSyntax syntax) {
        this.reader = new SNbtStringReader(string);
        this.syntax = syntax;
    }

    public NBTTag<?> parse() {
        NBTTag<?> tag = parseValue();

        // After a valid root tag, no additional input is allowed
        reader.skipWhitespaces();
        if (reader.canRead()) {
            throw new SNbtException(
                "Trailing data found",
                reader.getString(),
                reader.getCursor()
            );
        }
        return tag;
    }

    private NBTTag<?> parseValue() {
        reader.skipWhitespaces();

        if (!reader.canRead()) {
            throw new SNbtException("Expected value", reader.getString(), reader.getCursor());
        }

        char c = reader.peek();
        if (c == '{') {
            return parseCompound();
        }
        if (c == '[') {
            return parseListOrArray();
        }

        return parsePrimitive();
    }

    private NBTCompound parseCompound() {
        reader.expect('{');
        reader.skipWhitespaces();

        NBTCompound compound = new NBTCompound();

        while (reader.canRead() && reader.peek() != '}') {
            String key = readKey();
            if (key.isEmpty()) {
                throw new SNbtException("Expected key", reader.getString(), reader.getCursor());
            }

            reader.expect(':');
            compound.put(key, parseValue());

            if (!hasNext()) {
                break;
            }
        }

        reader.expect('}');
        return compound;
    }

    private NBTTag<?> parseListOrArray() {
        // Lookahead to check for array syntax [I; etc
        if (reader.canRead(3) && !isQuote(reader.peek(1)) && reader.peek(2) == ';') {
            return parseArray();
        }
        return parseList();
    }

    private NBTTag<?> parseArray() {
        reader.expect('[');

        char type = reader.read(); // B, I or L
        reader.read();             // skip ';'
        reader.skipWhitespaces();

        if (type == 'B') {
            TByteArrayList list = new TByteArrayList();
            while (reader.peek() != ']') {
                list.add(readNumberPrimitive("byte").asByte());
                if (!hasNext()) {
                    break;
                }
            }
            reader.expect(']');
            return new NBTByteArray(list);
        }

        if (type == 'I') {
            TIntArrayList list = new TIntArrayList();
            while (reader.peek() != ']') {
                list.add(readNumberPrimitive("int").asInt());
                if (!hasNext()) {
                    break;
                }
            }
            reader.expect(']');
            return new NBTIntArray(list);
        }

        if (type == 'L') {
            TLongArrayList list = new TLongArrayList();
            while (reader.peek() != ']') {
                list.add(readNumberPrimitive("long").asLong());
                if (!hasNext()) {
                    break;
                }
            }
            reader.expect(']');
            return new NBTLongArray(list);
        }

        throw new SNbtException("Invalid array type " + type);
    }

    private NBTList<?> parseList() {
        reader.expect('[');
        reader.skipWhitespaces();

        if (!reader.canRead()) {
            throw new SNbtException("Unexpected end", reader.getString(), reader.getCursor());
        }

        List<NBTTag<?>> elements = new ArrayList<>();

        while (reader.peek() != ']') {
            elements.add(parseValue());
            if (!hasNext()) {
                break;
            }
        }

        reader.expect(']');
        return NBTList.of(elements);
    }

    private NBTTag<?> parsePrimitive() {
        reader.skipWhitespaces();

        if (isQuote(reader.peek())) {
            return NBTString.of(reader.readQuotedString());
        }

        String token = reader.readUnquotedString();
        if (token.isEmpty()) {
            throw new SNbtException("Expected value", reader.getString(), reader.getCursor());
        }

        return parsePrimitiveFromString(token);
    }

    private NBTTag<?> parsePrimitiveFromString(String s) {
        try {
            if (FLOAT_PATTERN.matcher(s).matches()) {
                return NBTFloat.of(Float.parseFloat(stripSuffix(s)));
            }
            if (BYTE_PATTERN.matcher(s).matches()) {
                return NBTByte.of(Byte.parseByte(stripSuffix(s)));
            }
            if (SHORT_PATTERN.matcher(s).matches()) {
                return NBTShort.of(Short.parseShort(stripSuffix(s)));
            }
            if (LONG_PATTERN.matcher(s).matches()) {
                return NBTLong.of(Long.parseLong(stripSuffix(s)));
            }
            if (INT_PATTERN.matcher(s).matches()) {
                return NBTInt.of(Integer.parseInt(s));
            }
            if (DOUBLE_PATTERN.matcher(s).matches()) {
                return NBTDouble.of(Double.parseDouble(stripSuffix(s)));
            }
            if (DOUBLE_PATTERN_NOSUFFIX.matcher(s).matches()) {
                return NBTDouble.of(Double.parseDouble(s));
            }
            if (s.equalsIgnoreCase("true")) {
                return NBTByte.of((byte) 1);
            }
            if (s.equalsIgnoreCase("false")) {
                return NBTByte.of((byte) 0);
            }
        }
        catch (NumberFormatException ignored) {
            // Fall through to string
        }

        return NBTString.of(s);
    }

    private String readKey() {
        reader.skipWhitespaces();

        if (!reader.canRead()) {
            return "";
        }

        if (isQuote(reader.peek())) {
            return reader.readQuotedString();
        }

        return reader.readUnquotedString();
    }

    private boolean hasNext() {
        reader.skipWhitespaces();

        if (reader.canRead() && reader.peek() == ',') {
            reader.skip();
            reader.skipWhitespaces();
            return true;
        }
        return false;
    }

    private boolean isQuote(char c) {
        return c == '"' || (syntax.isAllowSingleQuotes() && c == '\'');
    }

    private String stripSuffix(String s) {
        return s.substring(0, s.length() - 1);
    }

    private NBTNumberPrimitive<?> readNumberPrimitive(String expected) {
        NBTTag<?> tag = parseValue();
        if (!(tag instanceof NBTNumberPrimitive<?>)) {
            throw new SNbtException(
                "Expected " + expected,
                reader.getString(),
                reader.getCursor()
            );
        }
        return (NBTNumberPrimitive<?>) tag;
    }
}