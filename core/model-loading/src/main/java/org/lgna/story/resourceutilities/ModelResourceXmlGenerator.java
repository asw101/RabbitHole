/*
 * Copyright (c) 2006-2011, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.xml.XMLUtilities;
import org.alice.math.immutable.AxisAlignedBox;
import org.lgna.story.implementation.alice.AliceResourceUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

final class ModelResourceXmlGenerator {
  private ModelResourceXmlGenerator() {
  }

  static String createXMLString(ModelResourceExporter exporter) {
    Document doc = createXMLDocument(exporter);
    if (doc != null) {
      try {
        TransformerFactory transfac = TransformerFactory.newInstance();
        transfac.setAttribute("indent-number", 4);
        Transformer trans = transfac.newTransformer();
        //                  trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
        String xmlString = sw.toString();

        return xmlString;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private static Document createXMLDocument(ModelResourceExporter exporter) {
    try {
      Document doc = XMLUtilities.createDocument();
      Element modelRoot = doc.createElement("AliceModel");
      modelRoot.setAttribute("name", exporter.getClassName());
      if ((exporter.getAttributionName() != null) && (exporter.getAttributionName().length() > 0)) {
        modelRoot.setAttribute("creator", exporter.getAttributionName());
      }
      if ((exporter.getAttributionYear() != null) && (exporter.getAttributionYear().length() > 0)) {
        modelRoot.setAttribute("creationYear", exporter.getAttributionYear());
      }
      if (exporter.isDeprecated()) {
        modelRoot.setAttribute("deprecated", "TRUE");
      }
      if (exporter.isPlaceOnGround()) {
        modelRoot.setAttribute("placeOnGround", "TRUE");
      }
      doc.appendChild(modelRoot);
      if (exporter.getBoundingBoxes().get(exporter.getClassName()) == null) {
        AxisAlignedBox superBox = AxisAlignedBox.NaN;
        for (Entry<String, AxisAlignedBox> entry : exporter.getBoundingBoxes().entrySet()) {
          superBox = superBox.union(entry.getValue());
        }
        exporter.getBoundingBoxes().put(exporter.getClassName(), superBox);
      }
      modelRoot.appendChild(createBoundingBoxElement(doc, exporter.getBoundingBoxes().get(exporter.getClassName())));
      modelRoot.appendChild(createTagsElement(doc, exporter.getTags()));
      modelRoot.appendChild(createGroupTagsElement(doc, exporter.getGroupTags()));
      modelRoot.appendChild(createThemeTagsElement(doc, exporter.getThemeTags()));

      for (ModelSubResourceExporter subResource : exporter.getSubResources()) {
        if (!subResource.getModelName().equalsIgnoreCase(exporter.getClassName()) && exporter.getBoundingBoxes().containsKey(subResource.getModelName())) {
          subResource.setBbox(exporter.getBoundingBoxes().get(subResource.getModelName()));
        }
        modelRoot.appendChild(createSubResourceElement(doc, subResource, exporter));
      }

      return doc;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static Element createBoundingBoxElement(Document doc, AxisAlignedBox bbox) {
    Element bboxElement = doc.createElement("BoundingBox");
    Element minElement = doc.createElement("Min");
    minElement.setAttribute("x", Double.toString(bbox.getXMinimum()));
    minElement.setAttribute("y", Double.toString(bbox.getYMinimum()));
    minElement.setAttribute("z", Double.toString(bbox.getZMinimum()));
    Element maxElement = doc.createElement("Max");
    maxElement.setAttribute("x", Double.toString(bbox.getXMaximum()));
    maxElement.setAttribute("y", Double.toString(bbox.getYMaximum()));
    maxElement.setAttribute("z", Double.toString(bbox.getZMaximum()));

    bboxElement.appendChild(minElement);
    bboxElement.appendChild(maxElement);

    return bboxElement;
  }

  private static Element createTagsElement(Document doc, List<String> tagList) {
    Element tagsElement = doc.createElement("Tags");
    for (String tag : tagList) {
      Element tagElement = doc.createElement("Tag");
      tagElement.setTextContent(tag);
      tagsElement.appendChild(tagElement);
    }
    return tagsElement;
  }

  private static Element createGroupTagsElement(Document doc, List<String> tagList) {
    Element tagsElement = doc.createElement("GroupTags");
    for (String tag : tagList) {
      Element tagElement = doc.createElement("GroupTag");
      tagElement.setTextContent(tag);
      tagsElement.appendChild(tagElement);
    }
    return tagsElement;
  }

  private static Element createThemeTagsElement(Document doc, List<String> tagList) {
    Element tagsElement = doc.createElement("ThemeTags");
    for (String tag : tagList) {
      Element tagElement = doc.createElement("ThemeTag");
      tagElement.setTextContent(tag);
      tagsElement.appendChild(tagElement);
    }
    return tagsElement;
  }

  private static Element createSubResourceElement(Document doc, ModelSubResourceExporter subResource, ModelResourceExporter exporter) {
    Element resourceElement = doc.createElement("Resource");
    resourceElement.setAttribute("textureName", AliceResourceUtilities.makeEnumName(subResource.getTextureName()));
    resourceElement.setAttribute("resourceName", ModelResourceExporter.createResourceEnumName(exporter, subResource));
    if (subResource.getModelName() != null) {
      resourceElement.setAttribute("modelName", subResource.getModelName());
    }
    if (subResource.getAttributionName() != null) {
      resourceElement.setAttribute("creator", subResource.getAttributionName());
    }
    if (subResource.getAttributionYear() != null) {
      resourceElement.setAttribute("creationYear", subResource.getAttributionYear());
    }
    if (subResource.getModelName() != null) {
      resourceElement.setAttribute("modelName", subResource.getModelName());
    }
    if (subResource.getBbox() != null) {
      resourceElement.appendChild(createBoundingBoxElement(doc, subResource.getBbox()));
    }
    appendUniqueTags(doc, resourceElement, "Tags", "Tag", subResource.getTags(), exporter.getTags());
    appendUniqueTags(doc, resourceElement, "GroupTags", "GroupTag", subResource.getGroupTags(), exporter.getGroupTags());
    appendUniqueTags(doc, resourceElement, "ThemeTags", "ThemeTag", subResource.getThemeTags(), exporter.getThemeTags());
    return resourceElement;
  }

  private static void appendUniqueTags(Document doc, Element resourceElement, String groupName, String itemName, List<String> resourceTags, List<String> parentTags) {
    if (!resourceTags.isEmpty()) {
      List<String> uniqueTags = new ArrayList<String>();
      for (String tag : resourceTags) {
        if ((parentTags == null) || !parentTags.contains(tag)) {
          uniqueTags.add(tag);
        }
      }
      if (!uniqueTags.isEmpty()) {
        resourceElement.appendChild(createNamedTagsElement(doc, groupName, itemName, uniqueTags));
      }
    }
  }

  private static Element createNamedTagsElement(Document doc, String groupName, String itemName, List<String> tagList) {
    Element tagsElement = doc.createElement(groupName);
    for (String tag : tagList) {
      Element tagElement = doc.createElement(itemName);
      tagElement.setTextContent(tag);
      tagsElement.appendChild(tagElement);
    }
    return tagsElement;
  }
}
