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

package com.dietrichpaul.mcnbt.exception;

/**
 * Exception that's raised, when the underlying NBT parser
 * encounters an unrecognized NBT tag type ID.
 */
public class UnknownTagException extends RuntimeException {
    /**
     * The unrecognized tag type ID.
     */
    private final int id;

    /**
     * Creates a new {@code UnknownTagException} with a default detail message
     * that includes the given unknown tag type ID.
     *
     * @param id the unrecognized NBT tag type ID
     */
    public UnknownTagException(int id) {
        this("Unknown tag id: " + id, id);
    }

    /**
     * Creates a new {@code UnknownTagException} with the specified detail message.
     *
     * @param message the detail message
     * @param id      the unrecognized NBT tag type ID
     */
    public UnknownTagException(String message, int id) {
        super(message);
        this.id = id;
    }

    /**
     * Creates a new {@code UnknownTagException} with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     * @param id      the unrecognized NBT tag type ID
     */
    public UnknownTagException(String message, Throwable cause, int id) {
        super(message, cause);
        this.id = id;
    }

    /**
     * Creates a new {@code UnknownTagException} with the specified cause.
     *
     * @param cause the cause
     * @param id    the unrecognized NBT tag type ID
     */
    public UnknownTagException(Throwable cause, int id) {
        super(cause);
        this.id = id;
    }

    /**
     * Returns the unrecognized NBT tag type ID that caused this exception.
     *
     * @return the unknown tag type ID
     */
    public int getId() {
        return id;
    }
}
