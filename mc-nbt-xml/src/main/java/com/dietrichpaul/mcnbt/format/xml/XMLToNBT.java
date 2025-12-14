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

package com.dietrichpaul.mcnbt.format.xml;

import com.dietrichpaul.mcnbt.*;
import com.dietrichpaul.mcnbt.primitive.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class for converting XML DOM in the <a href="https://github.com/Foresteam/XNBTEdit">XNBTEdit format</a>
 * back into {@link NBTTag} instances.
 *
 * <p>
 * This class parses <code>&lt;compound&gt;</code>, <code>&lt;list&gt;</code>, <code>&lt;array&gt;</code>,
 * and primitive elements, recreating the exact NBT structure. Boolean bytes ("true"/"false") are
 * converted back to 1/0 {@link NBTByte} values.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
 * Document doc = builder.parse(new File("playerdata.xml"));
 *
 * NBTTag<?> root = XMLToNBT.convertDocument(doc);
 * }</pre>
 */
public class XMLToNBT {
    private XMLToNBT() {
    }

    /**
     * Converts an XML {@link Document} into the corresponding root {@link NBTTag}.
     *
     * @param document the XML document to convert
     * @return the root {@link NBTTag} instance
     */
    public static NBTTag<?> convertDocument(Document document) {
        Element rootElement = document.getDocumentElement();
        return parseElement(rootElement);
    }

    /**
     * Recursively converts an XML {@link Element} into an {@link NBTTag}.
     *
     * @param element the XML element to parse
     * @return the corresponding {@link NBTTag} instance
     * @throws IllegalArgumentException if the element type is unsupported
     */
    private static <T extends NBTTag<?>> T parseElement(Element element) {
        String tagName = element.getTagName();
        String nameAttr = element.getAttribute("name");
        NBTTag<?> result;

        switch (tagName) {
            case "compound" -> {
                NBTCompound compound = new NBTCompound();
                NodeList children = element.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    if (node instanceof Element child) {
                        NBTTag<?> childTag = parseElement(child);
                        compound.put(child.getAttribute("name"), childTag);
                    }
                }
                result = compound;
            }
            case "list" -> {
                String ofType = element.getAttribute("of");
                NBTTagType internalType = NBTTagType.getByName(ofType);
                if (internalType == null)
                    throw new IllegalArgumentException("Unknown list type: " + ofType);

                NBTList<? extends NBTTag<?>> list = NBTList.of(internalType);
                NodeList children = element.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    if (node instanceof Element child) {
                        list.add(parseElement(child));
                    }
                }
                result = list;
            }
            case "array" -> {
                String ofType = element.getAttribute("of");
                NBTTagType arrayType = NBTTagType.getByName(ofType);
                NodeList children = element.getChildNodes();

                if ("byte".equals(ofType)) {
                    byte[] arr = new byte[children.getLength()];
                    int idx = 0;
                    for (int i = 0; i < children.getLength(); i++) {
                        Node node = children.item(i);
                        if (node instanceof Element child) {
                            String text = child.getTextContent();
                            if ("true".equalsIgnoreCase(text))
                                arr[idx++] = 1;
                            else if ("false".equalsIgnoreCase(text))
                                arr[idx++] = 0;
                            else
                                arr[idx++] = Byte.parseByte(text);
                        }
                    }
                    result = NBTByteArray.of(arr);
                }
                else if ("int".equals(ofType)) {
                    int[] arr = new int[children.getLength()];
                    int idx = 0;
                    for (int i = 0; i < children.getLength(); i++) {
                        Node node = children.item(i);
                        if (node instanceof Element child) {
                            arr[idx++] = Integer.parseInt(child.getTextContent());
                        }
                    }
                    result = NBTIntArray.of(arr);
                }
                else if ("long".equals(ofType)) {
                    long[] arr = new long[children.getLength()];
                    int idx = 0;
                    for (int i = 0; i < children.getLength(); i++) {
                        Node node = children.item(i);
                        if (node instanceof Element child) {
                            arr[idx++] = Long.parseLong(child.getTextContent());
                        }
                    }
                    result = NBTLongArray.of(arr);
                }
                else {
                    throw new IllegalArgumentException("Unknown array type: " + ofType);
                }
            }
            default -> {
                // Primitive tags
                String text = element.getTextContent();
                result = switch (tagName) {
                    case "byte" -> {
                        byte value;
                        if ("true".equalsIgnoreCase(text))
                            value = 1;
                        else if ("false".equalsIgnoreCase(text))
                            value = 0;
                        else
                            value = Byte.parseByte(text);
                        yield NBTByte.of(value);
                    }
                    case "short" -> NBTShort.of(Short.parseShort(text));
                    case "int" -> NBTInt.of(Integer.parseInt(text));
                    case "long" -> NBTLong.of(Long.parseLong(text));
                    case "float" -> NBTFloat.of(Float.parseFloat(text));
                    case "double" -> NBTDouble.of(Double.parseDouble(text));
                    case "string" -> NBTString.of(text);
                    default -> throw new IllegalArgumentException("Unknown primitive type: " + tagName);
                };
            }
        }

        @SuppressWarnings("unchecked") T t = (T) result;
        return t;
    }
}
