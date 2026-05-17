package org.alice.ide.croquet.models.html;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link HtmlEncoder} — section-filtering constants.
 * Most HtmlEncoder methods require a DOM Document and Swing components,
 * so these tests focus on the static filtering logic accessible
 * through constructable instances.
 */
public class HtmlEncoderContractTest {

  @Test
  public void htmlEncoderClass_isPublic() {
    assertTrue(java.lang.reflect.Modifier.isPublic(HtmlEncoder.class.getModifiers()));
  }

  @Test
  public void htmlEncoder_implementsAstProcessor() {
    boolean implementsIt = false;
    for (Class<?> iface : HtmlEncoder.class.getInterfaces()) {
      if ("AstProcessor".equals(iface.getSimpleName())) {
        implementsIt = true;
        break;
      }
    }
    assertTrue("HtmlEncoder should implement AstProcessor", implementsIt);
  }

  @Test
  public void constructorAcceptsDocument() throws Exception {
    // HtmlEncoder has package-private constructor taking Document
    javax.xml.parsers.DocumentBuilderFactory factory =
        javax.xml.parsers.DocumentBuilderFactory.newInstance();
    org.w3c.dom.Document doc = factory.newDocumentBuilder().newDocument();
    HtmlEncoder encoder = new HtmlEncoder(doc);
    assertNotNull(encoder);
  }

  @Test
  public void getNewCodeOrganizerForTypeName_scene() throws Exception {
    javax.xml.parsers.DocumentBuilderFactory factory =
        javax.xml.parsers.DocumentBuilderFactory.newInstance();
    org.w3c.dom.Document doc = factory.newDocumentBuilder().newDocument();
    HtmlEncoder encoder = new HtmlEncoder(doc);
    assertNotNull(encoder.getNewCodeOrganizerForTypeName("Scene"));
  }

  @Test
  public void getNewCodeOrganizerForTypeName_program() throws Exception {
    javax.xml.parsers.DocumentBuilderFactory factory =
        javax.xml.parsers.DocumentBuilderFactory.newInstance();
    org.w3c.dom.Document doc = factory.newDocumentBuilder().newDocument();
    HtmlEncoder encoder = new HtmlEncoder(doc);
    assertNotNull(encoder.getNewCodeOrganizerForTypeName("Program"));
  }

  @Test
  public void getNewCodeOrganizerForTypeName_unknownType_usesDefault() throws Exception {
    javax.xml.parsers.DocumentBuilderFactory factory =
        javax.xml.parsers.DocumentBuilderFactory.newInstance();
    org.w3c.dom.Document doc = factory.newDocumentBuilder().newDocument();
    HtmlEncoder encoder = new HtmlEncoder(doc);
    assertNotNull(encoder.getNewCodeOrganizerForTypeName("SomeOtherType"));
  }
}
