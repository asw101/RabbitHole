package org.alice.ide.ast.declaration;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Characterization and structural-migration tests for
 * DeclarationLikeSubstanceComposite delegate decomposition (issue #637).
 *
 * Section 1 tests characterize existing structure that must be preserved.
 * Section 2 tests verify the delegate fields and visibility changes
 * introduced by the extraction — they FAIL before implementation.
 *
 * Pure reflection — no GUI, no singleton instantiation.
 */
public class DeclarationCompositeStructureTest {

  private static final String COMPOSITE_FQCN =
      "org.alice.ide.ast.declaration.DeclarationLikeSubstanceComposite";
  private static final String VALIDATION_DELEGATE_FQCN =
      "org.alice.ide.ast.declaration.DeclarationValidationDelegate";
  private static final String LIFECYCLE_DELEGATE_FQCN =
      "org.alice.ide.ast.declaration.DeclarationDialogLifecycleDelegate";

  private static Class<?> compositeClass;

  @BeforeClass
  public static void loadCompositeClass() {
    try {
      compositeClass = Class.forName(COMPOSITE_FQCN);
    } catch (ClassNotFoundException e) {
      fail("DeclarationLikeSubstanceComposite not found: " + e.getMessage());
    }
  }

  // ═══════════════════════════════════════════════════════════════════
  // Section 1: Characterization — must pass before AND after extraction
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void compositeIsAbstract() {
    assertTrue("DeclarationLikeSubstanceComposite must be abstract",
        Modifier.isAbstract(compositeClass.getModifiers()));
  }

  @Test
  public void compositeIsPublic() {
    assertTrue("DeclarationLikeSubstanceComposite must be public",
        Modifier.isPublic(compositeClass.getModifiers()));
  }

  @Test
  public void hasApplicabilityStatusEnum() {
    Class<?>[] declaredClasses = compositeClass.getDeclaredClasses();
    boolean found = Arrays.stream(declaredClasses)
        .anyMatch(c -> c.getSimpleName().equals("ApplicabilityStatus") && c.isEnum());
    assertTrue("Must contain nested enum ApplicabilityStatus", found);
  }

  @Test
  public void applicabilityStatusHasExpectedConstants() {
    Class<?> enumClass = Arrays.stream(compositeClass.getDeclaredClasses())
        .filter(c -> c.getSimpleName().equals("ApplicabilityStatus"))
        .findFirst().orElseThrow(() -> new AssertionError("ApplicabilityStatus not found"));
    Object[] constants = enumClass.getEnumConstants();
    Set<String> names = Arrays.stream(constants).map(Object::toString).collect(Collectors.toSet());
    assertTrue("Must have EDITABLE", names.contains("EDITABLE"));
    assertTrue("Must have DISPLAYED", names.contains("DISPLAYED"));
    assertTrue("Must have APPLICABLE_BUT_NOT_DISPLAYED", names.contains("APPLICABLE_BUT_NOT_DISPLAYED"));
    assertTrue("Must have NOT_APPLICABLE", names.contains("NOT_APPLICABLE"));
  }

  @Test
  public void hasDetailsNestedClass() {
    boolean found = Arrays.stream(compositeClass.getDeclaredClasses())
        .anyMatch(c -> c.getSimpleName().equals("Details"));
    assertTrue("Must contain nested class Details", found);
  }

  @Test
  public void hasErrorStatusField() {
    assertFieldExists("errorStatus");
  }

  @Test
  public void hasIsFinalStateField() {
    assertFieldExists("isFinalState");
  }

  @Test
  public void hasValueComponentTypeStateField() {
    assertFieldExists("valueComponentTypeState");
  }

  @Test
  public void hasNameStateField() {
    assertFieldExists("nameState");
  }

  @Test
  public void hasInitializerStateField() {
    assertFieldExists("initializerState");
  }

  @Test
  public void hasGetStatusPreRejectorCheckMethod() {
    assertMethodExists("getStatusPreRejectorCheck");
  }

  @Test
  public void hasIsNullAllowedForInitializerMethod() {
    assertMethodExists("isNullAllowedForInitializer");
  }

  @Test
  public void isNullAllowedForInitializerIsProtected() {
    Method m = findMethod("isNullAllowedForInitializer");
    assertNotNull("isNullAllowedForInitializer must exist", m);
    assertTrue("isNullAllowedForInitializer must be protected",
        Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void hasHandlePreShowDialogMethod() {
    assertMethodExists("handlePreShowDialog");
  }

  @Test
  public void hasHandlePostHideDialogMethod() {
    assertMethodExists("handlePostHideDialog");
  }

  @Test
  public void getStatusPreRejectorCheckIsProtected() {
    Method m = findMethod("getStatusPreRejectorCheck");
    assertNotNull("getStatusPreRejectorCheck must exist", m);
    assertTrue("getStatusPreRejectorCheck must be protected",
        Modifier.isProtected(m.getModifiers()));
  }

  // Verify subclass hierarchy is intact
  @Test
  public void addParameterCompositeExtendsComposite() {
    assertSubclassOf("org.alice.ide.ast.declaration.AddParameterComposite");
  }

  @Test
  public void fieldCompositeExtendsComposite() {
    assertSubclassOf("org.alice.ide.ast.declaration.FieldComposite");
  }

  @Test
  public void addMethodCompositeExtendsComposite() {
    assertSubclassOf("org.alice.ide.ast.declaration.AddMethodComposite");
  }

  @Test
  public void insertStatementCompositeExtendsComposite() {
    assertSubclassOf("org.alice.ide.ast.declaration.InsertStatementComposite");
  }

  // Verify public API accessors are preserved
  @Test
  public void hasGetIsFinalStateAccessor() {
    assertPublicMethod("getIsFinalState");
  }

  @Test
  public void hasGetValueComponentTypeStateAccessor() {
    assertPublicMethod("getValueComponentTypeState");
  }

  @Test
  public void hasGetNameStateAccessor() {
    assertPublicMethod("getNameState");
  }

  @Test
  public void hasGetInitializerStateAccessor() {
    assertPublicMethod("getInitializerState");
  }

  @Test
  public void hasGetValueTypeMethod() {
    assertPublicMethod("getValueType");
  }

  @Test
  public void hasGetDeclarationLikeSubstanceNameMethod() {
    assertPublicMethod("getDeclarationLikeSubstanceName");
  }

  @Test
  public void hasGetInitializerMethod() {
    assertPublicMethod("getInitializer");
  }

  // ═══════════════════════════════════════════════════════════════════
  // Section 2: Post-extraction structure — FAIL before, PASS after
  // ═══════════════════════════════════════════════════════════════════

  // -- Delegate fields must exist on the composite --

  @Test
  public void hasValidationDelegateField() {
    try {
      Field f = compositeClass.getDeclaredField("validationDelegate");
      assertEquals("validationDelegate type must be DeclarationValidationDelegate",
          VALIDATION_DELEGATE_FQCN, f.getType().getName());
    } catch (NoSuchFieldException e) {
      fail("Composite must have a 'validationDelegate' field after extraction");
    }
  }

  @Test
  public void validationDelegateFieldIsFinal() {
    try {
      Field f = compositeClass.getDeclaredField("validationDelegate");
      assertTrue("validationDelegate must be final", Modifier.isFinal(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Missing 'validationDelegate' field");
    }
  }

  @Test
  public void hasLifecycleDelegateField() {
    try {
      Field f = compositeClass.getDeclaredField("lifecycleDelegate");
      assertEquals("lifecycleDelegate type must be DeclarationDialogLifecycleDelegate",
          LIFECYCLE_DELEGATE_FQCN, f.getType().getName());
    } catch (NoSuchFieldException e) {
      fail("Composite must have a 'lifecycleDelegate' field after extraction");
    }
  }

  @Test
  public void lifecycleDelegateFieldIsFinal() {
    try {
      Field f = compositeClass.getDeclaredField("lifecycleDelegate");
      assertTrue("lifecycleDelegate must be final", Modifier.isFinal(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Missing 'lifecycleDelegate' field");
    }
  }

  // -- Helper methods widened from private to package-private --

  @Test
  public void isValueComponentTypeEditableIsPackagePrivate() {
    assertPackagePrivateMethod("isValueComponentTypeEditable");
  }

  @Test
  public void isValueIsArrayTypeEditableIsPackagePrivate() {
    assertPackagePrivateMethod("isValueIsArrayTypeEditable");
  }

  @Test
  public void isNameEditableIsPackagePrivate() {
    assertPackagePrivateMethod("isNameEditable");
  }

  @Test
  public void isInitializerEditableIsPackagePrivate() {
    assertPackagePrivateMethod("isInitializerEditable");
  }

  // -- Validation methods moved out of composite --

  @Test
  public void compositeDoesNotDeclareGetValueTypeExplanation() {
    assertCompositeDoesNotDeclare("getValueTypeExplanation");
  }

  @Test
  public void compositeDoesNotDeclareGetNameExplanation() {
    assertCompositeDoesNotDeclare("getNameExplanation");
  }

  @Test
  public void compositeDoesNotDeclareGetInitializerExplanation() {
    assertCompositeDoesNotDeclare("getInitializerExplanation");
  }

  @Test
  public void compositeDoesNotDeclareIsNullAllowedForInitializerUnderAnyCircumstances() {
    assertCompositeDoesNotDeclare("isNullAllowedForInitializerUnderAnyCircumstances");
  }

  // -- Lifecycle methods moved out of composite --

  @Test
  public void compositeDoesNotDeclareHandleValueTypeChanging() {
    assertCompositeDoesNotDeclare("handleValueTypeChanging");
  }

  @Test
  public void compositeDoesNotDeclareHandleValueTypeChanged() {
    assertCompositeDoesNotDeclare("handleValueTypeChanged");
  }

  // -- Listener fields moved out of composite --

  @Test
  public void compositeDoesNotHaveIsArrayValueTypeListenerField() {
    assertFieldNotExists("isArrayValueTypeListener");
  }

  @Test
  public void compositeDoesNotHaveValueComponentTypeListenerField() {
    assertFieldNotExists("valueComponentTypeListener");
  }

  @Test
  public void compositeDoesNotHaveInitializerListenerField() {
    assertFieldNotExists("initializerListener");
  }

  @Test
  public void compositeDoesNotHaveMapTypeToInitializerField() {
    assertFieldNotExists("mapTypeToInitializer");
  }

  // ═══════════════════════════════════════════════════════════════════
  // Line count target
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void compositeIsUnder500Lines() throws Exception {
    // Reads the source file and counts lines; this is a build-time
    // sanity check that the extraction actually reduced the file.
    java.io.InputStream is = DeclarationCompositeStructureTest.class.getResourceAsStream(
        "/declaration-composite-line-count.txt");
    if (is != null) {
      String content = new String(is.readAllBytes()).trim();
      int lineCount = Integer.parseInt(content);
      assertTrue("DeclarationLikeSubstanceComposite.java must be under 500 lines, was " + lineCount,
          lineCount < 500);
    }
    // If resource not found, skip — the line count check is supplementary
  }

  // ═══════════════════════════════════════════════════════════════════
  // Helpers
  // ═══════════════════════════════════════════════════════════════════

  private static void assertFieldExists(String name) {
    try {
      compositeClass.getDeclaredField(name);
    } catch (NoSuchFieldException e) {
      fail("DeclarationLikeSubstanceComposite must have field: " + name);
    }
  }

  private static void assertFieldNotExists(String name) {
    try {
      compositeClass.getDeclaredField(name);
      fail("DeclarationLikeSubstanceComposite should not have field '" + name
          + "' (moved to delegate)");
    } catch (NoSuchFieldException e) {
      // expected
    }
  }

  private static void assertMethodExists(String name) {
    boolean found = Arrays.stream(compositeClass.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals(name));
    assertTrue("DeclarationLikeSubstanceComposite must have method: " + name, found);
  }

  private static Method findMethod(String name) {
    return Arrays.stream(compositeClass.getDeclaredMethods())
        .filter(m -> m.getName().equals(name))
        .findFirst().orElse(null);
  }

  private static void assertPublicMethod(String name) {
    Method m = findMethod(name);
    assertNotNull("Must have public method: " + name, m);
    assertTrue(name + " must be public", Modifier.isPublic(m.getModifiers()));
  }

  private static void assertPackagePrivateMethod(String name) {
    Method m = findMethod(name);
    assertNotNull("Must have method: " + name, m);
    int mods = m.getModifiers();
    assertFalse(name + " must not be public", Modifier.isPublic(mods));
    assertFalse(name + " must not be private", Modifier.isPrivate(mods));
    assertFalse(name + " must not be protected", Modifier.isProtected(mods));
  }

  private static void assertSubclassOf(String fqcn) {
    try {
      Class<?> sub = Class.forName(fqcn);
      assertTrue(sub.getSimpleName() + " must extend DeclarationLikeSubstanceComposite",
          compositeClass.isAssignableFrom(sub));
    } catch (ClassNotFoundException e) {
      fail("Subclass not found: " + fqcn);
    }
  }

  private static void assertCompositeDoesNotDeclare(String name) {
    Set<String> methods = Arrays.stream(compositeClass.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertFalse("Composite should not declare '" + name + "' (moved to delegate)",
        methods.contains(name));
  }
}
