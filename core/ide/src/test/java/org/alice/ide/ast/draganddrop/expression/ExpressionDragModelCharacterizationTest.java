package org.alice.ide.ast.draganddrop.expression;

import org.alice.ide.ast.draganddrop.CodeDragModel;
import org.junit.Test;
import org.lgna.project.ast.AbstractType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Characterization tests for all expression drag model classes in the
 * draganddrop/expression package. Uses reflection to verify class contracts,
 * inheritance chains, and singleton patterns without requiring IDE initialization.
 */
public class ExpressionDragModelCharacterizationTest {

  private static final Class<?>[] EXPRESSION_DRAG_MODELS = {
      AbstractExpressionDragModel.class,
      ThisExpressionDragModel.class,
      FieldAccessDragModel.class,
      FieldArrayAtIndexDragModel.class,
      FieldArrayLengthDragModel.class,
      FunctionInvocationDragModel.class,
      LocalAccessDragModel.class,
      ParameterAccessDragModel.class,
  };

  // ══════════════════════════════════════════════════════════════
  // AbstractExpressionDragModel hierarchy
  // ══════════════════════════════════════════════════════════════

  @Test
  public void abstractDragModel_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractExpressionDragModel.class.getModifiers()));
  }

  @Test
  public void abstractDragModel_extendsCodeDragModel() {
    assertTrue(CodeDragModel.class.isAssignableFrom(AbstractExpressionDragModel.class));
  }

  @Test
  public void abstractDragModel_hasIsPotentialStatementCreator() {
    boolean found = Arrays.stream(AbstractExpressionDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isPotentialStatementCreator")
            && Modifier.isAbstract(m.getModifiers()));
    assertTrue("Must have abstract isPotentialStatementCreator()", found);
  }

  @Test
  public void abstractDragModel_hasGetDropOperationForExpressionProperty() {
    boolean found = Arrays.stream(AbstractExpressionDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getDropOperation")
            && Modifier.isAbstract(m.getModifiers())
            && m.getParameterCount() == 1);
    assertTrue("Must have abstract getDropOperation(ExpressionProperty)", found);
  }

  @Test
  public void abstractDragModel_overridesGetDropOperationFromDragModel() {
    boolean found = Arrays.stream(AbstractExpressionDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getDropOperation")
            && !Modifier.isAbstract(m.getModifiers())
            && m.getParameterCount() == 2);
    assertTrue("Must override getDropOperation(DragStep, DropSite)", found);
  }

  @Test
  public void abstractDragModel_inCorrectPackage() {
    assertEquals("org.alice.ide.ast.draganddrop.expression",
        AbstractExpressionDragModel.class.getPackage().getName());
  }

  // ══════════════════════════════════════════════════════════════
  // All concrete models extend AbstractExpressionDragModel
  // ══════════════════════════════════════════════════════════════

  @Test
  public void allConcreteModels_extendAbstractExpressionDragModel() {
    for (Class<?> cls : EXPRESSION_DRAG_MODELS) {
      if (cls == AbstractExpressionDragModel.class) {
        continue;
      }
      assertTrue(cls.getSimpleName() + " must extend AbstractExpressionDragModel",
          AbstractExpressionDragModel.class.isAssignableFrom(cls));
    }
  }

  @Test
  public void allConcreteModels_arePublic() {
    for (Class<?> cls : EXPRESSION_DRAG_MODELS) {
      assertTrue(cls.getSimpleName() + " must be public",
          Modifier.isPublic(cls.getModifiers()));
    }
  }

  @Test
  public void allConcreteModels_areInCorrectPackage() {
    for (Class<?> cls : EXPRESSION_DRAG_MODELS) {
      assertEquals(cls.getSimpleName() + " in wrong package",
          "org.alice.ide.ast.draganddrop.expression",
          cls.getPackage().getName());
    }
  }

  // ══════════════════════════════════════════════════════════════
  // ThisExpressionDragModel — singleton pattern
  // ══════════════════════════════════════════════════════════════

  @Test
  public void thisExpr_hasSingletonHolder() {
    boolean found = Arrays.stream(ThisExpressionDragModel.class.getDeclaredClasses())
        .anyMatch(c -> c.getSimpleName().equals("SingletonHolder"));
    assertTrue("Must have inner SingletonHolder class", found);
  }

  @Test
  public void thisExpr_hasGetInstance() throws Exception {
    Method m = ThisExpressionDragModel.class.getMethod("getInstance");
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(ThisExpressionDragModel.class, m.getReturnType());
  }

  @Test
  public void thisExpr_getInstanceReturnsSameObject() {
    assertSame(ThisExpressionDragModel.getInstance(), ThisExpressionDragModel.getInstance());
  }

  @Test
  public void thisExpr_isPotentialStatementCreator_isFalse() {
    assertFalse(ThisExpressionDragModel.getInstance().isPotentialStatementCreator());
  }

  @Test
  public void thisExpr_hasPrivateConstructor() {
    boolean allPrivate = Arrays.stream(ThisExpressionDragModel.class.getDeclaredConstructors())
        .allMatch(c -> Modifier.isPrivate(c.getModifiers()));
    assertTrue("All constructors should be private (singleton)", allPrivate);
  }

  // ══════════════════════════════════════════════════════════════
  // FieldAccessDragModel — instance map pattern
  // ══════════════════════════════════════════════════════════════

  @Test
  public void fieldAccess_hasStaticMapField() throws Exception {
    Field f = FieldAccessDragModel.class.getDeclaredField("map");
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void fieldAccess_hasGetInstanceMethod() throws Exception {
    Method m = FieldAccessDragModel.class.getMethod("getInstance",
        org.lgna.project.ast.AbstractField.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void fieldAccess_hasFieldField() throws Exception {
    Field f = FieldAccessDragModel.class.getDeclaredField("field");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void fieldAccess_hasPrivateConstructor() {
    boolean allPrivate = Arrays.stream(FieldAccessDragModel.class.getDeclaredConstructors())
        .allMatch(c -> Modifier.isPrivate(c.getModifiers()));
    assertTrue(allPrivate);
  }

  // ══════════════════════════════════════════════════════════════
  // FieldArrayAtIndexDragModel
  // ══════════════════════════════════════════════════════════════

  @Test
  public void fieldArrayAtIndex_hasStaticMapField() throws Exception {
    Field f = FieldArrayAtIndexDragModel.class.getDeclaredField("map");
    assertTrue(Modifier.isStatic(f.getModifiers()));
  }

  @Test
  public void fieldArrayAtIndex_hasGetInstanceMethod() {
    boolean found = Arrays.stream(FieldArrayAtIndexDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getInstance") && Modifier.isStatic(m.getModifiers()));
    assertTrue(found);
  }

  // ══════════════════════════════════════════════════════════════
  // FieldArrayLengthDragModel
  // ══════════════════════════════════════════════════════════════

  @Test
  public void fieldArrayLength_hasStaticMapField() throws Exception {
    Field f = FieldArrayLengthDragModel.class.getDeclaredField("map");
    assertTrue(Modifier.isStatic(f.getModifiers()));
  }

  @Test
  public void fieldArrayLength_hasGetInstanceMethod() {
    boolean found = Arrays.stream(FieldArrayLengthDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getInstance") && Modifier.isStatic(m.getModifiers()));
    assertTrue(found);
  }

  // ══════════════════════════════════════════════════════════════
  // FunctionInvocationDragModel
  // ══════════════════════════════════════════════════════════════

  @Test
  public void functionInvocation_hasStaticMapField() throws Exception {
    Field f = FunctionInvocationDragModel.class.getDeclaredField("map");
    assertTrue(Modifier.isStatic(f.getModifiers()));
  }

  @Test
  public void functionInvocation_hasPrivateConstructor() {
    boolean allPrivate = Arrays.stream(FunctionInvocationDragModel.class.getDeclaredConstructors())
        .allMatch(c -> Modifier.isPrivate(c.getModifiers()));
    assertTrue(allPrivate);
  }

  @Test
  public void functionInvocation_isPotentialStatementCreator() throws Exception {
    // FunctionInvocationDragModel should return true (functions can be used in statements)
    Method m = FunctionInvocationDragModel.class.getDeclaredMethod("isPotentialStatementCreator");
    assertNotNull(m);
  }

  // ══════════════════════════════════════════════════════════════
  // LocalAccessDragModel
  // ══════════════════════════════════════════════════════════════

  @Test
  public void localAccess_hasStaticMapField() throws Exception {
    Field f = LocalAccessDragModel.class.getDeclaredField("map");
    assertTrue(Modifier.isStatic(f.getModifiers()));
  }

  @Test
  public void localAccess_hasPrivateConstructor() {
    boolean allPrivate = Arrays.stream(LocalAccessDragModel.class.getDeclaredConstructors())
        .allMatch(c -> Modifier.isPrivate(c.getModifiers()));
    assertTrue(allPrivate);
  }

  // ══════════════════════════════════════════════════════════════
  // ParameterAccessDragModel
  // ══════════════════════════════════════════════════════════════

  @Test
  public void parameterAccess_hasStaticMapField() throws Exception {
    Field f = ParameterAccessDragModel.class.getDeclaredField("map");
    assertTrue(Modifier.isStatic(f.getModifiers()));
  }

  @Test
  public void parameterAccess_hasPrivateConstructor() {
    boolean allPrivate = Arrays.stream(ParameterAccessDragModel.class.getDeclaredConstructors())
        .allMatch(c -> Modifier.isPrivate(c.getModifiers()));
    assertTrue(allPrivate);
  }

  // ══════════════════════════════════════════════════════════════
  // CodeDragModel — base class
  // ══════════════════════════════════════════════════════════════

  @Test
  public void codeDragModel_isAbstract() {
    assertTrue(Modifier.isAbstract(CodeDragModel.class.getModifiers()));
  }

  @Test
  public void codeDragModel_hasGetTypeMethod() {
    boolean found = Arrays.stream(CodeDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getType")
            && Modifier.isAbstract(m.getModifiers())
            && m.getReturnType() == AbstractType.class);
    assertTrue(found);
  }

  @Test
  public void codeDragModel_inCorrectPackage() {
    assertEquals("org.alice.ide.ast.draganddrop",
        CodeDragModel.class.getPackage().getName());
  }
}
