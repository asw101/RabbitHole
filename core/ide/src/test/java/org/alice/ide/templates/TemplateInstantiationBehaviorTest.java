package org.alice.ide.templates;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.alice.ide.ThemeUtilities;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.ast.draganddrop.expression.AbstractExpressionDragModel;
import org.alice.ide.ast.draganddrop.statement.StatementTemplateDragModel;
import org.junit.Test;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionProperty;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.StringLiteral;

import javax.swing.SwingUtilities;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class TemplateInstantiationBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void statementTemplateRetainsStatementClassAndUsesStatementTheme() throws Exception {
    TestStatementTemplate template = runOnEdt(TestStatementTemplate::new);

    assertSame(Comment.class, template.getStatementCls());
    assertTrue(template.isClickAndClackAppropriateForTest());
    assertEquals(ThemeUtilities.getActiveTheme().getColorFor(Comment.class), template.getBackgroundColor());
  }

  @Test
  public void expressionTemplateReportsExpressionTypeFromItsDragModel() throws Exception {
    TestExpressionTemplate template = runOnEdt(TestExpressionTemplate::new);

    assertSame(JavaType.getInstance(String.class), template.getExpressionType());
  }

  @Test
  public void expressionTemplateRefreshesOnlyOnFirstDisplayCycle() throws Exception {
    TestExpressionTemplate template = runOnEdt(TestExpressionTemplate::new);

    runOnEdt(() -> {
      template.display();
      template.display();
      template.undisplay();
      template.display();
      return null;
    });

    assertEquals(1, template.getCreateIncompleteExpressionCount());
  }

  @Test
  public void expressionTemplateManualRefreshCreatesAnotherIncompleteExpression() throws Exception {
    TestExpressionTemplate template = runOnEdt(TestExpressionTemplate::new);

    runOnEdt(() -> {
      template.display();
      template.refreshForTest();
      return null;
    });

    assertEquals(2, template.getCreateIncompleteExpressionCount());
  }

  private static <T> T runOnEdt(Callable<T> callable) throws Exception {
    AtomicReference<T> result = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> {
      try {
        result.set(callable.call());
      } catch (Throwable throwable) {
        failure.set(throwable);
      }
    });
    if (failure.get() != null) {
      if (failure.get() instanceof Exception exception) {
        throw exception;
      }
      throw new RuntimeException(failure.get());
    }
    return result.get();
  }

  private static final class TestStatementTemplate extends StatementTemplate {
    private TestStatementTemplate() {
      super(new TestStatementTemplateDragModel(), Comment.class);
    }

    private boolean isClickAndClackAppropriateForTest() {
      return this.isClickAndClackAppropriate();
    }
  }

  private static final class TestStatementTemplateDragModel extends StatementTemplateDragModel {
    private TestStatementTemplateDragModel() {
      super(
          UUID.fromString("92cd67a3-6198-4ef5-b35d-b2b07ed1ab95"),
          Comment.class,
          new Comment("template"));
    }

    @Override
    protected Triggerable getDropOperation(DragStep step, BlockStatementIndexPair dropSite) {
      return activity -> {
      };
    }
  }

  private static final class TestExpressionTemplate extends ExpressionTemplate {
    private int createIncompleteExpressionCount;

    private TestExpressionTemplate() {
      super(new TestExpressionDragModel());
    }

    @Override
    protected Expression createIncompleteExpression() {
      this.createIncompleteExpressionCount++;
      return new StringLiteral("template");
    }

    private int getCreateIncompleteExpressionCount() {
      return this.createIncompleteExpressionCount;
    }

    private void display() {
      this.handleDisplayable();
    }

    private void undisplay() {
      this.handleUndisplayable();
    }

    private void refreshForTest() {
      this.refresh();
    }
  }

  private static final class TestExpressionDragModel extends AbstractExpressionDragModel {
    private TestExpressionDragModel() {
      super(UUID.fromString("efa6f710-c68f-4cf6-a1ca-a040630b3707"));
    }

    @Override
    public org.lgna.project.ast.AbstractType<?, ?, ?> getType() {
      return JavaType.getInstance(String.class);
    }

    @Override
    public boolean isPotentialStatementCreator() {
      return false;
    }

    @Override
    protected Triggerable getDropOperation(ExpressionProperty expressionProperty) {
      return activity -> {
      };
    }
  }
}
