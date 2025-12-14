package com.dietrichpaul.mcnbt.format.nio;

import com.dietrichpaul.mcnbt.NBTTag;

import java.nio.ByteBuffer;

@FunctionalInterface
    public interface NIOSerializer<T extends NBTTag<?>> {
        /**
         * Schreibt die Payload eines NBT-Tags in den gegebenen ByteBuffer.
         *
         * @param buffer Der ByteBuffer, in den geschrieben werden soll.
         * @param tag Der NBT-Tag, dessen Payload geschrieben werden soll.
         */
        void serialize(ByteBuffer buffer, T tag);
    }