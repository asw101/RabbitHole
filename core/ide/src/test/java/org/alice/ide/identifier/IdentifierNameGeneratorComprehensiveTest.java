package org.alice.ide.identifier;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for {@link IdentifierNameGenerator} covering
 * createIdentifierNameFromInstanceCreation with real AST InstanceCreation objects,
 * S-prefix stripping in instance creation context, convertConstant edge cases,
 * and createIdentifierNameFromResourceKey null/empty handling.
 *
 * <p>Complements the existing {@link IdentifierNameGeneratorTest} with deeper
 * AST-backed scenarios and comprehensive edge-case coverage.
 */
public class IdentifierNameGeneratorComprehensiveTest {

  private static final JavaType OBJECT_TYPE = JavaType.getInstance(Object.class);

  private final IdentifierNameGenerator gen = IdentifierNameGenerator.SINGLETON;

  // ── createIdentifierNameFromInstanceCreation: AST-backed tests ─────

  @Test
  public void createFromInstanceCreation_withJavaTypeConstructor_lowercasesTypeName() {
    JavaType stringType = JavaType.getInstance(String.class);
    AbstractConstructor[] ctors = stringType.getDeclaredConstructors().toArray(new AbstractConstructor[0]);
    AbstractConstructor noArgCtor = null;
    for (AbstractConstructor c : ctors) {
      if (c.getRequiredParameters().isEmpty()) {
        noArgCtor = c;
        break;
      }
    }
    assertNotNull("String should have a no-arg constructor", noArgCtor);
    InstanceCreation creation = new InstanceCreation(noArgCtor);
    assertEquals("string", gen.createIdentifierNameFromInstanceCreation(creation));
  }

  @Test
  public void createFromInstanceCreation_withSPrefixType_stripsS() {
    JavaType bipedType = JavaType.getInstance(org.lgna.story.SBiped.class);
    AbstractConstructor[] ctors = bipedType.getDeclaredConstructors().toArray(new AbstractConstructor[0]);
    assertTrue("SBiped should have constructors", ctors.length > 0);
    InstanceCreation creation = new InstanceCreation(ctors[0]);
    assertEquals("biped", gen.createIdentifierNameFromInstanceCreation(creation));
  }

  @Test
  public void createFromInstanceCreation_withNonSPrefixType_preservesName() {
    JavaType intType = JavaType.getInstance(Integer.class);
    AbstractConstructor[] ctors = intType.getDeclaredConstructors().toArray(new AbstractConstructor[0]);
    assertTrue("Integer should have constructors", ctors.length > 0);
    InstanceCreation creation = new InstanceCreation(ctors[0]);
    assertEquals("integer", gen.createIdentifierNameFromInstanceCreation(creation));
  }

  @Test
  public void createFromInstanceCreation_withNamedUserType_doesNotStripS() {
    // NamedUserType named "SMyThing" — NOT a JavaType, so S-prefix NOT stripped
    NamedUserType userType = new NamedUserType();
    userType.name.setValue("SMyThing");
    userType.superType.setValue(OBJECT_TYPE);

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    body.constructorInvocationStatement.setValue(new SuperConstructorInvocationStatement());
    ctor.body.setValue(body);
    userType.constructors.add(ctor);

    InstanceCreation creation = new InstanceCreation(ctor);
    String result = gen.createIdentifierNameFromInstanceCreation(creation);
    // NamedUserType is NOT instanceof JavaType → no S-strip → sMyThing
    assertEquals("sMyThing", result);
  }

  @Test
  public void createFromInstanceCreation_null_returnsEmptyString() {
    assertEquals("", gen.createIdentifierNameFromInstanceCreation(null));
  }

  // ── createIdentifierNameFromClassName edge cases ───────────────────

  @Test
  public void createFromClassName_null_returnsNull() {
    assertNull(gen.createIdentifierNameFromClassName(null));
  }

  @Test
  public void createFromClassName_empty_returnsEmpty() {
    assertEquals("", gen.createIdentifierNameFromClassName(""));
  }

  @Test
  public void createFromClassName_singleLower_returnsSame() {
    assertEquals("a", gen.createIdentifierNameFromClassName("a"));
  }

  @Test
  public void createFromClassName_singleUpper_lowercases() {
    assertEquals("a", gen.createIdentifierNameFromClassName("A"));
  }

  @Test
  public void createFromClassName_camelCase_lowercasesFirst() {
    assertEquals("myWidget", gen.createIdentifierNameFromClassName("MyWidget"));
  }

  @Test
  public void createFromClassName_allUppercase_lowercasesFirst() {
    assertEquals("aBC", gen.createIdentifierNameFromClassName("ABC"));
  }

  @Test
  public void createFromClassName_withDigits_lowercasesFirst() {
    assertEquals("point3D", gen.createIdentifierNameFromClassName("Point3D"));
  }

  @Test
  public void createFromClassName_alreadyLowercase_returnsSame() {
    assertEquals("myclass", gen.createIdentifierNameFromClassName("myclass"));
  }

  @Test
  public void createFromClassName_preservesNumbers() {
    assertEquals("class123", gen.createIdentifierNameFromClassName("Class123"));
  }

  // ── convertConstantNameToMethodName comprehensive ──────────────────

  @Test
  public void convertConstant_leadingUnderscore() {
    // Leading underscore sets isUpperNext=true, so first letter stays uppercase
    assertEquals("MyConst", gen.convertConstantNameToMethodName("_MY_CONST"));
  }

  @Test
  public void convertConstant_doubleUnderscore() {
    assertEquals("abC", gen.convertConstantNameToMethodName("AB__C"));
  }

  @Test
  public void convertConstant_singleCharSegments() {
    assertEquals("aBCD", gen.convertConstantNameToMethodName("A_B_C_D"));
  }

  @Test
  public void convertConstant_numbersPreserved() {
    assertEquals("x3Y4Z", gen.convertConstantNameToMethodName("X_3_Y_4_Z"));
  }

  @Test
  public void convertConstant_emptyPrefix_noChange() {
    // Empty string prefix behaves like no prefix
    assertEquals("myConst", gen.convertConstantNameToMethodName("MY_CONST", ""));
  }

  @Test
  public void convertConstant_prefixWithConstant_capitalizesFirst() {
    assertEquals("getMyValue", gen.convertConstantNameToMethodName("MY_VALUE", "get"));
  }

  @Test
  public void convertConstant_prefixWithSingleWord() {
    assertEquals("setColor", gen.convertConstantNameToMethodName("COLOR", "set"));
  }

  @Test
  public void convertConstant_allUnderscores_returnsEmpty() {
    assertEquals("", gen.convertConstantNameToMethodName("____"));
  }

  @Test
  public void convertConstant_singleChar() {
    assertEquals("a", gen.convertConstantNameToMethodName("A"));
  }

  @Test
  public void convertConstant_lowerCaseInput_keepsLowercase() {
    // When no prefix, first char is not upper-next, so stays lowercase
    assertEquals("abc", gen.convertConstantNameToMethodName("abc"));
  }

  @Test
  public void convertConstant_mixedCaseNoUnderscore() {
    assertEquals("helloworld", gen.convertConstantNameToMethodName("HelloWorld"));
  }

  @Test
  public void convertConstant_preservesDigits() {
    assertEquals("point3d", gen.convertConstantNameToMethodName("POINT_3D"));
  }

  @Test
  public void convertConstant_trailingUnderscore() {
    assertEquals("hello", gen.convertConstantNameToMethodName("HELLO_"));
  }

  @Test
  public void convertConstant_mixedCase() {
    assertEquals("helloWorld", gen.convertConstantNameToMethodName("Hello_World"));
  }

  @Test
  public void convertConstant_withIsPrefix() {
    assertEquals("isReady", gen.convertConstantNameToMethodName("READY", "is"));
  }

  // ── createIdentifierNameFromResourceKey ────────────────────────────

  @Test
  public void createFromResourceKey_null_returnsEmptyString() {
    assertEquals("", gen.createIdentifierNameFromResourceKey(null));
  }

  // ── SINGLETON identity ─────────────────────────────────────────────

  @Test
  public void singleton_isSame() {
    assertSame(IdentifierNameGenerator.SINGLETON, IdentifierNameGenerator.SINGLETON);
  }

  @Test
  public void singleton_isEnum() {
    assertTrue("SINGLETON should be an enum constant",
        IdentifierNameGenerator.SINGLETON instanceof Enum);
  }

  @Test
  public void enumValues_containsSingleton() {
    IdentifierNameGenerator[] values = IdentifierNameGenerator.values();
    assertEquals(1, values.length);
    assertSame(IdentifierNameGenerator.SINGLETON, values[0]);
  }

  @Test
  public void valueOf_SINGLETON_returnsSingleton() {
    assertSame(IdentifierNameGenerator.SINGLETON,
        IdentifierNameGenerator.valueOf("SINGLETON"));
  }

  // ── StaticAnalysisUtilities.isValidIdentifier (indirect coverage) ──

  @Test
  public void isValidIdentifier_null_returnsFalse() {
    assertFalse(StaticAnalysisUtilities.isValidIdentifier(null));
  }

  @Test
  public void isValidIdentifier_empty_returnsFalse() {
    assertFalse(StaticAnalysisUtilities.isValidIdentifier(""));
  }

  @Test
  public void isValidIdentifier_startsWithDigit_returnsFalse() {
    assertFalse(StaticAnalysisUtilities.isValidIdentifier("1abc"));
  }

  @Test
  public void isValidIdentifier_startsWithUnderscore_returnsTrue() {
    assertTrue(StaticAnalysisUtilities.isValidIdentifier("_abc"));
  }

  @Test
  public void isValidIdentifier_startsWithLetter_returnsTrue() {
    assertTrue(StaticAnalysisUtilities.isValidIdentifier("abc"));
  }

  @Test
  public void isValidIdentifier_containsSpecialChar_returnsFalse() {
    assertFalse(StaticAnalysisUtilities.isValidIdentifier("abc!def"));
  }

  @Test
  public void isValidIdentifier_containsSpace_returnsFalse() {
    assertFalse(StaticAnalysisUtilities.isValidIdentifier("abc def"));
  }

  @Test
  public void isValidIdentifier_allDigitsAfterLetter_returnsTrue() {
    assertTrue(StaticAnalysisUtilities.isValidIdentifier("x123"));
  }

  @Test
  public void isValidIdentifier_underscoreOnly_returnsTrue() {
    assertTrue(StaticAnalysisUtilities.isValidIdentifier("_"));
  }

  @Test
  public void isValidIdentifier_mixedLettersDigitsUnderscores_returnsTrue() {
    assertTrue(StaticAnalysisUtilities.isValidIdentifier("_my_var_2"));
  }

  @Test
  public void isValidIdentifier_containsHyphen_returnsFalse() {
    assertFalse(StaticAnalysisUtilities.isValidIdentifier("my-var"));
  }

  @Test
  public void isValidIdentifier_containsDot_returnsFalse() {
    assertFalse(StaticAnalysisUtilities.isValidIdentifier("my.var"));
  }

  // ── StaticAnalysisUtilities.getUserTypeDepth ───────────────────────

  @Test
  public void getUserTypeDepth_null_returnsNegativeOne() {
    assertEquals(-1, StaticAnalysisUtilities.getUserTypeDepth(null));
  }

  @Test
  public void getUserTypeDepth_javaType_returnsNegativeOne() {
    assertEquals(-1, StaticAnalysisUtilities.getUserTypeDepth(
        JavaType.getInstance(String.class)));
  }

  @Test
  public void getUserTypeDepth_namedUserType_withJavaSuperType_returnsZero() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("MyType");
    type.superType.setValue(OBJECT_TYPE);
    // depth = 1 + getUserTypeDepth(JavaType) = 1 + (-1) = 0
    assertEquals(0, StaticAnalysisUtilities.getUserTypeDepth(type));
  }

  @Test
  public void getUserTypeDepth_nestedUserTypes_returnsCorrectDepth() {
    NamedUserType parent = new NamedUserType();
    parent.name.setValue("Parent");
    parent.superType.setValue(OBJECT_TYPE);

    NamedUserType child = new NamedUserType();
    child.name.setValue("Child");
    child.superType.setValue(parent);

    // child depth = 1 + getUserTypeDepth(parent) = 1 + (1 + getUserTypeDepth(JavaType)) = 1 + 0 = 1
    assertEquals(1, StaticAnalysisUtilities.getUserTypeDepth(child));
  }

  @Test
  public void getUserTypeDepth_threeDeep_returnsTwo() {
    NamedUserType grandparent = new NamedUserType();
    grandparent.name.setValue("Grandparent");
    grandparent.superType.setValue(OBJECT_TYPE);

    NamedUserType parent = new NamedUserType();
    parent.name.setValue("Parent");
    parent.superType.setValue(grandparent);

    NamedUserType child = new NamedUserType();
    child.name.setValue("Child");
    child.superType.setValue(parent);

    assertEquals(2, StaticAnalysisUtilities.getUserTypeDepth(child));
  }
}
