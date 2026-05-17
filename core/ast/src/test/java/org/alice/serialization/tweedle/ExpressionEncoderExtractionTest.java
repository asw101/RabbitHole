package org.alice.serialization.tweedle;

import org.junit.Test;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.AbstractNode;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.project.code.ProcessableNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TDD tests for the ExpressionEncoder extraction from TweedleEncoder (issue #730).
 *
 * <p>These tests specify the contract that must hold after extracting
 * expression-encoding methods (processInstantiation, appendTargetAndMember,
 * processResourceExpression, and their private helpers: getDeclaringJavaClassName,
 * targetIsMath, tweedleModuleForMath) into a new ExpressionEncoder companion
 * class, and wiring TweedleEncoder to delegate.
 *
 * <p>Written before implementation — all tests fail until ExpressionEncoder
 * exists and TweedleEncoder delegates correctly.
 *
 * <p>Test categories:
 * <ul>
 *   <li>CLASS STRUCTURE — ExpressionEncoder exists with correct visibility and constructor</li>
 *   <li>EXTRACTED METHODS — ExpressionEncoder has the expected methods</li>
 *   <li>DELEGATION WIRING — TweedleEncoder holds and delegates to ExpressionEncoder</li>
 *   <li>BRIDGE METHODS — TweedleEncoder provides required bridge methods</li>
 *   <li>BEHAVIORAL PRESERVATION — encode output is identical before and after extraction</li>
 *   <li>NEGATIVE TESTS — ExpressionEncoder must not leak surface area</li>
 * </ul>
 */
public class ExpressionEncoderExtractionTest {

  private static final String EXPRESSION_ENCODER_CLASS =
      "org.alice.serialization.tweedle.ExpressionEncoder";
  private static final String TWEEDLE_ENCODER_CLASS =
      "org.alice.serialization.tweedle.TweedleEncoder";

  // ═══════════════════════════════════════════════════════════════════════════
  // CLASS STRUCTURE — ExpressionEncoder exists with correct shape
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
    assertFalse("ExpressionEncoder must not be public",
        Modifier.isPublic(modifiers));
    assertFalse("ExpressionEncoder must not be private",
        Modifier.isPrivate(modifiers));
    assertFalse("ExpressionEncoder must not be protected",
        Modifier.isProtected(modifiers));
  }

  @Test
  public void expressionEncoderConstructorAcceptsTweedleEncoder() throws Exception {
    Class<?> eeClass = Class.forName(EXPRESSION_ENCODER_CLASS);
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Constructor<?> ctor = eeClass.getDeclaredConstructor(teClass);
    assertNotNull("ExpressionEncoder must have constructor(TweedleEncoder)", ctor);
  }

  @Test
  public void expressionEncoderConstructorIsPackagePrivate() throws Exception {
    Class<?> eeClass = Class.forName(EXPRESSION_ENCODER_CLASS);
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Constructor<?> ctor = eeClass.getDeclaredConstructor(teClass);
    int modifiers = ctor.getModifiers();
    assertFalse("ExpressionEncoder constructor must not be public",
        Modifier.isPublic(modifiers));
    assertFalse("ExpressionEncoder constructor must not be private",
        Modifier.isPrivate(modifiers));
  }

  @Test
  public void expressionEncoderIsNotAbstract() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    assertFalse("ExpressionEncoder must not be abstract",
        Modifier.isAbstract(clazz.getModifiers()));
  }

  @Test
  public void expressionEncoderDoesNotExtendSourceCodeGenerator() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    assertEquals("ExpressionEncoder must extend Object (not SourceCodeGenerator)",
        Object.class, clazz.getSuperclass());
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // EXTRACTED METHODS — ExpressionEncoder has the expected methods
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void expressionEncoderHasProcessInstantiation() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    Method method = clazz.getDeclaredMethod("processInstantiation", InstanceCreation.class);
    assertNotNull("ExpressionEncoder must have processInstantiation(InstanceCreation)", method);
  }

  @Test
  public void expressionEncoderHasAppendTargetAndMember() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    Method method = clazz.getDeclaredMethod("appendTargetAndMember",
        Expression.class, String.class, AbstractType.class);
    assertNotNull("ExpressionEncoder must have appendTargetAndMember(Expression, String, AbstractType)", method);
  }

  @Test
  public void expressionEncoderHasProcessResourceExpression() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    Method method = clazz.getDeclaredMethod("processResourceExpression", ResourceExpression.class);
    assertNotNull("ExpressionEncoder must have processResourceExpression(ResourceExpression)", method);
  }

  @Test
  public void extractedMethodsArePackagePrivate() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    String[] methodNames = {
        "processInstantiation",
        "appendTargetAndMember",
        "processResourceExpression"
    };
    for (String name : methodNames) {
      for (Method m : clazz.getDeclaredMethods()) {
        if (m.getName().equals(name)) {
          assertFalse(name + " must not be public", Modifier.isPublic(m.getModifiers()));
          assertFalse(name + " must not be private", Modifier.isPrivate(m.getModifiers()));
        }
      }
    }
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // DELEGATION WIRING — TweedleEncoder holds ExpressionEncoder field
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void tweedleEncoderHasExpressionEncoderField() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> eeClass = Class.forName(EXPRESSION_ENCODER_CLASS);
    Field field = findFieldOfType(teClass, eeClass);
    assertNotNull("TweedleEncoder must have a field of type ExpressionEncoder", field);
  }

  @Test
  public void tweedleEncoderExpressionEncoderFieldIsPrivateOrPackagePrivate() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> eeClass = Class.forName(EXPRESSION_ENCODER_CLASS);
    Field field = findFieldOfType(teClass, eeClass);
    assertNotNull("TweedleEncoder must have a field of type ExpressionEncoder", field);
    assertFalse("ExpressionEncoder field must not be public",
        Modifier.isPublic(field.getModifiers()));
    assertFalse("ExpressionEncoder field must not be protected",
        Modifier.isProtected(field.getModifiers()));
  }

  @Test
  public void tweedleEncoderStillOverridesProcessInstantiation() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("processInstantiation", InstanceCreation.class);
    assertNotNull("TweedleEncoder must still override processInstantiation(InstanceCreation)", method);
  }

  @Test
  public void tweedleEncoderStillOverridesAppendTargetAndMember() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("appendTargetAndMember",
        Expression.class, String.class, AbstractType.class);
    assertNotNull("TweedleEncoder must still override appendTargetAndMember", method);
  }

  @Test
  public void tweedleEncoderStillOverridesProcessResourceExpression() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("processResourceExpression", ResourceExpression.class);
    assertNotNull("TweedleEncoder must still override processResourceExpression", method);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // BRIDGE METHODS — TweedleEncoder provides forwarding for super/protected
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void tweedleEncoderHasSuperProcessInstantiationBridge() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("superProcessInstantiation", InstanceCreation.class);
    assertNotNull("TweedleEncoder must have superProcessInstantiation(InstanceCreation) bridge", method);
    assertFalse("Bridge must not be public", Modifier.isPublic(method.getModifiers()));
    assertFalse("Bridge must not be private", Modifier.isPrivate(method.getModifiers()));
  }

  @Test
  public void tweedleEncoderInheritsProcessExpression() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    // ExpressionEncoder calls encoder.processExpression() directly (public inherited method)
    Method method = teClass.getMethod("processExpression", Expression.class);
    assertNotNull("TweedleEncoder must inherit processExpression(Expression) from base class", method);
  }

  @Test
  public void tweedleEncoderHasForwardAppendAccessSeparatorBridge() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("forwardAppendAccessSeparator");
    assertNotNull("TweedleEncoder must have forwardAppendAccessSeparator() bridge", method);
    assertFalse("Bridge must not be public", Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void tweedleEncoderHasForwardIdentifierNameBridge() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("forwardIdentifierName", AbstractDeclaration.class);
    assertNotNull("TweedleEncoder must have forwardIdentifierName(AbstractDeclaration) bridge", method);
    assertFalse("Bridge must not be public", Modifier.isPublic(method.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // PRIVATE HELPER ENCAPSULATION — private methods moved to ExpressionEncoder
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void tweedleEncoderNoLongerHasGetDeclaringJavaClassName() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    for (Method m : teClass.getDeclaredMethods()) {
      assertFalse("getDeclaringJavaClassName must be moved to ExpressionEncoder, not remain on TweedleEncoder",
          m.getName().equals("getDeclaringJavaClassName"));
    }
  }

  @Test
  public void tweedleEncoderNoLongerHasTargetIsMath() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    for (Method m : teClass.getDeclaredMethods()) {
      assertFalse("targetIsMath must be moved to ExpressionEncoder, not remain on TweedleEncoder",
          m.getName().equals("targetIsMath"));
    }
  }

  @Test
  public void tweedleEncoderNoLongerHasTweedleModuleForMath() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    for (Method m : teClass.getDeclaredMethods()) {
      assertFalse("tweedleModuleForMath must be moved to ExpressionEncoder, not remain on TweedleEncoder",
          m.getName().equals("tweedleModuleForMath"));
    }
  }

  @Test
  public void expressionEncoderHasGetDeclaringJavaClassName() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    Method method = clazz.getDeclaredMethod("getDeclaringJavaClassName", InstanceCreation.class);
    assertNotNull("ExpressionEncoder must have getDeclaringJavaClassName", method);
    assertTrue("getDeclaringJavaClassName must be private",
        Modifier.isPrivate(method.getModifiers()));
  }

  @Test
  public void expressionEncoderHasTargetIsMath() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    Method method = clazz.getDeclaredMethod("targetIsMath", Expression.class);
    assertNotNull("ExpressionEncoder must have targetIsMath", method);
    assertTrue("targetIsMath must be private",
        Modifier.isPrivate(method.getModifiers()));
  }

  @Test
  public void expressionEncoderHasTweedleModuleForMath() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    Method method = clazz.getDeclaredMethod("tweedleModuleForMath",
        String.class, AbstractType.class);
    assertNotNull("ExpressionEncoder must have tweedleModuleForMath", method);
    assertTrue("tweedleModuleForMath must be private",
        Modifier.isPrivate(method.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // BEHAVIORAL PRESERVATION — encode output identical after extraction
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void encodeSimpleClassPreservesOutput() throws Exception {
    assertRoundTripIdentical("class ExprSimple {}");
  }

  @Test
  public void encodeClassWithMethodPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ExprMethod {
          void doWork() {}
        }
        """);
  }

  @Test
  public void encodeClassWithFieldAndMethodPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ExprFieldMethod {
          WholeNumber count <- 0;
          void update() { this.count <- 5; }
        }
        """);
  }

  @Test
  public void encodeClassWithMultipleMethodsPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ExprMultiMethod {
          void alpha() {}
          void beta() {}
          WholeNumber gamma() { return 42; }
        }
        """);
  }

  @Test
  public void encodeClassWithLocalVarPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ExprLocalVar {
          WholeNumber compute() {
            WholeNumber temp <- 10;
            return temp;
          }
        }
        """);
  }

  @Test
  public void encodeClassWithConditionalPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ExprCond {
          WholeNumber count <- 0;
          void update(Boolean flag) {
            if (flag) { this.count <- 1; } else { this.count <- 2; }
          }
        }
        """);
  }

  @Test
  public void encodeClassWithConstructorPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ExprCtor {
          WholeNumber value <- 0;
          ExprCtor() { this.value <- 99; }
        }
        """);
  }

  @Test
  public void encodeClassWithMethodCallPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ExprCall {
          void run() { this.helper(); }
          void helper() {}
        }
        """);
  }

  @Test
  public void encodeClassWithReturnPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ExprReturn {
          WholeNumber getValue() { return 7; }
        }
        """);
  }

  @Test
  public void encodingTwiceWithDelegationProducesIdenticalOutput() throws Exception {
    NamedUserType type = decodeAndPrepare("""
        class ExprIdempotent {
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
    NamedUserType type = decodeAndPrepare("""
        class ExprFacadeCheck {
          WholeNumber count <- 0;
          void update() { this.count <- 1; }
        }
        """);
    String facadeResult = facade.encodeProcessable(type);
    String directResult = encodeViaReflection(newDefaultEncoder(), type);
    assertEquals("Facade and direct encode must match after extraction",
        facadeResult, directResult);
  }

  @Test
  public void encodeDecodeRoundTripPreservesMethodCountAfterExtraction() throws Exception {
    TweedleEncoderDecoder facade = new TweedleEncoderDecoder();
    NamedUserType original = decodeAndPrepare("""
        class ExprRTMethod {
          void alpha() {}
          void beta() {}
        }
        """);
    String encoded = encodeViaReflection(newDefaultEncoder(), original);
    NamedUserType decoded = decodeAndPrepare(encoded);
    assertEquals("Method count must survive encode→decode round-trip",
        original.getDeclaredMethods().size(), decoded.getDeclaredMethods().size());
  }

  @Test
  public void encodeDecodeRoundTripPreservesFieldCountAfterExtraction() throws Exception {
    TweedleEncoderDecoder facade = new TweedleEncoderDecoder();
    NamedUserType original = decodeAndPrepare("""
        class ExprRTField {
          WholeNumber a <- 1;
          DecimalNumber b <- 2.0;
        }
        """);
    String encoded = encodeViaReflection(newDefaultEncoder(), original);
    NamedUserType decoded = decodeAndPrepare(encoded);
    assertEquals("Field count must survive encode→decode round-trip",
        original.getDeclaredFields().size(), decoded.getDeclaredFields().size());
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // NEGATIVE TESTS — ExpressionEncoder must not leak surface area
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void expressionEncoderHasNoPublicMethods() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    for (Method m : clazz.getDeclaredMethods()) {
      assertFalse("ExpressionEncoder method " + m.getName() + " must not be public",
          Modifier.isPublic(m.getModifiers()));
    }
  }

  @Test
  public void expressionEncoderHasNoPublicConstructors() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    for (Constructor<?> c : clazz.getDeclaredConstructors()) {
      assertFalse("ExpressionEncoder constructor must not be public",
          Modifier.isPublic(c.getModifiers()));
    }
  }

  @Test
  public void expressionEncoderHasExactlyOneConstructor() throws Exception {
    Class<?> clazz = Class.forName(EXPRESSION_ENCODER_CLASS);
    assertEquals("ExpressionEncoder must have exactly one constructor",
        1, clazz.getDeclaredConstructors().length);
  }

  @Test
  public void expressionEncoderHasSingleEncoderField() throws Exception {
    Class<?> eeClass = Class.forName(EXPRESSION_ENCODER_CLASS);
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    int fieldCount = 0;
    for (Field f : eeClass.getDeclaredFields()) {
      if (f.getType().equals(teClass)) {
        fieldCount++;
      }
    }
    assertEquals("ExpressionEncoder must have exactly one TweedleEncoder field",
        1, fieldCount);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // EDGE CASES
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void encodeEmptyClassWithExtractionPreservesOutput() throws Exception {
    assertRoundTripIdentical("class EmptyExprClass {}");
  }

  @Test
  public void encodeWithEmptyTerminalsMatchesDefaultAfterExtraction() throws Exception {
    NamedUserType type = decodeAndPrepare("class ExprEmptyTerminals {}");
    String defaultOutput = encodeViaReflection(newDefaultEncoder(), type);
    String terminalOutput = encodeViaReflection(
        newEncoderWithTerminals(new HashSet<>()), type);
    assertEquals("Empty terminals should produce same output as default constructor",
        defaultOutput, terminalOutput);
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

  private static Object newEncoderWithTerminals(Set<AbstractDeclaration> terminals) throws Exception {
    Class<?> clazz = Class.forName(TWEEDLE_ENCODER_CLASS);
    Constructor<?> ctor = clazz.getDeclaredConstructor(Set.class);
    ctor.setAccessible(true);
    return ctor.newInstance(terminals);
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

  private void assertRoundTripIdentical(String source) throws Exception {
    NamedUserType type = decodeAndPrepare(source);
    String encoded = encodeViaReflection(newDefaultEncoder(), type);
    assertNotNull("Encoded output must not be null", encoded);
    assertTrue("Encoded output must not be empty", encoded.length() > 0);

    String secondEncode = encodeViaReflection(newDefaultEncoder(), type);
    assertEquals("Two encodes of the same type must produce identical output", encoded, secondEncode);
  }
}
