package org.alice.netbeans.project;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class LauncherTemplateStructureTest {

  @Test
  public void classExists() {
    assertNotNull(LauncherTemplate.class);
  }

  @Test
  public void hasFileNameConstant() throws Exception {
    Field f = LauncherTemplate.class.getField("FILE_NAME");
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertNotNull(f.get(null));
  }

  @Test
  public void fileNameIsJavaFile() throws Exception {
    Field f = LauncherTemplate.class.getField("FILE_NAME");
    String name = (String) f.get(null);
    assertTrue("FILE_NAME should end with .java", name.endsWith(".java"));
  }

  @Test
  public void hasGenerationMethods() {
    boolean found = false;
    for (Method m : LauncherTemplate.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("generat") || m.getName().toLowerCase().contains("creat") ||
          m.getName().toLowerCase().contains("write") || m.getName().toLowerCase().contains("build")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have generation/creation methods", found);
  }

  @Test
  public void hasStaticMethods() {
    boolean found = false;
    for (Method m : LauncherTemplate.class.getDeclaredMethods()) {
      if (Modifier.isStatic(m.getModifiers())) {
        found = true;
        break;
      }
    }
    assertTrue("Should have static methods", found);
  }
}
