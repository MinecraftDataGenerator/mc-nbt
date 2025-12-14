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

public class UnknownTagException extends RuntimeException {

    private final int id;

    public UnknownTagException(int id) {
        this("Unknown tag id: " + id, id);
    }

    public UnknownTagException(String message, int id) {
        super(message);
        this.id = id;
    }

    public UnknownTagException(String message, Throwable cause, int id) {
        super(message, cause);
        this.id = id;
    }

    public UnknownTagException(Throwable cause, int id) {
        super(cause);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
