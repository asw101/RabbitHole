package org.alice.tweedle;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import static org.junit.Assert.*;

public class TweedleParserGrammarCoverageTest {

  private static TweedleParser parserFor(String source) {
    TweedleLexer lexer = new TweedleLexer(CharStreams.fromString(source));
    return new TweedleParser(new CommonTokenStream(lexer));
  }

  @Test
  public void typeDeclarationSupportsVisibilityModifiersAndVarargs() {
    TweedleParser parser = parserFor("""
        @PrimeTime class Scene extends SThing models Model {
          @TuckedAway static WholeNumber count;
          @CompletelyHidden Scene(WholeNumber amount, TextString label <- \"hi\", Number... rest) { }
          @PrimeTime void configure() { return; }
        }
        """);
    TweedleParser.TypeDeclarationContext context = parser.typeDeclaration();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(context.visibility());
    assertNotNull(context.visibility().visibilityLevel().PRIME_TIME());
    assertNotNull(context.classDeclaration().MODELS());

    TweedleParser.ClassBodyDeclarationContext field = context.classDeclaration().classBody().classBodyDeclaration().get(0);
    assertEquals(2, field.classModifier().size());
    assertNotNull(field.classModifier().get(0).visibility());
    assertNotNull(field.classModifier().get(1).STATIC());

    TweedleParser.ConstructorDeclarationContext constructor = context.classDeclaration().classBody().classBodyDeclaration().get(1).memberDeclaration().constructorDeclaration();
    assertEquals(1, constructor.formalParameters().formalParameterList().requiredParameter().size());
    assertEquals(1, constructor.formalParameters().formalParameterList().optionalParameter().size());
    assertNotNull(constructor.formalParameters().formalParameterList().lastFormalParameter());
  }

  @Test
  public void typeDeclarationSupportsSemicolonOnlyDeclarationsAndMethodSemicolons() {
    TweedleParser emptyParser = parserFor(";");
    TweedleParser.TypeDeclarationContext emptyType = emptyParser.typeDeclaration();
    TweedleParser classParser = parserFor("class Scene { ; void configure(); }");
    TweedleParser.TypeDeclarationContext classType = classParser.typeDeclaration();

    assertEquals(0, emptyParser.getNumberOfSyntaxErrors());
    assertEquals(";", emptyType.getText());
    assertEquals(0, classParser.getNumberOfSyntaxErrors());
    assertEquals(";", classType.classDeclaration().classBody().classBodyDeclaration().get(0).getText());
    assertNotNull(classType.classDeclaration().classBody().classBodyDeclaration().get(1).memberDeclaration().methodDeclaration().methodBody().SEMI());
  }

  @Test
  public void typeDeclarationSupportsEnumBodiesAndLambdaTypeSignatures() {
    TweedleParser enumParser = parserFor("""
        enum Direction {
          UP(amount: 1), DOWN;
          WholeNumber code <- 1;
          void mark() { return; }
        }
        """);
    TweedleParser.TypeDeclarationContext enumType = enumParser.typeDeclaration();
    TweedleParser lambdaParser = parserFor("""
        class Lambdas {
          <() -> void> none;
          <WholeNumber -> Boolean> single;
          <(WholeNumber, TextString) -> Number> many;
        }
        """);
    TweedleParser.TypeDeclarationContext lambdaTypes = lambdaParser.typeDeclaration();

    assertEquals(0, enumParser.getNumberOfSyntaxErrors());
    assertNotNull(enumType.enumDeclaration().enumBodyDeclarations());
    assertEquals(2, enumType.enumDeclaration().enumConstants().enumConstant().size());
    assertNotNull(enumType.enumDeclaration().enumConstants().enumConstant().getFirst().arguments());

    assertEquals(0, lambdaParser.getNumberOfSyntaxErrors());
    TweedleParser.ClassBodyDeclarationContext none = lambdaTypes.classDeclaration().classBody().classBodyDeclaration().get(0);
    TweedleParser.ClassBodyDeclarationContext single = lambdaTypes.classDeclaration().classBody().classBodyDeclaration().get(1);
    TweedleParser.ClassBodyDeclarationContext many = lambdaTypes.classDeclaration().classBody().classBodyDeclaration().get(2);
    assertEquals("()", none.memberDeclaration().fieldDeclaration().typeType().lambdaTypeSignature().typeList().getText());
    assertEquals("WholeNumber", single.memberDeclaration().fieldDeclaration().typeType().lambdaTypeSignature().typeList().getText());
    assertEquals("(WholeNumber,TextString)", many.memberDeclaration().fieldDeclaration().typeType().lambdaTypeSignature().typeList().getText());
  }

  @Test
  public void expressionGrammarSupportsLambdaCallsAndArrayCreators() {
    TweedleParser noArgParser = parserFor("(handler)(-)");
    TweedleParser.ExpressionContext noArgLambdaCall = noArgParser.expression();
    TweedleParser argParser = parserFor("(handler)(1, 2)");
    TweedleParser.ExpressionContext argLambdaCall = argParser.expression();
    TweedleParser sizedParser = parserFor("new WholeNumber[3]");
    TweedleParser.ExpressionContext sizedArray = sizedParser.expression();
    TweedleParser initializedParser = parserFor("new WholeNumber[] {1, 2}");
    TweedleParser.ExpressionContext initializedArray = initializedParser.expression();

    assertEquals(0, noArgParser.getNumberOfSyntaxErrors());
    assertNotNull(noArgLambdaCall.lambdaCall());
    assertEquals("(-)", noArgLambdaCall.lambdaCall().getText());
    assertEquals(0, argParser.getNumberOfSyntaxErrors());
    assertNotNull(argLambdaCall.lambdaCall());
    assertEquals(2, argLambdaCall.lambdaCall().unlabeledExpressionList().expression().size());
    assertEquals(0, sizedParser.getNumberOfSyntaxErrors());
    assertNotNull(sizedArray.creator().arrayCreatorRest().expression());
    assertEquals(0, initializedParser.getNumberOfSyntaxErrors());
    assertNotNull(initializedArray.creator().arrayCreatorRest().arrayInitializer());
    assertEquals(2, initializedArray.creator().arrayCreatorRest().arrayInitializer().unlabeledExpressionList().expression().size());
  }
}
