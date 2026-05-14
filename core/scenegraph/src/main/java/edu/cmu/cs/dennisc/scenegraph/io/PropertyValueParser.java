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
package edu.cmu.cs.dennisc.scenegraph.io;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.image.ImageUtilities;
import edu.cmu.cs.dennisc.java.io.FileUtilities;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.texture.TextureCoordinate2f;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector2f;
import org.alice.math.immutable.Vector3f;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Parses property values from XML elements and text content.
 * Extracted from ASGDecoder to reduce file size.
 *
 * @author Dennis Cosgrove
 */
class PropertyValueParser {

  // ── XML helpers ──────────────────────────────────────────────────

  static Element getFirstChild(Node node, String tag) {
    Node childNode = node.getFirstChild();
    while (childNode != null) {
      if (childNode instanceof Element element) {
        if (childNode.getNodeName().equals(tag)) {
          return element;
        }
      }
      childNode = childNode.getNextSibling();
    }
    return null;
  }

  static Element[] getChildren(Node node, String tag) {
    List<Element> list = new ArrayList<>();
    Node childNode = node.getFirstChild();
    while (childNode != null) {
      if (childNode instanceof Element element) {
        if (childNode.getNodeName().equals(tag)) {
          list.add(element);
        }
      }
      childNode = childNode.getNextSibling();
    }
    return list.toArray(new Element[0]);
  }

  static String getNodeText(Node node) {
    NodeList children = node.getChildNodes();
    int length = children.getLength();
    if (length == 1) {
      return ((Text) children.item(0)).getData().trim();
    }
    StringBuilder propertyTextBuffer = new StringBuilder();
    for (int j = 0; j < length; j++) {
      Text textNode = (Text) children.item(j);
      propertyTextBuffer.append(textNode.getData().trim());
    }
    return propertyTextBuffer.toString();
  }

  // ── Text parsing helpers ─────────────────────────────────────────

  private static void decodeIntArray(String s, int[] array, int offset, int length, boolean isHexadecimal) {
    int index = offset;
    int begin = 0;
    for (int lcv = 0; lcv < length; lcv++) {
      int end = s.indexOf(' ', begin);
      if (end == -1) {
        end = s.length();
      }
      String substr = s.substring(begin, end);
      int value;
      if (isHexadecimal) {
        value = (int) Long.parseLong(substr, 16);
      } else {
        value = Integer.parseInt(substr);
      }
      array[index++] = value;
      begin = end + 1;
    }
  }

  private static void decodeIntArray(String s, int[] array, boolean isHexadecimal) {
    decodeIntArray(s, array, 0, array.length, isHexadecimal);
  }

  private static void decodeDoubleArray(String s, double[] array, int offset, int length) {
    int index = offset;
    int begin = 0;
    for (int lcv = 0; lcv < length; lcv++) {
      int end = s.indexOf(' ', begin);
      if (end == -1) {
        end = s.length();
      }
      String substr = s.substring(begin, end);
      array[index++] = Double.parseDouble(substr);
      begin = end + 1;
    }
  }

  private static Point3 decodePoint3(String s) {
    int begin = 0;
    int end = s.indexOf(' ', begin);
    double x = Double.parseDouble(s.substring(begin, end));
    begin = end + 1;
    end = s.indexOf(' ', begin);
    double y = Double.parseDouble(s.substring(begin, end));
    begin = end + 1;
    end = s.length();
    double z = Double.parseDouble(s.substring(begin, end));
    return new Point3(x, y, z);
  }

  private static Vector3f decodeVector3f(String s) {
    int begin = 0;
    int end = s.indexOf(' ', begin);
    float x = Float.parseFloat(s.substring(begin, end));
    begin = end + 1;
    end = s.indexOf(' ', begin);
    float y = Float.parseFloat(s.substring(begin, end));
    begin = end + 1;
    end = s.length();
    float z = Float.parseFloat(s.substring(begin, end));
    return new Vector3f(x, y, z);
  }

  private static Vector2f decodeVector2f(String s) {
    int begin = 0;
    int end = s.indexOf(' ', begin);
    begin = end + 1;
    end = s.length();
    return new Vector2f(Float.parseFloat(s.substring(begin, end)), Float.parseFloat(s.substring(begin, end)));
  }

  private static TextureCoordinate2f decodeTexCoord2f(String s) {
    int begin = 0;
    int end = s.indexOf(' ', begin);
    float u = Float.parseFloat(s.substring(begin, end));
    begin = end + 1;
    end = s.length();
    float v = Float.parseFloat(s.substring(begin, end));
    return new TextureCoordinate2f(u, v);
  }

  private static Color4f decodeColor4f(String s) {
    int begin = 0;
    int end = s.indexOf(' ', begin);
    float red = Float.parseFloat(s.substring(begin, end));
    begin = end + 1;
    end = s.indexOf(' ', begin);
    float green = Float.parseFloat(s.substring(begin, end));
    begin = end + 1;
    end = s.indexOf(' ', begin);
    float blue = Float.parseFloat(s.substring(begin, end));
    begin = end + 1;
    end = s.length();
    float alpha = Float.parseFloat(s.substring(begin, end));
    return new Color4f(red, green, blue, alpha);
  }

  private static Object valueOf(Class<?> cls, String text) {
    if (String.class.isAssignableFrom(cls)) {
      return text;
    } else if (cls.equals(Double.class) && text.equals("Infinity")) {
      return Double.POSITIVE_INFINITY;
    } else if (cls.equals(Double.class) && text.equals("NaN")) {
      return Double.NaN;
    } else {
      Class<?>[] parameterTypes = {String.class};
      try {
        Method valueOfMethod = cls.getMethod("valueOf", parameterTypes);
        int modifiers = valueOfMethod.getModifiers();
        if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
          Object[] parameters = {text};
          return valueOfMethod.invoke(null, parameters);
        } else {
          throw new RuntimeException("valueOf method not public static.");
        }
      } catch (NoSuchMethodException nsme) {
        throw new RuntimeException("NoSuchMethodException: class[" + cls.getName() + "]; method[" + text + "]");
      } catch (IllegalAccessException iae) {
        throw new RuntimeException("IllegalAccessException: " + cls + " " + text);
      } catch (InvocationTargetException ite) {
        throw new RuntimeException("java.lang.reflect.InvocationTargetException: " + cls + " " + text);
      }
    }
  }

  // ── Property value resolution ────────────────────────────────────

  static Object parseValue(Element xmlProperty, Class<?> propertyValueClass, HashMap<String, InputStream> filenameToStreamMap) {
    if (xmlProperty.hasAttribute("filename")) {
      return parseFromFile(xmlProperty, propertyValueClass, filenameToStreamMap);
    }
    return parseFromXml(xmlProperty, propertyValueClass);
  }

  private static Object parseFromFile(Element xmlProperty, Class<?> propertyValueClass, HashMap<String, InputStream> filenameToStreamMap) {
    String filename = xmlProperty.getAttribute("filename");
    InputStream is = filenameToStreamMap.get(filename);
    if (is == null) {
      throw new RuntimeException();
    }
    if (Image.class.isAssignableFrom(propertyValueClass)) {
      String ext = FileUtilities.getExtension(filename);
      String codecName = ImageUtilities.getCodecNameForExtension(ext);
      try {
        return ImageUtilities.read(codecName, is);
      } catch (IOException ioe) {
        throw new RuntimeException(filename, ioe);
      }
    } else if (Vertex[].class.isAssignableFrom(propertyValueClass)) {
      return BinaryArrayDecoder.decodeVertexArray(is);
    } else if (int[].class.isAssignableFrom(propertyValueClass)) {
      return BinaryArrayDecoder.decodeIntArray(is);
    } else if (double[].class.isAssignableFrom(propertyValueClass)) {
      return BinaryArrayDecoder.decodeDoubleArray(is);
    }
    throw new RuntimeException();
  }

  private static Object parseFromXml(Element xmlProperty, Class<?> propertyValueClass) {
    if (AffineMatrix4x4.class.isAssignableFrom(propertyValueClass)) {
      Element[] xmlRows = getChildren(xmlProperty, "row");
      double[] values = new double[16];
      for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
        decodeDoubleArray(getNodeText(xmlRows[rowIndex]), values, 4 * rowIndex, 4);
      }
      return AffineMatrix4x4.createFromRowMajorArray(values);
    } else if (Matrix3x3.class.isAssignableFrom(propertyValueClass)) {
      Element[] xmlRows = getChildren(xmlProperty, "row");
      double[] values = new double[9];
      for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
        decodeDoubleArray(getNodeText(xmlRows[rowIndex]), values, 3 * rowIndex, 3);
      }
      return Matrix3x3.create(values);
    } else if (Image.class.isAssignableFrom(propertyValueClass)) {
      int width = Integer.parseInt(xmlProperty.getAttribute("width"));
      int height = Integer.parseInt(xmlProperty.getAttribute("height"));
      Element[] xmlRows = getChildren(xmlProperty, "row");
      int[] pixels = new int[width * height];
      int pixelIndex = 0;
      for (int rowIndex = 0; rowIndex < height; rowIndex++) {
        String s = getNodeText(xmlRows[rowIndex]);
        decodeIntArray(s, pixels, pixelIndex, width, true);
        pixelIndex += width;
      }
      return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixels, 0, width));
    } else if (Color4f.class.isAssignableFrom(propertyValueClass)) {
      float red = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "red")));
      float green = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "green")));
      float blue = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "blue")));
      float alpha = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "alpha")));
      return new Color4f(red, green, blue, alpha);
    } else if (int[].class.isAssignableFrom(propertyValueClass)) {
      int length = Integer.parseInt(xmlProperty.getAttribute("length"));
      int[] intArray = new int[length];
      decodeIntArray(getNodeText(xmlProperty), intArray, false);
      return intArray;
    } else if (double[].class.isAssignableFrom(propertyValueClass)) {
      int length = Integer.parseInt(xmlProperty.getAttribute("length"));
      double[] doubleArray = new double[length];
      decodeDoubleArray(getNodeText(xmlProperty), doubleArray, 0, length);
      return doubleArray;
    } else if (Point3[].class.isAssignableFrom(propertyValueClass)) {
      Element[] xmlPoints = getChildren(xmlProperty, "point");
      Point3[] pointArray = new Point3[xmlPoints.length];
      for (int tupleIndex = 0; tupleIndex < xmlPoints.length; tupleIndex++) {
        pointArray[tupleIndex] = decodePoint3(getNodeText(xmlPoints[tupleIndex]));
      }
      return pointArray;
    } else if (Vector3f[].class.isAssignableFrom(propertyValueClass)) {
      Element[] xmlNormals = getChildren(xmlProperty, "normal");
      Vector3f[] normalArray = new Vector3f[xmlNormals.length];
      for (int tupleIndex = 0; tupleIndex < xmlNormals.length; tupleIndex++) {
        normalArray[tupleIndex] = decodeVector3f(getNodeText(xmlNormals[tupleIndex]));
      }
      return normalArray;
    } else if (Vector2f[].class.isAssignableFrom(propertyValueClass)) {
      Element[] xmlTextureCoords = getChildren(xmlProperty, "textureCoordinate");
      Vector2f[] texCoordArray = new Vector2f[xmlTextureCoords.length];
      for (int tupleIndex = 0; tupleIndex < xmlTextureCoords.length; tupleIndex++) {
        texCoordArray[tupleIndex] = decodeVector2f(getNodeText(xmlTextureCoords[tupleIndex]));
      }
      return texCoordArray;
    } else if (Vertex[].class.isAssignableFrom(propertyValueClass)) {
      Element[] xmlVertices = getChildren(xmlProperty, "vertex");
      Vertex[] vertexArray = new Vertex[xmlVertices.length];
      for (int vertexIndex = 0; vertexIndex < xmlVertices.length; vertexIndex++) {
        Element xmlVertex = xmlVertices[vertexIndex];
        Element xmlPosition = getFirstChild(xmlVertex, "position");
        Point3 position = Point3.NaN;
        if (xmlPosition != null) {
          position = decodePoint3(getNodeText(xmlPosition));
        }
        Element xmlNormal = getFirstChild(xmlVertex, "normal");
        Vector3f normal = Vector3f.NaN;
        if (xmlNormal != null) {
          normal = decodeVector3f(getNodeText(xmlNormal));
        }
        Element xmlDiffuseColor = getFirstChild(xmlVertex, "diffuseColor");
        final Color4f diffuseColor;
        if (xmlDiffuseColor != null) {
          diffuseColor = decodeColor4f(getNodeText(xmlDiffuseColor));
        } else {
          diffuseColor = null;
        }
        Element xmlTextureCoordinate0 = getFirstChild(xmlVertex, "textureCoordinate0");
        final TextureCoordinate2f textureCoordinate0;
        if (xmlTextureCoordinate0 != null) {
          textureCoordinate0 = decodeTexCoord2f(getNodeText(xmlTextureCoordinate0));
        } else {
          textureCoordinate0 = null;
        }
        vertexArray[vertexIndex] = new Vertex(position, normal, diffuseColor, null, textureCoordinate0);
      }
      return vertexArray;
    } else {
      return valueOf(propertyValueClass, getNodeText(xmlProperty));
    }
  }
}
