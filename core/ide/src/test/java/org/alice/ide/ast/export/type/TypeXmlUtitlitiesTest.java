package org.alice.ide.ast.export.type;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class TypeXmlUtitlitiesTest {
  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(TypeXmlUtitlities.class.getModifiers()));
  }
  @Test
  public void hasDecodeMethod() throws Exception {
    Method m = TypeXmlUtitlities.class.getMethod("decode", org.w3c.dom.Document.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void hasEncodeMethod() throws Exception {
    Method m = TypeXmlUtitlities.class.getMethod("encode", TypeSummary.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
