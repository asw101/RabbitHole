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

import edu.cmu.cs.dennisc.java.lang.reflect.ReflectionUtilities;
import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.*;

/**
 * Handles all decoding (deserialization) logic for ASG scene graph I/O.
 * Delegates binary array decoding to {@link BinaryArrayDecoder} and
 * property value parsing to {@link PropertyValueParser}.
 *
 * @author Dennis Cosgrove
 */
class ASGDecoder {

  abstract static class AbstractPropertyReference {
    private edu.cmu.cs.dennisc.scenegraph.Element m_element;
    private InstanceProperty m_property;

    public AbstractPropertyReference(edu.cmu.cs.dennisc.scenegraph.Element element, InstanceProperty property) {
      m_element = element;
      m_property = property;
    }

    public abstract void resolve(HashMap<Integer, edu.cmu.cs.dennisc.scenegraph.Element> map);

    protected edu.cmu.cs.dennisc.scenegraph.Element getElement() {
      return m_element;
    }

    protected String getPropertyName() {
      return m_property.getName();
    }

    protected void setPropertyValue(Object value) {
      m_property.setValue(value);
    }
  }

  static class PropertyReferenceToElement extends AbstractPropertyReference {
    private Integer m_key;

    public PropertyReferenceToElement(edu.cmu.cs.dennisc.scenegraph.Element element, InstanceProperty<?> property, Integer key) {
      super(element, property);
      m_key = key;
    }

    @Override
    public void resolve(HashMap<Integer, edu.cmu.cs.dennisc.scenegraph.Element> map) {
      edu.cmu.cs.dennisc.scenegraph.Element value = map.get(m_key);
      if (value != null) {
        setPropertyValue(value);
      } else {
        throw new RuntimeException("could not resolve reference- element: " + getElement() + "; propertyName: " + getPropertyName() + "; key: " + m_key);
      }
    }
  }

  // ── Legacy property / classname migration ────────────────────────

  private static final Set<String> DEAD_PROPERTIES = Set.of(
      "IsFirstClass", "OpacityMap", "EmissiveColorMap", "SpecularHighlightColorMap",
      "BumpMap", "DetailMap", "Format", "VertexLowerBound", "VertexUpperBound"
  );

  private static boolean isDeadProperty(String property) {
    return DEAD_PROPERTIES.contains(property);
  }

  private static String convertPropertyIfNecessary(String property) {
    if (property.equals("DiffuseColorMap")) {
      property = "DiffuseColorTexture";
    }
    if (property.equals("Indices")) {
      property = "TriangleData";
    }
    return property;
  }

  private static final String OLD_PACKAGE = "edu.cmu.cs.stage3.";

  private static String convertClassnameIfNecessary(String className) {
    if (className.equals("edu.cmu.cs.stage3.alice.scenegraph.Color")) {
      return "edu.cmu.cs.dennisc.color.Color4f";
    } else if (className.equals("edu.cmu.cs.dennisc.math.Matrix4d")) {
      return "org.alice.math.immutable.AffineMatrix4x4";
    } else if (className.equals("edu.cmu.cs.dennisc.math.Matrix3d")) {
      return "org.alice.math.immutable.OrthogonalMatrix3x3";
    } else if (className.startsWith(OLD_PACKAGE)) {
      className = "edu.cmu.cs.dennisc." + className.substring(OLD_PACKAGE.length());
    } else if (className.startsWith("[L" + OLD_PACKAGE)) {
      className = "[Lorg." + className.substring(2 + OLD_PACKAGE.length());
    }
    if (className.endsWith("Vertex3d;")) {
      className = className.substring(0, className.length() - 3) + ';';
    } else if (className.endsWith("Vertex3d")) {
      className = className.substring(0, className.length() - 2);
    }
    if (className.endsWith("TextureMap")) {
      className = className.substring(0, className.length() - 3);
    }
    return className;
  }

  // ── Binary array delegates (preserve call-site compatibility) ────

  static Vertex[] decodeVertexArrayInBinary(InputStream is) {
    return BinaryArrayDecoder.decodeVertexArray(is);
  }

  static int[] decodeIntArrayInBinary(InputStream is) {
    return BinaryArrayDecoder.decodeIntArray(is);
  }

  static double[] decodeDoubleArrayInBinary(InputStream is) {
    return BinaryArrayDecoder.decodeDoubleArray(is);
  }

  // ── Element / component decoding ─────────────────────────────────

  private static edu.cmu.cs.dennisc.scenegraph.Element decodeElement(Element xmlElement, HashMap<String, InputStream> filenameToStreamMap, HashMap<Integer, edu.cmu.cs.dennisc.scenegraph.Element> keyToElementMap, List<AbstractPropertyReference> referencesToBeResolved) {
    String className = convertClassnameIfNecessary(xmlElement.getAttribute("class"));
    Integer elementKey = Integer.parseInt(xmlElement.getAttribute("key"));
    String elementName = xmlElement.getAttribute("name");
    edu.cmu.cs.dennisc.scenegraph.Element sgElement = (edu.cmu.cs.dennisc.scenegraph.Element) ReflectionUtilities.newInstance(className);
    sgElement.setName(elementName);
    keyToElementMap.put(elementKey, sgElement);
    Element[] xmlProperties = PropertyValueParser.getChildren(xmlElement, "property");
    for (Element xmlProperty : xmlProperties) {
      String propertyName = convertPropertyIfNecessary(xmlProperty.getAttribute("name"));
      if (isDeadProperty(propertyName)) {
        continue;
      }
      InstanceProperty property = sgElement.getPropertyNamed(propertyName);
      if (xmlProperty.hasAttribute("class")) {
        String pvClassname = convertClassnameIfNecessary(xmlProperty.getAttribute("class"));
        Class<?> propertyValueClass = ReflectionUtilities.getClassForName(pvClassname);
        Object value = PropertyValueParser.parseValue(xmlProperty, propertyValueClass, filenameToStreamMap);
        property.setValue(value);
      } else if (xmlProperty.hasAttribute("key")) {
        Integer key = Integer.parseInt(xmlProperty.getAttribute("key"));
        referencesToBeResolved.add(new PropertyReferenceToElement(sgElement, property, key));
      } else {
        property.setValue(null);
      }
    }
    return sgElement;
  }

  private static Component decodeComponent(Element xmlComponent, HashMap<String, InputStream> filenameToStreamMap, HashMap<Integer, edu.cmu.cs.dennisc.scenegraph.Element> keyToElementMap, List<AbstractPropertyReference> referencesToBeResolved) {
    Component sgComponent = (Component) decodeElement(xmlComponent, filenameToStreamMap, keyToElementMap, referencesToBeResolved);
    Element[] xmlChildren = PropertyValueParser.getChildren(xmlComponent, "child");
    for (Element element : xmlChildren) {
      decodeComponent(element, filenameToStreamMap, keyToElementMap, referencesToBeResolved).setParent((Composite) sgComponent);
    }
    return sgComponent;
  }

  private static Component decodeInternal(InputStream is, HashMap<String, InputStream> filenameToStreamMap) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(is);
      Element xmlRoot = document.getDocumentElement();
      HashMap<Integer, edu.cmu.cs.dennisc.scenegraph.Element> keyToElementMap = new HashMap<>();
      List<AbstractPropertyReference> referencesToBeResolved = new ArrayList<>();
      Component sgRoot = decodeComponent(xmlRoot, filenameToStreamMap, keyToElementMap, referencesToBeResolved);
      Element[] xmlElements = PropertyValueParser.getChildren(xmlRoot, "element");
      for (Element xmlElement : xmlElements) {
        decodeElement(xmlElement, filenameToStreamMap, keyToElementMap, referencesToBeResolved);
      }
      for (AbstractPropertyReference propertyReference : referencesToBeResolved) {
        propertyReference.resolve(keyToElementMap);
      }
      return sgRoot;
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    } catch (SAXException saxe) {
      throw new RuntimeException(saxe);
    } catch (ParserConfigurationException pce) {
      throw new RuntimeException(pce);
    }
  }

  // ── Public entry points ──────────────────────────────────────────

  static Component decode(InputStream is, HashMap<String, InputStream> filenameToStreamMap) {
    BufferedInputStream bis;
    if (is instanceof BufferedInputStream stream) {
      bis = stream;
    } else {
      bis = new BufferedInputStream(is);
    }
    return decodeInternal(bis, filenameToStreamMap);
  }

  static Component decodeZip(InputStream is) {
    ZipInputStream zis;
    if (is instanceof ZipInputStream stream) {
      zis = stream;
    } else {
      zis = new ZipInputStream(is);
    }
    HashMap<String, InputStream> filenameToStreamMap = new HashMap<>();
    ZipEntry zipEntry;
    try {
      while ((zipEntry = zis.getNextEntry()) != null) {
        String name = zipEntry.getName();
        if (zipEntry.isDirectory()) {
          // pass
        } else {
          final int BUFFER_SIZE = 8192;
          byte[] buffer = new byte[BUFFER_SIZE];
          ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
          int count;
          while ((count = zis.read(buffer, 0, BUFFER_SIZE)) != -1) {
            baos.write(buffer, 0, count);
          }
          zis.closeEntry();
          ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
          filenameToStreamMap.put(name, bais);
        }
      }
      InputStream rootIS = filenameToStreamMap.get(ASG.ROOT_FILENAME);
      if (rootIS == null) {
        throw new RuntimeException(ASG.ROOT_FILENAME);
      }
      filenameToStreamMap.remove(ASG.ROOT_FILENAME);
      return decode(rootIS, filenameToStreamMap);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  static Component decode(File file) {
    try {
      try (ZipFile zipFile = new ZipFile(file)) {
        // Valid zip — decode as zip stream
      }
      try (FileInputStream fis = new FileInputStream(file)) {
        return decodeZip(new ZipInputStream(fis));
      }
    } catch (ZipException ze) {
      try (FileInputStream fis = new FileInputStream(file)) {
        return decode(fis, new HashMap<>());
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  static Component decode(String path) {
    return decode(new File(path));
  }
}
