package org.alice.netbeans.completion;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class Alice3CompletionProviderStructureTest {

  @Test
  public void classExists() {
    assertNotNull(Alice3CompletionProvider.class);
  }

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(Alice3CompletionProvider.class.getModifiers()));
  }

  @Test
  public void hasCompletionMethods() {
    boolean found = false;
    for (Method m : Alice3CompletionProvider.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("complet") || m.getName().toLowerCase().contains("task") ||
          m.getName().toLowerCase().contains("query")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have completion-related methods", found);
  }

  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(Alice3CompletionProvider.class.getModifiers()));
  }
}
