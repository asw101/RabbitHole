package org.alice.ide.ast.declaration;

import org.alice.ide.ast.declaration.views.DeclarationLikeSubstanceView;
import org.alice.ide.preview.PreviewContainingOperationInputDialogCoreComposite;
import org.alice.ide.croquet.models.declaration.InitializerStateOwner;
import org.junit.Test;
import org.lgna.project.ast.Node;
import org.lgna.project.ast.UserType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;

public class DeclarationCompositeComprehensiveTest {

  private static final Class<DeclarationLikeSubstanceComposite> TYPE = DeclarationLikeSubstanceComposite.class;

  private static Class<?> nested(String simpleName) {
    for (Class<?> candidate : TYPE.getDeclaredClasses()) {
      if (candidate.getSimpleName().equals(simpleName)) {
        return candidate;
      }
    }
    fail("Missing nested type: " + simpleName);
    return null;
  }

  private static void assertField(String name) throws Exception {
    assertNotNull(TYPE.getDeclaredField(name));
  }

  private static void assertMethod(String name, Class<?>... parameterTypes) throws Exception {
    assertNotNull(TYPE.getDeclaredMethod(name, parameterTypes));
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(TYPE.getModifiers()));
  }

  @Test
  public void classIsAbstract() {
    assertTrue(Modifier.isAbstract(TYPE.getModifiers()));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("DeclarationLikeSubstanceComposite", TYPE.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.declaration", TYPE.getPackage().getName());
  }

  @Test
  public void extendsPreviewContainingComposite() {
    assertTrue(PreviewContainingOperationInputDialogCoreComposite.class.isAssignableFrom(TYPE));
  }

  @Test
  public void implementsInitializerStateOwner() {
    assertTrue(InitializerStateOwner.class.isAssignableFrom(TYPE));
  }

  @Test
  public void genericSuperclassContainsViewType() {
    assertTrue(TYPE.getGenericSuperclass().getTypeName().contains(DeclarationLikeSubstanceView.class.getSimpleName()));
  }

  @Test
  public void genericTypeParameterIsNamedN() {
    assertEquals(1, TYPE.getTypeParameters().length);
    assertEquals("N", TYPE.getTypeParameters()[0].getName());
  }

  @Test
  public void constructorAcceptsUuidAndDetails() throws Exception {
    assertNotNull(TYPE.getDeclaredConstructor(UUID.class, nested("Details")));
  }

  @Test
  public void constructorIsPublic() throws Exception {
    Constructor<?> constructor = TYPE.getDeclaredConstructor(UUID.class, nested("Details"));
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void applicabilityStatusIsNestedEnum() {
    Class<?> status = nested("ApplicabilityStatus");
    assertTrue(status.isEnum());
  }

  @Test
  public void applicabilityStatusDefinesExpectedConstants() {
    Class<?> status = nested("ApplicabilityStatus");
    assertEquals(4, status.getEnumConstants().length);
    assertTrue(Arrays.toString(status.getEnumConstants()).contains("EDITABLE"));
    assertTrue(Arrays.toString(status.getEnumConstants()).contains("DISPLAYED"));
  }

  @Test
  public void applicabilityStatusDefinesVisibilityMethods() throws Exception {
    Class<?> status = nested("ApplicabilityStatus");
    assertNotNull(status.getDeclaredMethod("isEditable"));
    assertNotNull(status.getDeclaredMethod("isDisplayed"));
    assertNotNull(status.getDeclaredMethod("isApplicable"));
  }

  @Test
  public void detailsNestedClassIsProtectedStatic() {
    int modifiers = nested("Details").getModifiers();
    assertTrue(Modifier.isProtected(modifiers));
    assertTrue(Modifier.isStatic(modifiers));
  }

  @Test
  public void detailsSupportsFluentConfigurationMethods() throws Exception {
    Class<?> details = nested("Details");
    assertNotNull(details.getDeclaredMethod("isFinal", nested("ApplicabilityStatus"), boolean.class));
    assertNotNull(details.getDeclaredMethod("valueComponentType", nested("ApplicabilityStatus"), org.lgna.project.ast.AbstractType.class));
    assertNotNull(details.getDeclaredMethod("valueIsArrayType", nested("ApplicabilityStatus"), boolean.class));
    assertNotNull(details.getDeclaredMethod("name", nested("ApplicabilityStatus"), String.class));
    assertNotNull(details.getDeclaredMethod("name", nested("ApplicabilityStatus")));
    assertNotNull(details.getDeclaredMethod("initializer", nested("ApplicabilityStatus"), org.lgna.project.ast.Expression.class));
  }

  @Test
  public void stateFieldsExist() throws Exception {
    assertField("isFinalState");
    assertField("valueComponentTypeState");
    assertField("valueIsArrayTypeState");
    assertField("nameState");
    assertField("initializerState");
  }

  @Test
  public void delegateFieldsExist() throws Exception {
    assertField("validationDelegate");
    assertField("lifecycleDelegate");
    assertField("details");
    assertField("errorStatus");
  }

  @Test
  public void delegateFieldsAreFinal() throws Exception {
    Field validation = TYPE.getDeclaredField("validationDelegate");
    Field lifecycle = TYPE.getDeclaredField("lifecycleDelegate");
    assertTrue(Modifier.isFinal(validation.getModifiers()));
    assertTrue(Modifier.isFinal(lifecycle.getModifiers()));
  }

  @Test
  public void publicAccessorMethodsExist() throws Exception {
    assertNotNull(TYPE.getMethod("getIsFinalState"));
    assertNotNull(TYPE.getMethod("getValueComponentTypeState"));
    assertNotNull(TYPE.getMethod("getValueIsArrayTypeState"));
    assertNotNull(TYPE.getMethod("getNameState"));
    assertNotNull(TYPE.getMethod("getInitializerState"));
    assertNotNull(TYPE.getMethod("getValueComponentType"));
    assertNotNull(TYPE.getMethod("getValueType"));
    assertNotNull(TYPE.getMethod("getDeclarationLikeSubstanceName"));
    assertNotNull(TYPE.getMethod("getInitializer"));
  }

  @Test
  public void visibilityPredicateMethodsExist() throws Exception {
    assertMethod("isValueComponentTypeDisplayed");
    assertMethod("isValueIsArrayTypeStateDisplayed");
    assertMethod("isInitializerDisplayed");
  }

  @Test
  public void protectedLifecycleMethodsExist() throws Exception {
    Method preShow = TYPE.getDeclaredMethod("handlePreShowDialog", org.lgna.croquet.views.Dialog.class);
    Method postHide = TYPE.getDeclaredMethod("handlePostHideDialog");
    assertTrue(Modifier.isProtected(preShow.getModifiers()));
    assertTrue(Modifier.isProtected(postHide.getModifiers()));
  }

  @Test
  public void abstractTemplateMethodsExist() throws Exception {
    Method declaringType = TYPE.getDeclaredMethod("getDeclaringType");
    Method nameAvailable = TYPE.getDeclaredMethod("isNameAvailable", String.class);
    assertTrue(Modifier.isAbstract(declaringType.getModifiers()));
    assertTrue(Modifier.isAbstract(nameAvailable.getModifiers()));
    assertEquals(UserType.class, declaringType.getReturnType());
  }

  @Test
  public void protectedAndPackagePrivateHelperMethodsExist() throws Exception {
    Method nullAllowed = TYPE.getDeclaredMethod("isNullAllowedForInitializer");
    Method createCustomizer = TYPE.getDeclaredMethod("createInitializerCustomizer");
    Method createState = TYPE.getDeclaredMethod("createInitializerState", org.lgna.project.ast.Expression.class);
    Method localized = TYPE.getDeclaredMethod("findLocalizedTextForDelegate", String.class);
    assertTrue(Modifier.isProtected(nullAllowed.getModifiers()));
    assertTrue(Modifier.isProtected(createCustomizer.getModifiers()));
    assertTrue(Modifier.isProtected(createState.getModifiers()));
    assertFalse(Modifier.isPrivate(localized.getModifiers()));
  }

  @Test
  public void statusComputationAndEditabilityMethodsExist() throws Exception {
    Method status = TYPE.getDeclaredMethod("getStatusPreRejectorCheck");
    assertTrue(Modifier.isProtected(status.getModifiers()));
    assertMethod("isValueComponentTypeEditable");
    assertMethod("isValueIsArrayTypeEditable");
    assertMethod("isNameEditable");
    assertMethod("isInitializerEditable");
    assertMethod("isNameValid", String.class);
  }

  @Test
  public void subclassHierarchyIncludesCommonComposites() throws Exception {
    assertTrue(TYPE.isAssignableFrom(AddFieldComposite.class));
    assertTrue(TYPE.isAssignableFrom(AddMethodComposite.class));
    assertTrue(TYPE.isAssignableFrom(AddFunctionComposite.class));
    assertTrue(TYPE.isAssignableFrom(AddParameterComposite.class));
    assertTrue(TYPE.isAssignableFrom(AddProcedureComposite.class));
    assertTrue(TYPE.isAssignableFrom(EditFieldComposite.class));
    assertTrue(TYPE.isAssignableFrom(FieldComposite.class));
    assertTrue(TYPE.isAssignableFrom(InsertStatementComposite.class));
  }
}
