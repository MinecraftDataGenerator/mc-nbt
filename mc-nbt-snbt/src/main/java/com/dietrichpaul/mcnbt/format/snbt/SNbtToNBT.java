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

import com.dietrichpaul.mcnbt.NBTTag;

/**
 * Utility for parsing SNBT (stringified NBT) into {@link NBTTag} instances.
 * <p>
 * This class exposes convenience methods to parse SNBT strings following
 * different Minecraft versions' syntax rules. For most use cases, prefer
 * {@link #parse(String)} which uses the latest supported syntax.
 */
public class SNbtToNBT {
    private SNbtToNBT() {
    }

    /**
     * Parses an SNBT string using the latest supported syntax rules (currently 1.21.5+).
     *
     * @param snbt the SNBT input string (e.g. "{foo:1b}")
     * @return the parsed {@link NBTTag} tree
     * @throws SNbtException if the input cannot be parsed according to the latest syntax rules
     */
    public static NBTTag<?> parse(String snbt) {
        return parse(snbt, SNbtSyntax.V1_21_5);
    }

    /**
     * Parses an SNBT string using specific version rules.
     *
     * @param snbt   the SNBT input string
     * @param syntax the syntax profile that defines parsing behavior
     * @return the parsed {@link NBTTag} tree
     * @throws SNbtException if parsing fails for the provided syntax
     */
    public static NBTTag<?> parse(String snbt, SNbtSyntax syntax) {
        if (syntax.isLegacyParser()) {
            return new LegacyParser(snbt).parse();
        }
        else {
            return new ModernParser(snbt, syntax).parse();
        }
    }
}