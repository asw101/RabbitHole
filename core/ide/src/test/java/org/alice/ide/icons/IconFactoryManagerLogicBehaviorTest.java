package org.alice.ide.icons;

import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.StringLiteral;
import org.lgna.story.SSphere;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IconFactoryManagerLogicBehaviorTest {
  @Test
  public void getRequiredArgumentsInInitializerCountsOnlyInstanceCreationArguments() {
    assertEquals(1, IconFactoryManagerLogic.getRequiredArgumentsInInitializer(
        AstUtilities.createInstanceCreation(String.class, new Class<?>[]{String.class}, new StringLiteral("value"))));
    assertEquals(-1, IconFactoryManagerLogic.getRequiredArgumentsInInitializer(new StringLiteral("value")));
  }

  @Test
  public void shouldUseDynamicFieldIconOnlyForVisualTypes() {
    assertTrue(IconFactoryManagerLogic.shouldUseDynamicFieldIcon(JavaType.getInstance(SSphere.class)));
    assertFalse(IconFactoryManagerLogic.shouldUseDynamicFieldIcon(JavaType.getInstance(String.class)));
  }
}
