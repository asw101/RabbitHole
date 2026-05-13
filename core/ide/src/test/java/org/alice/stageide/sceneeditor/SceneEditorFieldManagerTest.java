package org.alice.stageide.sceneeditor;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.project.ast.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link SceneEditorFieldManager} — the field-management delegate
 * extracted from StorytellingSceneEditor (issue #528 step 2).
 *
 * Structure:
 *   1. Reflection contract tests — class shape, visibility, method signatures
 *   2. Unit tests for asSetVehicleCall — the only static, dependency-free method
 */
public class SceneEditorFieldManagerTest {

  private static final String FQCN = "org.alice.stageide.sceneeditor.SceneEditorFieldManager";
  private static Class<?> clazz;

  @BeforeClass
  public static void loadClass() throws Exception {
    clazz = Class.forName(FQCN);
  }

  // ── 1. Contract: Class structure ──────────────────────────────────

  @Test
  public void isPackagePrivate() {
    int mods = clazz.getModifiers();
    assertFalse("must not be public", Modifier.isPublic(mods));
    assertFalse("must not be private", Modifier.isPrivate(mods));
    assertFalse("must not be protected", Modifier.isProtected(mods));
  }

  @Test
  public void isNotFinal() {
    assertFalse("should not be final (plain delegate class)", Modifier.isFinal(clazz.getModifiers()));
  }

  @Test
  public void hasConstructorTakingAbstractSceneEditor() throws Exception {
    Class<?> editorType = Class.forName("org.alice.ide.sceneeditor.AbstractSceneEditor");
    Constructor<?> ctor = clazz.getDeclaredConstructor(editorType);
    assertNotNull("constructor(AbstractSceneEditor) must exist", ctor);
    assertFalse("constructor must not be public (package-private delegate)", Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void hasEditorField() throws Exception {
    java.lang.reflect.Field f = clazz.getDeclaredField("editor");
    assertTrue("editor field must be final", Modifier.isFinal(f.getModifiers()));
    assertTrue("editor field must be private", Modifier.isPrivate(f.getModifiers()));
  }

  // ── 2. Contract: asSetVehicleCall signature ───────────────────────

  @Test
  public void asSetVehicleCall_isStatic() throws Exception {
    Method m = clazz.getDeclaredMethod("asSetVehicleCall", Statement.class);
    assertTrue("asSetVehicleCall must be static", Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void asSetVehicleCall_returnsMethodInvocation() throws Exception {
    Method m = clazz.getDeclaredMethod("asSetVehicleCall", Statement.class);
    assertEquals("return type must be MethodInvocation", MethodInvocation.class, m.getReturnType());
  }

  @Test
  public void asSetVehicleCall_isPackagePrivateAccess() throws Exception {
    Method m = clazz.getDeclaredMethod("asSetVehicleCall", Statement.class);
    int mods = m.getModifiers();
    assertFalse("must not be public", Modifier.isPublic(mods));
    assertFalse("must not be private", Modifier.isPrivate(mods));
    assertFalse("must not be protected", Modifier.isProtected(mods));
  }

  // ── 3. Contract: Delegate method signatures ───────────────────────

  @Test
  public void hasGetDoStatementsForCopyField() throws Exception {
    Method m = clazz.getDeclaredMethod("getDoStatementsForCopyField",
        UserField.class, UserField.class,
        org.alice.math.immutable.AffineMatrix4x4.class, Statement.class);
    assertNotNull(m);
    assertEquals("must return Statement[]", Statement[].class, m.getReturnType());
  }

  @Test
  public void hasGetDoStatementsForAddField() throws Exception {
    Method m = clazz.getDeclaredMethod("getDoStatementsForAddField",
        UserField.class, org.alice.math.immutable.AffineMatrix4x4.class);
    assertNotNull(m);
    assertEquals("must return Statement[]", Statement[].class, m.getReturnType());
  }

  @Test
  public void hasGetUndoStatementsForAddField() throws Exception {
    Method m = clazz.getDeclaredMethod("getUndoStatementsForAddField", UserField.class);
    assertNotNull(m);
    assertEquals("must return Statement[]", Statement[].class, m.getReturnType());
  }

  @Test
  public void hasGetRiders() throws Exception {
    Method m = clazz.getDeclaredMethod("getRiders", UserField.class);
    assertNotNull(m);
    assertEquals("must return Map", Map.class, m.getReturnType());
  }

  @Test
  public void hasGetDoStatementsForRemoveField() throws Exception {
    Method m = clazz.getDeclaredMethod("getDoStatementsForRemoveField", UserField.class, Map.class);
    assertNotNull(m);
    assertEquals("must return Statement[]", Statement[].class, m.getReturnType());
  }

  @Test
  public void hasGetUndoStatementsForRemoveField() throws Exception {
    Method m = clazz.getDeclaredMethod("getUndoStatementsForRemoveField", UserField.class, Map.class);
    assertNotNull(m);
    assertEquals("must return Statement[]", Statement[].class, m.getReturnType());
  }

  // ── 4. Contract: Private helper methods exist ─────────────────────

  @Test
  public void hasReplaceReferencesInExpression() throws Exception {
    Method m = clazz.getDeclaredMethod("replaceReferencesInExpression",
        UserField.class, UserField.class, Statement.class);
    assertTrue("must be private", Modifier.isPrivate(m.getModifiers()));
  }

  @Test
  public void hasDoesSetVehicleImplyVehicle() throws Exception {
    Method m = clazz.getDeclaredMethod("doesSetVehicleImplyVehicle",
        MethodInvocation.class, UserField.class);
    assertTrue("must be private", Modifier.isPrivate(m.getModifiers()));
  }

  @Test
  public void hasIsDirectRider() throws Exception {
    Method m = clazz.getDeclaredMethod("isDirectRider", UserField.class, Expression.class);
    assertTrue("must be private", Modifier.isPrivate(m.getModifiers()));
  }

  @Test
  public void hasIsJointRider() throws Exception {
    Method m = clazz.getDeclaredMethod("isJointRider", UserField.class, Expression.class);
    assertTrue("must be private", Modifier.isPrivate(m.getModifiers()));
  }

  // ── 5. Unit: asSetVehicleCall with setVehicle ExpressionStatement ─

  @Test
  public void asSetVehicleCall_withSetVehicleStatement_returnsInvocation() {
    MethodInvocation mi = buildMethodInvocation("setVehicle");
    ExpressionStatement stmt = new ExpressionStatement();
    stmt.expression.setValue(mi);

    MethodInvocation result = SceneEditorFieldManager.asSetVehicleCall(stmt);
    assertSame("must return the same MethodInvocation", mi, result);
  }

  @Test
  public void asSetVehicleCall_caseInsensitive_uppercase() {
    MethodInvocation mi = buildMethodInvocation("SETVEHICLE");
    ExpressionStatement stmt = new ExpressionStatement();
    stmt.expression.setValue(mi);

    assertSame("setVehicle matching is case-insensitive", mi,
        SceneEditorFieldManager.asSetVehicleCall(stmt));
  }

  @Test
  public void asSetVehicleCall_caseInsensitive_mixedCase() {
    MethodInvocation mi = buildMethodInvocation("SetVehicle");
    ExpressionStatement stmt = new ExpressionStatement();
    stmt.expression.setValue(mi);

    assertSame("SetVehicle should match", mi,
        SceneEditorFieldManager.asSetVehicleCall(stmt));
  }

  // ── 6. Unit: asSetVehicleCall null-returning branches ─────────────

  @Test
  public void asSetVehicleCall_nonSetVehicleMethod_returnsNull() {
    MethodInvocation mi = buildMethodInvocation("moveForward");
    ExpressionStatement stmt = new ExpressionStatement();
    stmt.expression.setValue(mi);

    assertNull("non-setVehicle method → null", SceneEditorFieldManager.asSetVehicleCall(stmt));
  }

  @Test
  public void asSetVehicleCall_blockStatement_returnsNull() {
    assertNull("BlockStatement → null", SceneEditorFieldManager.asSetVehicleCall(new BlockStatement()));
  }

  @Test
  public void asSetVehicleCall_doTogetherStatement_returnsNull() {
    assertNull("DoTogether → null", SceneEditorFieldManager.asSetVehicleCall(new DoTogether()));
  }

  @Test
  public void asSetVehicleCall_doInOrderStatement_returnsNull() {
    assertNull("DoInOrder → null", SceneEditorFieldManager.asSetVehicleCall(new DoInOrder()));
  }

  @Test
  public void asSetVehicleCall_expressionStatement_withFieldAccess_returnsNull() {
    UserField field = new UserField();
    FieldAccess fa = new FieldAccess();
    fa.field.setValue(field);

    ExpressionStatement stmt = new ExpressionStatement();
    stmt.expression.setValue(fa);

    assertNull("ExpressionStatement(FieldAccess) → null", SceneEditorFieldManager.asSetVehicleCall(stmt));
  }

  @Test
  public void asSetVehicleCall_expressionStatement_withNullExpression_returnsNull() {
    ExpressionStatement stmt = new ExpressionStatement();
    // expression property is unset (null value)
    assertNull("empty ExpressionStatement → null", SceneEditorFieldManager.asSetVehicleCall(stmt));
  }

  @Test
  public void asSetVehicleCall_emptyMethodName_returnsNull() {
    MethodInvocation mi = buildMethodInvocation("");
    ExpressionStatement stmt = new ExpressionStatement();
    stmt.expression.setValue(mi);

    assertNull("empty method name → null", SceneEditorFieldManager.asSetVehicleCall(stmt));
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private static MethodInvocation buildMethodInvocation(String methodName) {
    UserMethod method = new UserMethod();
    method.name.setValue(methodName);

    MethodInvocation mi = new MethodInvocation();
    mi.method.setValue(method);
    return mi;
  }
}
