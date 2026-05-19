package org.alice.netbeans.project;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class NetbeansJavaCodeGeneratorStructureTest {

  @Test
  public void classExists() {
    assertNotNull(NetbeansJavaCodeGenerator.class);
  }

  @Test
  public void hasGenerateMethods() {
    boolean found = false;
    for (Method m : NetbeansJavaCodeGenerator.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("generat")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have generate methods", found);
  }

  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(NetbeansJavaCodeGenerator.class.getModifiers()));
  }
}
