package org.lgna.project.ast;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TDD tests for JavaConcurrencyEmitter — the delegate extracted from
 * JavaCodeGenerator that handles processDoTogether and
 * processEachInTogether emission.
 *
 * These tests verify behavior through JavaCodeGenerator since the emitter
 * needs the generator's appendString/appendStatement infrastructure.
 * The emitter is package-private and composed into JavaCodeGenerator.
 *
 * These tests will FAIL until JavaConcurrencyEmitter is implemented
 * and wired into JavaCodeGenerator.
 */
public class JavaConcurrencyEmitterTest {

  // --- processDoTogether with lambda support ---

  @Test
  public void emitsDoTogetherWithLambdasWhenSupported() {
    DoTogether doTogether = new DoTogether(new BlockStatement(
        new LocalDeclarationStatement(
            new UserLocal("left", String.class, true),
            new StringLiteral("L")),
        new LocalDeclarationStatement(
            new UserLocal("right", String.class, true),
            new StringLiteral("R"))));

    String source = generateWithLambda(doTogether);

    assertTrue("lambda syntax expected",
        source.contains("()->{"));
    assertTrue("first lambda body",
        source.contains("final String left=\"L\";"));
    assertTrue("second lambda body",
        source.contains("final String right=\"R\";"));
    assertTrue("ThreadUtilities.doTogether call",
        source.contains("ThreadUtilities.doTogether("));
    assertTrue("statement ends with );",
        source.endsWith(");"));
  }

  @Test
  public void emitsDoTogetherWithAnonymousClassWhenLambdaNotSupported() {
    DoTogether doTogether = new DoTogether(new BlockStatement(
        new LocalDeclarationStatement(
            new UserLocal("task", String.class, true),
            new StringLiteral("work"))));

    String source = generateNoLambda(doTogether);

    assertTrue("anonymous Runnable expected",
        source.contains("new Runnable(){public void run(){"));
    assertFalse("lambda syntax should not appear",
        source.contains("()->"));
  }

  @Test
  public void emitsDoTogetherFlatteningDoInOrderChildren() {
    // DoTogether with a DoInOrder child should flatten the DoInOrder's body
    LocalDeclarationStatement step1 = new LocalDeclarationStatement(
        new UserLocal("step1", String.class, true), new StringLiteral("A"));
    LocalDeclarationStatement step2 = new LocalDeclarationStatement(
        new UserLocal("step2", String.class, true), new StringLiteral("B"));
    DoInOrder doInOrder = new DoInOrder(new BlockStatement(step1, step2));

    DoTogether doTogether = new DoTogether(new BlockStatement(doInOrder));

    String source = generateWithLambda(doTogether);

    // The DoInOrder's statements should appear directly inside the lambda
    assertTrue("step1 inside lambda", source.contains("final String step1=\"A\";"));
    assertTrue("step2 inside lambda", source.contains("final String step2=\"B\";"));
  }

  @Test
  public void emitsDoTogetherWithSingleStatement() {
    DoTogether doTogether = new DoTogether(new BlockStatement(
        new LocalDeclarationStatement(
            new UserLocal("only", String.class, true),
            new StringLiteral("one"))));

    String source = generateWithLambda(doTogether);

    assertFalse("should not have comma before first lambda",
        source.contains("(,"));
    assertTrue("single lambda body",
        source.contains("final String only=\"one\";"));
  }

  @Test
  public void emitsDoTogetherWithEmptyBody() {
    DoTogether doTogether = new DoTogether(new BlockStatement());

    String source = generateWithLambda(doTogether);

    assertTrue("should have method call with empty args",
        source.contains("ThreadUtilities.doTogether("));
    assertTrue("should close parens",
        source.contains(");"));
  }

  // --- processEachInTogether ---

  @Test
  public void emitsEachInTogetherWithLambdaSyntax() {
    UserLocal item = new UserLocal("animal", String.class, true);
    AbstractEachInTogether eachIn = new ForEachInArrayLoop(
        item,
        AstUtilities.createArrayInstanceCreation(
            String[].class,
            new StringLiteral("cat"),
            new StringLiteral("dog")),
        new BlockStatement(
            new ExpressionStatement(new LocalAccess(item))));

    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .isLambdaSupported(true)
        .build();
    gen.processEachInTogether(eachIn);
    String source = gen.getText();

    assertTrue("eachInTogether call",
        source.contains("ThreadUtilities.eachInTogether("));
    assertTrue("lambda param",
        source.contains("(String animal)->"));
    assertTrue("array items as trailing args",
        source.contains(",\"cat\""));
  }

  @Test
  public void emitsEachInTogetherWithAnonymousClassWhenLambdaNotSupported() {
    UserLocal item = new UserLocal("item", String.class, true);
    AbstractEachInTogether eachIn = new ForEachInArrayLoop(
        item,
        AstUtilities.createArrayInstanceCreation(
            String[].class,
            new StringLiteral("x")),
        new BlockStatement());

    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .isLambdaSupported(false)
        .build();
    gen.processEachInTogether(eachIn);
    String source = gen.getText();

    assertTrue("anonymous EachInTogetherRunnable expected",
        source.contains("new EachInTogetherRunnable"));
    assertTrue("run method signature",
        source.contains("public void run(String item)"));
    assertFalse("lambda syntax should not appear",
        source.contains("->"));
  }

  @Test
  public void emitsEachInTogetherWithNonArrayExpression() {
    UserLocal item = new UserLocal("element", String.class, true);
    UserLocal items = new UserLocal("myList", Iterable.class, false);

    ForEachInIterableLoop eachIn = new ForEachInIterableLoop(
        item,
        new LocalAccess(items),
        new BlockStatement());

    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .isLambdaSupported(true)
        .build();
    gen.processEachInTogether(eachIn);
    String source = gen.getText();

    assertTrue("non-array expression as second arg after comma",
        source.contains(",myList"));
  }

  // --- helper methods ---

  private static String generateWithLambda(Statement statement) {
    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .isLambdaSupported(true)
        .build();
    statement.process(gen);
    return gen.getText();
  }

  private static String generateNoLambda(Statement statement) {
    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .isLambdaSupported(false)
        .build();
    statement.process(gen);
    return gen.getText();
  }
}
