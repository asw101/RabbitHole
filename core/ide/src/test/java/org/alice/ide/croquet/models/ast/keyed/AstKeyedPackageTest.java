package org.alice.ide.croquet.models.ast.keyed;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AstKeyedPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.ast.keyed", KeyedBlank.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.ast.keyed", RemoveKeyedArgumentOperation.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(KeyedBlank.class.getModifiers()));
    assertFalse(Modifier.isInterface(RemoveKeyedArgumentOperation.class.getModifiers()));
  }
}
