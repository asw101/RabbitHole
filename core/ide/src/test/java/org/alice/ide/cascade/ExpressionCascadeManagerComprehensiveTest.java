package org.alice.ide.cascade;

import org.alice.ide.ast.EmptyExpression;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.lgna.croquet.CascadeMenuModel;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.EachInArrayTogether;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserLocal;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ExpressionCascadeManagerComprehensiveTest {
  private InspectableManager manager;

  @Before
  public void setUp() {
    this.manager = new InspectableManager();
  }

  @Test
  public void emptyPopAndExplicitNullContextReturnSameSingletonContext() {
    ExpressionCascadeContext fromEmptyPop = this.manager.popContext();
    this.manager.pushNullContext();
    ExpressionCascadeContext fromExplicitNull = this.manager.popContext();

    assertSame(fromEmptyPop, fromExplicitNull);
  }

  @Test
  public void nestedContextsExposeTopPreviousExpressionOnly() {
    Expression first = new StringLiteral("first");
    Expression second = new IntegerLiteral(2);
    this.manager.pushContext(context(first, null));
    this.manager.pushContext(context(second, null));

    assertSame(second, this.manager.getPreviousExpression());
    this.manager.popContext();
    assertSame(first, this.manager.getPreviousExpression());
  }

  @Test
  public void popAndCheckContextOnEmptyStackReturnsNullContext() {
    ExpressionCascadeContext context = this.manager.popAndCheckContext(context(new NullLiteral(), null));

    assertNull(context.getPreviousExpression());
    assertNull(context.getBlockStatementIndexPair());
  }

  @Test
  public void getAccessibleLocalsForEachInArrayTogetherExposesItsItem() {
    EachInArrayTogether together = org.lgna.project.ast.AstUtilities.createEachInArrayTogether(new EmptyExpression(Object[].class));

    List<String> names = namesOf(this.manager.getAccessibleLocals(new BlockStatementIndexPair(together.body.getValue(), 0)));

    assertEquals(Arrays.asList(together.item.getValue().getName()), names);
  }

  @Test
  public void nestedEachInArrayTogetherIncludesOuterLocalsAfterInnerItem() {
    BlockStatement outer = new BlockStatement();
    outer.statements.add(new LocalDeclarationStatement(new UserLocal("outer", JavaType.STRING_TYPE, false), new StringLiteral("a")));
    EachInArrayTogether outerTogether = org.lgna.project.ast.AstUtilities.createEachInArrayTogether(new EmptyExpression(Object[].class));
    outer.statements.add(outerTogether);
    outerTogether.body.getValue().statements.add(new LocalDeclarationStatement(new UserLocal("inner", JavaType.STRING_TYPE, false), new StringLiteral("b")));

    List<String> names = namesOf(this.manager.getAccessibleLocals(new BlockStatementIndexPair(outerTogether.body.getValue(), 1)));

    assertTrue(names.contains("inner"));
    assertTrue(names.contains(outerTogether.item.getValue().getName()));
    assertTrue(names.contains("outer"));
  }

  @Test
  public void largeIndexCorrectionStillReturnsAllLocalsInReverseOrder() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new LocalDeclarationStatement(new UserLocal("one", JavaType.STRING_TYPE, false), new StringLiteral("1")));
    block.statements.add(new LocalDeclarationStatement(new UserLocal("two", JavaType.STRING_TYPE, false), new StringLiteral("2")));
    block.statements.add(new LocalDeclarationStatement(new UserLocal("three", JavaType.STRING_TYPE, false), new StringLiteral("3")));

    List<String> names = namesOf(this.manager.getAccessibleLocals(new BlockStatementIndexPair(block, 20)));

    assertEquals(Arrays.asList("three", "two", "one"), names);
  }

  @Test
  public void localsDeclaredAfterRequestedIndexRemainHidden() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new LocalDeclarationStatement(new UserLocal("one", JavaType.STRING_TYPE, false), new StringLiteral("1")));
    block.statements.add(new LocalDeclarationStatement(new UserLocal("two", JavaType.STRING_TYPE, false), new StringLiteral("2")));

    List<String> names = namesOf(this.manager.getAccessibleLocals(new BlockStatementIndexPair(block, 1)));

    assertEquals(Arrays.asList("one"), names);
  }

  @Test
  public void fillInAndPartFillInReturnsTrueForAssignableTypes() {
    this.manager.partFillInApplicable = false;

    assertTrue(this.manager.exposedIsApplicableForFillInAndPossiblyPartFillIns(JavaType.OBJECT_TYPE, JavaType.STRING_TYPE));
  }

  @Test
  public void fillInAndPartFillInReturnsTrueWhenOnlyPartFillInApplies() {
    this.manager.partFillInApplicable = true;

    assertTrue(this.manager.exposedIsApplicableForFillInAndPossiblyPartFillIns(JavaType.INTEGER_OBJECT_TYPE, JavaType.STRING_TYPE));
  }

  @Test
  public void fillInAndPartFillInReturnsFalseWhenNeitherApplies() {
    this.manager.partFillInApplicable = false;

    assertFalse(this.manager.exposedIsApplicableForFillInAndPossiblyPartFillIns(JavaType.INTEGER_OBJECT_TYPE, JavaType.STRING_TYPE));
  }

  @Test
  public void getTypeForNumberStillMapsToDouble() {
    assertSame(JavaType.DOUBLE_OBJECT_TYPE, this.manager.exposedGetTypeFor(JavaType.getInstance(Number.class)));
  }

  @Test
  public void appendOtherTypesRetainsDocumentedOrder() {
    assertEquals(Arrays.asList(JavaType.STRING_TYPE, JavaType.DOUBLE_OBJECT_TYPE, JavaType.INTEGER_OBJECT_TYPE), this.manager.exposedOtherTypes());
  }

  @Test
  public void popAndCheckNullContextAfterNestedContextsRestoresOuterExpression() {
    this.manager.pushContext(context(new StringLiteral("outer"), null));
    this.manager.pushNullContext();

    this.manager.popAndCheckNullContext();

    assertTrue(this.manager.getPreviousExpression() instanceof StringLiteral);
  }

  @Test
  public void explicitNullContextMakesPreviousExpressionNull() {
    this.manager.pushNullContext();

    assertNull(this.manager.getPreviousExpression());
  }

  @Test
  public void popAndCheckContextReturnsSameContextInstance() {
    ExpressionCascadeContext expected = context(new IntegerLiteral(5), null);
    this.manager.pushContext(expected);

    assertSame(expected, this.manager.popAndCheckContext(expected));
  }

  private static ExpressionCascadeContext context(final Expression expression, final BlockStatementIndexPair pair) {
    return new ExpressionCascadeContext() {
      @Override
      public Expression getPreviousExpression() {
        return expression;
      }

      @Override
      public BlockStatementIndexPair getBlockStatementIndexPair() {
        return pair;
      }
    };
  }

  private static List<String> namesOf(Iterable<UserLocal> locals) {
    List<String> names = new ArrayList<String>();
    for (UserLocal local : locals) {
      names.add(local.getName());
    }
    return names;
  }

  private static final class InspectableManager extends ExpressionCascadeManager {
    private boolean partFillInApplicable;

    @Override
    protected boolean isApplicableForPartFillIn(AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType) {
      return this.partFillInApplicable;
    }

    @Override
    protected CascadeMenuModel<Expression> createPartMenuModel(Expression expression, AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType, boolean isOwnedByCascadeItemMenuCombo) {
      return null;
    }

    private boolean exposedIsApplicableForFillInAndPossiblyPartFillIns(AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType) {
      return this.isApplicableForFillInAndPossiblyPartFillIns(desiredType, expressionType);
    }

    private AbstractType<?, ?, ?> exposedGetTypeFor(AbstractType<?, ?, ?> type) {
      return this.getTypeFor(type);
    }

    private List<AbstractType<?, ?, ?>> exposedOtherTypes() {
      List<AbstractType<?, ?, ?>> types = new ArrayList<AbstractType<?, ?, ?>>();
      this.appendOtherTypes(types);
      return types;
    }
  }
}
