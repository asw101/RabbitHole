package org.alice.ide.properties.uicontroller;

import org.junit.Test;
import org.lgna.croquet.views.AwtComponentView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class BasicPropertyControllerStructureTest {
  @Test
  public void classIsAbstractAndExtendsAbstractAdapterController() {
    assertTrue(Modifier.isAbstract(BasicPropertyController.class.getModifiers()));
    assertEquals(AbstractAdapterController.class, BasicPropertyController.class.getSuperclass());
  }

  @Test
  public void blankStringConstantMatchesExpectedSentinel() throws Exception {
    Field field = BasicPropertyController.class.getDeclaredField("BLANK_STRING");
    field.setAccessible(true);
    assertTrue(Modifier.isStatic(field.getModifiers()));
    assertEquals("NO VALUE", field.get(null));
  }

  @Test
  public void templateMethodsUseExpectedTypes() throws Exception {
    Method createPropertyComponent = BasicPropertyController.class.getDeclaredMethod("createPropertyComponent");
    Method updateUIFromNewAdapter = BasicPropertyController.class.getDeclaredMethod("updateUIFromNewAdapter");

    assertEquals(AwtComponentView.class, createPropertyComponent.getReturnType());
    assertTrue(Modifier.isAbstract(createPropertyComponent.getModifiers()));
    assertTrue(Modifier.isProtected(updateUIFromNewAdapter.getModifiers()));
  }
}
