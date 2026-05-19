package org.alice.netbeans.completion;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class Alice3CompletionItemBuilderStructureTest {

  @Test
  public void classExists() {
    assertNotNull(Alice3CompletionItemBuilder.class);
  }

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(Alice3CompletionItemBuilder.class.getModifiers()));
  }

  @Test
  public void hasBuildMethods() {
    boolean found = false;
    for (Method m : Alice3CompletionItemBuilder.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("build") || m.getName().toLowerCase().contains("creat") ||
          m.getName().toLowerCase().contains("item")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have build/create methods", found);
  }
}
