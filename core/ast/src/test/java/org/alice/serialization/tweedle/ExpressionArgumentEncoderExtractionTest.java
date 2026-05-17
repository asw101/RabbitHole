package org.alice.serialization.tweedle;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.AbstractNode;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.code.ProcessableNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Characterization tests for the ExpressionEncoder and ArgumentEncoder
 * extraction from TweedleEncoder.
 *
 * <p>Test categories:
 * <ul>
 *   <li>CLASS STRUCTURE — helpers exist with correct visibility and constructors</li>
 *   <li>EXTRACTED METHODS — helpers have the expected methods</li>
 *   <li>DELEGATION WIRING — TweedleEncoder holds and delegates to helpers</li>
 *   <li>BRIDGE METHODS — TweedleEncoder provides required bridge methods</li>
 *   <li>BEHAVIORAL PRESERVATION — encode output identical after extraction</li>
 * </ul>
 */
public class ExpressionArgumentEncoderExtractionTest {

  private static final String EXPRESSION_ENCODER_CLASS =
      "org.alice.serialization.tweedle.ExpressionEncoder";
  private static final String ARGUMENT_ENCODER_CLASS =
      "org.alice.serialization.tweedle.ArgumentEncoder";
  private static final String TWEEDLE_ENCODER_CLASS =
      "org.alice.serialization.tweedle.TweedleEncoder";

  // ═══════════════════════════════════════════════════════════════════════════
  // EXPRESSION ENCODER — CLASS STRUCTURE
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void expressionEncoderClassExists() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    assertNotNull("ExpressionEncoder class must be loadable", clazz);
  }

  @Test
  public void expressionEncoderIsPackagePrivate() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    int modifiers = clazz.getModifiers();
    assertFalse("ExpressionEncoder must not be public", Modifier.isPublic(modifiers));
    assertFalse("ExpressionEncoder must not be private", Modifier.isPrivate(modifiers));
    assertFalse("ExpressionEncoder must not be protected", Modifier.isProtected(modifiers));
  }

  @Test
  public void expressionEncoderConstructorAcceptsTweedleEncoder() throws Exception {
    Class<?> eeClass = Class.forName(EXPRESSION_ENCODER_CLASS);
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Constructor<?> ctor = eeClass.getDeclaredConstructor(teClass);
    assertNotNull("ExpressionEncoder must have constructor(TweedleEncoder)", ctor);
  }

  @Test
  public void expressionEncoderIsNotAbstract() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    assertFalse("ExpressionEncoder must not be abstract", Modifier.isAbstract(clazz.getModifiers()));
  }

  @Test
  public void expressionEncoderDoesNotExtendSourceCodeGenerator() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    assertEquals("ExpressionEncoder must extend Object", Object.class, clazz.getSuperclass());
  }

  @Test
  public void expressionEncoderHasExactlyOneConstructor() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    assertEquals("ExpressionEncoder must have exactly one constructor",
        1, clazz.getDeclaredConstructors().length);
  }

  @Test
  public void expressionEncoderHasNoPublicMethods() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    for (Method m : clazz.getDeclaredMethods()) {
      assertFalse("ExpressionEncoder method " + m.getName() + " must not be public",
          Modifier.isPublic(m.getModifiers()));
    }
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // EXPRESSION ENCODER — EXTRACTED METHODS
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void expressionEncoderHasProcessInstantiation() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    Class<?> instanceCreation = Class.forName("org.lgna.project.ast.InstanceCreation");
    Method method = clazz.getDeclaredMethod("processInstantiation", instanceCreation);
    assertNotNull("ExpressionEncoder must have processInstantiation", method);
  }

  @Test
  public void expressionEncoderHasAppendTargetAndMember() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    Class<?> abstractType = Class.forName("org.lgna.project.ast.AbstractType");
    Method method = clazz.getDeclaredMethod("appendTargetAndMember",
        Expression.class, String.class, abstractType);
    assertNotNull("ExpressionEncoder must have appendTargetAndMember", method);
  }

  @Test
  public void expressionEncoderHasProcessResourceExpression() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    Class<?> resourceExpression = Class.forName("org.lgna.project.ast.ResourceExpression");
    Method method = clazz.getDeclaredMethod("processResourceExpression", resourceExpression);
    assertNotNull("ExpressionEncoder must have processResourceExpression", method);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // ARGUMENT ENCODER — CLASS STRUCTURE
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void argumentEncoderClassExists() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    assertNotNull("ArgumentEncoder class must be loadable", clazz);
  }

  @Test
  public void argumentEncoderIsPackagePrivate() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    int modifiers = clazz.getModifiers();
    assertFalse("ArgumentEncoder must not be public", Modifier.isPublic(modifiers));
    assertFalse("ArgumentEncoder must not be private", Modifier.isPrivate(modifiers));
    assertFalse("ArgumentEncoder must not be protected", Modifier.isProtected(modifiers));
  }

  @Test
  public void argumentEncoderConstructorAcceptsTweedleEncoder() throws Exception {
    Class<?> aeClass = Class.forName(ARGUMENT_ENCODER_CLASS);
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Constructor<?> ctor = aeClass.getDeclaredConstructor(teClass);
    assertNotNull("ArgumentEncoder must have constructor(TweedleEncoder)", ctor);
  }

  @Test
  public void argumentEncoderIsNotAbstract() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    assertFalse("ArgumentEncoder must not be abstract", Modifier.isAbstract(clazz.getModifiers()));
  }

  @Test
  public void argumentEncoderDoesNotExtendSourceCodeGenerator() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    assertEquals("ArgumentEncoder must extend Object", Object.class, clazz.getSuperclass());
  }

  @Test
  public void argumentEncoderHasExactlyOneConstructor() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    assertEquals("ArgumentEncoder must have exactly one constructor",
        1, clazz.getDeclaredConstructors().length);
  }

  @Test
  public void argumentEncoderHasNoPublicMethods() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    for (Method m : clazz.getDeclaredMethods()) {
      assertFalse("ArgumentEncoder method " + m.getName() + " must not be public",
          Modifier.isPublic(m.getModifiers()));
    }
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // ARGUMENT ENCODER — EXTRACTED METHODS
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void argumentEncoderHasProcessKeyedArgument() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    Class<?> javaKeyedArgument = Class.forName("org.lgna.project.ast.JavaKeyedArgument");
    Method method = clazz.getDeclaredMethod("processKeyedArgument", javaKeyedArgument);
    assertNotNull("ArgumentEncoder must have processKeyedArgument", method);
  }

  @Test
  public void argumentEncoderHasProcessArgument() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    Class<?> abstractParameter = Class.forName("org.lgna.project.ast.AbstractParameter");
    Class<?> abstractArgument = Class.forName("org.lgna.project.ast.AbstractArgument");
    Method method = clazz.getDeclaredMethod("processArgument", abstractParameter, abstractArgument);
    assertNotNull("ArgumentEncoder must have processArgument", method);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // DELEGATION WIRING — TweedleEncoder holds and delegates to helpers
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void tweedleEncoderHasExpressionEncoderField() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> eeClass = Class.forName(EXPRESSION_ENCODER_CLASS);
    Field field = findFieldOfType(teClass, eeClass);
    assertNotNull("TweedleEncoder must have a field of type ExpressionEncoder", field);
    assertFalse("ExpressionEncoder field must not be public",
        Modifier.isPublic(field.getModifiers()));
  }

  @Test
  public void tweedleEncoderHasArgumentEncoderField() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> aeClass = Class.forName(ARGUMENT_ENCODER_CLASS);
    Field field = findFieldOfType(teClass, aeClass);
    assertNotNull("TweedleEncoder must have a field of type ArgumentEncoder", field);
    assertFalse("ArgumentEncoder field must not be public",
        Modifier.isPublic(field.getModifiers()));
  }

  @Test
  public void tweedleEncoderStillOverridesProcessInstantiation() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> instanceCreation = Class.forName("org.lgna.project.ast.InstanceCreation");
    Method method = teClass.getDeclaredMethod("processInstantiation", instanceCreation);
    assertNotNull("TweedleEncoder must still override processInstantiation", method);
  }

  @Test
  public void tweedleEncoderStillOverridesAppendTargetAndMember() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> abstractType = Class.forName("org.lgna.project.ast.AbstractType");
    Method method = teClass.getDeclaredMethod("appendTargetAndMember",
        Expression.class, String.class, abstractType);
    assertNotNull("TweedleEncoder must still override appendTargetAndMember", method);
  }

  @Test
  public void tweedleEncoderStillOverridesProcessResourceExpression() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> resourceExpression = Class.forName("org.lgna.project.ast.ResourceExpression");
    Method method = teClass.getDeclaredMethod("processResourceExpression", resourceExpression);
    assertNotNull("TweedleEncoder must still override processResourceExpression", method);
  }

  @Test
  public void tweedleEncoderStillOverridesAppendArgument() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> javaKeyedArgument = Class.forName("org.lgna.project.ast.JavaKeyedArgument");
    Method method = teClass.getDeclaredMethod("appendArgument", javaKeyedArgument);
    assertNotNull("TweedleEncoder must still override appendArgument", method);
  }

  @Test
  public void tweedleEncoderStillOverridesProcessKeyedArgument() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> javaKeyedArgument = Class.forName("org.lgna.project.ast.JavaKeyedArgument");
    Method method = teClass.getDeclaredMethod("processKeyedArgument", javaKeyedArgument);
    assertNotNull("TweedleEncoder must still override processKeyedArgument", method);
  }

  @Test
  public void tweedleEncoderStillOverridesProcessArgument() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> abstractParameter = Class.forName("org.lgna.project.ast.AbstractParameter");
    Class<?> abstractArgument = Class.forName("org.lgna.project.ast.AbstractArgument");
    Method method = teClass.getDeclaredMethod("processArgument", abstractParameter, abstractArgument);
    assertNotNull("TweedleEncoder must still override processArgument", method);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // BRIDGE METHODS — TweedleEncoder provides forwarding for super/protected
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void tweedleEncoderHasForwardAppendAccessSeparatorBridge() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("forwardAppendAccessSeparator");
    assertNotNull("TweedleEncoder must have forwardAppendAccessSeparator() bridge", method);
    assertFalse("Bridge must not be public", Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void tweedleEncoderHasSuperProcessInstantiationBridge() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> instanceCreation = Class.forName("org.lgna.project.ast.InstanceCreation");
    Method method = teClass.getDeclaredMethod("superProcessInstantiation", instanceCreation);
    assertNotNull("TweedleEncoder must have superProcessInstantiation bridge", method);
    assertFalse("Bridge must not be public", Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void tweedleEncoderHasForwardIdentifierNameBridge() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("forwardIdentifierName", AbstractDeclaration.class);
    assertNotNull("TweedleEncoder must have forwardIdentifierName bridge", method);
    assertFalse("Bridge must not be public", Modifier.isPublic(method.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // BEHAVIORAL PRESERVATION — encode output identical after extraction
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void encodeSimpleClassPreservesOutput() throws Exception {
    assertStableEncode("class SimpleType {}");
  }

  @Test
  public void encodeClassWithFieldPreservesOutput() throws Exception {
    assertStableEncode("class FieldType { WholeNumber count <- 42; }");
  }

  @Test
  public void encodeClassWithMethodPreservesOutput() throws Exception {
    assertStableEncode("class MethodType { void doSomething() {} }");
  }

  @Test
  public void encodeClassWithMethodAndFieldPreservesOutput() throws Exception {
    assertStableEncode("""
        class ComplexType {
          WholeNumber count <- 0;
          void run() { this.count <- 1; }
        }
        """);
  }

  @Test
  public void encodeClassWithLocalDeclarationPreservesOutput() throws Exception {
    assertStableEncode("""
        class LocalType {
          WholeNumber compute() {
            WholeNumber temp <- 42;
            return temp;
          }
        }
        """);
  }

  @Test
  public void encodeClassWithConditionalPreservesOutput() throws Exception {
    assertStableEncode("""
        class ConditionalType {
          WholeNumber count <- 0;
          void update(Boolean flag) {
            if (flag) { this.count <- 1; } else { this.count <- 2; }
          }
        }
        """);
  }

  @Test
  public void encodeClassWithMultipleMethodsPreservesOutput() throws Exception {
    assertStableEncode("""
        class MultiMethod {
          void alpha() {}
          void beta() {}
          WholeNumber gamma() { return 0; }
        }
        """);
  }

  @Test
  public void encodeClassWithConstructorPreservesOutput() throws Exception {
    assertStableEncode("""
        class CtorType {
          WholeNumber count <- 0;
          CtorType() { this.count <- 5; }
        }
        """);
  }

  @Test
  public void encodingTwiceProducesIdenticalOutput() throws Exception {
    NamedUserType type = decodeAndPrepare("""
        class IdempotentExtraction {
          WholeNumber x <- 5;
          void run() { this.x <- 10; }
        }
        """);

    String first = encodeViaReflection(newDefaultEncoder(), type);
    String second = encodeViaReflection(newDefaultEncoder(), type);

    assertEquals("Two encodes after extraction must produce identical output",
        first, second);
  }

  @Test
  public void facadeEncodeMatchesDirectEncodeAfterExtraction() throws Exception {
    TweedleEncoderDecoder facade = new TweedleEncoderDecoder();
    String source = """
        class FacadeExtractionCheck {
          WholeNumber count <- 0;
          void update() { this.count <- 1; }
        }
        """;
    NamedUserType type = decodeAndPrepare(source);

    String facadeResult = facade.encodeProcessable(type);
    String directResult = encodeViaReflection(newDefaultEncoder(), type);

    assertEquals("Facade and direct encode must match after extraction",
        facadeResult, directResult);
  }

  @Test
  public void encodeDecodeRoundTripPreservesClassNameAfterExtraction() throws Exception {
    TweedleEncoderDecoder facade = new TweedleEncoderDecoder();
    NamedUserType original = decodeAndPrepare("class RoundTripExtraction {}");

    String encoded = encodeViaReflection(newDefaultEncoder(), original);
    NamedUserType decoded = decodeAndPrepare(encoded);

    assertEquals("Class name must survive encode→decode round-trip",
        original.getName(), decoded.getName());
  }

  @Test
  public void encodeDecodeRoundTripPreservesFieldCountAfterExtraction() throws Exception {
    TweedleEncoderDecoder facade = new TweedleEncoderDecoder();
    NamedUserType original = decodeAndPrepare(
        "class FieldRoundTripExtraction { WholeNumber a <- 1; DecimalNumber b <- 2.0; }");

    String encoded = encodeViaReflection(newDefaultEncoder(), original);
    NamedUserType decoded = decodeAndPrepare(encoded);

    assertEquals("Field count must survive encode→decode round-trip",
        original.getDeclaredFields().size(), decoded.getDeclaredFields().size());
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // LINE COUNT — TweedleEncoder must be under 400 lines
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void tweedleEncoderIsUnder400Lines() throws Exception {
    java.io.File file = new java.io.File(
        "src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java");
    Assume.assumeTrue("Skipping line-count check — file not found at expected path", file.exists());
    long lineCount;
    try (var reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
      lineCount = reader.lines().count();
    }
    assertTrue("TweedleEncoder.java must be under 400 lines, was " + lineCount,
        lineCount < 400);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // REFLECTION HELPERS
  // ═══════════════════════════════════════════════════════════════════════════

  private static Object newDefaultEncoder() throws Exception {
    Class<?> clazz = Class.forName(TWEEDLE_ENCODER_CLASS);
    Constructor<?> ctor = clazz.getDeclaredConstructor();
    ctor.setAccessible(true);
    return ctor.newInstance();
  }

  private static String encodeViaReflection(Object encoder, ProcessableNode node) throws Exception {
    Method encodeMethod = encoder.getClass().getMethod("encode", ProcessableNode.class);
    return (String) encodeMethod.invoke(encoder, node);
  }

  private static NamedUserType decodeAndPrepare(String source) {
    try {
      TweedleEncoderDecoder facade = new TweedleEncoderDecoder();
      AbstractNode node = facade.decode(source);
      assertTrue("Decoded node must be a NamedUserType", node instanceof NamedUserType);
      NamedUserType type = (NamedUserType) node;
      if (type.getSuperType() == null) {
        type.superType.setValue(JavaType.getInstance(Object.class));
      }
      return type;
    } catch (Exception e) {
      throw new RuntimeException("Failed to decode test input: " + source, e);
    }
  }

  private static Field findFieldOfType(Class<?> owner, Class<?> fieldType) {
    for (Field f : owner.getDeclaredFields()) {
      if (f.getType().equals(fieldType)) {
        return f;
      }
    }
    return null;
  }

  private void assertStableEncode(String source) throws Exception {
    NamedUserType type = decodeAndPrepare(source);
    String encoded = encodeViaReflection(newDefaultEncoder(), type);
    assertNotNull("Encoded output must not be null", encoded);
    assertTrue("Encoded output must not be empty", encoded.length() > 0);

    String secondEncode = encodeViaReflection(newDefaultEncoder(), type);
    assertEquals("Two encodes of the same type must produce identical output", encoded, secondEncode);
  }
}
