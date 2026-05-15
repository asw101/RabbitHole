/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
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
 *******************************************************************************/
package org.lgna.story.resourceutilities;

import org.alice.math.immutable.AxisAlignedBox;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Package-private utility class holding static XML-parsing helpers
 * extracted from ModelResourceInfo to reduce its line count.
 *
 * All methods are stateless and operate on DOM elements.
 */
final class ModelResourceXmlParser {

  private ModelResourceXmlParser() {
  }

  static AxisAlignedBox getBoundingBoxFromXML(Element bboxElement) {
    if (bboxElement != null) {
      Element min = (Element) bboxElement.getElementsByTagName("Min").item(0);
      Element max = (Element) bboxElement.getElementsByTagName("Max").item(0);

      double minX = Double.parseDouble(min.getAttribute("x"));
      double minY = Double.parseDouble(min.getAttribute("y"));
      double minZ = Double.parseDouble(min.getAttribute("z"));

      double maxX = Double.parseDouble(max.getAttribute("x"));
      double maxY = Double.parseDouble(max.getAttribute("y"));
      double maxZ = Double.parseDouble(max.getAttribute("z"));

      return AxisAlignedBox.createAxisAlignedBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    return null;
  }

  static ModelResourceInfo getSubResourceFromXML(Element resourceElement, ModelResourceInfo parent) {
    if (resourceElement != null) {
      AxisAlignedBox bbox = null;
      NodeList bboxNodeList = resourceElement.getElementsByTagName("BoundingBox");
      if (bboxNodeList.getLength() > 0) {
        bbox = getBoundingBoxFromXML((Element) bboxNodeList.item(0));
      }
      String modelName = null;
      if (resourceElement.hasAttribute("modelName")) {
        modelName = resourceElement.getAttribute("modelName");
      }
      String textureName = null;
      if (resourceElement.hasAttribute("textureName")) {
        textureName = resourceElement.getAttribute("textureName");
      }
      String resourceName = null;
      if (resourceElement.hasAttribute("resourceName")) {
        resourceName = resourceElement.getAttribute("resourceName");
      }
      String creatorName = null;
      if (resourceElement.hasAttribute("creator")) {
        creatorName = resourceElement.getAttribute("creator");
      }
      int creationYearTemp = -1;
      if (resourceElement.hasAttribute("creationYear")) {
        try {
          creationYearTemp = Integer.parseInt(resourceElement.getAttribute("creationYear"));
        } catch (NumberFormatException ignored) {
        }
      }
      boolean isDeprecated = resourceElement.hasAttribute("deprecated")
          && Boolean.parseBoolean(resourceElement.getAttribute("deprecated"));
      Boolean placeOnGround = resourceElement.hasAttribute("placeOnGround")
          ? Boolean.parseBoolean(resourceElement.getAttribute("placeOnGround")) : null;
      String[] tags = getResourceTags(resourceElement, "Tags", "Tag");
      String[] groupTags = getResourceTags(resourceElement, "GroupTags", "GroupTag");
      String[] themeTags = getResourceTags(resourceElement, "ThemeTags", "ThemeTag");

      return new ModelResourceInfo(parent, resourceName, creatorName, creationYearTemp, bbox, tags, groupTags, themeTags, modelName, textureName, isDeprecated, placeOnGround);
    }

    return null;
  }

  static String[] getResourceTags(Element resourceElement, String containerTagName, String tagName) {
    List<String> tagList = new ArrayList<>();
    addImmediateChildTextContent(resourceElement, tagName, tagList);
    for (Element container : getImmediateChildElementsByTagName(resourceElement, containerTagName)) {
      addImmediateChildTextContent(container, tagName, tagList);
    }
    return tagList.toArray(new String[0]);
  }

  static void addImmediateChildTextContent(Element parent, String tagName, List<String> textContent) {
    for (Element child : getImmediateChildElementsByTagName(parent, tagName)) {
      textContent.add(child.getTextContent());
    }
  }

  static List<Element> getImmediateChildElementsByTagName(Element node, String tagName) {
    List<Element> elements = new ArrayList<>();
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if ((child instanceof Element element) && child.getNodeName().equals(tagName)) {
        elements.add(element);
      }
    }
    return elements;
  }
}
