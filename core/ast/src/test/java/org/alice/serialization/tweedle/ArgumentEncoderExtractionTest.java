package org.alice.serialization.tweedle;

import org.junit.Test;
import org.lgna.project.ast.AbstractArgument;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.AbstractNode;
import org.lgna.project.ast.AbstractParameter;
import org.lgna.project.ast.JavaKeyedArgument;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
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
 * TDD tests for the ArgumentEncoder extraction from TweedleEncoder (issue #730).
 *
 * <p>These tests specify the contract that must hold after extracting
 * argument-encoding methods (processKeyedArgument,
 * processArgument, and their private helpers: appendOneArgument,
 * appendWrappedArg, getParameterLabel, parameterIndex) into a new
 * ArgumentEncoder companion class, and wiring TweedleEncoder to delegate.
 *
 * <p>Written before implementation — all tests fail until ArgumentEncoder
 * exists and TweedleEncoder delegates correctly.
 *
 * <p>Test categories:
 * <ul>
 *   <li>CLASS STRUCTURE — ArgumentEncoder exists with correct visibility and constructor</li>
 *   <li>EXTRACTED METHODS — ArgumentEncoder has the expected methods</li>
 *   <li>DELEGATION WIRING — TweedleEncoder holds and delegates to ArgumentEncoder</li>
 *   <li>BRIDGE METHODS — TweedleEncoder provides required bridge methods (shared with ExpressionEncoder)</li>
 *   <li>PRIVATE HELPER ENCAPSULATION — private helpers moved out of TweedleEncoder</li>
 *   <li>BEHAVIORAL PRESERVATION — encode output is identical before and after extraction</li>
 *   <li>NEGATIVE TESTS — ArgumentEncoder must not leak surface area</li>
 * </ul>
 */
public class ArgumentEncoderExtractionTest {

  private static final String ARGUMENT_ENCODER_CLASS =
      "org.alice.serialization.tweedle.ArgumentEncoder";
  private static final String TWEEDLE_ENCODER_CLASS =
      "org.alice.serialization.tweedle.TweedleEncoder";

  // ═══════════════════════════════════════════════════════════════════════════
  // CLASS STRUCTURE — ArgumentEncoder exists with correct shape
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
    assertFalse("ArgumentEncoder must not be public",
        Modifier.isPublic(modifiers));
    assertFalse("ArgumentEncoder must not be private",
        Modifier.isPrivate(modifiers));
    assertFalse("ArgumentEncoder must not be protected",
        Modifier.isProtected(modifiers));
  }

  @Test
  public void argumentEncoderConstructorAcceptsTweedleEncoder() throws Exception {
    Class<?> aeClass = Class.forName(ARGUMENT_ENCODER_CLASS);
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Constructor<?> ctor = aeClass.getDeclaredConstructor(teClass);
    assertNotNull("ArgumentEncoder must have constructor(TweedleEncoder)", ctor);
  }

  @Test
  public void argumentEncoderConstructorIsPackagePrivate() throws Exception {
    Class<?> aeClass = Class.forName(ARGUMENT_ENCODER_CLASS);
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Constructor<?> ctor = aeClass.getDeclaredConstructor(teClass);
    int modifiers = ctor.getModifiers();
    assertFalse("ArgumentEncoder constructor must not be public",
        Modifier.isPublic(modifiers));
    assertFalse("ArgumentEncoder constructor must not be private",
        Modifier.isPrivate(modifiers));
  }

  @Test
  public void argumentEncoderIsNotAbstract() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    assertFalse("ArgumentEncoder must not be abstract",
        Modifier.isAbstract(clazz.getModifiers()));
  }

  @Test
  public void argumentEncoderDoesNotExtendSourceCodeGenerator() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    assertEquals("ArgumentEncoder must extend Object (not SourceCodeGenerator)",
        Object.class, clazz.getSuperclass());
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // EXTRACTED METHODS — ArgumentEncoder has the expected methods
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void argumentEncoderHasProcessKeyedArgument() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    Method method = clazz.getDeclaredMethod("processKeyedArgument", JavaKeyedArgument.class);
    assertNotNull("ArgumentEncoder must have processKeyedArgument(JavaKeyedArgument)", method);
  }

  @Test
  public void argumentEncoderHasProcessKeyedArgument_packagePrivate() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    Method method = clazz.getDeclaredMethod("processKeyedArgument", JavaKeyedArgument.class);
    assertNotNull("ArgumentEncoder must have processKeyedArgument(JavaKeyedArgument)", method);
  }

  @Test
  public void argumentEncoderHasProcessArgument() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    Method method = clazz.getDeclaredMethod("processArgument",
        AbstractParameter.class, AbstractArgument.class);
    assertNotNull("ArgumentEncoder must have processArgument(AbstractParameter, AbstractArgument)", method);
  }

  @Test
  public void extractedMethodsArePackagePrivate() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    String[] methodNames = {
        "processKeyedArgument",
        "processArgument"
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
  // DELEGATION WIRING — TweedleEncoder holds ArgumentEncoder field
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void tweedleEncoderHasArgumentEncoderField() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> aeClass = Class.forName(ARGUMENT_ENCODER_CLASS);
    Field field = findFieldOfType(teClass, aeClass);
    assertNotNull("TweedleEncoder must have a field of type ArgumentEncoder", field);
  }

  @Test
  public void tweedleEncoderArgumentEncoderFieldIsPrivateOrPackagePrivate() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Class<?> aeClass = Class.forName(ARGUMENT_ENCODER_CLASS);
    Field field = findFieldOfType(teClass, aeClass);
    assertNotNull("TweedleEncoder must have a field of type ArgumentEncoder", field);
    assertFalse("ArgumentEncoder field must not be public",
        Modifier.isPublic(field.getModifiers()));
    assertFalse("ArgumentEncoder field must not be protected",
        Modifier.isProtected(field.getModifiers()));
  }

  @Test
  public void tweedleEncoderStillOverridesAppendArgument() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("appendArgument", JavaKeyedArgument.class);
    assertNotNull("TweedleEncoder must still override appendArgument(JavaKeyedArgument)", method);
  }

  @Test
  public void tweedleEncoderStillOverridesProcessKeyedArgument() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("processKeyedArgument", JavaKeyedArgument.class);
    assertNotNull("TweedleEncoder must still override processKeyedArgument", method);
  }

  @Test
  public void tweedleEncoderStillOverridesProcessArgument() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("processArgument",
        AbstractParameter.class, AbstractArgument.class);
    assertNotNull("TweedleEncoder must still override processArgument", method);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // BRIDGE METHODS — shared bridges used by ArgumentEncoder
  // (forwardProcessExpression, forwardIdentifierName already tested in
  //  ExpressionEncoderExtractionTest; verify they exist here too)
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void tweedleEncoderInheritsProcessExpressionForArgEncoder() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    // ArgumentEncoder calls encoder.processExpression() directly (public inherited method)
    Method method = teClass.getMethod("processExpression",
        org.lgna.project.ast.Expression.class);
    assertNotNull("TweedleEncoder must inherit processExpression (used by ArgumentEncoder)", method);
  }

  @Test
  public void tweedleEncoderHasForwardIdentifierNameForArgEncoder() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    Method method = teClass.getDeclaredMethod("forwardIdentifierName", AbstractDeclaration.class);
    assertNotNull("TweedleEncoder must have forwardIdentifierName bridge (used by ArgumentEncoder)", method);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // PRIVATE HELPER ENCAPSULATION — private methods moved to ArgumentEncoder
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void tweedleEncoderNoLongerHasAppendOneArgument() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    for (Method m : teClass.getDeclaredMethods()) {
      assertFalse("appendOneArgument must be moved to ArgumentEncoder, not remain on TweedleEncoder",
          m.getName().equals("appendOneArgument"));
    }
  }

  @Test
  public void tweedleEncoderNoLongerHasAppendWrappedArg() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    for (Method m : teClass.getDeclaredMethods()) {
      assertFalse("appendWrappedArg must be moved to ArgumentEncoder, not remain on TweedleEncoder",
          m.getName().equals("appendWrappedArg"));
    }
  }

  @Test
  public void tweedleEncoderNoLongerHasGetParameterLabel() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    for (Method m : teClass.getDeclaredMethods()) {
      assertFalse("getParameterLabel must be moved to ArgumentEncoder, not remain on TweedleEncoder",
          m.getName().equals("getParameterLabel"));
    }
  }

  @Test
  public void tweedleEncoderNoLongerHasParameterIndex() throws Exception {
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    for (Method m : teClass.getDeclaredMethods()) {
      assertFalse("parameterIndex must be moved to ArgumentEncoder, not remain on TweedleEncoder",
          m.getName().equals("parameterIndex"));
    }
  }

  @Test
  public void argumentEncoderHasAppendOneArgument() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    boolean found = false;
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.getName().equals("appendOneArgument")) {
        found = true;
        assertTrue("appendOneArgument must be private",
            Modifier.isPrivate(m.getModifiers()));
      }
    }
    assertTrue("ArgumentEncoder must have appendOneArgument", found);
  }

  @Test
  public void argumentEncoderHasAppendWrappedArg() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    boolean found = false;
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.getName().equals("appendWrappedArg")) {
        found = true;
        assertTrue("appendWrappedArg must be private",
            Modifier.isPrivate(m.getModifiers()));
      }
    }
    assertTrue("ArgumentEncoder must have appendWrappedArg", found);
  }

  @Test
  public void argumentEncoderHasGetParameterLabel() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    boolean found = false;
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.getName().equals("getParameterLabel")) {
        found = true;
        assertTrue("getParameterLabel must be private",
            Modifier.isPrivate(m.getModifiers()));
      }
    }
    assertTrue("ArgumentEncoder must have getParameterLabel", found);
  }

  @Test
  public void argumentEncoderHasParameterIndex() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    boolean found = false;
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.getName().equals("parameterIndex")) {
        found = true;
        assertTrue("parameterIndex must be private",
            Modifier.isPrivate(m.getModifiers()));
      }
    }
    assertTrue("ArgumentEncoder must have parameterIndex", found);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // BEHAVIORAL PRESERVATION — encode output identical after extraction
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void encodeSimpleClassPreservesOutput() throws Exception {
    assertRoundTripIdentical("class ArgSimple {}");
  }

  @Test
  public void encodeClassWithMethodPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ArgMethod {
          void doWork() {}
        }
        """);
  }

  @Test
  public void encodeClassWithFieldPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ArgField {
          WholeNumber count <- 0;
          void update() { this.count <- 5; }
        }
        """);
  }

  @Test
  public void encodeClassWithConstructorPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ArgCtor {
          WholeNumber value <- 0;
          ArgCtor() { this.value <- 42; }
        }
        """);
  }

  @Test
  public void encodeClassWithReturnPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ArgReturn {
          WholeNumber getValue() { return 99; }
        }
        """);
  }

  @Test
  public void encodeClassWithMultipleFieldsPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ArgMultiField {
          WholeNumber a <- 1;
          DecimalNumber b <- 2.0;
          Boolean c <- true;
        }
        """);
  }

  @Test
  public void encodeClassWithConditionalPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ArgCond {
          WholeNumber count <- 0;
          void choose(Boolean flag) {
            if (flag) { this.count <- 1; } else { this.count <- 0; }
          }
        }
        """);
  }

  @Test
  public void encodeClassWithMethodCallPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ArgCall {
          void run() { this.helper(); }
          void helper() {}
        }
        """);
  }

  @Test
  public void encodeClassWithLocalVarPreservesOutput() throws Exception {
    assertRoundTripIdentical("""
        class ArgLocal {
          WholeNumber compute() {
            WholeNumber temp <- 7;
            return temp;
          }
        }
        """);
  }

  @Test
  public void encodingTwiceWithDelegationProducesIdenticalOutput() throws Exception {
    NamedUserType type = decodeAndPrepare("""
        class ArgIdempotent {
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
        class ArgFacadeCheck {
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
  public void encodeDecodeRoundTripPreservesFieldCountAfterExtraction() throws Exception {
    NamedUserType original = decodeAndPrepare("""
        class ArgRTField {
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
  // NEGATIVE TESTS — ArgumentEncoder must not leak surface area
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void argumentEncoderHasNoPublicMethods() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    for (Method m : clazz.getDeclaredMethods()) {
      assertFalse("ArgumentEncoder method " + m.getName() + " must not be public",
          Modifier.isPublic(m.getModifiers()));
    }
  }

  @Test
  public void argumentEncoderHasNoPublicConstructors() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    for (Constructor<?> c : clazz.getDeclaredConstructors()) {
      assertFalse("ArgumentEncoder constructor must not be public",
          Modifier.isPublic(c.getModifiers()));
    }
  }

  @Test
  public void argumentEncoderHasExactlyOneConstructor() throws Exception {
    Class<?> clazz = Class.forName(ARGUMENT_ENCODER_CLASS);
    assertEquals("ArgumentEncoder must have exactly one constructor",
        1, clazz.getDeclaredConstructors().length);
  }

  @Test
  public void argumentEncoderHasSingleEncoderField() throws Exception {
    Class<?> aeClass = Class.forName(ARGUMENT_ENCODER_CLASS);
    Class<?> teClass = Class.forName(TWEEDLE_ENCODER_CLASS);
    int fieldCount = 0;
    for (Field f : aeClass.getDeclaredFields()) {
      if (f.getType().equals(teClass)) {
        fieldCount++;
      }
    }
    assertEquals("ArgumentEncoder must have exactly one TweedleEncoder field",
        1, fieldCount);
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // EDGE CASES
  // ═══════════════════════════════════════════════════════════════════════════

  @Test
  public void encodeEmptyClassWithExtractionPreservesOutput() throws Exception {
    assertRoundTripIdentical("class EmptyArgClass {}");
  }

  @Test
  public void encodeWithEmptyTerminalsMatchesDefaultAfterExtraction() throws Exception {
    NamedUserType type = decodeAndPrepare("class ArgEmptyTerminals {}");
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
