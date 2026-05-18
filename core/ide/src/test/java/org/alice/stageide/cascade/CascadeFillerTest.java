package org.alice.stageide.cascade;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for cascade model classes used in expression menus:
 * ExpressionCascadeManager, JointedModelTypeSeparator,
 * JointExpressionFillIn, JointExpressionMenuModel.
 */
public class CascadeFillerTest {

  // -- ExpressionCascadeManager ------------------------------------------

  @Test
  public void expressionCascadeManager_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
  }

  @Test
  public void expressionCascadeManager_extendsBase() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Class<?> base = Class.forName("org.alice.ide.cascade.ExpressionCascadeManager");
    assertTrue(base.isAssignableFrom(cls));
  }

  @Test
  public void expressionCascadeManager_hasDefaultConstructor() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Constructor<?> ctor = cls.getConstructor();
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  // -- JointedModelTypeSeparator -----------------------------------------

  @Test
  public void jointedModelTypeSeparator_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.cascade.JointedModelTypeSeparator");
  }

  // -- JointExpressionFillIn ---------------------------------------------

  @Test
  public void jointExpressionFillIn_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.cascade.JointExpressionFillIn");
  }

  // -- JointExpressionMenuModel ------------------------------------------

  @Test
  public void jointExpressionMenuModel_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.cascade.JointExpressionMenuModel");
  }

  // -- StageIde ExpressionCascadeManager adds filler inners ---------------

  @Test
  public void stageExpressionCascadeManager_instantiates() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Object instance = cls.getConstructor().newInstance();
    assertNotNull(instance);
  }
}
