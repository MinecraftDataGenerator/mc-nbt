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
import com.dietrichpaul.mcnbt.primitive.NBTByte;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Utility class for converting {@link NBTTag} instances into an XML DOM
 * that conforms to the <a href="https://github.com/Foresteam/XNBTEdit">XNBTEdit XML format</a>.
 *
 * <p>
 * XNBTEdit uses a human-readable XML representation of NBT data, where:
 * <ul>
 *     <li>Compound tags are represented as <code>&lt;compound&gt;</code> elements.</li>
 *     <li>Lists are represented as <code>&lt;list of="...">&lt;/list&gt;</code> elements with homogeneous type.</li>
 *     <li>Primitive tags are represented using their type as the element name, e.g., <code>&lt;int&gt;</code>, <code>&lt;string&gt;</code>.</li>
 *     <li>Byte tags with value 0 or 1 are serialized as <code>false</code> or <code>true</code>.</li>
 *     <li>Other primitive values are serialized as text content of the element.</li>
 * </ul>
 *
 * <p>
 * This class uses the Java built-in <code>org.w3c.dom</code> API to create a structured XML tree.
 * It does not perform XML serialization itself; use a {@link javax.xml.transform.Transformer}
 * to write the {@link Document} to a file or stream.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * NBTTagIdentifiable<NBTCompound> root = ...; // read from NBT file
 * Document doc = NBTToXML.convertToDocument(root.tag());
 *
 * Transformer transformer = TransformerFactory.newInstance().newTransformer();
 * transformer.setOutputProperty(OutputKeys.INDENT, "yes");
 * transformer.transform(new DOMSource(doc), new StreamResult(System.out));
 * }</pre>
 */
public class NBTToXML {
    private NBTToXML() {}

    /**
     * Converts a root NBT tag into an XML {@link Document} suitable for XNBTEdit.
     *
     * @param tag the root NBT tag to convert, typically a {@link NBTCompound}
     * @return a {@link Document} containing the XML representation of the NBT
     * @throws ParserConfigurationException if a {@link DocumentBuilder} cannot be created
     */
    public static Document convertToDocument(NBTTag<?> tag) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = buildElement(doc, tag, null);
        doc.appendChild(root);

        return doc;
    }

    /**
     * Recursively converts an NBT tag into a corresponding {@link Element} for the XML DOM.
     *
     * @param doc  the {@link Document} to create elements in
     * @param tag  the NBT tag to convert
     * @param name the optional name of the tag; {@code null} if unnamed (e.g., list elements)
     * @return an {@link Element} representing the NBT tag in XML
     * @throws IllegalArgumentException if the NBT type is unknown
     */
    private static Element buildElement(Document doc, NBTTag<?> tag, String name) {
        Element element;

        if (tag.getTagType().isCompound()) {
            // Compound tags: <compound name="...">...</compound>
            element = doc.createElement("compound");
            if (name != null) element.setAttribute("name", name);

            NBTCompound compound = tag.asCompound();
            for (NBTTagIdentifiable<?> namedTag : compound.getTagList()) {
                element.appendChild(buildElement(doc, namedTag.tag(), namedTag.name()));
            }

        } else if (tag.getTagType().isList()) {
            // List tags: <list of="type" name="...">...</list>
            NBTList<?> list = tag.asList();
            element = doc.createElement("list");
            element.setAttribute("of", list.getInternalType().getName());
            if (name != null) element.setAttribute("name", name);

            for (NBTTag<?> item : list) {
                element.appendChild(buildElement(doc, item, null));
            }

        } else if (tag.getTagType().isArray()) {
            // Array tags: <byte_array>...</byte_array>, <int_array>..., <long_array>...
            element = doc.createElement("array");
            if (name != null) element.setAttribute("name", name);

            if (tag instanceof NBTByteArray ba) {
                element.setAttribute("of", "byte");
                for (byte b : ba.asIterable()) {
                    Element e = doc.createElement("byte");
                    if (b == 0) e.setTextContent("false");
                    else if (b == 1) e.setTextContent("true");
                    else e.setTextContent(Byte.toString(b));
                    element.appendChild(e);
                }
            } else if (tag instanceof NBTIntArray ia) {
                element.setAttribute("of", "int");
                for (int v : ia.asIterable()) {
                    Element e = doc.createElement("int");
                    e.setTextContent(Integer.toString(v));
                    element.appendChild(e);
                }
            } else if (tag instanceof NBTLongArray la) {
                element.setAttribute("of", "long");
                for (long v : la.asIterable()) {
                    Element e = doc.createElement("long");
                    e.setTextContent(Long.toString(v));
                    element.appendChild(e);
                }
            }

        } else if (tag.getTagType().isPrimitive()) {
            // Primitive tags: <byte>, <short>, <int>, <long>, <float>, <double>, <string>
            element = doc.createElement(tag.getTagType().getName());
            if (name != null) element.setAttribute("name", name);

            Object value = ((NBTPrimitive<?>) tag).getPrimitiveType();
            if (tag instanceof NBTByte btag) {
                if (btag.getPrimitiveType() == 0) element.setTextContent("false");
                else if (btag.getPrimitiveType() == 1) element.setTextContent("true");
                else element.setTextContent(Byte.toString(btag.getPrimitiveType()));
            } else {
                element.setTextContent(value.toString());
            }

        } else {
            throw new IllegalArgumentException("Unknown NBT type: " + tag);
        }

        return element;
    }
}
