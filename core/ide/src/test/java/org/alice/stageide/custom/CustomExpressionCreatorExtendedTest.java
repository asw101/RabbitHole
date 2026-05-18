package org.alice.stageide.custom;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.*;

public class CustomExpressionCreatorExtendedTest {

  private static Class<?> load(String name) throws Exception {
    return Class.forName(name, false, CustomExpressionCreatorExtendedTest.class.getClassLoader());
  }

  private static void assertSingletonMethod(Class<?> cls) throws Exception {
    Method getInstance = cls.getMethod("getInstance");
    assertTrue(Modifier.isPublic(getInstance.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertSame(cls, getInstance.getReturnType());
    Object first = getInstance.invoke(null);
    Object second = getInstance.invoke(null);
    assertSame(first, second);
  }

  @Test
  public void volumeLevelUtilities_toDouble_coversRequestedValues() {
    assertEquals(0.0, VolumeLevelUtilities.toDouble(0), 0.0);
    assertEquals(0.5, VolumeLevelUtilities.toDouble(50), 0.0);
    assertEquals(1.0, VolumeLevelUtilities.toDouble(100), 0.0);
    assertEquals(2.0, VolumeLevelUtilities.toDouble(200), 0.0);
  }

  @Test
  public void volumeLevelUtilities_toInt_coversRequestedValues() {
    assertEquals(0, VolumeLevelUtilities.toInt(0.0));
    assertEquals(50, VolumeLevelUtilities.toInt(0.5));
    assertEquals(100, VolumeLevelUtilities.toInt(1.0));
    assertEquals(200, VolumeLevelUtilities.toInt(2.0));
  }

  @Test
  public void volumeLevelUtilities_toInt_handlesSpecialDoubles() {
    assertEquals(0, VolumeLevelUtilities.toInt(Double.NaN));
    assertEquals(Integer.MAX_VALUE, VolumeLevelUtilities.toInt(Double.POSITIVE_INFINITY));
    assertEquals(Integer.MIN_VALUE, VolumeLevelUtilities.toInt(Double.NEGATIVE_INFINITY));
  }

  @Test
  public void volumeLevelUtilities_roundTripsRequestedValues() {
    int[] values = {0, 50, 100, 200};
    for (int value : values) {
      assertEquals(value, VolumeLevelUtilities.toInt(VolumeLevelUtilities.toDouble(value)));
    }
  }

  @Test
  public void volumeLevelUtilities_createDetails_returnsNonNullDetails() {
    assertNotNull(VolumeLevelUtilities.createDetails());
  }

  @Test
  public void keyState_isFinalSimpleItemStateSingleton() throws Exception {
    assertTrue(Modifier.isFinal(KeyState.class.getModifiers()));
    assertTrue(load("org.lgna.croquet.SimpleItemState").isAssignableFrom(KeyState.class));
    assertSingletonMethod(KeyState.class);
  }

  @Test
  public void keyState_declaresRequestedMethods() throws Exception {
    Method handleKeyPressed = KeyState.class.getDeclaredMethod(
        "handleKeyPressed",
        org.alice.stageide.custom.components.KeyViewController.class,
        java.awt.event.KeyEvent.class);
    Method createViewController = KeyState.class.getDeclaredMethod("createViewController");
    Method getSwingValue = KeyState.class.getDeclaredMethod("getSwingValue");
    Method setSwingValue = KeyState.class.getDeclaredMethod("setSwingValue", org.lgna.story.Key.class);
    Method localize = KeyState.class.getDeclaredMethod("localize");
    Method getPotentialPrepModelPaths = KeyState.class.getDeclaredMethod("getPotentialPrepModelPaths", org.lgna.croquet.edits.Edit.class);

    assertTrue(Modifier.isPublic(handleKeyPressed.getModifiers()));
    assertTrue(Modifier.isPublic(createViewController.getModifiers()));
    assertTrue(Modifier.isProtected(getSwingValue.getModifiers()));
    assertTrue(Modifier.isProtected(setSwingValue.getModifiers()));
    assertTrue(Modifier.isProtected(localize.getModifiers()));
    assertTrue(Modifier.isPublic(getPotentialPrepModelPaths.getModifiers()));
  }

  @Test
  public void keyState_canCreateViewControllerAndStoreSwingValue() throws Exception {
    KeyState keyState = KeyState.getInstance();
    Object controller = keyState.createViewController();
    assertNotNull(controller);
    assertTrue(load("org.alice.stageide.custom.components.KeyViewController").isInstance(controller));

    Method setSwingValue = KeyState.class.getDeclaredMethod("setSwingValue", org.lgna.story.Key.class);
    Method getSwingValue = KeyState.class.getDeclaredMethod("getSwingValue");
    setSwingValue.setAccessible(true);
    getSwingValue.setAccessible(true);

    setSwingValue.invoke(keyState, org.lgna.story.Key.SPACE);
    assertEquals(org.lgna.story.Key.SPACE, getSwingValue.invoke(keyState));
  }

  @Test
  public void keyState_getPotentialPrepModelPaths_isEmptyForNullEdit() {
    List<List<org.lgna.croquet.PrepModel>> paths = KeyState.getInstance().getPotentialPrepModelPaths(null);
    assertNotNull(paths);
    assertTrue(paths.isEmpty());
  }

  @Test
  public void colorCustomExpressionCreatorComposite_isPublicSingleton() throws Exception {
    assertTrue(Modifier.isPublic(ColorCustomExpressionCreatorComposite.class.getModifiers()));
    assertSingletonMethod(ColorCustomExpressionCreatorComposite.class);
  }

  @Test
  public void keyCustomExpressionCreatorComposite_isPublicSingleton() throws Exception {
    assertTrue(Modifier.isPublic(KeyCustomExpressionCreatorComposite.class.getModifiers()));
    assertSingletonMethod(KeyCustomExpressionCreatorComposite.class);
  }

  @Test
  public void volumeLevelCustomExpressionCreatorComposite_isPublicSingleton() throws Exception {
    assertTrue(Modifier.isPublic(VolumeLevelCustomExpressionCreatorComposite.class.getModifiers()));
    assertSingletonMethod(VolumeLevelCustomExpressionCreatorComposite.class);
  }

  @Test
  public void audioSourceCustomExpressionCreatorComposite_isSingleton() throws Exception {
    assertTrue(Modifier.isPublic(AudioSourceCustomExpressionCreatorComposite.class.getModifiers()));
    assertTrue(Modifier.isFinal(AudioSourceCustomExpressionCreatorComposite.class.getModifiers()));
    assertSingletonMethod(AudioSourceCustomExpressionCreatorComposite.class);
  }

  @Test
  public void audioResourceExpressionState_isSingleton() throws Exception {
    assertTrue(Modifier.isPublic(AudioResourceExpressionState.class.getModifiers()));
    assertSingletonMethod(AudioResourceExpressionState.class);
    assertTrue(load("org.alice.ide.croquet.models.StandardExpressionState").isAssignableFrom(AudioResourceExpressionState.class));
  }

  @Test
  public void componentViewClasses_load() throws Exception {
    assertTrue(Modifier.isPublic(load("org.alice.stageide.custom.components.KeyViewController").getModifiers()));
    assertTrue(Modifier.isPublic(load("org.alice.stageide.custom.components.VolumeLevelSlider").getModifiers()));
    assertTrue(Modifier.isPublic(load("org.alice.stageide.custom.components.AudioSourceCustomExpressionCreatorView").getModifiers()));
    assertTrue(Modifier.isPublic(load("org.alice.stageide.custom.components.KeyCustomExpressionCreatorView").getModifiers()));
    assertTrue(Modifier.isPublic(load("org.alice.stageide.custom.components.VolumeLevelCustomExpressionCreatorView").getModifiers()));
  }

  @Test
  public void customSingletonConstructors_arePrivate() throws Exception {
    Class<?>[] singletonTypes = {
        KeyState.class,
        ColorCustomExpressionCreatorComposite.class,
        KeyCustomExpressionCreatorComposite.class,
        VolumeLevelCustomExpressionCreatorComposite.class,
        AudioSourceCustomExpressionCreatorComposite.class,
        AudioResourceExpressionState.class
    };

    for (Class<?> singletonType : singletonTypes) {
      boolean foundPrivateConstructor = false;
      for (Constructor<?> constructor : singletonType.getDeclaredConstructors()) {
        if (Modifier.isPrivate(constructor.getModifiers())) {
          foundPrivateConstructor = true;
        }
      }
      assertTrue("Expected private constructor for " + singletonType.getName(), foundPrivateConstructor);
    }
  }
}
