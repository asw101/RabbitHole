package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.NamedUserType;

import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TypeManagerTypeCacheTest {
  @Test
  public void getTypeCache_returnsMutableEmptySetWhenNoProjectIsActive() {
    Set<NamedUserType> cache = TypeManager.getTypeCache();
    assertNotNull(cache);
    assertTrue(cache.isEmpty());
    cache.add(new NamedUserType());
    assertEquals(1, cache.size());
  }

  private static void assertEquals(int expected, int actual) {
    org.junit.Assert.assertEquals(expected, actual);
  }
}
