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

/**
 * Lightweight cursor-based reader for SNBT parsing.
 * <p>
 * Provides utility methods to peek, read and skip characters and whitespace,
 * read quoted/unquoted strings, and track the current cursor position for
 * error reporting.
 */
public class SNbtStringReader {
    private final String buffer;
    private int index;

    /**
     * Creates a new reader for the given buffer. The initial cursor position is at the start.
     *
     * @param buffer the input string to read from (not modified)
     */
    public SNbtStringReader(String buffer) {
        this.buffer = buffer;
    }

    /**
     * @return true if there is at least one character available to read from the current cursor
     */
    public boolean canRead() {
        return canRead(1);
    }

    /**
     * Checks if the reader can read the specified number of characters from the current cursor.
     *
     * @param length the number of characters to check for
     * @return true if {@code length} characters are available
     */
    public boolean canRead(int length) {
        return index + length <= buffer.length();
    }

    /**
     * Peeks the next character without advancing the cursor.
     *
     * @return the next character
     * @throws IndexOutOfBoundsException if no character is available
     */
    public char peek() {
        return buffer.charAt(index);
    }

    /**
     * Peeks a character at the given offset from the current cursor without advancing.
     *
     * @param offset the offset from the current cursor (0 = next character)
     * @return the character at the offset
     * @throws IndexOutOfBoundsException if that position is out of bounds
     */
    public char peek(int offset) {
        return buffer.charAt(index + offset);
    }

    /**
     * Reads and returns the next character, advancing the cursor by one.
     *
     * @return the next character
     * @throws IndexOutOfBoundsException if no character is available
     */
    public char read() {
        return buffer.charAt(index++);
    }

    /**
     * Advances the cursor by one character without returning it.
     *
     * @throws IndexOutOfBoundsException if no character is available
     */
    public void skip() {
        index++;
    }

    /**
     * Advances the cursor while the current character is a whitespace according to
     * {@link Character#isWhitespace(char)}.
     */
    public void skipWhitespaces() {
        while (canRead() && Character.isWhitespace(peek())) {
            skip();
        }
    }

    /**
     * Skips any leading whitespace and consumes the expected character.
     *
     * @param c the character that must appear at the current position (after skipping whitespace)
     * @throws SNbtException if the expected character is not present
     */
    public void expect(char c) {
        skipWhitespaces();
        if (!canRead() || read() != c) {
            throw new SNbtException("Expected '" + c + "'", buffer, index);
        }
    }

    /**
     * Checks whether the upcoming characters match the provided string exactly.
     * Does not advance the cursor.
     *
     * @param s the sequence to match
     * @return true if the buffer has {@code s} at the current cursor
     */
    public boolean matches(String s) {
        if (!canRead(s.length()))
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (buffer.charAt(index + i) != s.charAt(i))
                return false;
        }
        return true;
    }

    /**
     * Reads an unquoted string consisting of characters allowed by SNBT for identifiers
     * (letters, digits, {@code _ - . +}).
     *
     * @return the substring read; may be empty if the next char is not allowed
     */
    public String readUnquotedString() {
        int start = index;
        while (canRead() && isAllowedInUnquoted(peek())) {
            skip();
        }
        return buffer.substring(start, index);
    }

    /**
     * Reads a quoted string. Supports both single and double quotes and the escape sequences
     * for the active quote character and backslash (e.g., {@code \"} inside double quotes).
     *
     * @return the unescaped string content
     * @throws SNbtException if the string is not properly quoted or contains invalid escapes
     */
    public String readQuotedString() {
        if (!canRead())
            return "";
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
            }
            else if (c == '\\') {
                escaped = true;
            }
            else if (c == quote) {
                return output.toString();
            }
            else {
                output.append(c);
            }
        }
        throw new SNbtException("Unclosed quote", buffer, index);
    }

    private boolean isAllowedInUnquoted(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_' || c == '-' || c == '.' || c == '+';
    }

    /**
     * @return the current zero-based cursor index within the buffer
     */
    public int getCursor() {
        return index;
    }

    /**
     * @return the underlying buffer string
     */
    public String getString() {
        return buffer;
    }
}