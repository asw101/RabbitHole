package org.alice.ide.cascade;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.CascadeMenuModel;
import org.lgna.croquet.imp.cascade.BlankNode;
import org.lgna.project.annotations.ValueDetails;
import org.lgna.project.ast.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link ExpressionCascadeManager} — context stack, accessible locals,
 * and type-applicability methods. Uses a minimal concrete subclass.
 */
public class ExpressionCascadeManagerTest {

  private TestableExpressionCascadeManager manager;

  /** Minimal concrete subclass for testing the abstract class */
  private static class TestableExpressionCascadeManager extends ExpressionCascadeManager {
    @Override
    protected boolean isApplicableForPartFillIn(AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType) {
      return false;
    }

    @Override
    protected CascadeMenuModel<Expression> createPartMenuModel(Expression expression, AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType, boolean isOwnedByCascadeItemMenuCombo) {
      return null;
    }

    // Expose protected method for testing
    public boolean testIsApplicableForFillIn(AbstractType<?, ?, ?> desired, AbstractType<?, ?, ?> expression) {
      return isApplicableForFillIn(desired, expression);
    }

    public boolean testIsApplicableForFillInAndPossiblyPartFillIns(AbstractType<?, ?, ?> desired, AbstractType<?, ?, ?> expression) {
      return isApplicableForFillInAndPossiblyPartFillIns(desired, expression);
    }

    public AbstractType<?, ?, ?> testGetTypeFor(AbstractType<?, ?, ?> type) {
      return getTypeFor(type);
    }

    public boolean testAreEnumConstantsDesired(AbstractType<?, ?, ?> enumType) {
      return areEnumConstantsDesired(enumType);
    }

    public boolean testIsNullLiteralAllowedForType(AbstractType<?, ?, ?> type, List<CascadeBlankChild> items) {
      return isNullLiteralAllowedForType(type, items);
    }

    public AbstractType<?, ?, ?> testGetEnumTypeForInterfaceType(AbstractType<?, ?, ?> type) {
      return getEnumTypeForInterfaceType(type);
    }
  }

  @Before
  public void setUp() {
    manager = new TestableExpressionCascadeManager();
  }

  // ---- Context stack operations ----

  @Test
  public void pushAndPopContext_roundTrips() {
    ExpressionCascadeContext ctx = new ExpressionCascadeContext() {
      @Override
      public Expression getPreviousExpression() {
        return new NullLiteral();
      }
      @Override
      public BlockStatementIndexPair getBlockStatementIndexPair() {
        return null;
      }
    };
    manager.pushContext(ctx);
    ExpressionCascadeContext popped = manager.popContext();
    assertSame(ctx, popped);
  }

  @Test
  public void popContext_emptyStack_returnsNullContext() {
    // Popping from empty stack should not throw, returns null context
    ExpressionCascadeContext result = manager.popContext();
    assertNotNull(result);
    assertNull(result.getPreviousExpression());
    assertNull(result.getBlockStatementIndexPair());
  }

  @Test
  public void pushNullContext_andPop_returnsNullContextValues() {
    manager.pushNullContext();
    ExpressionCascadeContext ctx = manager.popContext();
    assertNotNull(ctx);
    assertNull(ctx.getPreviousExpression());
    assertNull(ctx.getBlockStatementIndexPair());
  }

  @Test
  public void popAndCheckNullContext_doesNotThrow() {
    manager.pushNullContext();
    manager.popAndCheckNullContext();
  }

  @Test
  public void popAndCheckContext_mismatch_logsButDoesNotThrow() {
    ExpressionCascadeContext ctx1 = new ExpressionCascadeContext() {
      @Override
      public Expression getPreviousExpression() { return null; }
      @Override
      public BlockStatementIndexPair getBlockStatementIndexPair() { return null; }
    };
    ExpressionCascadeContext ctx2 = new ExpressionCascadeContext() {
      @Override
      public Expression getPreviousExpression() { return null; }
      @Override
      public BlockStatementIndexPair getBlockStatementIndexPair() { return null; }
    };
    manager.pushContext(ctx1);
    // Expects ctx2 but pops ctx1 — should log severe but not throw
    ExpressionCascadeContext popped = manager.popAndCheckContext(ctx2);
    assertSame(ctx1, popped);
  }

  @Test
  public void multipleContextPush_popsInLifoOrder() {
    ExpressionCascadeContext ctx1 = new ExpressionCascadeContext() {
      @Override
      public Expression getPreviousExpression() { return null; }
      @Override
      public BlockStatementIndexPair getBlockStatementIndexPair() { return null; }
    };
    ExpressionCascadeContext ctx2 = new ExpressionCascadeContext() {
      @Override
      public Expression getPreviousExpression() { return new NullLiteral(); }
      @Override
      public BlockStatementIndexPair getBlockStatementIndexPair() { return null; }
    };

    manager.pushContext(ctx1);
    manager.pushContext(ctx2);
    assertSame(ctx2, manager.popContext());
    assertSame(ctx1, manager.popContext());
  }

  // ---- getPreviousExpression from context ----

  @Test
  public void getPreviousExpression_emptyStack_returnsNull() {
    assertNull(manager.getPreviousExpression());
  }

  @Test
  public void getPreviousExpression_withContext_returnsContextValue() {
    NullLiteral expr = new NullLiteral();
    manager.pushContext(new ExpressionCascadeContext() {
      @Override
      public Expression getPreviousExpression() { return expr; }
      @Override
      public BlockStatementIndexPair getBlockStatementIndexPair() { return null; }
    });
    assertSame(expr, manager.getPreviousExpression());
    manager.popContext();
  }

  // ---- getAccessibleLocals ----

  @Test
  public void getAccessibleLocals_emptyBlock_returnsEmpty() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    Iterable<UserLocal> locals = manager.getAccessibleLocals(pair);
    assertNotNull(locals);
    assertFalse(locals.iterator().hasNext());
  }

  @Test
  public void getAccessibleLocals_withLocalDeclaration_returnsLocal() {
    BlockStatement block = new BlockStatement();
    UserLocal local = new UserLocal("testVar", JavaType.DOUBLE_OBJECT_TYPE, false);
    LocalDeclarationStatement decl = new LocalDeclarationStatement(local, new DoubleLiteral(1.0));
    block.statements.add(decl);

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 1);
    Iterable<UserLocal> locals = manager.getAccessibleLocals(pair);
    assertTrue(locals.iterator().hasNext());
    assertEquals("testVar", locals.iterator().next().getName());
  }

  @Test
  public void getAccessibleLocals_multipleDeclarations_returnsAll() {
    BlockStatement block = new BlockStatement();
    UserLocal local1 = new UserLocal("a", JavaType.INTEGER_OBJECT_TYPE, false);
    UserLocal local2 = new UserLocal("b", JavaType.STRING_TYPE, false);
    block.statements.add(new LocalDeclarationStatement(local1, new IntegerLiteral(1)));
    block.statements.add(new LocalDeclarationStatement(local2, new StringLiteral("hi")));

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 2);
    int count = 0;
    for (UserLocal l : manager.getAccessibleLocals(pair)) {
      count++;
    }
    assertEquals(2, count);
  }

  @Test
  public void getAccessibleLocals_atIndex0_returnsNone() {
    BlockStatement block = new BlockStatement();
    UserLocal local = new UserLocal("x", JavaType.DOUBLE_OBJECT_TYPE, false);
    block.statements.add(new LocalDeclarationStatement(local, new DoubleLiteral(0.0)));

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    assertFalse(manager.getAccessibleLocals(pair).iterator().hasNext());
  }

  // ---- isApplicableForFillIn ----

  @Test
  public void isApplicableForFillIn_sameType_returnsTrue() {
    assertTrue(manager.testIsApplicableForFillIn(JavaType.STRING_TYPE, JavaType.STRING_TYPE));
  }

  @Test
  public void isApplicableForFillIn_superType_returnsTrue() {
    assertTrue(manager.testIsApplicableForFillIn(JavaType.OBJECT_TYPE, JavaType.STRING_TYPE));
  }

  @Test
  public void isApplicableForFillIn_unrelatedType_returnsFalse() {
    assertFalse(manager.testIsApplicableForFillIn(JavaType.DOUBLE_OBJECT_TYPE, JavaType.STRING_TYPE));
  }

  // ---- getTypeFor ----

  @Test
  public void getTypeFor_number_returnsDouble() {
    AbstractType<?, ?, ?> result = manager.testGetTypeFor(JavaType.getInstance(Number.class));
    assertEquals(JavaType.DOUBLE_OBJECT_TYPE, result);
  }

  @Test
  public void getTypeFor_string_returnsString() {
    AbstractType<?, ?, ?> result = manager.testGetTypeFor(JavaType.STRING_TYPE);
    assertEquals(JavaType.STRING_TYPE, result);
  }

  // ---- default behaviors of protected methods ----

  @Test
  public void areEnumConstantsDesired_defaultTrue() {
    assertTrue(manager.testAreEnumConstantsDesired(JavaType.STRING_TYPE));
  }

  @Test
  public void isNullLiteralAllowed_defaultFalse() {
    assertFalse(manager.testIsNullLiteralAllowedForType(JavaType.STRING_TYPE, java.util.Collections.emptyList()));
  }

  @Test
  public void getEnumTypeForInterfaceType_defaultNull() {
    assertNull(manager.testGetEnumTypeForInterfaceType(JavaType.STRING_TYPE));
  }

  // ---- addRelationalType ----

  @Test
  public void addRelationalTypeToBooleanFillerInner_doesNotThrow() {
    manager.addRelationalTypeToBooleanFillerInner(JavaType.DOUBLE_OBJECT_TYPE);
  }

  @Test
  public void addRelationalTypeToBooleanFillerInner_byClass_doesNotThrow() {
    manager.addRelationalTypeToBooleanFillerInner(Integer.class);
  }
}
