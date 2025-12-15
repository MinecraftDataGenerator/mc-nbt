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

public class SNbtStringReader {
    private final String buffer;
    private int index;

    public SNbtStringReader(String buffer) {
        this.buffer = buffer;
    }

    public boolean canRead() {
        return canRead(1);
    }

    public boolean canRead(int length) {
        return index + length <= buffer.length();
    }

    public char peek() {
        return buffer.charAt(index);
    }

    public char peek(int offset) {
        return buffer.charAt(index + offset);
    }

    public char read() {
        return buffer.charAt(index++);
    }

    public void skip() {
        index++;
    }

    public void skipWhitespaces() {
        while (canRead() && Character.isWhitespace(peek())) {
            skip();
        }
    }

    public void expect(char c) {
        skipWhitespaces();
        if (!canRead() || read() != c) {
            throw new SNbtException("Expected '" + c + "'", buffer, index);
        }
    }

    public boolean matches(String s) {
        if (!canRead(s.length())) return false;
        for (int i = 0; i < s.length(); i++) {
            if (buffer.charAt(index + i) != s.charAt(i)) return false;
        }
        return true;
    }

    public String readUnquotedString() {
        int start = index;
        while (canRead() && isAllowedInUnquoted(peek())) {
            skip();
        }
        return buffer.substring(start, index);
    }

    public String readQuotedString() {
        if (!canRead()) return "";
        char quote = read();
        if (quote != '"' && quote != '\'') {
            throw new SNbtException("Expected quote", buffer, index);
        }

        StringBuilder output = new StringBuilder();
        boolean escaped = false;
        while (canRead()) {
            char c = read();
            if (escaped) {
                if (c != '\\' && c != quote) {
                    throw new SNbtException("Invalid escape sequence '\\" + c + "'", buffer, index);
                }
                output.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == quote) {
                return output.toString();
            } else {
                output.append(c);
            }
        }
        throw new SNbtException("Unclosed quote", buffer, index);
    }

    private boolean isAllowedInUnquoted(char c) {
        return (c >= '0' && c <= '9') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= 'a' && c <= 'z') ||
                c == '_' || c == '-' || c == '.' || c == '+';
    }

    public int getCursor() {
        return index;
    }

    public String getString() {
        return buffer;
    }
}