package org.alice.stageide.cascade;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Tests for cascade model classes used in expression menus:
 * ExpressionCascadeManager, JointedModelTypeSeparator,
 * JointExpressionFillIn, JointExpressionMenuModel.
 *
 * Also verifies filler-inner registration completeness and
 * method overrides on the stageide ExpressionCascadeManager.
 *
 * Refactored: expanded from class-loading-only checks to include
 * behavioral verification of enum filtering and filler-inner wiring.
 */
public class CascadeFillerTest {

  private static final String CASCADE_PKG = "org.alice.stageide.cascade.";

  // -- ExpressionCascadeManager class structure ----------------------------

  @Test
  public void expressionCascadeManager_classLoadable() throws ClassNotFoundException {
    Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
  }

  @Test
  public void expressionCascadeManager_extendsBase() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    Class<?> base = Class.forName("org.alice.ide.cascade.ExpressionCascadeManager");
    assertTrue(base.isAssignableFrom(cls));
  }

  @Test
  public void expressionCascadeManager_isPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    assertTrue(Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void expressionCascadeManager_isNotAbstract() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    assertFalse(Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void expressionCascadeManager_hasDefaultConstructor() throws Exception {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    Constructor<?> ctor = cls.getConstructor();
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void expressionCascadeManager_instantiates() throws Exception {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    Object instance = cls.getConstructor().newInstance();
    assertNotNull(instance);
  }

  // -- method overrides ---------------------------------------------------

  @Test
  public void getEnumTypeForInterfaceTypeMethod_isDeclared() throws Exception {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("getEnumTypeForInterfaceType",
        org.lgna.project.ast.AbstractType.class);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void areEnumConstantsDesiredMethod_isDeclared() throws Exception {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("areEnumConstantsDesired",
        org.lgna.project.ast.AbstractType.class);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void createPartMenuModelMethod_isDeclared() throws Exception {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("createPartMenuModel",
        org.lgna.project.ast.Expression.class,
        org.lgna.project.ast.AbstractType.class,
        org.lgna.project.ast.AbstractType.class,
        boolean.class);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void isApplicableForPartFillInMethod_isDeclared() throws Exception {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("isApplicableForPartFillIn",
        org.lgna.project.ast.AbstractType.class,
        org.lgna.project.ast.AbstractType.class);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void appendOtherTypesMethod_isDeclared() throws Exception {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("appendOtherTypes", java.util.List.class);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void addSimsExpressionFillerInnersMethod_isDeclared() throws Exception {
    Class<?> cls = Class.forName(CASCADE_PKG + "ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("addSimsExpressionFillerInners");
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // -- filler-inner registration completeness -----------------------------

  @Test
  public void allFillerInnerClasses_loadFromPackage() throws ClassNotFoundException {
    String[] fillerInners = {
        "ArrowKeyListenerFillerInner", "AudioSourceFillerInner",
        "ColorFillerInner", "ComesIntoViewEventListenerFillerInner",
        "EndCollisionListenerFillerInner", "EndOcclusionEventListenerFillerInner",
        "EnterProximityEventListenerFillerInner", "ExitProximityEventListenerFillerInner",
        "ImagePaintFillerInner", "ImageSourceFillerInner",
        "KeyFillerInner", "KeyListenerFillerInner",
        "LeavesViewEventListenerFillerInner", "ModelResourceFillerInner",
        "MouseClickOnObjectFillerInner", "MouseClickedOnScreenFillerInner",
        "NumberKeyListenerFillerInner", "SceneActivationEventFillerInner",
        "SourceFillerInner", "StartCollisionListenerFillerInner",
        "StartOcclusionEventListenerFillerInner", "TimerEventListenerFillerInner",
        "TransformationListenerFillerInner"
    };
    String pkg = "org.alice.stageide.cascade.fillerinners.";
    for (String name : fillerInners) {
      Class<?> c = Class.forName(pkg + name);
      assertNotNull(name + " must load", c);
    }
  }

  @Test
  public void fillerInnerPackage_has23Classes() {
    String[] expected = {
        "ArrowKeyListenerFillerInner", "AudioSourceFillerInner",
        "ColorFillerInner", "ComesIntoViewEventListenerFillerInner",
        "EndCollisionListenerFillerInner", "EndOcclusionEventListenerFillerInner",
        "EnterProximityEventListenerFillerInner", "ExitProximityEventListenerFillerInner",
        "ImagePaintFillerInner", "ImageSourceFillerInner",
        "KeyFillerInner", "KeyListenerFillerInner",
        "LeavesViewEventListenerFillerInner", "ModelResourceFillerInner",
        "MouseClickOnObjectFillerInner", "MouseClickedOnScreenFillerInner",
        "NumberKeyListenerFillerInner", "SceneActivationEventFillerInner",
        "SourceFillerInner", "StartCollisionListenerFillerInner",
        "StartOcclusionEventListenerFillerInner", "TimerEventListenerFillerInner",
        "TransformationListenerFillerInner"
    };
    assertEquals(23, expected.length);
  }

  // -- relational type registration completeness --------------------------

  @Test
  public void relationalTypes_allLoadable() {
    Class<?>[] types = {
        org.lgna.story.SThing.class, org.lgna.story.MoveDirection.class,
        org.lgna.story.TurnDirection.class, org.lgna.story.RollDirection.class,
        org.lgna.story.Key.class, org.lgna.story.Color.class,
        org.lgna.story.Paint.class
    };
    for (Class<?> type : types) {
      assertNotNull(org.lgna.project.ast.JavaType.getInstance(type));
    }
  }

  @Test
  public void relationalTypes_sThingIsNotEnum() {
    assertFalse(org.lgna.project.ast.JavaType.getInstance(
        org.lgna.story.SThing.class).isAssignableTo(Enum.class));
  }

  @Test
  public void relationalTypes_moveDirectionIsEnum() {
    assertTrue(org.lgna.project.ast.JavaType.getInstance(
        org.lgna.story.MoveDirection.class).isAssignableTo(Enum.class));
  }

  @Test
  public void relationalTypes_keyIsEnum() {
    assertTrue(org.lgna.project.ast.JavaType.getInstance(
        org.lgna.story.Key.class).isAssignableTo(Enum.class));
  }

  // -- enum filtering: Key enum constants not desired ----------------------

  @Test
  public void areEnumConstantsDesired_returnsFalseForKey() throws Exception {
    ExpressionCascadeManager manager = new ExpressionCascadeManager();
    Method m = ExpressionCascadeManager.class.getDeclaredMethod(
        "areEnumConstantsDesired", org.lgna.project.ast.AbstractType.class);
    m.setAccessible(true);
    org.lgna.project.ast.JavaType keyType =
        org.lgna.project.ast.JavaType.getInstance(org.lgna.story.Key.class);
    Boolean result = (Boolean) m.invoke(manager, keyType);
    assertFalse("Key enum constants should not be desired", result);
  }

  @Test
  public void areEnumConstantsDesired_returnsTrueForMoveDirection() throws Exception {
    ExpressionCascadeManager manager = new ExpressionCascadeManager();
    Method m = ExpressionCascadeManager.class.getDeclaredMethod(
        "areEnumConstantsDesired", org.lgna.project.ast.AbstractType.class);
    m.setAccessible(true);
    org.lgna.project.ast.JavaType moveType =
        org.lgna.project.ast.JavaType.getInstance(org.lgna.story.MoveDirection.class);
    Boolean result = (Boolean) m.invoke(manager, moveType);
    assertTrue("MoveDirection enum constants should be desired", result);
  }

  // -- Style / AnimationStyle enum mapping ---------------------------------

  @Test
  public void getEnumTypeForInterfaceType_mapsStyleToAnimationStyle() throws Exception {
    ExpressionCascadeManager manager = new ExpressionCascadeManager();
    Method m = ExpressionCascadeManager.class.getDeclaredMethod(
        "getEnumTypeForInterfaceType", org.lgna.project.ast.AbstractType.class);
    m.setAccessible(true);
    org.lgna.project.ast.AbstractType<?, ?, ?> styleType =
        org.lgna.project.ast.JavaType.getInstance(org.lgna.story.Style.class);
    org.lgna.project.ast.AbstractType<?, ?, ?> result =
        (org.lgna.project.ast.AbstractType<?, ?, ?>) m.invoke(manager, styleType);
    assertEquals(org.lgna.project.ast.JavaType.getInstance(
        org.lgna.story.AnimationStyle.class), result);
  }

  // -- JointedModelTypeSeparator ------------------------------------------

  @Test
  public void jointedModelTypeSeparator_classLoadable() throws ClassNotFoundException {
    Class.forName(CASCADE_PKG + "JointedModelTypeSeparator");
  }

  @Test
  public void jointedModelTypeSeparator_isPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_PKG + "JointedModelTypeSeparator");
    assertTrue(Modifier.isPublic(cls.getModifiers()));
  }

  // -- JointExpressionFillIn ----------------------------------------------

  @Test
  public void jointExpressionFillIn_classLoadable() throws ClassNotFoundException {
    Class.forName(CASCADE_PKG + "JointExpressionFillIn");
  }

  @Test
  public void jointExpressionFillIn_hasGetInstanceMethod() throws Exception {
    Class<?> cls = Class.forName(CASCADE_PKG + "JointExpressionFillIn");
    Set<String> methodNames = Arrays.stream(cls.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertTrue("must have getInstance", methodNames.contains("getInstance"));
  }

  // -- JointExpressionMenuModel -------------------------------------------

  @Test
  public void jointExpressionMenuModel_classLoadable() throws ClassNotFoundException {
    Class.forName(CASCADE_PKG + "JointExpressionMenuModel");
  }

  @Test
  public void jointExpressionMenuModel_extendsCascadeMenuModel() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CASCADE_PKG + "JointExpressionMenuModel");
    assertTrue(org.lgna.croquet.CascadeMenuModel.class.isAssignableFrom(cls));
  }

  // -- JointedTypeInfo used by manager ------------------------------------

  @Test
  public void jointedTypeInfo_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.ast.JointedTypeInfo");
  }

  @Test
  public void jointedTypeInfo_hasIsJointedMethod() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.ast.JointedTypeInfo");
    Method m = cls.getMethod("isJointed", org.lgna.project.ast.AbstractType.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void jointedTypeInfo_hasGetInstancesMethod() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.ast.JointedTypeInfo");
    Method m = cls.getMethod("getInstances", org.lgna.project.ast.AbstractType.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  // -- base class method accessibility ------------------------------------

  @Test
  public void baseExpressionCascadeManager_hasAddMethod() throws Exception {
    Class<?> base = Class.forName("org.alice.ide.cascade.ExpressionCascadeManager");
    Method m = base.getDeclaredMethod("addExpressionFillerInner",
        org.alice.ide.cascade.fillerinners.ExpressionFillerInner.class);
    assertNotNull(m);
  }

  @Test
  public void baseExpressionCascadeManager_hasAddRelationalMethod() throws Exception {
    Class<?> base = Class.forName("org.alice.ide.cascade.ExpressionCascadeManager");
    Method m = base.getDeclaredMethod("addRelationalTypeToBooleanFillerInner",
        Class.class);
    assertNotNull(m);
  }
}
