package org.alice.ide.icons;

import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.JavaType;
import org.lgna.story.SSphere;
import org.lgna.story.Visual;

import static org.junit.Assert.*;

public class IconFactoryManagerLogicTest {
  @Test
  public void getRequiredArgumentsInInitializerCountsInstanceCreationArguments() {
    assertEquals(0, IconFactoryManagerLogic.getRequiredArgumentsInInitializer(AstUtilities.createInstanceCreation(JavaConstructor.getInstance(StringBuilder.class))));
    assertEquals(-1, IconFactoryManagerLogic.getRequiredArgumentsInInitializer(null));
  }

  @Test
  public void shouldUseDynamicFieldIconRequiresVisualType() {
    assertTrue(IconFactoryManagerLogic.shouldUseDynamicFieldIcon(JavaType.getInstance(SSphere.class)));
    assertFalse(IconFactoryManagerLogic.shouldUseDynamicFieldIcon(JavaType.getInstance(String.class)));
  }
}
