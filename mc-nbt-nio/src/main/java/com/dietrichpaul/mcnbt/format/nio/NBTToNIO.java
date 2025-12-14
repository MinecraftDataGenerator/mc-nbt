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

package com.dietrichpaul.mcnbt.format.nio;

import com.dietrichpaul.mcnbt.*;
import com.dietrichpaul.mcnbt.primitive.*;
import com.dietrichpaul.mcnbt.exception.UnknownTagException;
import gnu.trove.list.TByteList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility-Klasse zum Serialisieren von NBT-Tags in einen {@link ByteBuffer}
 * im standardmäßigen NBT-Format (Big-Endian).
 * Wichtig ist das für Java Big Endian ausgewählt ist und für Bedrock Little Endian.
 *
 * <p>Spiegelt funktional die Deserialisierung von {@code NIOToNBT} wider, indem
 * NBT-Strukturen in den Puffer geschrieben werden.
 */
public class NBTToNIO {

    /**
     * Eine Map, die jeden {@link NBTTagType} auf den entsprechenden {@link NIOSerializer} abbildet.
     */
    private static final Map<NBTTagType, NIOSerializer<?>> SERIALIZERS;

    static {
        SERIALIZERS = new EnumMap<>(NBTTagType.class);

        // Primitive Zahlentypen
        SERIALIZERS.put(NBTTagType.BYTE, (NIOSerializer<NBTByte>) (buffer, tag) -> buffer.put(tag.asByte()));
        SERIALIZERS.put(NBTTagType.SHORT, (NIOSerializer<NBTShort>) (buffer, tag) -> buffer.putShort(tag.asShort()));
        SERIALIZERS.put(NBTTagType.INT, (NIOSerializer<NBTInt>) (buffer, tag) -> buffer.putInt(tag.asInt()));
        SERIALIZERS.put(NBTTagType.LONG, (NIOSerializer<NBTLong>) (buffer, tag) -> buffer.putLong(tag.asLong()));
        SERIALIZERS.put(NBTTagType.FLOAT, (NIOSerializer<NBTFloat>) (buffer, tag) -> buffer.putFloat(tag.asFloat()));
        SERIALIZERS.put(NBTTagType.DOUBLE, (NIOSerializer<NBTDouble>) (buffer, tag) -> buffer.putDouble(tag.asDouble()));

        // String
        SERIALIZERS.put(NBTTagType.STRING, (NIOSerializer<NBTString>) (buffer, tag) -> writeStringPayload(buffer, tag.asString()));

        // Struktur-Typen
        SERIALIZERS.put(NBTTagType.LIST, (NIOSerializer<NBTList<?>>) NBTToNIO::writeListPayload);
        SERIALIZERS.put(NBTTagType.COMPOUND, (NIOSerializer<NBTCompound>) NBTToNIO::writeCompoundPayload);

        // Array-Typen
        SERIALIZERS.put(NBTTagType.BYTE_ARRAY, (NIOSerializer<NBTByteArray>) NBTToNIO::writeByteArrayPayload);
        SERIALIZERS.put(NBTTagType.INT_ARRAY, (NIOSerializer<NBTIntArray>) NBTToNIO::writeIntArrayPayload);
        SERIALIZERS.put(NBTTagType.LONG_ARRAY, (NIOSerializer<NBTLongArray>) NBTToNIO::writeLongArrayPayload);

        // TAG_End (NULL) hat keine Payload und wird hier nicht abgebildet.
    }


    /**
     * Serialisiert einen einzelnen, benannten NBT-Tag (wie das Root-Tag oder ein Tag in einem Compound)
     * in den ByteBuffer.
     *
     * @param buffer          Der ByteBuffer, in den geschrieben werden soll.
     * @param identifiableTag Der benannte NBT-Tag zur Serialisierung.
     */
    public static void writeNamedTag(ByteBuffer buffer, NBTTagIdentifiable<?> identifiableTag) {
        Objects.requireNonNull(buffer, "buffer");
        Objects.requireNonNull(identifiableTag, "identifiableTag");

        NBTTag<?> tag = identifiableTag.tag();
        NBTTagType type = tag.getTagType();

        // 1. Tag ID schreiben (1 Byte)
        buffer.put((byte) type.getId());

        // TAG_End (0x00) hat keinen Namen und keine Payload
        if (type == NBTTagType.NULL) {
            return;
        }

        // 2. Tag Name schreiben (2-Byte Länge + UTF-8 Bytes)
        writeStringPayload(buffer, identifiableTag.name());

        // 3. Tag Payload schreiben
        writeTagPayload(buffer, tag);
    }

    /**
     * Serialisiert die Payload eines NBT-Tags in den ByteBuffer.
     * Nutzt die {@link #SERIALIZERS}-Map zur Auswahl der passenden Serialisierungsmethode.
     *
     * @param buffer Der ByteBuffer, in den geschrieben werden soll.
     * @param tag    Der NBT-Tag, dessen Payload geschrieben werden soll.
     */
    public static void writeTagPayload(ByteBuffer buffer, NBTTag<?> tag) {
        NBTTagType type = tag.getTagType();

        // TAG_End (NULL) hat keine Payload
        if (type == NBTTagType.NULL) {
            return;
        }

        // Verwende die Map für den Funktions-Dispatch
        @SuppressWarnings("unchecked")
        NIOSerializer<NBTTag<?>> serializer = (NIOSerializer<NBTTag<?>>) SERIALIZERS.get(type);

        if (serializer == null) {
            throw new UnknownTagException("Unbekannter Tag-Typ zur Serialisierung: " + type.getName(), type.getId());
        }

        try {
            serializer.serialize(buffer, tag);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Inkonsistenter NBT-Tag-Typ: Erwartet " + type.getName() + ", erhalten " + tag.getClass().getSimpleName(), e);
        }
    }

    /**
     * Schreibt die NBT String-Payload: 2-Byte vorzeichenlose Länge (Short), gefolgt von UTF-8 Bytes.
     */
    public static void writeStringPayload(ByteBuffer buffer, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

        if (bytes.length > 65535) {
            throw new IllegalArgumentException("String ist zu lang für das NBT-Format (max 65535 Bytes): " + bytes.length + " Bytes");
        }

        // 2 Bytes für die Länge (unsigned short, Big-Endian)
        buffer.putShort((short) bytes.length);

        // UTF-8 Inhalt
        buffer.put(bytes);
    }

    /**
     * Schreibt die TAG_List Payload: 1-Byte Element-Tag-ID, 4-Byte Größe (Int), gefolgt von den Element-Payloads.
     */
    private static void writeListPayload(ByteBuffer buffer, NBTList<?> list) {
        NBTTagType internType = list.getInternalType();
        if (internType == null) {
            // Eine leere Liste muss TAG_End (0x00) als internen Typ haben.
            internType = NBTTagType.NULL;
        }

        // 1. Internen Tag ID schreiben (1 Byte)
        buffer.put((byte) internType.getId());

        // 2. Listenlänge schreiben (4 Bytes, Big-Endian)
        buffer.putInt(list.size());

        // 3. Element-Payloads schreiben
        if (internType != NBTTagType.NULL) {
            for (NBTTag<?> tag : list) {
                writeTagPayload(buffer, tag);
            }
        }
    }

    /**
     * Schreibt die TAG_Compound Payload: eine Sequenz von benannten Tags, abgeschlossen durch einen TAG_End (0x00).
     */
    private static void writeCompoundPayload(ByteBuffer buffer, NBTCompound compound) {
        // Alle enthaltenen benannten Tags schreiben
        for (NBTTagIdentifiable<?> identifiable : compound) {
            writeNamedTag(buffer, identifiable);
        }

        // TAG_End Begrenzer schreiben (0x00)
        buffer.put((byte) NBTTagType.NULL.getId());
    }

    /**
     * Schreibt die TAG_Byte_Array Payload: 4-Byte Größe (Int), gefolgt von den Bytes.
     */
    private static void writeByteArrayPayload(ByteBuffer buffer, NBTByteArray tag) {
        TByteList content = tag.getContent();
        // 1. Array-Länge schreiben (4 Bytes, Big-Endian)
        buffer.putInt(content.size());

        // 2. Byte-Array-Inhalt schreiben
        buffer.put(content.toArray());
    }

    /**
     * Schreibt die TAG_Int_Array Payload: 4-Byte Größe (Int), gefolgt von den 4-Byte (Big-Endian/Little-Endian) Integern.
     */
    private static void writeIntArrayPayload(ByteBuffer buffer, NBTIntArray tag) {
        TIntList content = tag.getContent();
        int size = content.size();

        // 1. Array-Länge schreiben (4 Bytes, Big-Endian)
        buffer.putInt(size);

        // 2. Int-Array-Inhalt schreiben
        if (size > 0) {
            // asIntBuffer übernimmt die ByteOrder des Hauptpuffers
            buffer.asIntBuffer().put(content.toArray());
            // Position des Hauptpuffers manuell vorschieben
            buffer.position(buffer.position() + size * Integer.BYTES);
        }
    }

    /**
     * Schreibt die TAG_Long_Array Payload: 4-Byte Größe (Int), gefolgt von den 8-Byte (Big-Endian/Little-Endian) Longs.
     */
    private static void writeLongArrayPayload(ByteBuffer buffer, NBTLongArray tag) {
        TLongList content = tag.getContent();
        int size = content.size();

        // 1. Array-Länge schreiben (4 Bytes, Big-Endian)
        buffer.putInt(size);

        // 2. Long-Array-Inhalt schreiben
        if (size > 0) {
            // asLongBuffer übernimmt die ByteOrder des Hauptpuffers
            buffer.asLongBuffer().put(content.toArray());
            // Position des Hauptpuffers manuell vorschieben
            buffer.position(buffer.position() + size * Long.BYTES);
        }
    }
}