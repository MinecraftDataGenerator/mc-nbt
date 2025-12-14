package com.dietrichpaul.mcnbt.format.nio;

import com.dietrichpaul.mcnbt.NBTTag;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface NIODeserializer<T extends NBTTag<?>> {

    T deserialize(ByteBuffer buffer);

}
