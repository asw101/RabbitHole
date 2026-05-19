package org.alice.netbeans.project;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CodeFormatterStructureTest {

  @Test
  public void classExists() {
    assertNotNull(CodeFormatter.class);
  }

  @Test
  public void isFinalPackagePrivate() {
    assertTrue(Modifier.isFinal(CodeFormatter.class.getModifiers()));
    assertFalse(Modifier.isPublic(CodeFormatter.class.getModifiers()));
  }

  @Test
  public void hasFormatMethod() {
    boolean found = false;
    for (Method m : CodeFormatter.class.getDeclaredMethods()) {
      if (m.getName().contains("format") || m.getName().contains("Format")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have format methods", found);
  }

  @Test
  public void formatMethodIsStatic() {
    for (Method m : CodeFormatter.class.getDeclaredMethods()) {
      if (m.getName().contains("format") || m.getName().contains("Format")) {
        assertTrue(Modifier.isStatic(m.getModifiers()));
      }
    }
  }
}
