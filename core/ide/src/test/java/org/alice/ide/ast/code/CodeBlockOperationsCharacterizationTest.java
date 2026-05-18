package org.alice.ide.ast.code;

import org.alice.ide.ast.ReflectionTestHelper;
import org.alice.ide.ast.code.edits.EnvelopStatementsEdit;
import org.alice.ide.ast.code.edits.MoveStatementEdit;
import org.alice.ide.ast.code.edits.SwapParametersEdit;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.junit.Test;
import org.lgna.croquet.ActionOperation;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Characterization tests for code block operations and their edit classes.
 * Uses reflection for classes requiring IDE singleton (SwapParametersEdit,
 * MoveStatementEdit, BackwardShiftParameterOperation, ForwardShiftParameterOperation).
 * Uses behavioral tests for EnvelopStatementsOperation (proven headless).
 *
 * Complements existing EnvelopStatementsOperationTest/ExtendedTest.
 */
public class CodeBlockOperationsCharacterizationTest {

  // ══════════════════════════════════════════════════════════════
  // EnvelopStatementsOperation — behavioral (headless-safe)
  // ══════════════════════════════════════════════════════════════

  @Test
  public void envelopOp_isFinalClass() {
    assertTrue("EnvelopStatementsOperation must be final",
        Modifier.isFinal(EnvelopStatementsOperation.class.getModifiers()));
  }

  @Test
  public void envelopOp_extendsActionOperation() {
    assertTrue("EnvelopStatementsOperation must extend ActionOperation",
        ActionOperation.class.isAssignableFrom(EnvelopStatementsOperation.class));
  }

  @Test
  public void envelopOp_getInstanceIsSynchronizedStatic() throws Exception {
    Method getInstance = EnvelopStatementsOperation.class.getMethod(
        "getInstance", BlockStatementIndexPair.class, BlockStatementIndexPair.class);
    assertTrue("getInstance must be synchronized",
        Modifier.isSynchronized(getInstance.getModifiers()));
    assertTrue("getInstance must be static",
        Modifier.isStatic(getInstance.getModifiers()));
  }

  @Test
  public void envelopOp_hasNoPublicConstructor() {
    for (Constructor<?> c : EnvelopStatementsOperation.class.getDeclaredConstructors()) {
      assertFalse("Constructor must not be public",
          Modifier.isPublic(c.getModifiers()));
    }
  }

  @Test
  public void envelopOp_instanceCaching_threadSafe() throws Exception {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 1);

    final int THREAD_COUNT = 4;
    final EnvelopStatementsOperation[] results = new EnvelopStatementsOperation[THREAD_COUNT];
    java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
    java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(THREAD_COUNT);

    for (int i = 0; i < THREAD_COUNT; i++) {
      final int idx = i;
      executor.submit(() -> {
        try {
          latch.await();
          results[idx] = EnvelopStatementsOperation.getInstance(from, to);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
    }

    latch.countDown();
    executor.shutdown();
    assertTrue("Threads should finish within 5 seconds",
        executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS));

    for (int i = 1; i < THREAD_COUNT; i++) {
      assertSame("All threads should get same instance", results[0], results[i]);
    }
  }

  @Test
  public void envelopOp_hasMapToMapField() throws Exception {
    Field mapField = EnvelopStatementsOperation.class.getDeclaredField("map");
    assertTrue("map field should be static", Modifier.isStatic(mapField.getModifiers()));
    assertTrue("map field should be private", Modifier.isPrivate(mapField.getModifiers()));
  }

  // ══════════════════════════════════════════════════════════════
  // MoveStatementOperation — reflection (needs IDE for perform)
  // ══════════════════════════════════════════════════════════════

  @Test
  public void moveStatementOp_isFinalClass() {
    assertTrue("MoveStatementOperation must be final",
        Modifier.isFinal(MoveStatementOperation.class.getModifiers()));
  }

  @Test
  public void moveStatementOp_extendsActionOperation() {
    assertTrue("Must extend ActionOperation",
        ActionOperation.class.isAssignableFrom(MoveStatementOperation.class));
  }

  @Test
  public void moveStatementOp_hasPublicConstructor() {
    boolean found = Arrays.stream(MoveStatementOperation.class.getDeclaredConstructors())
        .anyMatch(c -> Modifier.isPublic(c.getModifiers()));
    assertTrue("MoveStatementOperation must have a public constructor", found);
  }

  @Test
  public void moveStatementOp_constructorTakesFourParams() {
    boolean found = Arrays.stream(MoveStatementOperation.class.getDeclaredConstructors())
        .anyMatch(c -> {
          Class<?>[] params = c.getParameterTypes();
          return params.length == 4
              && BlockStatementIndexPair.class.isAssignableFrom(params[0])
              && Statement.class.isAssignableFrom(params[1])
              && BlockStatementIndexPair.class.isAssignableFrom(params[2])
              && (params[3] == boolean.class);
        });
    assertTrue("Constructor must take (BlockStatementIndexPair, Statement, BlockStatementIndexPair, boolean)", found);
  }

  @Test
  public void moveStatementOp_hasFromLocationField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(MoveStatementOperation.class, "fromLocation");
  }

  @Test
  public void moveStatementOp_hasToLocationField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(MoveStatementOperation.class, "toLocation");
  }

  @Test
  public void moveStatementOp_hasStatementField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(MoveStatementOperation.class, "statement");
  }

  @Test
  public void moveStatementOp_hasIsMultipleField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(MoveStatementOperation.class, "isMultiple");
  }

  // ══════════════════════════════════════════════════════════════
  // SwapParametersOperation — reflection (abstract, needs IDE)
  // ══════════════════════════════════════════════════════════════

  @Test
  public void swapParamsOp_isAbstract() {
    assertTrue("SwapParametersOperation must be abstract",
        Modifier.isAbstract(SwapParametersOperation.class.getModifiers()));
  }

  @Test
  public void swapParamsOp_hasIsAppropriateAbstractMethod() {
    boolean found = Arrays.stream(SwapParametersOperation.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isAppropriate")
            && Modifier.isAbstract(m.getModifiers())
            && m.getParameterCount() == 2);
    assertTrue("Must have abstract isAppropriate(int, int)", found);
  }

  @Test
  public void swapParamsOp_hasGetIndexAAbstractMethod() {
    boolean found = Arrays.stream(SwapParametersOperation.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getIndexA")
            && Modifier.isAbstract(m.getModifiers())
            && m.getParameterCount() == 0);
    assertTrue("Must have abstract getIndexA()", found);
  }

  @Test
  public void swapParamsOp_hasIsIndexAppropriateMethod() {
    boolean found = Arrays.stream(SwapParametersOperation.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isIndexAppropriate")
            && !Modifier.isAbstract(m.getModifiers()));
    assertTrue("Must have concrete isIndexAppropriate()", found);
  }

  // ══════════════════════════════════════════════════════════════
  // BackwardShiftParameterOperation — reflection
  // ══════════════════════════════════════════════════════════════

  @Test
  public void backwardShiftOp_extendsSwapParametersOperation() {
    assertTrue("BackwardShiftParameterOperation must extend SwapParametersOperation",
        SwapParametersOperation.class.isAssignableFrom(BackwardShiftParameterOperation.class));
  }

  @Test
  public void backwardShiftOp_isConcreteClass() {
    assertFalse("BackwardShiftParameterOperation must not be abstract",
        Modifier.isAbstract(BackwardShiftParameterOperation.class.getModifiers()));
  }

  @Test
  public void backwardShiftOp_overridesIsAppropriate() {
    boolean found = Arrays.stream(BackwardShiftParameterOperation.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isAppropriate")
            && m.getParameterCount() == 2
            && !Modifier.isAbstract(m.getModifiers()));
    assertTrue("Must override isAppropriate", found);
  }

  @Test
  public void backwardShiftOp_overridesGetIndexA() {
    boolean found = Arrays.stream(BackwardShiftParameterOperation.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getIndexA")
            && m.getParameterCount() == 0
            && !Modifier.isAbstract(m.getModifiers()));
    assertTrue("Must override getIndexA", found);
  }

  @Test
  public void backwardShiftOp_constructorTakesParametersPropertyAndParameter() {
    boolean found = Arrays.stream(BackwardShiftParameterOperation.class.getDeclaredConstructors())
        .anyMatch(c -> {
          Class<?>[] params = c.getParameterTypes();
          return params.length == 2
              && NodeListProperty.class.isAssignableFrom(params[0])
              && UserParameter.class.isAssignableFrom(params[1]);
        });
    assertTrue("Constructor must take (NodeListProperty<UserParameter>, UserParameter)", found);
  }

  // ══════════════════════════════════════════════════════════════
  // ForwardShiftParameterOperation — reflection
  // ══════════════════════════════════════════════════════════════

  @Test
  public void forwardShiftOp_extendsSwapParametersOperation() {
    assertTrue("ForwardShiftParameterOperation must extend SwapParametersOperation",
        SwapParametersOperation.class.isAssignableFrom(ForwardShiftParameterOperation.class));
  }

  @Test
  public void forwardShiftOp_isConcreteClass() {
    assertFalse("ForwardShiftParameterOperation must not be abstract",
        Modifier.isAbstract(ForwardShiftParameterOperation.class.getModifiers()));
  }

  @Test
  public void forwardShiftOp_overridesIsAppropriate() {
    boolean found = Arrays.stream(ForwardShiftParameterOperation.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isAppropriate")
            && m.getParameterCount() == 2);
    assertTrue("Must override isAppropriate", found);
  }

  @Test
  public void forwardShiftOp_overridesGetIndexA() {
    boolean found = Arrays.stream(ForwardShiftParameterOperation.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getIndexA")
            && m.getParameterCount() == 0);
    assertTrue("Must override getIndexA", found);
  }

  // ══════════════════════════════════════════════════════════════
  // SwapParametersEdit — reflection (uses IDE.getActiveInstance)
  // ══════════════════════════════════════════════════════════════

  @Test
  public void swapParamsEdit_extendsAbstractEdit() {
    assertTrue("SwapParametersEdit must extend AbstractEdit",
        AbstractEdit.class.isAssignableFrom(SwapParametersEdit.class));
  }

  @Test
  public void swapParamsEdit_hasMethodField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(SwapParametersEdit.class, "method");
  }

  @Test
  public void swapParamsEdit_methodFieldIsUserMethod() throws Exception {
    assertEquals("method must be UserMethod type", UserMethod.class,
        SwapParametersEdit.class.getDeclaredField("method").getType());
  }

  @Test
  public void swapParamsEdit_hasAIndexField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(SwapParametersEdit.class, "aIndex");
  }

  @Test
  public void swapParamsEdit_hasDoOrRedoInternalMethod() {
    boolean found = Arrays.stream(SwapParametersEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("doOrRedoInternal")
            && Modifier.isFinal(m.getModifiers()));
    assertTrue("Must have final doOrRedoInternal", found);
  }

  @Test
  public void swapParamsEdit_hasUndoInternalMethod() {
    boolean found = Arrays.stream(SwapParametersEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("undoInternal")
            && Modifier.isFinal(m.getModifiers()));
    assertTrue("Must have final undoInternal", found);
  }

  @Test
  public void swapParamsEdit_hasAppendDescriptionMethod() {
    boolean found = Arrays.stream(SwapParametersEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("appendDescription"));
    assertTrue("Must have appendDescription", found);
  }

  @Test
  public void swapParamsEdit_hasEncodeMethod() {
    boolean found = Arrays.stream(SwapParametersEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("encode") && m.getParameterCount() == 1);
    assertTrue("Must have encode method", found);
  }

  @Test
  public void swapParamsEdit_hasBinaryDecoderConstructor() {
    boolean found = Arrays.stream(SwapParametersEdit.class.getDeclaredConstructors())
        .anyMatch(c -> c.getParameterCount() == 2
            && c.getParameterTypes()[0].getSimpleName().equals("BinaryDecoder"));
    assertTrue("Must have BinaryDecoder constructor", found);
  }

  // ══════════════════════════════════════════════════════════════
  // MoveStatementEdit — reflection
  // ══════════════════════════════════════════════════════════════

  @Test
  public void moveStatementEdit_hasFromLocationField() {
    ReflectionTestHelper.assertFieldExists(MoveStatementEdit.class, "fromLocation");
  }

  @Test
  public void moveStatementEdit_hasToLocationField() {
    ReflectionTestHelper.assertFieldExists(MoveStatementEdit.class, "toLocation");
  }

  @Test
  public void moveStatementEdit_hasIsMultipleField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(MoveStatementEdit.class, "isMultiple");
  }

  @Test
  public void moveStatementEdit_hasCountField() {
    ReflectionTestHelper.assertFieldExists(MoveStatementEdit.class, "count");
  }

  @Test
  public void moveStatementEdit_hasDoOrRedoInternalMethod() {
    boolean found = Arrays.stream(MoveStatementEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("doOrRedoInternal"));
    assertTrue("Must have doOrRedoInternal", found);
  }

  @Test
  public void moveStatementEdit_hasUndoInternalMethod() {
    boolean found = Arrays.stream(MoveStatementEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("undoInternal"));
    assertTrue("Must have undoInternal", found);
  }

  @Test
  public void moveStatementEdit_hasAppendDescriptionMethod() {
    boolean found = Arrays.stream(MoveStatementEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("appendDescription"));
    assertTrue("Must have appendDescription", found);
  }

  @Test
  public void moveStatementEdit_primaryConstructorParams() {
    boolean found = Arrays.stream(MoveStatementEdit.class.getDeclaredConstructors())
        .anyMatch(c -> {
          Class<?>[] params = c.getParameterTypes();
          return params.length == 5
              && params[1] == BlockStatementIndexPair.class
              && Statement.class.isAssignableFrom(params[2])
              && params[3] == BlockStatementIndexPair.class
              && params[4] == boolean.class;
        });
    assertTrue("Primary constructor must take (UserActivity, BlockStatementIndexPair, Statement, BlockStatementIndexPair, boolean)", found);
  }

  // ══════════════════════════════════════════════════════════════
  // EnvelopStatementsEdit — reflection
  // ══════════════════════════════════════════════════════════════

  @Test
  public void envelopStatementsEdit_extendsAbstractEdit() {
    assertTrue("EnvelopStatementsEdit must extend AbstractEdit",
        AbstractEdit.class.isAssignableFrom(EnvelopStatementsEdit.class));
  }

  @Test
  public void envelopStatementsEdit_hasLocationFields() throws Exception {
    Field from = EnvelopStatementsEdit.class.getDeclaredField("fromLocation");
    Field to = EnvelopStatementsEdit.class.getDeclaredField("toLocation");
    assertNotNull("Must have fromLocation field", from);
    assertNotNull("Must have toLocation field", to);
  }

  @Test
  public void envelopStatementsEdit_hasCountField() throws Exception {
    Field f = EnvelopStatementsEdit.class.getDeclaredField("count");
    assertNotNull("Must have count field", f);
  }

  @Test
  public void envelopStatementsEdit_doOrRedoAndUndo_methodsExist() {
    Set<String> methods = Arrays.stream(EnvelopStatementsEdit.class.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertTrue("Must have doOrRedoInternal", methods.contains("doOrRedoInternal"));
    assertTrue("Must have undoInternal", methods.contains("undoInternal"));
    assertTrue("Must have appendDescription", methods.contains("appendDescription"));
  }
}
