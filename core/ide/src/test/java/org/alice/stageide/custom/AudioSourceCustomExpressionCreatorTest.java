package org.alice.stageide.custom;

import org.alice.ide.custom.CustomExpressionCreatorComposite;
import org.junit.Test;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Tests for {@link AudioSourceCustomExpressionCreatorComposite} —
 * singleton, hierarchy, API surface, and internal state composition.
 *
 * Refactored: previously contained duplicate VolumeLevelUtilities tests
 * (already covered by {@link VolumeLevelUtilitiesTest}).
 */
public class AudioSourceCustomExpressionCreatorTest {

  // ---- class structure --------------------------------------------------

  @Test
  public void classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.custom.AudioSourceCustomExpressionCreatorComposite");
  }

  @Test
  public void extendsCustomExpressionCreatorComposite() {
    assertTrue(CustomExpressionCreatorComposite.class
        .isAssignableFrom(AudioSourceCustomExpressionCreatorComposite.class));
  }

  @Test
  public void classIsFinal() {
    assertTrue(Modifier.isFinal(
        AudioSourceCustomExpressionCreatorComposite.class.getModifiers()));
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(
        AudioSourceCustomExpressionCreatorComposite.class.getModifiers()));
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(Modifier.isAbstract(
        AudioSourceCustomExpressionCreatorComposite.class.getModifiers()));
  }

  // ---- singleton accessor -----------------------------------------------

  @Test
  public void getInstanceMethod_exists() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(AudioSourceCustomExpressionCreatorComposite.class, m.getReturnType());
  }

  // ---- constructor is private -------------------------------------------

  @Test
  public void constructor_isPrivate() throws Exception {
    for (Constructor<?> ctor : AudioSourceCustomExpressionCreatorComposite.class.getDeclaredConstructors()) {
      if (ctor.getParameterCount() == 0) {
        assertTrue("no-arg ctor must be private", Modifier.isPrivate(ctor.getModifiers()));
      }
    }
  }

  // ---- public API methods -----------------------------------------------

  @Test
  public void getAudioResourceExpressionStateMethod_exists() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getMethod("getAudioResourceExpressionState");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(AudioResourceExpressionState.class, m.getReturnType());
  }

  @Test
  public void getVolumeStateMethod_exists() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getMethod("getVolumeState");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getStartMarkerStateMethod_exists() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getMethod("getStartMarkerState");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getStopMarkerStateMethod_exists() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getMethod("getStopMarkerState");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getResourceSidekickLabelMethod_exists() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getMethod("getResourceSidekickLabel");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getTestOperationMethod_exists() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getMethod("getTestOperation");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ---- overridden methods from CustomExpressionCreatorComposite ---------

  @Test
  public void createViewMethod_isDeclared() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getDeclaredMethod("createView");
    assertNotNull(m);
  }

  @Test
  public void createValueMethod_isDeclared() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getDeclaredMethod("createValue");
    assertNotNull(m);
  }

  @Test
  public void getStatusPreRejectorCheckMethod_isDeclared() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getDeclaredMethod("getStatusPreRejectorCheck");
    assertNotNull(m);
  }

  @Test
  public void initializeToPreviousExpressionMethod_isDeclared() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getDeclaredMethod("initializeToPreviousExpression",
            org.lgna.project.ast.Expression.class);
    assertNotNull(m);
  }

  // ---- protected overrides -----------------------------------------------

  @Test
  public void handlePostHideDialogMethod_isDeclared() throws NoSuchMethodException {
    Method m = AudioSourceCustomExpressionCreatorComposite.class
        .getDeclaredMethod("handlePostHideDialog");
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // ---- AudioSource type binding -----------------------------------------

  @Test
  public void audioSourceType_resolves() {
    JavaType type = JavaType.getInstance(org.lgna.story.AudioSource.class);
    assertNotNull(type);
    assertFalse(type.isAssignableTo(Enum.class));
  }

  @Test
  public void audioSourceType_isNotAssignableToColor() {
    JavaType audioType = JavaType.getInstance(org.lgna.story.AudioSource.class);
    JavaType colorType = JavaType.getInstance(org.lgna.story.Color.class);
    assertFalse(colorType.isAssignableFrom(audioType));
  }

  // ---- internal state field verification --------------------------------

  @Test
  public void declaredFieldCount_isReasonable() {
    Field[] fields = AudioSourceCustomExpressionCreatorComposite.class.getDeclaredFields();
    assertTrue("should have internal state fields", fields.length >= 3);
  }

  @Test
  public void declaredMethodCount_coversOverrides() {
    Set<String> methodNames = Arrays.stream(
            AudioSourceCustomExpressionCreatorComposite.class.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertTrue("must override createView", methodNames.contains("createView"));
    assertTrue("must override createValue", methodNames.contains("createValue"));
  }
}
