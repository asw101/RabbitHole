package org.alice.stageide;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class OneShotPackageTest {

  private static final String ONESHOT = "org.alice.stageide.oneshot.";
  private static final String ONESHOT_EDITS = ONESHOT + "edits.";

  private static final String[] CONCRETE_EDITS = {
      "SetPaintEdit",
      "SetOpacityEdit",
      "StrikePoseEdit",
      "LocalTransformationEdit",
      "AllJointLocalTransformationsEdit"
  };

  private static final String[] FACTORIES = {
      "AllJointLocalTransformationsMethodInvocationEditFactory",
      "LocalTransformationMethodInvocationEditFactory",
      "SetOpacityMethodInvocationEditFactory",
      "SetPaintMethodInvocationEditFactory",
      "StrikePoseMethodInvocationEditFactory"
  };

  private static final String[] CONCRETE_FILL_INS = {
      "LocalTransformationMethodInvocationFillIn",
      "SetPaintMethodInvocationFillIn",
      "SetOpacityMethodInvocationFillIn",
      "StrikePoseMethodInvocationFillIn",
      "JavaDefinedStrikePoseMethodInvocationFillIn",
      "AllJointLocalTransformationsMethodInvocationFillIn"
  };

  private static Class<?> load(String name) throws Exception {
    return Class.forName(name, false, OneShotPackageTest.class.getClassLoader());
  }

  private static Method findDeclaredMethodNamed(Class<?> cls, String name) {
    for (Method method : cls.getDeclaredMethods()) {
      if (method.getName().equals(name)) {
        return method;
      }
    }
    fail("Missing declared method: " + cls.getName() + "." + name);
    return null;
  }

  private static Method findMethodNamed(Class<?> cls, String name) {
    for (Method method : cls.getMethods()) {
      if (method.getName().equals(name)) {
        return method;
      }
    }
    fail("Missing public method: " + cls.getName() + "." + name);
    return null;
  }

  private static void assertPrivateConstructors(Class<?> cls) {
    Constructor<?>[] constructors = cls.getDeclaredConstructors();
    assertTrue(constructors.length >= 1);
    for (Constructor<?> constructor : constructors) {
      assertTrue(cls.getName() + " constructors should be private", Modifier.isPrivate(constructor.getModifiers()));
    }
  }

  @Test
  public void methodInvocationEditFactory_isSingleMethodInterface() throws Exception {
    Class<?> cls = load(ONESHOT + "MethodInvocationEditFactory");
    Method createEdit = cls.getDeclaredMethod("createEdit", org.lgna.croquet.history.UserActivity.class);

    assertTrue(cls.isInterface());
    assertEquals(1, cls.getDeclaredMethods().length);
    assertEquals("createEdit", createEdit.getName());
    assertEquals(org.lgna.croquet.edits.Edit.class, createEdit.getReturnType());
  }

  @Test
  public void methodInvocationBlank_extendsCascadeBlank_andDeclaresRequestedMembers() throws Exception {
    Class<?> cls = load(ONESHOT + "MethodInvocationBlank");
    Method getInstance = cls.getMethod("getInstance", org.alice.ide.instancefactory.InstanceFactory.class);
    Method updateChildren = findDeclaredMethodNamed(cls, "updateChildren");
    Field instanceFactory = cls.getDeclaredField("instanceFactory");

    assertTrue(load("org.lgna.croquet.CascadeBlank").isAssignableFrom(cls));
    assertTrue(Modifier.isPublic(cls.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertTrue(Modifier.isPrivate(instanceFactory.getModifiers()));
    assertTrue(Modifier.isFinal(instanceFactory.getModifiers()));
    assertTrue(Modifier.isProtected(updateChildren.getModifiers()));
    assertPrivateConstructors(cls);
  }

  @Test
  public void proceduresCascade_extendsImmutableCascade_andOverridesCreateEdit() throws Exception {
    Class<?> cls = load(ONESHOT + "ProceduresCascade");
    Method getInstance = cls.getMethod("getInstance", org.alice.ide.instancefactory.InstanceFactory.class);
    Method createEdit = findDeclaredMethodNamed(cls, "createEdit");

    assertTrue(load("org.lgna.croquet.ImmutableCascade").isAssignableFrom(cls));
    assertTrue(Modifier.isPublic(cls.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertTrue(Modifier.isProtected(createEdit.getModifiers()));
    assertPrivateConstructors(cls);
  }

  @Test
  public void oneShotMenuModel_extendsPredeterminedMenuModel_andHasPrivateConstructor() throws Exception {
    Class<?> cls = load(ONESHOT + "OneShotMenuModel");
    Method getInstance = cls.getMethod("getInstance", org.alice.ide.instancefactory.InstanceFactory.class);

    assertTrue(load("org.lgna.croquet.PredeterminedMenuModel").isAssignableFrom(cls));
    assertTrue(Modifier.isPublic(cls.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertPrivateConstructors(cls);
  }

  @Test
  public void dynamicOneShotMenuModel_extendsMenuModel_andOverridesPopupPrologue() throws Exception {
    Class<?> cls = load(ONESHOT + "DynamicOneShotMenuModel");
    Method getInstance = cls.getMethod("getInstance");
    Method popupPrologue = cls.getMethod("handlePopupMenuPrologue", org.lgna.croquet.views.PopupMenu.class);

    assertTrue(load("org.lgna.croquet.MenuModel").isAssignableFrom(cls));
    assertTrue(Modifier.isPublic(cls.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertTrue(Modifier.isPublic(popupPrologue.getModifiers()));
    assertPrivateConstructors(cls);
  }

  @Test
  public void instanceFactoryLabelSeparatorModel_extendsLabelMenuSeparatorModel_andOverridesGetName() throws Exception {
    Class<?> cls = load(ONESHOT + "InstanceFactoryLabelSeparatorModel");
    Method getName = cls.getMethod("getName");
    Method getInstance = cls.getMethod("getInstance", org.alice.ide.instancefactory.InstanceFactory.class);

    assertTrue(load("org.lgna.croquet.LabelMenuSeparatorModel").isAssignableFrom(cls));
    assertTrue(Modifier.isPublic(cls.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertEquals(String.class, getName.getReturnType());
    assertPrivateConstructors(cls);
  }

  @Test
  public void oneShotUtilities_hasAssertionThrowingPrivateConstructor_andStaticFactoryMethod() throws Exception {
    Class<?> cls = load(ONESHOT + "OneShotUtilities");
    Method createMenuItemPrepModels = cls.getMethod("createMenuItemPrepModels", org.alice.ide.instancefactory.InstanceFactory.class);
    Constructor<?> constructor = cls.getDeclaredConstructor();

    assertTrue(Modifier.isPublic(cls.getModifiers()));
    assertTrue(Modifier.isStatic(createMenuItemPrepModels.getModifiers()));
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));

    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected AssertionError from private utility constructor");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void methodInvocationEdit_isAbstractAbstractEdit_withRequestedHooks() throws Exception {
    Class<?> cls = load(ONESHOT_EDITS + "MethodInvocationEdit");
    Field instanceFactory = cls.getDeclaredField("instanceFactory");
    Field method = cls.getDeclaredField("method");
    Field argumentExpressions = cls.getDeclaredField("argumentExpressions");

    assertTrue(Modifier.isAbstract(cls.getModifiers()));
    assertTrue(load("org.lgna.croquet.edits.AbstractEdit").isAssignableFrom(cls));
    assertTrue(Modifier.isPrivate(instanceFactory.getModifiers()));
    assertTrue(Modifier.isFinal(instanceFactory.getModifiers()));
    assertTrue(Modifier.isPrivate(method.getModifiers()));
    assertTrue(Modifier.isFinal(method.getModifiers()));
    assertTrue(Modifier.isPrivate(argumentExpressions.getModifiers()));
    assertTrue(Modifier.isFinal(argumentExpressions.getModifiers()));

    assertTrue(Modifier.isAbstract(findDeclaredMethodNamed(cls, "preserveUndoInfo").getModifiers()));
    assertTrue(Modifier.isFinal(findDeclaredMethodNamed(cls, "doOrRedoInternal").getModifiers()));
    assertEquals("appendDescription", findDeclaredMethodNamed(cls, "appendDescription").getName());
  }

  @Test
  public void abstractSetPaintEdit_isAbstractMethodInvocationEditSubclass() throws Exception {
    Class<?> cls = load(ONESHOT_EDITS + "AbstractSetPaintEdit");
    assertTrue(Modifier.isAbstract(cls.getModifiers()));
    assertTrue(load(ONESHOT_EDITS + "MethodInvocationEdit").isAssignableFrom(cls));
    assertNotNull(findDeclaredMethodNamed(cls, "getPaintProperty"));
    assertNotNull(findDeclaredMethodNamed(cls, "preserveUndoInfo"));
    assertNotNull(findDeclaredMethodNamed(cls, "undoInternal"));
  }

  @Test
  public void concreteEditClasses_extendExpectedBaseClasses() throws Exception {
    assertSame(load(ONESHOT_EDITS + "AbstractSetPaintEdit"), load(ONESHOT_EDITS + "SetPaintEdit").getSuperclass());
    for (String simpleName : CONCRETE_EDITS) {
      Class<?> cls = load(ONESHOT_EDITS + simpleName);
      assertFalse(simpleName + " should be concrete", Modifier.isAbstract(cls.getModifiers()));
      assertTrue(Modifier.isPublic(cls.getModifiers()));
      assertTrue(load(ONESHOT_EDITS + "MethodInvocationEdit").isAssignableFrom(cls));
    }
  }

  @Test
  public void factoryClasses_implementMethodInvocationEditFactory_andDeclareCreateEdit() throws Exception {
    Class<?> factoryInterface = load(ONESHOT + "MethodInvocationEditFactory");
    for (String simpleName : FACTORIES) {
      Class<?> cls = load(ONESHOT + simpleName);
      Method createEdit = cls.getMethod("createEdit", org.lgna.croquet.history.UserActivity.class);
      assertTrue(Modifier.isPublic(cls.getModifiers()));
      assertTrue(simpleName + " should implement MethodInvocationEditFactory", factoryInterface.isAssignableFrom(cls));
      assertEquals(org.lgna.croquet.edits.Edit.class, createEdit.getReturnType());
      assertFalse(simpleName + " should be concrete", Modifier.isAbstract(cls.getModifiers()));
    }
  }

  @Test
  public void factoryClasses_exposePublicConstructors_forEditCreation() throws Exception {
    for (String simpleName : FACTORIES) {
      Class<?> cls = load(ONESHOT + simpleName);
      boolean hasPublicConstructor = false;
      for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
        if (Modifier.isPublic(constructor.getModifiers())) {
          hasPublicConstructor = true;
        }
      }
      assertTrue("Expected public constructor for " + cls.getName(), hasPublicConstructor);
    }
  }

  @Test
  public void concreteFillIns_loadAndExposeGetInstance() throws Exception {
    for (String simpleName : CONCRETE_FILL_INS) {
      Class<?> cls = load(ONESHOT + simpleName);
      assertTrue(Modifier.isPublic(cls.getModifiers()));
      assertNotNull(findMethodNamed(cls, "getInstance"));
    }
  }

  @Test
  public void oneShotJavaMethodInvocationFillIn_isAbstractImmutableCascadeFillInSubclass() throws Exception {
    Class<?> cls = load(ONESHOT + "OneShotJavaMethodInvocationFillIn");
    assertTrue(Modifier.isAbstract(cls.getModifiers()));
    assertTrue(load("org.lgna.croquet.ImmutableCascadeFillIn").isAssignableFrom(cls));
    assertNotNull(findDeclaredMethodNamed(cls, "createMethodInvocationEditFactory"));
    assertNotNull(findDeclaredMethodNamed(cls, "createValue"));
    assertNotNull(findDeclaredMethodNamed(cls, "getTransientValue"));
  }
}
