package org.alice.ide.cascade;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.lgna.croquet.CascadeMenuModel;
import org.lgna.project.ast.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

/**
 * Additional tests for {@link ExpressionCascadeManager} focusing on context stack
 * edge cases, accessible locals in complex statement hierarchies, and type resolution.
 */
public class ExpressionCascadeContextStackTest {

  private TestableManager manager;

  private static class TestableManager extends ExpressionCascadeManager {
    private boolean partFillInApplicable = false;

    @Override
    protected boolean isApplicableForPartFillIn(AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType) {
      return partFillInApplicable;
    }

    @Override
    protected CascadeMenuModel<Expression> createPartMenuModel(Expression expression, AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType, boolean isOwnedByCascadeItemMenuCombo) {
      return null;
    }

    void setPartFillInApplicable(boolean applicable) {
      this.partFillInApplicable = applicable;
    }

    boolean testIsApplicableForFillIn(AbstractType<?, ?, ?> desired, AbstractType<?, ?, ?> expression) {
      return isApplicableForFillIn(desired, expression);
    }

    boolean testIsApplicableForFillInAndPossiblyPartFillIns(AbstractType<?, ?, ?> desired, AbstractType<?, ?, ?> expression) {
      return isApplicableForFillInAndPossiblyPartFillIns(desired, expression);
    }
  }

  @Before
  public void setUp() {
    manager = new TestableManager();
  }

  private static <T> List<T> toList(Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false)
        .collect(Collectors.toList());
  }

  // ---- Deep context stack operations ----

  @Test
  public void contextStack_deepNesting_preservesOrder() {
    ExpressionCascadeContext[] ctxs = new ExpressionCascadeContext[10];
    for (int i = 0; i < ctxs.length; i++) {
      int idx = i;
      ctxs[i] = new ExpressionCascadeContext() {
        @Override
        public Expression getPreviousExpression() {
          return new IntegerLiteral(idx);
        }
        @Override
        public BlockStatementIndexPair getBlockStatementIndexPair() {
          return null;
        }
      };
      manager.pushContext(ctxs[i]);
    }
    for (int i = ctxs.length - 1; i >= 0; i--) {
      ExpressionCascadeContext popped = manager.popContext();
      assertSame("LIFO order at " + i, ctxs[i], popped);
    }
  }

  @Test
  public void pushContext_afterPop_worksCorrectly() {
    ExpressionCascadeContext ctx1 = makeContext(new NullLiteral());
    ExpressionCascadeContext ctx2 = makeContext(new DoubleLiteral(1.0));

    manager.pushContext(ctx1);
    manager.popContext();
    manager.pushContext(ctx2);
    assertSame(ctx2, manager.popContext());
  }

  @Test
  public void getPreviousExpression_peeksWithoutPopping() {
    ExpressionCascadeContext ctx = makeContext(new StringLiteral("peek"));
    manager.pushContext(ctx);
    // Call twice — it should peek, not pop
    assertSame(ctx.getPreviousExpression(), manager.getPreviousExpression());
    assertSame(ctx.getPreviousExpression(), manager.getPreviousExpression());
    // Pop should still return our context
    assertSame(ctx, manager.popContext());
  }

  @Test
  public void popContext_multipleEmptyPops_allReturnNullContext() {
    for (int i = 0; i < 5; i++) {
      ExpressionCascadeContext ctx = manager.popContext();
      assertNotNull(ctx);
      assertNull(ctx.getPreviousExpression());
      assertNull(ctx.getBlockStatementIndexPair());
    }
  }

  // ---- isApplicableForFillIn type checks ----

  @Test
  public void isApplicableForFillIn_objectDesired_integerExpression_returnsTrue() {
    assertTrue(manager.testIsApplicableForFillIn(JavaType.OBJECT_TYPE, JavaType.INTEGER_OBJECT_TYPE));
  }

  @Test
  public void isApplicableForFillIn_integerDesired_objectExpression_returnsFalse() {
    assertFalse(manager.testIsApplicableForFillIn(JavaType.INTEGER_OBJECT_TYPE, JavaType.OBJECT_TYPE));
  }

  @Test
  public void isApplicableForFillIn_booleanDesired_booleanExpression_returnsTrue() {
    assertTrue(manager.testIsApplicableForFillIn(JavaType.BOOLEAN_OBJECT_TYPE, JavaType.BOOLEAN_OBJECT_TYPE));
  }

  @Test
  public void isApplicableForFillInAndPossiblyPart_withPartEnabled_returnsTrue() {
    manager.setPartFillInApplicable(true);
    // Even if fill-in itself fails (Integer not assignable from String),
    // part fill-in returns true
    assertTrue(manager.testIsApplicableForFillInAndPossiblyPartFillIns(
        JavaType.INTEGER_OBJECT_TYPE, JavaType.STRING_TYPE));
  }

  @Test
  public void isApplicableForFillInAndPossiblyPart_bothFalse_returnsFalse() {
    manager.setPartFillInApplicable(false);
    assertFalse(manager.testIsApplicableForFillInAndPossiblyPartFillIns(
        JavaType.INTEGER_OBJECT_TYPE, JavaType.STRING_TYPE));
  }

  // ---- Accessible locals in nested loops ----

  @Test
  public void getAccessibleLocals_eachInTogetherLoop_exposesItem() {
    UserLocal itemLocal = new UserLocal("togetherItem", JavaType.STRING_TYPE, true);
    BlockStatement innerBlock = new BlockStatement();
    EachInArrayTogether eachInTogether = new EachInArrayTogether(
        itemLocal, new NullLiteral(), innerBlock);

    BlockStatementIndexPair pair = new BlockStatementIndexPair(innerBlock, 0);
    List<UserLocal> locals = toList(manager.getAccessibleLocals(pair));
    assertTrue("EachInArrayTogether item should be accessible", locals.contains(itemLocal));
  }

  @Test
  public void getAccessibleLocals_localDecl_followedByNestedLoop_onlySeesPriorLocals() {
    BlockStatement outer = new BlockStatement();
    UserLocal outerLocal = new UserLocal("outerVal", JavaType.DOUBLE_OBJECT_TYPE, false);
    outer.statements.add(new LocalDeclarationStatement(outerLocal, new DoubleLiteral(2.0)));

    UserLocal loopItem = new UserLocal("loopItem", JavaType.STRING_TYPE, true);
    BlockStatement loopBody = new BlockStatement();
    UserLocal innerLocal = new UserLocal("innerVal", JavaType.INTEGER_OBJECT_TYPE, false);
    loopBody.statements.add(new LocalDeclarationStatement(innerLocal, new IntegerLiteral(42)));
    ForEachInArrayLoop loop = new ForEachInArrayLoop(loopItem, new NullLiteral(), loopBody);
    outer.statements.add(loop);

    // From inside the loop body at index 1, we should see innerVal, loopItem, and outerVal
    BlockStatementIndexPair pair = new BlockStatementIndexPair(loopBody, 1);
    List<String> names = toList(manager.getAccessibleLocals(pair)).stream()
        .map(UserLocal::getName).collect(Collectors.toList());
    assertTrue("innerVal should be accessible", names.contains("innerVal"));
    assertTrue("loopItem should be accessible", names.contains("loopItem"));
    assertTrue("outerVal should be accessible", names.contains("outerVal"));
  }

  @Test
  public void getAccessibleLocals_insideConditionalBody_seesOuterLocals() {
    BlockStatement outer = new BlockStatement();
    UserLocal local = new UserLocal("condVar", JavaType.INTEGER_OBJECT_TYPE, false);
    outer.statements.add(new LocalDeclarationStatement(local, new IntegerLiteral(10)));

    // The ConditionalStatement's body block nested inside the outer block
    BlockStatement ifBody = new BlockStatement();
    BooleanExpressionBodyPair pair = new BooleanExpressionBodyPair(new BooleanLiteral(true), ifBody);
    ConditionalStatement conditional = new ConditionalStatement(new BooleanExpressionBodyPair[]{pair}, new BlockStatement());
    outer.statements.add(conditional);

    // From the if body, condVar should be visible via parent traversal
    BlockStatementIndexPair bsip = new BlockStatementIndexPair(ifBody, 0);
    List<String> names = toList(manager.getAccessibleLocals(bsip)).stream()
        .map(UserLocal::getName).collect(Collectors.toList());
    assertTrue("condVar should be accessible inside if body", names.contains("condVar"));
  }

  @Test
  public void getAccessibleLocals_doInOrderBody_seesOuterLocals() {
    BlockStatement outer = new BlockStatement();
    UserLocal local = new UserLocal("doVar", JavaType.STRING_TYPE, false);
    outer.statements.add(new LocalDeclarationStatement(local, new StringLiteral("test")));

    BlockStatement doBody = new BlockStatement();
    DoInOrder doInOrder = new DoInOrder(doBody);
    outer.statements.add(doInOrder);

    BlockStatementIndexPair bsip = new BlockStatementIndexPair(doBody, 0);
    List<String> names = toList(manager.getAccessibleLocals(bsip)).stream()
        .map(UserLocal::getName).collect(Collectors.toList());
    assertTrue("doVar should be accessible inside DoInOrder body", names.contains("doVar"));
  }

  @Test
  public void getAccessibleLocals_doTogetherBody_seesOuterLocals() {
    BlockStatement outer = new BlockStatement();
    UserLocal local = new UserLocal("togetherVar", JavaType.DOUBLE_OBJECT_TYPE, false);
    outer.statements.add(new LocalDeclarationStatement(local, new DoubleLiteral(5.0)));

    BlockStatement togetherBody = new BlockStatement();
    DoTogether doTogether = new DoTogether(togetherBody);
    outer.statements.add(doTogether);

    BlockStatementIndexPair bsip = new BlockStatementIndexPair(togetherBody, 0);
    List<String> names = toList(manager.getAccessibleLocals(bsip)).stream()
        .map(UserLocal::getName).collect(Collectors.toList());
    assertTrue("togetherVar should be accessible inside DoTogether body", names.contains("togetherVar"));
  }

  // ---- Accessible locals edge case: non-LocalDeclarationStatements interleaved ----

  @Test
  public void getAccessibleLocals_interleavedStatements_onlyReturnsLocals() {
    BlockStatement block = new BlockStatement();
    UserLocal first = new UserLocal("first", JavaType.INTEGER_OBJECT_TYPE, false);
    block.statements.add(new LocalDeclarationStatement(first, new IntegerLiteral(1)));
    block.statements.add(new ExpressionStatement(new NullLiteral())); // not a local
    block.statements.add(new ExpressionStatement(new StringLiteral("noise"))); // not a local
    UserLocal second = new UserLocal("second", JavaType.STRING_TYPE, false);
    block.statements.add(new LocalDeclarationStatement(second, new StringLiteral("b")));

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 4);
    List<String> names = toList(manager.getAccessibleLocals(pair)).stream()
        .map(UserLocal::getName).collect(Collectors.toList());
    assertEquals(2, names.size());
    assertTrue(names.contains("first"));
    assertTrue(names.contains("second"));
  }

  // ---- Helper ----

  private static ExpressionCascadeContext makeContext(Expression expr) {
    return new ExpressionCascadeContext() {
      @Override
      public Expression getPreviousExpression() { return expr; }
      @Override
      public BlockStatementIndexPair getBlockStatementIndexPair() { return null; }
    };
  }
}
