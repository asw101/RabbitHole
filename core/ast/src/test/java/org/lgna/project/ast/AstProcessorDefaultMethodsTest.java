package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.code.CodeOrganizer;
import org.lgna.project.code.InstantiableTweedleNode;

import static org.junit.Assert.*;

/**
 * TDD tests for AstProcessor default method migration.
 *
 * After refactoring, all 48 void methods should have default {} bodies,
 * leaving only getNewCodeOrganizerForTypeName as the sole required method.
 * A minimal implementor that provides only that method must compile and
 * the defaults must be no-ops (not throw).
 */
public class AstProcessorDefaultMethodsTest {

  /**
   * A minimal AstProcessor that only implements the required method.
   * This class will fail to compile until default {} bodies are added
   * to all 48 void methods in AstProcessor.
   */
  private static class MinimalProcessor implements AstProcessor {
    @Override
    public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
      return new CodeOrganizer(CodeOrganizer.defaultCodeOrganizer);
    }
  }

  @Test
  public void minimalProcessorInstantiates() {
    AstProcessor processor = new MinimalProcessor();
    assertNotNull("Minimal processor with only required method should instantiate", processor);
  }

  @Test
  public void getNewCodeOrganizerForTypeNameRemainsRequired() {
    MinimalProcessor p = new MinimalProcessor();
    CodeOrganizer organizer = p.getNewCodeOrganizerForTypeName("Scene");
    assertNotNull("Required method must still return a value", organizer);
  }

  // --- All void methods must have no-op defaults (not throw) ---

  @Test
  public void processClassDefaultIsNoOp() {
    new MinimalProcessor().processClass(null, null);
  }

  @Test
  public void processConstructorDefaultIsNoOp() {
    new MinimalProcessor().processConstructor(null);
  }

  @Test
  public void processMethodDefaultIsNoOp() {
    new MinimalProcessor().processMethod(null);
  }

  @Test
  public void processGetterDefaultIsNoOp() {
    new MinimalProcessor().processGetter(null);
  }

  @Test
  public void processIndexedGetterDefaultIsNoOp() {
    new MinimalProcessor().processIndexedGetter(null);
  }

  @Test
  public void processSetterDefaultIsNoOp() {
    new MinimalProcessor().processSetter(null);
  }

  @Test
  public void processIndexedSetterDefaultIsNoOp() {
    new MinimalProcessor().processIndexedSetter(null);
  }

  @Test
  public void processFieldDefaultIsNoOp() {
    new MinimalProcessor().processField(null);
  }

  @Test
  public void processLocalDeclarationDefaultIsNoOp() {
    new MinimalProcessor().processLocalDeclaration(null);
  }

  @Test
  public void processExpressionStatementDefaultIsNoOp() {
    new MinimalProcessor().processExpressionStatement(null);
  }

  @Test
  public void processReturnStatementDefaultIsNoOp() {
    new MinimalProcessor().processReturnStatement(null);
  }

  @Test
  public void processBlockDefaultIsNoOp() {
    new MinimalProcessor().processBlock(null);
  }

  @Test
  public void processConstructorBlockDefaultIsNoOp() {
    new MinimalProcessor().processConstructorBlock(null);
  }

  @Test
  public void processSuperConstructorDefaultIsNoOp() {
    new MinimalProcessor().processSuperConstructor(null);
  }

  @Test
  public void processThisConstructorDefaultIsNoOp() {
    new MinimalProcessor().processThisConstructor(null);
  }

  @Test
  public void processConditionalDefaultIsNoOp() {
    new MinimalProcessor().processConditional(null);
  }

  @Test
  public void processCountLoopDefaultIsNoOp() {
    new MinimalProcessor().processCountLoop(null);
  }

  @Test
  public void processForEachDefaultIsNoOp() {
    new MinimalProcessor().processForEach(null);
  }

  @Test
  public void processWhileLoopDefaultIsNoOp() {
    new MinimalProcessor().processWhileLoop(null);
  }

  @Test
  public void processDoInOrderDefaultIsNoOp() {
    new MinimalProcessor().processDoInOrder(null);
  }

  @Test
  public void processDoTogetherDefaultIsNoOp() {
    new MinimalProcessor().processDoTogether(null);
  }

  @Test
  public void processEachInTogetherDefaultIsNoOp() {
    new MinimalProcessor().processEachInTogether(null);
  }

  @Test
  public void processLambdaDefaultIsNoOp() {
    new MinimalProcessor().processLambda(null);
  }

  @Test
  public void processExpressionDefaultIsNoOp() {
    new MinimalProcessor().processExpression(null);
  }

  @Test
  public void processMethodCallDefaultIsNoOp() {
    new MinimalProcessor().processMethodCall(null);
  }

  @Test
  public void processKeyedArgumentDefaultIsNoOp() {
    new MinimalProcessor().processKeyedArgument(null);
  }

  @Test
  public void processAssignmentExpressionDefaultIsNoOp() {
    new MinimalProcessor().processAssignmentExpression(null);
  }

  @Test
  public void processConcatenationDefaultIsNoOp() {
    new MinimalProcessor().processConcatenation(null);
  }

  @Test
  public void processLogicalComplementDefaultIsNoOp() {
    new MinimalProcessor().processLogicalComplement(null);
  }

  @Test
  public void processInfixExpressionDefaultIsNoOp() {
    new MinimalProcessor().processInfixExpression(null);
  }

  @Test
  public void processInstantiationDefaultIsNoOp() {
    new MinimalProcessor().processInstantiation(null);
  }

  @Test
  public void processArrayInstantiationDefaultIsNoOp() {
    new MinimalProcessor().processArrayInstantiation(null);
  }

  @Test
  public void processArrayAccessDefaultIsNoOp() {
    new MinimalProcessor().processArrayAccess(null);
  }

  @Test
  public void processArrayLengthDefaultIsNoOp() {
    new MinimalProcessor().processArrayLength(null);
  }

  @Test
  public void processFieldAccessDefaultIsNoOp() {
    new MinimalProcessor().processFieldAccess(null);
  }

  @Test
  public void processNullDefaultIsNoOp() {
    new MinimalProcessor().processNull();
  }

  @Test
  public void processThisReferenceDefaultIsNoOp() {
    new MinimalProcessor().processThisReference();
  }

  @Test
  public void processSuperReferenceDefaultIsNoOp() {
    new MinimalProcessor().processSuperReference();
  }

  @Test
  public void processBooleanDefaultIsNoOp() {
    new MinimalProcessor().processBoolean(true);
  }

  @Test
  public void processIntDefaultIsNoOp() {
    new MinimalProcessor().processInt(42);
  }

  @Test
  public void processFloatDefaultIsNoOp() {
    new MinimalProcessor().processFloat(3.14f);
  }

  @Test
  public void processDoubleDefaultIsNoOp() {
    new MinimalProcessor().processDouble(2.718);
  }

  @Test
  public void processEscapedStringLiteralDefaultIsNoOp() {
    new MinimalProcessor().processEscapedStringLiteral(null);
  }

  @Test
  public void processTypeNameDefaultIsNoOp() {
    new MinimalProcessor().processTypeName(null);
  }

  @Test
  public void processTypeLiteralDefaultIsNoOp() {
    new MinimalProcessor().processTypeLiteral(null);
  }

  @Test
  public void processResourceExpressionDefaultIsNoOp() {
    new MinimalProcessor().processResourceExpression(null);
  }

  @Test
  public void processMultiLineCommentDefaultIsNoOp() {
    new MinimalProcessor().processMultiLineComment(null);
  }

  @Test
  public void processVariableIdentifierDefaultIsNoOp() {
    new MinimalProcessor().processVariableIdentifier(null);
  }

  // --- Pre-existing defaults still work ---

  @Test
  public void processResourceTypePreExistingDefaultIsNoOp() {
    new MinimalProcessor().processResourceType("someResource");
  }

  @Test
  public void processDynamicResourcePreExistingDefaultIsNoOp() {
    new MinimalProcessor().processDynamicResource("model", "name", new InstantiableTweedleNode[0]);
  }

  @Test
  public void isPublicStaticFinalFieldGetterDesiredDefaultsToTrue() {
    assertTrue("Pre-existing default should return true",
        new MinimalProcessor().isPublicStaticFinalFieldGetterDesired());
  }
}
