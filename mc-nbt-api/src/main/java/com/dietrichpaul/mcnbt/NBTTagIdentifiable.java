package com.dietrichpaul.mcnbt;

/**
 * Represents a **named NBT tag**, pairing a {@link NBTTag} with its associated name.
 * <p>
 * This is used for all tags that have a name in NBT files, including:
 * <ul>
 *     <li>The root tag of an NBT file (whose name is stored in the file header)</li>
 *     <li>Tags inside a {@link NBTCompound}</li>
 * </ul>
 *
 * @param <T> the type of the underlying {@link NBTTag}
 */
public record NBTTagIdentifiable<T extends NBTTag<?>>(String name, T tag) {
}
