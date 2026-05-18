package org.alice.ide.ast.declaration;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Characterization tests for DeclarationLikeSubstanceComposite structure:
 * ApplicabilityStatus enum, Details builder, delegate fields,
 * state fields, and InitializerStateOwner interface.
 *
 * Complements DeclarationCompositeStructureTest.
 * Pure reflection — no GUI, no singleton instantiation.
 */
public class DeclarationCompositeCharacterizationTest {

  private static final String COMPOSITE_FQCN =
      "org.alice.ide.ast.declaration.DeclarationLikeSubstanceComposite";
  private static final String VALIDATION_FQCN =
      "org.alice.ide.ast.declaration.DeclarationValidationDelegate";
  private static final String LIFECYCLE_FQCN =
      "org.alice.ide.ast.declaration.DeclarationDialogLifecycleDelegate";
  private static final String INITIALIZER_OWNER_FQCN =
      "org.alice.ide.croquet.models.declaration.InitializerStateOwner";

  private static Class<?> compositeClass;
  private static Class<?> applicabilityStatusClass;
  private static Class<?> detailsClass;

  @BeforeClass
  public static void loadClasses() {
    try {
      compositeClass = Class.forName(COMPOSITE_FQCN);
    } catch (ClassNotFoundException e) {
      fail("DeclarationLikeSubstanceComposite not found: " + e.getMessage());
    }
    applicabilityStatusClass = findNestedClass("ApplicabilityStatus");
    detailsClass = findNestedClass("Details");
  }

  // ══════════════════════════════════════════════════════════════
  // ApplicabilityStatus enum
  // ══════════════════════════════════════════════════════════════

  @Test
  public void applicabilityStatus_isNestedEnum() {
    Class<?>[] declaredClasses = compositeClass.getDeclaredClasses();
    boolean found = Arrays.stream(declaredClasses)
        .anyMatch(c -> c.getSimpleName().equals("ApplicabilityStatus") && c.isEnum());
    assertTrue("ApplicabilityStatus must be a nested enum", found);
  }

  @Test
  public void applicabilityStatus_isProtectedStatic() {
    Class<?> enumClass = applicabilityStatusClass;
    assertNotNull(enumClass);
    int mods = enumClass.getModifiers();
    assertTrue("ApplicabilityStatus must be protected", Modifier.isProtected(mods));
    assertTrue("ApplicabilityStatus must be static", Modifier.isStatic(mods));
  }

  @Test
  public void applicabilityStatus_hasFourValues() {
    Class<?> enumClass = applicabilityStatusClass;
    assertNotNull(enumClass);
    Object[] constants = enumClass.getEnumConstants();
    assertNotNull("Must have enum constants", constants);
    assertEquals("Must have 4 values: EDITABLE, DISPLAYED, APPLICABLE_BUT_NOT_DISPLAYED, NOT_APPLICABLE",
        4, constants.length);
  }

  @Test
  public void applicabilityStatus_values_inCorrectOrder() {
    Class<?> enumClass = applicabilityStatusClass;
    assertNotNull(enumClass);
    Object[] constants = enumClass.getEnumConstants();
    assertEquals("EDITABLE", ((Enum<?>) constants[0]).name());
    assertEquals("DISPLAYED", ((Enum<?>) constants[1]).name());
    assertEquals("APPLICABLE_BUT_NOT_DISPLAYED", ((Enum<?>) constants[2]).name());
    assertEquals("NOT_APPLICABLE", ((Enum<?>) constants[3]).name());
  }

  @Test
  public void applicabilityStatus_hasIsEditableMethod() {
    Class<?> enumClass = applicabilityStatusClass;
    assertNotNull(enumClass);
    boolean found = Arrays.stream(enumClass.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isEditable")
            && m.getReturnType() == boolean.class
            && m.getParameterCount() == 0);
    assertTrue("Must have isEditable() method", found);
  }

  @Test
  public void applicabilityStatus_hasIsDisplayedMethod() {
    Class<?> enumClass = applicabilityStatusClass;
    assertNotNull(enumClass);
    boolean found = Arrays.stream(enumClass.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isDisplayed")
            && m.getReturnType() == boolean.class
            && m.getParameterCount() == 0);
    assertTrue("Must have isDisplayed() method", found);
  }

  @Test
  public void applicabilityStatus_hasIsApplicableMethod() {
    Class<?> enumClass = applicabilityStatusClass;
    assertNotNull(enumClass);
    boolean found = Arrays.stream(enumClass.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isApplicable")
            && m.getReturnType() == boolean.class
            && m.getParameterCount() == 0);
    assertTrue("Must have isApplicable() method", found);
  }

  @Test
  public void applicabilityStatus_hasValueField() {
    Class<?> enumClass = applicabilityStatusClass;
    assertNotNull(enumClass);
    boolean found = Arrays.stream(enumClass.getDeclaredFields())
        .anyMatch(f -> f.getName().equals("value") && f.getType() == int.class);
    assertTrue("Must have int value field", found);
  }

  // ── Threshold-based utility method semantics (truth table) ─────

  @Test
  public void applicabilityStatus_truthTable() throws Exception {
    Class<?> enumClass = applicabilityStatusClass;
    assertNotNull(enumClass);
    Method isEditable = enumClass.getDeclaredMethod("isEditable");
    Method isDisplayed = enumClass.getDeclaredMethod("isDisplayed");
    Method isApplicable = enumClass.getDeclaredMethod("isApplicable");
    isEditable.setAccessible(true);
    isDisplayed.setAccessible(true);
    isApplicable.setAccessible(true);

    // EDITABLE: all true
    Object editable = Enum.valueOf((Class<Enum>) enumClass, "EDITABLE");
    assertTrue("EDITABLE.isEditable()", (Boolean) isEditable.invoke(editable));
    assertTrue("EDITABLE.isDisplayed()", (Boolean) isDisplayed.invoke(editable));
    assertTrue("EDITABLE.isApplicable()", (Boolean) isApplicable.invoke(editable));

    // DISPLAYED: not editable, but displayed and applicable
    Object displayed = Enum.valueOf((Class<Enum>) enumClass, "DISPLAYED");
    assertFalse("DISPLAYED.isEditable()", (Boolean) isEditable.invoke(displayed));
    assertTrue("DISPLAYED.isDisplayed()", (Boolean) isDisplayed.invoke(displayed));
    assertTrue("DISPLAYED.isApplicable()", (Boolean) isApplicable.invoke(displayed));

    // APPLICABLE_BUT_NOT_DISPLAYED: not editable, not displayed, but applicable
    Object applicable = Enum.valueOf((Class<Enum>) enumClass, "APPLICABLE_BUT_NOT_DISPLAYED");
    assertFalse("APPLICABLE_BUT_NOT_DISPLAYED.isEditable()", (Boolean) isEditable.invoke(applicable));
    assertFalse("APPLICABLE_BUT_NOT_DISPLAYED.isDisplayed()", (Boolean) isDisplayed.invoke(applicable));
    assertTrue("APPLICABLE_BUT_NOT_DISPLAYED.isApplicable()", (Boolean) isApplicable.invoke(applicable));

    // NOT_APPLICABLE: all false
    Object notApplicable = Enum.valueOf((Class<Enum>) enumClass, "NOT_APPLICABLE");
    assertFalse("NOT_APPLICABLE.isEditable()", (Boolean) isEditable.invoke(notApplicable));
    assertFalse("NOT_APPLICABLE.isDisplayed()", (Boolean) isDisplayed.invoke(notApplicable));
    assertFalse("NOT_APPLICABLE.isApplicable()", (Boolean) isApplicable.invoke(notApplicable));
  }

  // ══════════════════════════════════════════════════════════════
  // Details nested class
  // ══════════════════════════════════════════════════════════════

  @Test
  public void details_isNestedClass() {
    assertNotNull("Details must be a nested class", detailsClass);
  }

  @Test
  public void details_isProtectedStatic() {
    assertNotNull(detailsClass);
    int mods = detailsClass.getModifiers();
    assertTrue("Details must be protected", Modifier.isProtected(mods));
    assertTrue("Details must be static", Modifier.isStatic(mods));
  }

  @Test
  public void details_hasBuilderStyleMethods() {
    assertNotNull(detailsClass);
    Set<String> methodNames = Arrays.stream(detailsClass.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertTrue("Must have isFinal builder method", methodNames.contains("isFinal"));
    assertTrue("Must have valueComponentType builder method", methodNames.contains("valueComponentType"));
    assertTrue("Must have valueIsArrayType builder method", methodNames.contains("valueIsArrayType"));
    assertTrue("Must have name builder method", methodNames.contains("name"));
    assertTrue("Must have initializer builder method", methodNames.contains("initializer"));
  }

  @Test
  public void details_builderMethodsReturnDetails() {
    assertNotNull(detailsClass);
    for (Method m : detailsClass.getDeclaredMethods()) {
      String name = m.getName();
      if ("isFinal".equals(name) || "valueComponentType".equals(name)
          || "valueIsArrayType".equals(name) || "initializer".equals(name)) {
        assertEquals("Builder method " + name + " should return Details",
            detailsClass, m.getReturnType());
      }
    }
  }

  @Test
  public void details_hasStatusFields() {
    assertNotNull(detailsClass);
    Set<String> fieldNames = Arrays.stream(detailsClass.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());
    assertTrue("Must have isFinalStatus field", fieldNames.contains("isFinalStatus"));
    assertTrue("Must have valueComponentTypeStatus field", fieldNames.contains("valueComponentTypeStatus"));
    assertTrue("Must have valueIsArrayTypeStatus field", fieldNames.contains("valueIsArrayTypeStatus"));
    assertTrue("Must have nameStatus field", fieldNames.contains("nameStatus"));
    assertTrue("Must have initializerStatus field", fieldNames.contains("initializerStatus"));
  }

  @Test
  public void details_hasInitialValueFields() {
    assertNotNull(detailsClass);
    Set<String> fieldNames = Arrays.stream(detailsClass.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());
    assertTrue("Must have inFinalInitialValue field", fieldNames.contains("inFinalInitialValue"));
    assertTrue("Must have nameInitialValue field", fieldNames.contains("nameInitialValue"));
    assertTrue("Must have valueIsArrayTypeInitialValue field", fieldNames.contains("valueIsArrayTypeInitialValue"));
  }

  // ══════════════════════════════════════════════════════════════
  // Delegate fields in composite
  // ══════════════════════════════════════════════════════════════

  @Test
  public void composite_hasValidationDelegateField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(compositeClass, "validationDelegate");
  }

  @Test
  public void composite_validationDelegateType() throws Exception {
    Field f = findCompositeField("validationDelegate");
    assertNotNull(f);
    Class<?> expectedType = Class.forName(VALIDATION_FQCN);
    assertEquals("validationDelegate must be DeclarationValidationDelegate type",
        expectedType, f.getType());
  }

  @Test
  public void composite_hasLifecycleDelegateField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(compositeClass, "lifecycleDelegate");
  }

  @Test
  public void composite_lifecycleDelegateType() throws Exception {
    Field f = findCompositeField("lifecycleDelegate");
    assertNotNull(f);
    Class<?> expectedType = Class.forName(LIFECYCLE_FQCN);
    assertEquals("lifecycleDelegate must be DeclarationDialogLifecycleDelegate type",
        expectedType, f.getType());
  }

  // ══════════════════════════════════════════════════════════════
  // State fields in composite
  // ══════════════════════════════════════════════════════════════

  @Test
  public void composite_hasIsFinalStateField() {
    ReflectionTestHelper.assertFieldExists(compositeClass, "isFinalState");
  }

  @Test
  public void composite_hasValueComponentTypeStateField() {
    ReflectionTestHelper.assertFieldExists(compositeClass, "valueComponentTypeState");
  }

  @Test
  public void composite_hasValueIsArrayTypeStateField() {
    ReflectionTestHelper.assertFieldExists(compositeClass, "valueIsArrayTypeState");
  }

  @Test
  public void composite_hasNameStateField() {
    ReflectionTestHelper.assertFieldExists(compositeClass, "nameState");
  }

  @Test
  public void composite_hasInitializerStateField() {
    ReflectionTestHelper.assertFieldExists(compositeClass, "initializerState");
  }

  @Test
  public void composite_hasErrorStatusField() {
    ReflectionTestHelper.assertFieldExists(compositeClass, "errorStatus");
  }

  @Test
  public void composite_hasDetailsField() {
    ReflectionTestHelper.assertFieldExists(compositeClass, "details");
  }

  // ══════════════════════════════════════════════════════════════
  // InitializerStateOwner interface
  // ══════════════════════════════════════════════════════════════

  @Test
  public void initializerStateOwner_interfaceExists() {
    try {
      Class<?> iface = Class.forName(INITIALIZER_OWNER_FQCN);
      assertTrue("InitializerStateOwner must be an interface", iface.isInterface());
    } catch (ClassNotFoundException e) {
      fail("InitializerStateOwner interface not found: " + e.getMessage());
    }
  }

  @Test
  public void initializerStateOwner_hasGetValueTypeMethod() throws Exception {
    Class<?> iface = Class.forName(INITIALIZER_OWNER_FQCN);
    boolean found = Arrays.stream(iface.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getValueType"));
    assertTrue("InitializerStateOwner must have getValueType method", found);
  }

  @Test
  public void composite_implementsInitializerStateOwner() throws Exception {
    Class<?> iface = Class.forName(INITIALIZER_OWNER_FQCN);
    assertTrue("DeclarationLikeSubstanceComposite must implement InitializerStateOwner",
        iface.isAssignableFrom(compositeClass));
  }

  // ══════════════════════════════════════════════════════════════
  // Composite class structure
  // ══════════════════════════════════════════════════════════════

  @Test
  public void composite_isAbstract() {
    assertTrue("DeclarationLikeSubstanceComposite must be abstract",
        Modifier.isAbstract(compositeClass.getModifiers()));
  }

  @Test
  public void composite_isPublic() {
    assertTrue("DeclarationLikeSubstanceComposite must be public",
        Modifier.isPublic(compositeClass.getModifiers()));
  }

  @Test
  public void composite_hasGenericTypeParameter() {
    assertTrue("Must have type parameters",
        compositeClass.getTypeParameters().length > 0);
  }

  // ── Helpers ────────────────────────────────────────────────────

  private static Class<?> findNestedClass(String simpleName) {
    return Arrays.stream(compositeClass.getDeclaredClasses())
        .filter(c -> c.getSimpleName().equals(simpleName))
        .findFirst().orElse(null);
  }

  private static Field findCompositeField(String name) {
    return ReflectionTestHelper.findField(compositeClass, name);
  }
}
