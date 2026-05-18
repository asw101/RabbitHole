package org.alice.tweedle.unlinked;

import org.alice.tweedle.*;
import org.alice.tweedle.ast.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import static org.junit.Assert.*;

public class TweedleAdvancedParseCoverageTest {

  private static TweedleParser expressionParser(String source) {
    return new TweedleParser(new CommonTokenStream(new TweedleLexer(CharStreams.fromString(source))));
  }

  @Test
  public void parseTypeCapturesStaticMethodsOptionalParametersAndConstructors() {
    TweedleClass scene = (TweedleClass) new TweedleUnlinkedParser().parseType("""
        class Scene extends SScene {
          static WholeNumber build(WholeNumber amount, TextString label <- \"hello\") {
            return amount;
          }
          Scene(WholeNumber amount) {
            return;
          }
          TextString title <- null;
        }
        """);

    assertEquals("Scene", scene.getName());
    assertEquals("SScene", scene.getSuperclassName());
    assertEquals(1, scene.getMethods().size());
    assertTrue(scene.getMethods().getFirst().isStatic());
    assertEquals(1, scene.getMethods().getFirst().getRequiredParameters().size());
    assertEquals(1, scene.getMethods().getFirst().getOptionalParameters().size());
    assertEquals(1, scene.getMethods().getFirst().getBody().size());
    assertEquals(1, scene.getConstructors().size());
    assertEquals(1, scene.getProperties().size());
    assertSame(TweedleNull.NULL, scene.getProperties().getFirst().getInitializer());
  }

  @Test
  public void parseTypeSupportsEnumArgumentsAndBodyDeclarations() {
    TweedleEnum direction = (TweedleEnum) new TweedleUnlinkedParser().parseType("""
        enum Direction {
          UP(amount: 1), DOWN;
          WholeNumber code <- 1;
          Direction() { return; }
          void mark() { return; }
        }
        """);

    assertEquals("Direction", direction.getName());
    assertEquals(2, direction.getValues().size());
    assertNotNull(direction.getValue("UP"));
    assertNotNull(direction.getValue("DOWN"));
  }

  @Test
  public void parseStatementSupportsConstantsAndDisabledStatements() {
    LocalVariableDeclaration local = (LocalVariableDeclaration) new TweedleUnlinkedParser().parseStatement("constant WholeNumber count <- 1;");
    ReturnStatement disabled = (ReturnStatement) new TweedleUnlinkedParser().parseStatement("*< return 1; >*");

    assertTrue(local.isConstant());
    assertEquals("count", local.getDeclaration().getName());
    assertNotNull(local.getDeclaration().getInitializer());
    assertFalse(disabled.isEnabled());
    assertEquals(Integer.valueOf(1), ((TweedlePrimitiveValue<Integer>) disabled.getExpression()).getPrimitiveValue());
  }

  @Test
  public void parseExpressionSupportsSuperAndArrayCreatorForms() {
    TweedleExpression superCall = new TweedleUnlinkedParser().parseExpression("super(amount: 1)");
    TweedleExpression superField = new TweedleUnlinkedParser().parseExpression("super.helper");
    TweedleExpression superMethod = new TweedleUnlinkedParser().parseExpression("super.helper(amount: 1)");
    TweedleArrayInitializer sized = (TweedleArrayInitializer) new TweedleUnlinkedParser().parseExpression("new WholeNumber[3]");
    TweedleArrayInitializer initialized = (TweedleArrayInitializer) new TweedleUnlinkedParser().parseExpression("new WholeNumber[] {1, 2}");

    assertTrue(superCall instanceof Instantiation);
    assertTrue(superField instanceof FieldAccess);
    assertTrue(((FieldAccess) superField).getTarget() instanceof SuperExpression);
    assertTrue(superMethod instanceof MethodCallExpression);
    assertTrue(((MethodCallExpression) superMethod).getTarget() instanceof SuperExpression);
    assertFalse(sized.hasElementInitializers());
    assertNotNull(sized.getInitializeSize());
    assertTrue(initialized.hasElementInitializers());
    assertEquals(2, initialized.getElements().size());
  }

  @Test
  public void expressionVisitorRejectsWrongExpectedTypeButCanAllowPrimitiveNull() {
    TweedleParser.ExpressionContext stringContext = expressionParser("\"wrong\"").expression();
    TweedleParser.ExpressionContext nullContext = expressionParser("null").expression();
    TweedleUnlinkedParser parser = new TweedleUnlinkedParser();

    assertThrows(RuntimeException.class,
        () -> new ExpressionVisitor(parser, TweedleTypes.WHOLE_NUMBER).visitExpression(stringContext));
    assertSame(TweedleNull.NULL,
        new ExpressionVisitor(parser, TweedleTypes.WHOLE_NUMBER, true).visitExpression(nullContext));
  }
}
