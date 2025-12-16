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
 * Runtime exception thrown when SNBT parsing or validation fails.
 * <p>
 * Provides a convenience constructor that appends a small excerpt of the input
 * string with a cursor marker to simplify debugging parse errors.
 */
public class SNbtException extends RuntimeException {

    /**
     * Creates a new exception with the given message.
     *
     * @param message description of the error
     */
    public SNbtException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the given message and adds a trimmed preview
     * of the offending input with a {@code <--[HERE]} cursor.
     *
     * @param message a description of the error
     * @param content the full SNBT input string
     * @param index   the cursor position (0-based) where the error occurred
     */
    public SNbtException(String message, String content, int index) {
        super(message + " at: " + trim(content, index));
    }

    private static String trim(String rawTag, int position) {
        StringBuilder out = new StringBuilder();
        int end = Math.min(rawTag.length(), position);
        if (end > 35)
            out.append("...");
        out.append(rawTag, Math.max(0, end - 35), end).append("<--[HERE]");
        return out.toString();
    }
}