package org.alice.ide.icons;

import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.StringLiteral;
import org.lgna.story.SSphere;
import org.lgna.story.resources.BipedResource;

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

  @Test
  public void shouldUseBundledSvgIcon_requiresKnownResourceTypeAndNoSpecificModelName() {
    assertTrue(IconFactoryManagerLogic.shouldUseBundledSvgIcon(
        BipedResource.class,
        null,
        IconFactoryManager.getSetOfClassesWithIcons()));
    assertFalse(IconFactoryManagerLogic.shouldUseBundledSvgIcon(
        BipedResource.class,
        "WALKER",
        IconFactoryManager.getSetOfClassesWithIcons()));
    assertFalse(IconFactoryManagerLogic.shouldUseBundledSvgIcon(
        org.lgna.story.resources.ModelResource.class,
        null,
        IconFactoryManager.getSetOfClassesWithIcons()));
  }

  @Test
  public void getBundledSvgPath_usesSimpleClassName() {
    assertEquals("images/resources/BipedResource.svg", IconFactoryManagerLogic.getBundledSvgPath(BipedResource.class));
  }
}
