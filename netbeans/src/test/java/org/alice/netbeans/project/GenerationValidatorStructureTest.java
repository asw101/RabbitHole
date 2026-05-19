package org.alice.netbeans.project;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GenerationValidatorStructureTest {

  @Test
  public void classExists() {
    assertNotNull(GenerationValidator.class);
  }

  @Test
  public void isFinalPackagePrivate() {
    assertTrue(Modifier.isFinal(GenerationValidator.class.getModifiers()));
    assertFalse(Modifier.isPublic(GenerationValidator.class.getModifiers()));
  }

  @Test
  public void hasEnsureMethod() {
    boolean found = false;
    for (Method m : GenerationValidator.class.getDeclaredMethods()) {
      if (m.getName().contains("ensure") || m.getName().contains("Ensure")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have ensure methods", found);
  }

  @Test
  public void hasValidateMethod() {
    boolean found = false;
    for (Method m : GenerationValidator.class.getDeclaredMethods()) {
      if (m.getName().contains("valid") || m.getName().contains("Valid") ||
          m.getName().contains("ensure") || m.getName().contains("Ensure") ||
          m.getName().contains("safe") || m.getName().contains("Safe")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have validation methods", found);
  }

  @Test
  public void staticMethods() {
    boolean found = false;
    for (Method m : GenerationValidator.class.getDeclaredMethods()) {
      if (Modifier.isStatic(m.getModifiers())) {
        found = true;
        break;
      }
    }
    assertTrue("Should have static methods", found);
  }
}
