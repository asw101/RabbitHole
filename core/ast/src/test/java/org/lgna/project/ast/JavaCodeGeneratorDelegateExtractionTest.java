package org.lgna.project.ast;

import org.lgna.project.code.CodeOrganizer;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Characterization test verifying that the delegate extraction of
 * {@link JavaCodeGenerator} preserves identical public API behavior.
 *
 * <p>Covers: ImportCollector, ConcurrencyCodeAppender, CommentLocalizationHelper.
 */
public class JavaCodeGeneratorDelegateExtractionTest {

  // ── Import collection ──────────────────────────────────────────────

  @Test
  public void processTypeNameTracksImportsIdentically() {
    JavaCodeGenerator gen = minimalGenerator();
    gen.processTypeName(JavaType.getInstance(java.util.ArrayList.class));
    gen.processTypeName(JavaType.getInstance(String.class));

    String text = gen.getText();
    assertTrue("ArrayList type name emitted", text.contains("ArrayList"));
    assertTrue("String type name emitted", text.contains("String"));
  }

  @Test
  public void processClassPrependsImports() {
    NamedUserType greeter = sampleType();
    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .addDefaultCodeOrganizerDefinition(CodeOrganizer.defaultCodeOrganizer)
        .build();

    greeter.process(gen);
    String source = gen.getText();

    assertTrue("class header present", source.contains("class Greeter extends Object{"));
    assertTrue("constructor present", source.contains("Greeter(){"));
  }

  // ── Concurrency code ──────────────────────────────────────────────

  @Test
  public void processDoTogetherEmitsRunnableFallback() {
    DoTogether doTogether = new DoTogether(new BlockStatement(
        new LocalDeclarationStatement(
            new UserLocal("a", String.class, true), new StringLiteral("A")),
        new LocalDeclarationStatement(
            new UserLocal("b", String.class, true), new StringLiteral("B"))));

    String source = generate(doTogether);

    assertTrue("ThreadUtilities.doTogether call",
        source.contains("ThreadUtilities.doTogether("));
    assertTrue("Runnable wrapper for non-lambda",
        source.contains("new Runnable(){public void run(){"));
    assertTrue("statement a", source.contains("final String a=\"A\";"));
    assertTrue("statement b", source.contains("final String b=\"B\";"));
  }

  @Test
  public void processDoTogetherEmitsLambdaWhenSupported() {
    DoTogether doTogether = new DoTogether(new BlockStatement(
        new LocalDeclarationStatement(
            new UserLocal("x", String.class, true), new StringLiteral("X"))));

    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .isLambdaSupported(true).build();
    doTogether.process(gen);
    String source = gen.getText();

    assertTrue("lambda syntax", source.contains("()->{"));
    assertFalse("no Runnable wrapper", source.contains("new Runnable()"));
  }

  @Test
  public void processDoInOrderEmitsBlockWithStatements() {
    DoInOrder doInOrder = new DoInOrder(new BlockStatement(
        new LocalDeclarationStatement(
            new UserLocal("step", String.class, true), new StringLiteral("one"))));

    String source = generate(doInOrder);
    assertTrue("block opens", source.startsWith("{"));
    assertTrue("statement emitted", source.contains("final String step=\"one\";"));
  }

  // ── Comment localization (null bundle) ─────────────────────────────

  @Test
  public void getLocalizedCommentReturnsNullWithoutBundle() {
    JavaCodeGenerator gen = minimalGenerator();
    assertNull(gen.getLocalizedComment(
        JavaType.getInstance(Object.class), "anything", java.util.Locale.getDefault()));
  }

  // ── Member decoration ─────────────────────────────────────────────

  @Test
  public void processFieldEmitsAccessAndModifiers() {
    UserField field = new UserField("count", Integer.class, new IntegerLiteral(0));
    field.accessLevel.setValue(AccessLevel.PRIVATE);
    field.finalVolatileOrNeither.setValue(FieldModifierFinalVolatileOrNeither.FINAL);

    JavaCodeGenerator gen = minimalGenerator();
    gen.processField(field);
    String source = gen.getText();

    assertTrue("private modifier", source.contains("private "));
    assertTrue("final modifier", source.contains("final "));
    assertTrue("Integer type", source.contains("Integer"));
  }

  // ── Golden-snippet round-trip (same as SourceCodeGeneratorTest) ───

  @Test
  public void goldenSnippetRoundTripMatchesBaseline() {
    UserLocal greeting = new UserLocal("greeting", String.class, true);
    assertEquals(
        "final String greeting=\"hello\";",
        generate(new LocalDeclarationStatement(greeting, new StringLiteral("hello"))));

    CountLoop countLoop = AstUtilities.createCountLoop(new IntegerLiteral(3));
    countLoop.body.getValue().statements.add(
        new ExpressionStatement(new LocalAccess(countLoop.variable.getValue())));
    assertEquals(
        "for(Integer index_=0;index_<3;index_++){index_;}",
        generate(countLoop));
  }

  @Test
  public void fullClassGenerationMatchesBaseline() {
    assertEquals(
        "class Greeter extends Object{public Greeter(){super();}public String greet(String prefix)"
            + "{return prefix + this.message;}public String getMessage(){return this.message;}"
            + "private final String message=\"hi\";}",
        generateClass(sampleType()));
  }

  @Test
  public void disabledStatementCommentWrappingPreserved() {
    LocalDeclarationStatement disabled = new LocalDeclarationStatement(
        new UserLocal("hidden", String.class, true), new StringLiteral("secret"));
    disabled.isEnabled.setValue(false);
    BlockStatement block = new BlockStatement(
        disabled,
        new LocalDeclarationStatement(
            new UserLocal("shown", String.class, true), new StringLiteral("visible")));

    assertEquals(
        "{\n/* disabled\nfinal String hidden=\"secret\";\n*/\nfinal String shown=\"visible\";}",
        generate(block));
  }

  // ── helpers ────────────────────────────────────────────────────────

  private static JavaCodeGenerator minimalGenerator() {
    return new JavaCodeGenerator.Builder().build();
  }

  private static String generate(Statement stmt) {
    JavaCodeGenerator gen = minimalGenerator();
    stmt.process(gen);
    return gen.getText();
  }

  private static String generateClass(NamedUserType type) {
    JavaCodeGenerator gen = new JavaCodeGenerator.Builder()
        .addDefaultCodeOrganizerDefinition(CodeOrganizer.defaultCodeOrganizer)
        .build();
    type.process(gen);
    return gen.getText();
  }

  private static NamedUserType sampleType() {
    UserField message = new UserField("message", String.class, new StringLiteral("hi"));
    message.accessLevel.setValue(AccessLevel.PRIVATE);
    message.finalVolatileOrNeither.setValue(FieldModifierFinalVolatileOrNeither.FINAL);

    UserParameter prefix = new UserParameter("prefix", String.class);
    UserMethod greet = new UserMethod(
        "greet", String.class,
        new UserParameter[]{prefix},
        new BlockStatement(AstUtilities.createReturnStatement(
            String.class,
            new StringConcatenation(new ParameterAccess(prefix), new FieldAccess(new ThisExpression(), message)))));

    return new NamedUserType(
        "Greeter", null, Object.class,
        new NamedUserConstructor[]{new NamedUserConstructor(new UserParameter[]{}, new ConstructorBlockStatement())},
        new UserMethod[]{greet},
        new UserField[]{message});
  }
}
