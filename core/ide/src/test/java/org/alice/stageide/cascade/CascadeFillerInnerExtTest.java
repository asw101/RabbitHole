package org.alice.stageide.cascade;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CascadeFillerInnerExtTest {

  private static final String FILLER_INNER_PACKAGE = "org.alice.stageide.cascade.fillerinners.";

  private static final String[] EVENT_FILLER_INNERS = {
      "ArrowKeyListenerFillerInner",
      "KeyListenerFillerInner",
      "NumberKeyListenerFillerInner",
      "MouseClickOnObjectFillerInner",
      "MouseClickedOnScreenFillerInner",
      "SceneActivationEventFillerInner",
      "TimerEventListenerFillerInner",
      "TransformationListenerFillerInner",
      "StartCollisionListenerFillerInner",
      "EndCollisionListenerFillerInner",
      "EnterProximityEventListenerFillerInner",
      "ExitProximityEventListenerFillerInner",
      "StartOcclusionEventListenerFillerInner",
      "EndOcclusionEventListenerFillerInner",
      "ComesIntoViewEventListenerFillerInner",
      "LeavesViewEventListenerFillerInner"
  };

  private static final String[] OTHER_FILLER_INNERS = {
      "ColorFillerInner",
      "KeyFillerInner",
      "SourceFillerInner",
      "ImageSourceFillerInner",
      "AudioSourceFillerInner",
      "ImagePaintFillerInner",
      "ModelResourceFillerInner"
  };

  private static Class<?> load(String name) throws Exception {
    return Class.forName(name, false, CascadeFillerInnerExtTest.class.getClassLoader());
  }

  private static Class<?> loadFiller(String simpleName) throws Exception {
    return load(FILLER_INNER_PACKAGE + simpleName);
  }

  private static void assertExtends(String className, String expectedSuperName) throws Exception {
    Class<?> cls = load(className);
    Class<?> expectedSuper = load(expectedSuperName);
    assertTrue(className + " should extend " + expectedSuperName, expectedSuper.isAssignableFrom(cls));
  }

  private static void assertPublicNoArgConstructor(String className) throws Exception {
    Constructor<?> constructor = load(className).getConstructor();
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  private static void assertDeclaredMethod(Class<?> cls, String name) {
    boolean found = false;
    for (Method method : cls.getDeclaredMethods()) {
      if (method.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Missing declared method: " + cls.getName() + "." + name, found);
  }

  @Test
  public void eventFillerInnerCount_matchesRequestedCoverage() {
    assertEquals(16, EVENT_FILLER_INNERS.length);
  }

  @Test
  public void totalFillerInnerCount_matchesRequestedCoverage() {
    assertEquals(23, EVENT_FILLER_INNERS.length + OTHER_FILLER_INNERS.length);
  }

  @Test
  public void colorAndKeyFillerInners_extendExpressionFillerInner_andExposeAppendItems() throws Exception {
    Class<?> expressionFillerInner = load("org.alice.ide.cascade.fillerinners.ExpressionFillerInner");

    Class<?> color = loadFiller("ColorFillerInner");
    assertTrue(expressionFillerInner.isAssignableFrom(color));
    assertPublicNoArgConstructor(color.getName());
    assertDeclaredMethod(color, "appendItems");

    Class<?> key = loadFiller("KeyFillerInner");
    assertTrue(expressionFillerInner.isAssignableFrom(key));
    assertPublicNoArgConstructor(key.getName());
    assertDeclaredMethod(key, "appendItems");
  }

  @Test
  public void sourceFillerInner_isAbstract_andExposesTemplateMethodsAndField() throws Exception {
    Class<?> sourceFillerInner = loadFiller("SourceFillerInner");
    Field resourceCls = sourceFillerInner.getDeclaredField("resourceCls");

    assertTrue(Modifier.isAbstract(sourceFillerInner.getModifiers()));
    assertTrue(Modifier.isPrivate(resourceCls.getModifiers()));
    assertTrue(Modifier.isFinal(resourceCls.getModifiers()));

    Method getResourceFillIn = sourceFillerInner.getDeclaredMethod("getResourceFillIn", org.lgna.common.Resource.class);
    Method getImportFillIn = sourceFillerInner.getDeclaredMethod("getImportFillIn");

    assertTrue(Modifier.isAbstract(getResourceFillIn.getModifiers()));
    assertTrue(Modifier.isProtected(getResourceFillIn.getModifiers()));
    assertTrue(Modifier.isAbstract(getImportFillIn.getModifiers()));
    assertTrue(Modifier.isProtected(getImportFillIn.getModifiers()));
  }

  @Test
  public void imageAndAudioSourceFillerInners_extendSourceFillerInner() throws Exception {
    Class<?> sourceFillerInner = loadFiller("SourceFillerInner");
    Class<?> imageSource = loadFiller("ImageSourceFillerInner");
    Class<?> audioSource = loadFiller("AudioSourceFillerInner");

    assertTrue(sourceFillerInner.isAssignableFrom(imageSource));
    assertTrue(sourceFillerInner.isAssignableFrom(audioSource));
    assertFalse(Modifier.isAbstract(imageSource.getModifiers()));
    assertFalse(Modifier.isAbstract(audioSource.getModifiers()));
  }

  @Test
  public void imagePaintAndModelResourceFillerInners_loadAndExtendExpressionFillerInner() throws Exception {
    Class<?> expressionFillerInner = load("org.alice.ide.cascade.fillerinners.ExpressionFillerInner");

    Class<?> imagePaint = loadFiller("ImagePaintFillerInner");
    assertTrue(expressionFillerInner.isAssignableFrom(imagePaint));
    assertPublicNoArgConstructor(imagePaint.getName());

    Class<?> modelResource = loadFiller("ModelResourceFillerInner");
    assertTrue(expressionFillerInner.isAssignableFrom(modelResource));
    assertPublicNoArgConstructor(modelResource.getName());
  }

  @Test
  public void eventFillerInners_arePublicConcreteExpressionFillerInners() throws Exception {
    Class<?> expressionFillerInner = load("org.alice.ide.cascade.fillerinners.ExpressionFillerInner");

    for (String simpleName : EVENT_FILLER_INNERS) {
      Class<?> cls = loadFiller(simpleName);
      assertTrue(simpleName + " should be public", Modifier.isPublic(cls.getModifiers()));
      assertFalse(simpleName + " should be concrete", Modifier.isAbstract(cls.getModifiers()));
      assertTrue(simpleName + " should extend ExpressionFillerInner", expressionFillerInner.isAssignableFrom(cls));
    }
  }

  @Test
  public void eventFillerInners_havePublicNoArgConstructors() throws Exception {
    for (String simpleName : EVENT_FILLER_INNERS) {
      assertPublicNoArgConstructor(FILLER_INNER_PACKAGE + simpleName);
    }
  }

  @Test
  public void jointedModelTypeSeparator_hasSingletonAndPrivateConstructor() throws Exception {
    Class<?> cls = load("org.alice.stageide.cascade.JointedModelTypeSeparator");
    Method getInstance = cls.getMethod("getInstance", org.lgna.project.ast.AbstractType.class);
    Constructor<?> constructor = cls.getDeclaredConstructor(org.lgna.project.ast.AbstractType.class);

    assertTrue(Modifier.isPublic(cls.getModifiers()));
    assertTrue(load("org.lgna.croquet.CascadeLabelSeparator").isAssignableFrom(cls));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertTrue(Modifier.isSynchronized(getInstance.getModifiers()));
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    assertDeclaredMethod(cls, "findDefaultLocalizedText");
  }

  @Test
  public void jointExpressionFillIn_hasSingletonAndPrivateConstructor() throws Exception {
    Class<?> cls = load("org.alice.stageide.cascade.JointExpressionFillIn");
    Method getInstance = cls.getMethod("getInstance", org.lgna.project.ast.Expression.class, org.lgna.project.ast.AbstractMethod.class);
    Constructor<?> constructor = cls.getDeclaredConstructor(org.lgna.project.ast.Expression.class, org.lgna.project.ast.AbstractMethod.class);

    assertTrue(Modifier.isPublic(cls.getModifiers()));
    assertTrue(load("org.alice.ide.croquet.models.cascade.MethodInvocationFillIn").isAssignableFrom(cls));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    assertDeclaredMethod(cls, "createExpression");
  }

  @Test
  public void jointExpressionMenuModel_loadsAndExtendsCascadeMenuModel() throws Exception {
    Class<?> cls = load("org.alice.stageide.cascade.JointExpressionMenuModel");
    Constructor<?> constructor = cls.getConstructor(
        org.lgna.project.ast.Expression.class,
        java.util.List.class,
        int.class,
        boolean.class);

    assertTrue(Modifier.isPublic(cls.getModifiers()));
    assertTrue(load("org.lgna.croquet.CascadeMenuModel").isAssignableFrom(cls));
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertDeclaredMethod(cls, "getMenuProxy");
    assertDeclaredMethod(cls, "updateBlankChildren");
  }

  @Test
  public void expressionCascadeManager_loadsAndOverridesCoreHooks() throws Exception {
    Class<?> cls = load("org.alice.stageide.cascade.ExpressionCascadeManager");

    assertTrue(Modifier.isPublic(cls.getModifiers()));
    assertTrue(load("org.alice.ide.cascade.ExpressionCascadeManager").isAssignableFrom(cls));
    assertPublicNoArgConstructor(cls.getName());
    assertDeclaredMethod(cls, "getEnumTypeForInterfaceType");
    assertDeclaredMethod(cls, "areEnumConstantsDesired");
    assertDeclaredMethod(cls, "createPartMenuModel");
  }

  @Test
  public void allOtherRequestedFillerInners_load() throws Exception {
    for (String simpleName : OTHER_FILLER_INNERS) {
      assertNotNull(loadFiller(simpleName));
    }
  }

  @Test
  public void allEventFillerInners_load() throws Exception {
    for (String simpleName : EVENT_FILLER_INNERS) {
      assertNotNull(loadFiller(simpleName));
    }
  }
}
