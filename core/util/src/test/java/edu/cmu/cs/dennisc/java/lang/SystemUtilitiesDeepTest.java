package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

public class SystemUtilitiesDeepTest {
  private String setProperty(String name, String value) {
    String original = System.getProperty(name);
    if (value == null) {
      System.clearProperty(name);
    } else {
      System.setProperty(name, value);
    }
    return original;
  }

  private void restoreProperty(String name, String original) {
    if (original == null) {
      System.clearProperty(name);
    } else {
      System.setProperty(name, original);
    }
  }

  @Test
  public void getBooleanPropertyUnderstandsUppercaseTrue() {
    String original = setProperty("test.system.upper.true", "TRUE");
    try {
      assertTrue(SystemUtilities.getBooleanProperty("test.system.upper.true", false));
    } finally {
      restoreProperty("test.system.upper.true", original);
    }
  }

  @Test
  public void getBooleanPropertyInvalidTextReturnsFalseNotDefault() {
    String original = setProperty("test.system.invalid.bool", "not-a-boolean");
    try {
      assertFalse(SystemUtilities.getBooleanProperty("test.system.invalid.bool", true));
    } finally {
      restoreProperty("test.system.invalid.bool", original);
    }
  }

  @Test
  public void isPropertyTrueAndFalseAreCaseSensitive() {
    String originalTrue = setProperty("test.system.case.true", "TRUE");
    String originalFalse = setProperty("test.system.case.false", "FALSE");
    try {
      assertFalse(SystemUtilities.isPropertyTrue("test.system.case.true"));
      assertFalse(SystemUtilities.isPropertyFalse("test.system.case.false"));
    } finally {
      restoreProperty("test.system.case.true", originalTrue);
      restoreProperty("test.system.case.false", originalFalse);
    }
  }

  @Test
  public void malformedJavaVersionReturnsNaN() {
    String original = setProperty("java.version", "bad-version");
    try {
      assertTrue(Double.isNaN(SystemUtilities.getJavaVersionAsDouble()));
    } finally {
      restoreProperty("java.version", original);
    }
  }

  @Test
  public void invalidBitCountReturnsNull() {
    String original = setProperty("sun.arch.data.model", "not-a-number");
    try {
      assertNull(SystemUtilities.getBitCount());
    } finally {
      restoreProperty("sun.arch.data.model", original);
    }
  }

  @Test
  public void missingBitCountMakesIs64BitAndIs32BitFalse() {
    String original = setProperty("sun.arch.data.model", null);
    try {
      assertFalse(SystemUtilities.is64Bit());
      assertFalse(SystemUtilities.is32Bit());
    } finally {
      restoreProperty("sun.arch.data.model", original);
    }
  }

  @Test
  public void propertiesAsXmlContainsInjectedPropertyValue() throws Exception {
    String original = setProperty("test.system.xml.key", "value<&>");
    try {
      byte[] xmlBytes = SystemUtilities.getPropertiesAsXMLByteArray();
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .parse(new ByteArrayInputStream(xmlBytes));
      NodeList properties = document.getElementsByTagName("property");
      boolean found = false;
      for (int i = 0; i < properties.getLength(); i++) {
        org.w3c.dom.Element property = (org.w3c.dom.Element) properties.item(i);
        if ("test.system.xml.key".equals(property.getAttribute("key"))) {
          assertEquals("value<&>", property.getTextContent());
          found = true;
        }
      }
      assertTrue(found);
    } finally {
      restoreProperty("test.system.xml.key", original);
    }
  }

  @Test
  public void createArraySkipsNullArrays() {
    String[] combined = SystemUtilities.createArray(String.class, new String[] {"a"}, null, new String[] {"b", "c"});
    assertArrayEquals(new String[] {"a", "b", "c"}, combined);
  }

  @Test
  public void returnArrayReturnsSameReference() {
    String[] values = new String[] {"x", "y"};
    assertSame(values, SystemUtilities.returnArray(String.class, values));
  }
}
