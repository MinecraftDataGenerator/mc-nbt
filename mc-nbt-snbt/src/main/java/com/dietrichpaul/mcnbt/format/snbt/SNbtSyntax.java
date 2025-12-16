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
 * Syntax profiles for parsing/serializing SNBT across different Minecraft versions.
 * <p>
 * Each constant describes how numbers, arrays and quotes are treated by the parser
 * and serializer. Use together with {@link SNbtToNBT#parse(String, SNbtSyntax)} and
 * {@link NBTToSNbt#serialize(com.dietrichpaul.mcnbt.NBTTag, SNbtSyntax)}.
 */
public enum SNbtSyntax {
    // @formatter:off
    /** Every version from 1.7 until 1.8 */
    V1_7    (true, false, false, false),
    /** Every version from 1.8 until 1.12 */
    V1_8    (true, false, false, false),
    /** Every version from 1.12 until 1.13 */
    V1_12   (false, false, true, false),
    /** Every version from 1.13 until 1.14 */
    V1_13   (false, false, true, false), // 1.13 parsing logic is mostly 1.12 but strictly no legacy quirks
    /** Every version from 1.14 until 1.21.5 */
    V1_14   (false, true, true, false),
    /** Every version from 1.21.5 going onward */
    V1_21_5 (false, true, true, true);
    // @formatter:on

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

    /**
     * Determines whether this syntax instance using legacy parsing behavior.
     *
     * @return whether to use the legacy 1.7/1.8 parsing behavior
     */
    public boolean isLegacyParser() {
        return legacyParser;
    }

    /**
     * Determines whether single quotes are allowed with this syntax.
     *
     * @return whether single quotes are allowed for strings when serializing/parsing
     */
    public boolean isAllowSingleQuotes() {
        return allowSingleQuotes;
    }

    /**
     * Determines whether numeric type suffixes are used with this syntax.
     *
     * @return whether numeric type suffixes like {@code 5b}, {@code 12s}, {@code 1L} are used
     */
    public boolean isUseTypeSuffix() {
        return useTypeSuffix;
    }

    /**
     * Determines whether this syntax uses modern array notation.
     *
     * @return whether modern array notation like {@code [I; 1, 2, 3]} is enforced
     */
    public boolean isModernArrays() {
        return modernArrays;
    }
}