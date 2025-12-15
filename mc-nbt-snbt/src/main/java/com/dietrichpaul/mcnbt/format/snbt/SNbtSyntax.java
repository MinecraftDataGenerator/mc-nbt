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

public enum SNbtSyntax {
    V1_7(true, false, false, false),
    V1_8(true, false, false, false),
    V1_12(false, false, true, false),
    V1_13(false, false, true, false), // 1.13 parsing logic is mostly 1.12 but strictly no legacy quirks
    V1_14(false, true, true, false),
    V1_21_5(false, true, true, true);

    private final boolean legacyParser; // Uses the old 1.7/1.8 weird parsing logic
    private final boolean allowSingleQuotes;
    private final boolean useTypeSuffix; // e.g. 12s, 5b
    private final boolean modernArrays; // strict [I; ...] syntax

    SNbtSyntax(boolean legacyParser, boolean allowSingleQuotes, boolean useTypeSuffix, boolean modernArrays) {
        this.legacyParser = legacyParser;
        this.allowSingleQuotes = allowSingleQuotes;
        this.useTypeSuffix = useTypeSuffix;
        this.modernArrays = modernArrays;
    }

    public boolean isLegacyParser() {
        return legacyParser;
    }

    public boolean isAllowSingleQuotes() {
        return allowSingleQuotes;
    }

    public boolean isUseTypeSuffix() {
        return useTypeSuffix;
    }

    public boolean isModernArrays() {
        return modernArrays;
    }
}