package com.dietrichpaul.mcnbt.format.nio;

import com.dietrichpaul.mcnbt.*;
import com.dietrichpaul.mcnbt.primitive.NBTString;

import java.nio.ByteBuffer;

/**
 * Stellt eine Schätzung der maximalen Byte-Anzahl bereit, die zur Serialisierung
 * einer NBT-Struktur in einen {@link ByteBuffer} benötigt wird.
 *
 * <p>Dies ist nützlich für die effiziente Allokation des Puffers (z.B. mit {@code ByteBuffer.allocate(maxSize)}).
 * Die Schätzung ist konservativ, um Pufferüberläufe zu verhindern.
 */
public class NBTSizeProvider {

    private NBTSizeProvider() {
        // Utility class
    }

    /**
     * Berechnet die maximal mögliche Größe des serialisierten benannten NBT-Tags.
     * Dies ist der Haupteinstiegspunkt für das Root-Tag.
     *
     * @param tagIdentifiable Das benannte Tag (z.B. das Root-Compound).
     * @return Die maximal erforderliche Anzahl von Bytes.
     */
    public static int estimateMaxSize(NBTTagIdentifiable<?> tagIdentifiable) {
        int size = 0;
        NBTTag<?> tag = tagIdentifiable.tag();

        // 1. Tag ID (1 Byte)
        size += Byte.BYTES;

        // 2. Tag Name (2-Byte Länge + max 4 Bytes pro Char für UTF-8-String)
        size += estimateStringPayloadSize(tagIdentifiable.name());

        // 3. Tag Payload
        size += estimatePayloadSize(tag);

        return size;
    }

    /**
     * Berechnet die maximal mögliche Größe der serialisierten NBT-Tag-Payload.
     *
     * @param tag Der Tag.
     * @return Die maximal erforderliche Anzahl von Bytes für die Payload.
     */
    public static int estimatePayloadSize(NBTTag<?> tag) {
        if (tag == null || tag.getTagType() == NBTTagType.NULL) {
            return 0;
        }

        return switch (tag.getTagType()) {
            // Primitive Zahlentypen (Feste Größe)
            case BYTE -> Byte.BYTES;
            case SHORT -> Short.BYTES;
            case INT -> Integer.BYTES;
            case LONG -> Long.BYTES;
            case FLOAT -> Float.BYTES;
            case DOUBLE -> Double.BYTES;

            // Array-Typen (Länge + Inhalt)
            case BYTE_ARRAY -> estimateByteArrayPayloadSize((NBTByteArray) tag);
            case INT_ARRAY -> estimateIntArrayPayloadSize((NBTIntArray) tag);
            case LONG_ARRAY -> estimateLongArrayPayloadSize((NBTLongArray) tag);

            // String (Länge + Inhalt)
            case STRING -> estimateStringPayloadSize(((NBTString) tag).asString());

            // Struktur-Typen (Länge + Inhalt)
            case LIST -> estimateListPayloadSize((NBTList<?>) tag);
            case COMPOUND -> estimateCompoundPayloadSize((NBTCompound) tag);

            default ->
                    throw new IllegalArgumentException("Unbekannter Tag-Typ für Größenschätzung: " + tag.getTagType().getName());
        };
    }

    /**
     * Berechnet die maximale Größe für die String-Payload (2-Byte Länge + UTF-8-Inhalt).
     * <p>Maximale Länge für NBT-String ist 65535 Bytes, die Schätzung nimmt 4 Bytes/Char an.</p>
     */
    public static int estimateStringPayloadSize(String s) {
        if (s == null || s.isEmpty()) {
            return Short.BYTES; // 2 Bytes für Länge 0
        }
        // 2 Bytes für Länge + (Worst Case: 4 Bytes pro Java-Char für UTF-8)
        return Short.BYTES + s.length() * 4;
    }

    private static int estimateByteArrayPayloadSize(NBTByteArray tag) {
        // 4 Bytes für Länge + Byte-Array-Inhalt
        return Integer.BYTES + tag.getContent().size();
    }

    private static int estimateIntArrayPayloadSize(NBTIntArray tag) {
        // 4 Bytes für Länge + (Anzahl * 4 Bytes pro Int)
        return Integer.BYTES + tag.getContent().size() * Integer.BYTES;
    }

    private static int estimateLongArrayPayloadSize(NBTLongArray tag) {
        // 4 Bytes für Länge + (Anzahl * 8 Bytes pro Long)
        return Integer.BYTES + tag.getContent().size() * Long.BYTES;
    }

    private static int estimateListPayloadSize(NBTList<?> list) {
        // 1 Byte für internen Tag ID + 4 Bytes für List-Länge (Int)
        int size = Byte.BYTES + Integer.BYTES;

        // Wenn die Liste leer ist, ist internType NULL (0x00), und size ist 5.
        if (list.size() > 0) {
            // Summe der Payloads aller Elemente
            for (NBTTag<?> tag : list) {
                size += estimatePayloadSize(tag);
            }
        }
        return size;
    }

    private static int estimateCompoundPayloadSize(NBTCompound compound) {
        int size = 0;
        // Summe der maximalen Größen aller enthaltenen benannten Tags
        for (NBTTagIdentifiable<?> identifiable : compound) {
            size += estimateMaxSize(identifiable);
        }
        // 1 Byte für den TAG_End (0x00) Begrenzer
        size += Byte.BYTES;
        return size;
    }
}