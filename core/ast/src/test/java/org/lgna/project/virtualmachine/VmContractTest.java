package org.lgna.project.virtualmachine;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Reflection-based contract test for {@link VirtualMachine}.
 *
 * <p>Verifies that the public API (20 public methods) and abstract protocol
 * (16 abstract methods) exist with correct signatures, return types, and
 * modifiers. This test must pass both <em>before</em> and <em>after</em>
 * the delegate extraction (VmExpressionEvaluator / VmStatementExecutor).
 *
 * <p>Headless scope: pure reflection — no JavaFX, no gallery assets,
 * no scene graph, no display.
 */
public class VmContractTest {

  private static final Class<?> VM = VirtualMachine.class;

  // ──────────────────────────────────────────────────────────
  //  Class-level structural assertions
  // ──────────────────────────────────────────────────────────

  @Test
  public void classIsPublicAndAbstract() {
    assertTrue("VirtualMachine must be public",
        Modifier.isPublic(VM.getModifiers()));
    assertTrue("VirtualMachine must be abstract",
        Modifier.isAbstract(VM.getModifiers()));
  }

  @Test
  public void classExtendsObject() {
    assertEquals("VirtualMachine should extend Object directly",
        Object.class, VM.getSuperclass());
  }

  @Test
  public void classIsInCorrectPackage() {
    assertEquals("org.lgna.project.virtualmachine", VM.getPackage().getName());
  }

  @Test
  public void releaseVirtualMachineExtendsVirtualMachine() {
    assertTrue("ReleaseVirtualMachine must extend VirtualMachine",
        VM.isAssignableFrom(ReleaseVirtualMachine.class));
    assertFalse("ReleaseVirtualMachine must not be abstract",
        Modifier.isAbstract(ReleaseVirtualMachine.class.getModifiers()));
  }

  // ──────────────────────────────────────────────────────────
  //  Public API — 20 public methods
  // ──────────────────────────────────────────────────────────

  @Test
  public void publicMethod_getStackTrace() {
    assertPublicMethod("getStackTrace",
        new Class<?>[]{Thread.class},
        org.lgna.project.virtualmachine.LgnaStackTraceElement[].class);
  }

  @Test
  public void publicMethod_ENTRY_POINT_evaluate() {
    assertPublicMethod("ENTRY_POINT_evaluate",
        new Class<?>[]{UserInstance.class, org.lgna.project.ast.Expression[].class},
        Object[].class);
  }

  @Test
  public void publicMethod_ENTRY_POINT_invoke() {
    assertPublicMethod("ENTRY_POINT_invoke",
        new Class<?>[]{UserInstance.class, org.lgna.project.ast.AbstractMethod.class, Object[].class},
        Object.class);
  }

  @Test
  public void publicMethod_ENTRY_POINT_createInstance() {
    assertPublicMethod("ENTRY_POINT_createInstance",
        new Class<?>[]{org.lgna.project.ast.NamedUserType.class, Object[].class},
        UserInstance.class);
  }

  @Test
  public void publicMethod_createAndSetFieldInstance() {
    assertPublicMethod("createAndSetFieldInstance",
        new Class<?>[]{UserInstance.class, org.lgna.project.ast.UserField.class},
        void.class);
  }

  @Test
  public void publicMethod_ACCEPTABLE_HACK_FOR_SCENE_EDITOR_initializeField() {
    assertPublicMethod("ACCEPTABLE_HACK_FOR_SCENE_EDITOR_initializeField",
        new Class<?>[]{UserInstance.class, org.lgna.project.ast.UserField.class},
        void.class);
  }

  @Test
  public void publicMethod_ACCEPTABLE_HACK_FOR_SCENE_EDITOR_executeStatement() {
    assertPublicMethod("ACCEPTABLE_HACK_FOR_SCENE_EDITOR_executeStatement",
        new Class<?>[]{UserInstance.class, org.lgna.project.ast.Statement.class},
        void.class);
  }

  @Test
  public void publicMethod_registerAbstractClassAdapter() {
    assertPublicMethod("registerAbstractClassAdapter",
        new Class<?>[]{Class.class, Class.class},
        void.class);
  }

  @Test
  public void publicMethod_evaluateArguments() {
    assertPublicMethod("evaluateArguments",
        new Class<?>[]{
            org.lgna.project.ast.AbstractCode.class,
            org.lgna.project.ast.NodeListProperty.class,
            org.lgna.project.ast.NodeListProperty.class,
            org.lgna.project.ast.NodeListProperty.class},
        Object[].class);
  }

  @Test
  public void publicMethod_get() {
    assertPublicMethod("get",
        new Class<?>[]{org.lgna.project.ast.AbstractField.class, Object.class},
        Object.class);
  }

  @Test
  public void publicMethod_set() {
    assertPublicMethod("set",
        new Class<?>[]{org.lgna.project.ast.AbstractField.class, Object.class, Object.class},
        void.class);
  }

  @Test
  public void publicMethod_getItemAtIndex() {
    assertPublicMethod("getItemAtIndex",
        new Class<?>[]{org.lgna.project.ast.AbstractType.class, Object.class, Integer.class},
        Object.class);
  }

  @Test
  public void publicMethod_setItemAtIndex() {
    assertPublicMethod("setItemAtIndex",
        new Class<?>[]{org.lgna.project.ast.AbstractType.class, Object.class, Integer.class, Object.class},
        void.class);
  }

  @Test
  public void publicMethod_invokeUserMethod() {
    assertPublicMethod("invokeUserMethod",
        new Class<?>[]{Object.class, org.lgna.project.ast.UserMethod.class, Object[].class},
        Object.class);
  }

  @Test
  public void publicMethod_invokeMethodDeclaredInJava() {
    assertPublicMethod("invokeMethodDeclaredInJava",
        new Class<?>[]{Object.class, org.lgna.project.ast.JavaMethod.class, Object[].class},
        Object.class);
  }

  @Test
  public void publicMethod_stopExecution() {
    assertPublicMethod("stopExecution", new Class<?>[0], void.class);
  }

  @Test
  public void publicMethod_addVirtualMachineListener() {
    assertPublicMethod("addVirtualMachineListener",
        new Class<?>[]{org.lgna.project.virtualmachine.events.VirtualMachineListener.class},
        void.class);
  }

  @Test
  public void publicMethod_removeVirtualMachineListener() {
    assertPublicMethod("removeVirtualMachineListener",
        new Class<?>[]{org.lgna.project.virtualmachine.events.VirtualMachineListener.class},
        void.class);
  }

  @Test
  public void publicMethod_getVirtualMachineListeners() {
    assertPublicMethod("getVirtualMachineListeners", new Class<?>[0], List.class);
  }

  @Test
  public void publicMethod_setForSceneEditor() {
    assertPublicMethod("setForSceneEditor", new Class<?>[0], void.class);
  }

  @Test
  public void publicMethodCountIs20() {
    long count = Arrays.stream(VM.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .count();
    assertEquals("VirtualMachine should declare exactly 20 public methods", 20, count);
  }

  // ──────────────────────────────────────────────────────────
  //  Abstract protocol — 16 abstract methods
  // ──────────────────────────────────────────────────────────

  @Test
  public void abstractMethod_getStackTrace() {
    assertAbstractMethod("getStackTrace",
        new Class<?>[]{Thread.class},
        LgnaStackTraceElement[].class);
  }

  @Test
  public void abstractMethod_getThis() {
    assertAbstractMethod("getThis", new Class<?>[0], UserInstance.class);
  }

  @Test
  public void abstractMethod_pushBogusFrame() {
    assertAbstractMethod("pushBogusFrame",
        new Class<?>[]{UserInstance.class}, void.class);
  }

  @Test
  public void abstractMethod_pushConstructorFrame() {
    assertAbstractMethod("pushConstructorFrame",
        new Class<?>[]{org.lgna.project.ast.NamedUserType.class, Map.class},
        void.class);
  }

  @Test
  public void abstractMethod_setConstructorFrameUserInstance() {
    assertAbstractMethod("setConstructorFrameUserInstance",
        new Class<?>[]{UserInstance.class}, void.class);
  }

  @Test
  public void abstractMethod_pushMethodFrame() {
    assertAbstractMethod("pushMethodFrame",
        new Class<?>[]{UserInstance.class, org.lgna.project.ast.UserMethod.class, Map.class},
        void.class);
  }

  @Test
  public void abstractMethod_pushLambdaFrame() {
    assertAbstractMethod("pushLambdaFrame",
        new Class<?>[]{UserInstance.class, org.lgna.project.ast.UserLambda.class,
            org.lgna.project.ast.AbstractMethod.class, Map.class},
        void.class);
  }

  @Test
  public void abstractMethod_popFrame() {
    assertAbstractMethod("popFrame", new Class<?>[0], void.class);
  }

  @Test
  public void abstractMethod_lookup() {
    assertAbstractMethod("lookup",
        new Class<?>[]{org.lgna.project.ast.UserParameter.class}, Object.class);
  }

  @Test
  public void abstractMethod_pushLocal() {
    assertAbstractMethod("pushLocal",
        new Class<?>[]{org.lgna.project.ast.UserLocal.class, Object.class},
        void.class);
  }

  @Test
  public void abstractMethod_getLocal() {
    assertAbstractMethod("getLocal",
        new Class<?>[]{org.lgna.project.ast.UserLocal.class}, Object.class);
  }

  @Test
  public void abstractMethod_setLocal() {
    assertAbstractMethod("setLocal",
        new Class<?>[]{org.lgna.project.ast.UserLocal.class, Object.class},
        void.class);
  }

  @Test
  public void abstractMethod_popLocal() {
    assertAbstractMethod("popLocal",
        new Class<?>[]{org.lgna.project.ast.UserLocal.class}, void.class);
  }

  @Test
  public void abstractMethod_getFrameForThread() {
    assertAbstractMethod("getFrameForThread",
        new Class<?>[]{Thread.class}, Frame.class);
  }

  @Test
  public void abstractMethod_pushCurrentThread() {
    assertAbstractMethod("pushCurrentThread",
        new Class<?>[]{Frame.class}, void.class);
  }

  @Test
  public void abstractMethod_popCurrentThread() {
    assertAbstractMethod("popCurrentThread", new Class<?>[0], void.class);
  }

  @Test
  public void abstractMethodCountIs16() {
    long count = Arrays.stream(VM.getDeclaredMethods())
        .filter(m -> Modifier.isAbstract(m.getModifiers()))
        .count();
    assertEquals("VirtualMachine should declare exactly 16 abstract methods", 16, count);
  }

  // ──────────────────────────────────────────────────────────
  //  Protected concrete methods — expected to exist
  // ──────────────────────────────────────────────────────────

  @Test
  public void protectedConcreteMethod_evaluate_Expression() {
    assertProtectedConcreteMethod("evaluate",
        new Class<?>[]{org.lgna.project.ast.Expression.class}, Object.class);
  }

  @Test
  public void protectedConcreteMethod_execute_Statement() {
    assertProtectedConcreteMethod("execute",
        new Class<?>[]{org.lgna.project.ast.Statement.class}, void.class);
  }

  @Test
  public void protectedConcreteMethod_invoke() {
    assertProtectedConcreteMethod("invoke",
        new Class<?>[]{Object.class, org.lgna.project.ast.AbstractMethod.class, Object[].class},
        Object.class);
  }

  // ──────────────────────────────────────────────────────────
  //  Package-private method — createInstance
  // ──────────────────────────────────────────────────────────

  @Test
  public void packagePrivateMethod_createInstance() {
    Method m = findDeclaredMethod("createInstance",
        org.lgna.project.ast.UserType.class, UserInstance.class,
        java.lang.reflect.Constructor.class, Object[].class);
    assertNotNull("createInstance(UserType, UserInstance, Constructor, Object...) must exist", m);
    assertFalse("createInstance should not be public", Modifier.isPublic(m.getModifiers()));
    assertFalse("createInstance should not be private", Modifier.isPrivate(m.getModifiers()));
    assertFalse("createInstance should not be abstract", Modifier.isAbstract(m.getModifiers()));
    assertEquals(Object.class, m.getReturnType());
  }

  // ──────────────────────────────────────────────────────────
  //  Fields — key instance fields exist
  // ──────────────────────────────────────────────────────────

  @Test
  public void field_virtualMachineListeners_exists() {
    assertDeclaredField("virtualMachineListeners", CopyOnWriteArrayList.class);
  }

  @Test
  public void field_isStopped_exists() {
    assertDeclaredField("isStopped", boolean.class);
  }

  @Test
  public void field_isForRunning_exists() {
    assertDeclaredField("isForRunning", boolean.class);
  }

  @Test
  public void field_mapAbstractClsToAdapterCls_exists() {
    assertDeclaredField("mapAbstractClsToAdapterCls", Map.class);
  }

  @Test
  public void field_expressionEvaluator_exists() {
    assertDeclaredField("expressionEvaluator", VmExpressionEvaluator.class);
  }

  @Test
  public void field_statementExecutor_exists() {
    assertDeclaredField("statementExecutor", VmStatementExecutor.class);
  }

  // ──────────────────────────────────────────────────────────
  //  Varargs verification
  // ──────────────────────────────────────────────────────────

  @Test
  public void ENTRY_POINT_invoke_isVarargs() {
    Method m = findDeclaredMethod("ENTRY_POINT_invoke",
        UserInstance.class, org.lgna.project.ast.AbstractMethod.class, Object[].class);
    assertNotNull(m);
    assertTrue("ENTRY_POINT_invoke should be varargs", m.isVarArgs());
  }

  @Test
  public void ENTRY_POINT_createInstance_isVarargs() {
    Method m = findDeclaredMethod("ENTRY_POINT_createInstance",
        org.lgna.project.ast.NamedUserType.class, Object[].class);
    assertNotNull(m);
    assertTrue("ENTRY_POINT_createInstance should be varargs", m.isVarArgs());
  }

  @Test
  public void invokeUserMethod_isVarargs() {
    Method m = findDeclaredMethod("invokeUserMethod",
        Object.class, org.lgna.project.ast.UserMethod.class, Object[].class);
    assertNotNull(m);
    assertTrue("invokeUserMethod should be varargs", m.isVarArgs());
  }

  @Test
  public void invokeMethodDeclaredInJava_isVarargs() {
    Method m = findDeclaredMethod("invokeMethodDeclaredInJava",
        Object.class, org.lgna.project.ast.JavaMethod.class, Object[].class);
    assertNotNull(m);
    assertTrue("invokeMethodDeclaredInJava should be varargs", m.isVarArgs());
  }

  // ──────────────────────────────────────────────────────────
  //  Method name inventory — detect unexpected additions
  // ──────────────────────────────────────────────────────────

  @Test
  public void allPublicMethodNamesMatchExpectedSet() {
    Set<String> expected = Set.of(
        "getStackTrace",
        "ENTRY_POINT_evaluate",
        "ENTRY_POINT_invoke",
        "ENTRY_POINT_createInstance",
        "createAndSetFieldInstance",
        "ACCEPTABLE_HACK_FOR_SCENE_EDITOR_initializeField",
        "ACCEPTABLE_HACK_FOR_SCENE_EDITOR_executeStatement",
        "registerAbstractClassAdapter",
        "evaluateArguments",
        "get", "set",
        "getItemAtIndex", "setItemAtIndex",
        "invokeUserMethod",
        "invokeMethodDeclaredInJava",
        "stopExecution",
        "addVirtualMachineListener",
        "removeVirtualMachineListener",
        "getVirtualMachineListeners",
        "setForSceneEditor");

    Set<String> actual = Arrays.stream(VM.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toSet());

    assertEquals("Public method names should match exactly", expected, actual);
  }

  @Test
  public void allAbstractMethodNamesMatchExpectedSet() {
    Set<String> expected = Set.of(
        "getStackTrace",
        "getThis",
        "pushBogusFrame",
        "pushConstructorFrame",
        "setConstructorFrameUserInstance",
        "pushMethodFrame",
        "pushLambdaFrame",
        "popFrame",
        "lookup",
        "pushLocal",
        "getLocal",
        "setLocal",
        "popLocal",
        "getFrameForThread",
        "pushCurrentThread",
        "popCurrentThread");

    Set<String> actual = Arrays.stream(VM.getDeclaredMethods())
        .filter(m -> Modifier.isAbstract(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toSet());

    assertEquals("Abstract method names should match exactly", expected, actual);
  }

  // ──────────────────────────────────────────────────────────
  //  Helpers
  // ──────────────────────────────────────────────────────────

  private static void assertPublicMethod(String name, Class<?>[] paramTypes, Class<?> returnType) {
    Method m = findDeclaredMethod(name, paramTypes);
    assertNotNull("Public method " + name + " with params "
        + Arrays.toString(paramTypes) + " must exist on VirtualMachine", m);
    assertTrue("Method " + name + " must be public",
        Modifier.isPublic(m.getModifiers()));
    assertEquals("Return type of " + name, returnType, m.getReturnType());
  }

  private static void assertAbstractMethod(String name, Class<?>[] paramTypes, Class<?> returnType) {
    Method m = findDeclaredMethod(name, paramTypes);
    assertNotNull("Abstract method " + name + " with params "
        + Arrays.toString(paramTypes) + " must exist on VirtualMachine", m);
    assertTrue("Method " + name + " must be abstract",
        Modifier.isAbstract(m.getModifiers()));
    assertEquals("Return type of " + name, returnType, m.getReturnType());
  }

  private static void assertProtectedConcreteMethod(String name, Class<?>[] paramTypes, Class<?> returnType) {
    Method m = findDeclaredMethod(name, paramTypes);
    assertNotNull("Protected concrete method " + name + " with params "
        + Arrays.toString(paramTypes) + " must exist on VirtualMachine", m);
    assertTrue("Method " + name + " must be protected",
        Modifier.isProtected(m.getModifiers()));
    assertFalse("Method " + name + " must not be abstract",
        Modifier.isAbstract(m.getModifiers()));
    assertEquals("Return type of " + name, returnType, m.getReturnType());
  }

  private static Method findDeclaredMethod(String name, Class<?>... paramTypes) {
    try {
      return VM.getDeclaredMethod(name, paramTypes);
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  private static void assertDeclaredField(String name, Class<?> expectedType) {
    try {
      java.lang.reflect.Field f = VM.getDeclaredField(name);
      assertEquals("Field " + name + " type", expectedType, f.getType());
    } catch (NoSuchFieldException e) {
      fail("Field " + name + " must exist on VirtualMachine");
    }
  }
}
