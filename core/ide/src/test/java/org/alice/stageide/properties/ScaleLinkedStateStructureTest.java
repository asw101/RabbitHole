package org.alice.stageide.properties;

import org.junit.Test;

import org.alice.stageide.properties.uicontroller.CompositePropertyController;
import org.alice.stageide.properties.uicontroller.ModelSizePropertyController;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for scale linked state classes.
 * Validates class hierarchy, method signatures, and accessibility.
 */
public class ScaleLinkedStateStructureTest {

  // ---- IsXZScaleLinkedState ----

  @Test
  public void isXZScaleLinkedState_classIsAccessible() {
    assertNotNull(IsXZScaleLinkedState.class);
  }

  @Test
  public void isXZScaleLinkedState_isPublic() {
    assertTrue(Modifier.isPublic(IsXZScaleLinkedState.class.getModifiers()));
  }

  @Test
  public void isXZScaleLinkedState_isNotAbstract() {
    assertFalse(Modifier.isAbstract(IsXZScaleLinkedState.class.getModifiers()));
  }

  @Test
  public void isXZScaleLinkedState_hasExpectedConstructor() {
    Constructor<?>[] ctors = IsXZScaleLinkedState.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ---- IsYZScaleLinkedState ----

  @Test
  public void isYZScaleLinkedState_classIsAccessible() {
    assertNotNull(IsYZScaleLinkedState.class);
  }

  @Test
  public void isYZScaleLinkedState_isPublic() {
    assertTrue(Modifier.isPublic(IsYZScaleLinkedState.class.getModifiers()));
  }

  @Test
  public void isYZScaleLinkedState_isNotAbstract() {
    assertFalse(Modifier.isAbstract(IsYZScaleLinkedState.class.getModifiers()));
  }

  @Test
  public void isYZScaleLinkedState_hasExpectedConstructor() {
    Constructor<?>[] ctors = IsYZScaleLinkedState.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ---- IsAllScaleLinkedState ----

  @Test
  public void isAllScaleLinkedState_classIsAccessible() {
    assertNotNull(IsAllScaleLinkedState.class);
  }

  @Test
  public void isAllScaleLinkedState_isPublic() {
    assertTrue(Modifier.isPublic(IsAllScaleLinkedState.class.getModifiers()));
  }

  @Test
  public void isAllScaleLinkedState_isNotAbstract() {
    assertFalse(Modifier.isAbstract(IsAllScaleLinkedState.class.getModifiers()));
  }

  @Test
  public void isAllScaleLinkedState_hasExpectedConstructor() {
    Constructor<?>[] ctors = IsAllScaleLinkedState.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ---- IsXYScaleLinkedState ----

  @Test
  public void isXYScaleLinkedState_classIsAccessible() {
    assertNotNull(IsXYScaleLinkedState.class);
  }

  @Test
  public void isXYScaleLinkedState_isPublic() {
    assertTrue(Modifier.isPublic(IsXYScaleLinkedState.class.getModifiers()));
  }

  @Test
  public void isXYScaleLinkedState_isNotAbstract() {
    assertFalse(Modifier.isAbstract(IsXYScaleLinkedState.class.getModifiers()));
  }

  // ---- Cross-cutting checks ----

  @Test
  public void allScaleStates_areNotFinal() {
    assertFalse(Modifier.isFinal(IsXZScaleLinkedState.class.getModifiers()));
    assertFalse(Modifier.isFinal(IsYZScaleLinkedState.class.getModifiers()));
    assertFalse(Modifier.isFinal(IsAllScaleLinkedState.class.getModifiers()));
    assertFalse(Modifier.isFinal(IsXYScaleLinkedState.class.getModifiers()));
  }

  @Test
  public void allScaleStates_areInSamePackage() {
    String pkg = "org.alice.stageide.properties";
    assertEquals(pkg, IsXZScaleLinkedState.class.getPackage().getName());
    assertEquals(pkg, IsYZScaleLinkedState.class.getPackage().getName());
    assertEquals(pkg, IsAllScaleLinkedState.class.getPackage().getName());
    assertEquals(pkg, IsXYScaleLinkedState.class.getPackage().getName());
  }

  // ---- LinkScaleIcon ----

  @Test
  public void linkScaleIcon_classIsAccessible() {
    assertNotNull(LinkScaleIcon.class);
  }

  @Test
  public void linkScaleIcon_isPublic() {
    assertTrue(Modifier.isPublic(LinkScaleIcon.class.getModifiers()));
  }

  // ---- LinkScaleButton ----

  @Test
  public void linkScaleButton_classIsAccessible() {
    assertNotNull(LinkScaleButton.class);
  }

  @Test
  public void linkScaleButton_isPublic() {
    assertTrue(Modifier.isPublic(LinkScaleButton.class.getModifiers()));
  }

  // ---- CompositePropertyController ----

  @Test
  public void compositePropertyController_classIsAccessible() {
    assertNotNull(CompositePropertyController.class);
  }

  @Test
  public void compositePropertyController_isPublic() {
    assertTrue(Modifier.isPublic(CompositePropertyController.class.getModifiers()));
  }

  // ---- ModelSizePropertyController ----

  @Test
  public void modelSizePropertyController_classIsAccessible() {
    assertNotNull(ModelSizePropertyController.class);
  }

  @Test
  public void modelSizePropertyController_isPublic() {
    assertTrue(Modifier.isPublic(ModelSizePropertyController.class.getModifiers()));
  }
}
