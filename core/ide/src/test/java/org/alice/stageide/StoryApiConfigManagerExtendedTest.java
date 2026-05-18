package org.alice.stageide;

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
 * Extended StoryApiConfigurationManager tests covering singleton behavior,
 * category composites, method surface, field structure, and type predicates.
 */
public class StoryApiConfigManagerExtendedTest {

  private static Class<?> mgrClass;

  @BeforeClass
  public static void loadClass() {
    try {
      mgrClass = Class.forName("org.alice.stageide.StoryApiConfigurationManager");
    } catch (ClassNotFoundException e) {
      fail("StoryApiConfigurationManager not found");
    }
  }

  @Test public void isPublicClass() { assertTrue(Modifier.isPublic(mgrClass.getModifiers())); }
  @Test public void isNotAbstract() { assertFalse(Modifier.isAbstract(mgrClass.getModifiers())); }
  @Test public void extendsApiConfigManager() { assertTrue(org.alice.ide.ApiConfigurationManager.class.isAssignableFrom(mgrClass)); }
  @Test public void isNotFinal() { assertFalse(Modifier.isFinal(mgrClass.getModifiers())); }

  @Test
  public void setActiveSceneMethod_publicStaticFinal() throws NoSuchFieldException {
    Field f = mgrClass.getField("SET_ACTIVE_SCENE_METHOD");
    int m = f.getModifiers();
    assertTrue(Modifier.isPublic(m)); assertTrue(Modifier.isStatic(m)); assertTrue(Modifier.isFinal(m));
  }

  @Test public void setActiveSceneMethod_notNull() throws Exception { assertNotNull(mgrClass.getField("SET_ACTIVE_SCENE_METHOD").get(null)); }

  @Test
  public void bipedResourceType_protectedStaticFinal() throws NoSuchFieldException {
    Field f = mgrClass.getDeclaredField("BIPED_RESOURCE_TYPE");
    int m = f.getModifiers();
    assertTrue(Modifier.isStatic(m)); assertTrue(Modifier.isProtected(m)); assertTrue(Modifier.isFinal(m));
  }

  @Test
  public void bipedResourceType_isJavaType() throws Exception {
    Field f = mgrClass.getDeclaredField("BIPED_RESOURCE_TYPE"); f.setAccessible(true);
    assertTrue(f.get(null) instanceof org.lgna.project.ast.JavaType);
  }

  @Test public void getInstance_notNull() { assertNotNull(StoryApiConfigurationManager.getInstance()); }
  @Test public void getInstance_consistent() { assertSame(StoryApiConfigurationManager.getInstance(), StoryApiConfigurationManager.getInstance()); }

  @Test public void categoryProcedureSubs_notEmpty() { assertFalse(StoryApiConfigurationManager.getInstance().getCategoryProcedureSubComposites().isEmpty()); }
  @Test public void categoryProcedureSubs_unmodifiable() { try { StoryApiConfigurationManager.getInstance().getCategoryProcedureSubComposites().add(null); fail(); } catch (UnsupportedOperationException e) { /* ok */ } }
  @Test public void categoryFunctionSubs_notEmpty() { assertFalse(StoryApiConfigurationManager.getInstance().getCategoryFunctionSubComposites().isEmpty()); }
  @Test public void categoryFunctionSubs_unmodifiable() { try { StoryApiConfigurationManager.getInstance().getCategoryFunctionSubComposites().add(null); fail(); } catch (UnsupportedOperationException e) { /* ok */ } }
  @Test public void catOrAlphProcedure_notNull() { assertNotNull(StoryApiConfigurationManager.getInstance().getCategoryOrAlphabeticalProcedureSubComposites()); }
  @Test public void catOrAlphProcedure_unmodifiable() { try { StoryApiConfigurationManager.getInstance().getCategoryOrAlphabeticalProcedureSubComposites().add(null); fail(); } catch (UnsupportedOperationException e) { /* ok */ } }
  @Test public void catOrAlphFunction_notNull() { assertNotNull(StoryApiConfigurationManager.getInstance().getCategoryOrAlphabeticalFunctionSubComposites()); }
  @Test public void catOrAlphFunction_unmodifiable() { try { StoryApiConfigurationManager.getInstance().getCategoryOrAlphabeticalFunctionSubComposites().add(null); fail(); } catch (UnsupportedOperationException e) { /* ok */ } }

  @Test public void typeComparator_isSingleton() { assertSame(StoryTypeComparator.SINGLETON, StoryApiConfigurationManager.getInstance().getTypeComparator()); }
  @Test public void instanceFactoryDesired_null_false() { assertFalse(StoryApiConfigurationManager.getInstance().isInstanceFactoryDesiredForType(null)); }
  @Test public void expressionCreator_notNull() { assertNotNull(StoryApiConfigurationManager.getInstance().getExpressionCreator()); }
  @Test public void expressionCreator_consistent() { assertSame(StoryApiConfigurationManager.getInstance().getExpressionCreator(), StoryApiConfigurationManager.getInstance().getExpressionCreator()); }

  @Test
  public void publicMethodSurface_complete() {
    Set<String> methods = Arrays.stream(mgrClass.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers())).map(Method::getName).collect(Collectors.toSet());
    for (String n : new String[]{"getInstance","getTypeComparator","isDeclaringTypeForManagedFields","isInstanceFactoryDesiredForType","getGalleryResourceParentFor","getGalleryResourceChildrenFor","getInstanceFactorySubMenuForThis","getInstanceFactorySubMenuForThisFieldAccess","getInstanceFactorySubMenuForParameterAccess","getInstanceFactorySubMenuForLocalAccess","getInstanceFactorySubMenuForParameterAccessMethodInvocation","createReplacementForFieldAccessIfAppropriate","getExpressionCreator","isSignatureLocked","isTabClosable","isExportTypeDesiredFor","augmentTypeIfNecessary","getBuildMethodPoseBuilderType","isBuildMethod"}) {
      assertTrue("missing: " + n, methods.contains(n));
    }
  }

  @Test public void procedureCompositeCount_ge10() { assertTrue(StoryApiConfigurationManager.getInstance().getCategoryProcedureSubComposites().size() >= 10); }
  @Test public void functionCompositeCount_ge4() { assertTrue(StoryApiConfigurationManager.getInstance().getCategoryFunctionSubComposites().size() >= 4); }
  @Test public void orAlphProcedureCount_1() { assertEquals(1, StoryApiConfigurationManager.getInstance().getCategoryOrAlphabeticalProcedureSubComposites().size()); }
  @Test public void orAlphFunctionCount_1() { assertEquals(1, StoryApiConfigurationManager.getInstance().getCategoryOrAlphabeticalFunctionSubComposites().size()); }

  @Test public void cameraFieldsMenuModel_private() throws NoSuchFieldException { assertTrue(Modifier.isPrivate(mgrClass.getDeclaredField("cameraFieldsMenuModel").getModifiers())); }
  @Test public void vrUserFieldsMenuModel_private() throws NoSuchFieldException { assertTrue(Modifier.isPrivate(mgrClass.getDeclaredField("vrUserFieldsMenuModel").getModifiers())); }
  @Test public void expressionCreatorField_privateFinal() throws NoSuchFieldException { Field f = mgrClass.getDeclaredField("expressionCreator"); assertTrue(Modifier.isPrivate(f.getModifiers())); assertTrue(Modifier.isFinal(f.getModifiers())); }

  @Test public void addSecondaryJavaTypes_protected() { assertTrue(Arrays.stream(mgrClass.getDeclaredMethods()).anyMatch(m -> "addSecondaryJavaTypes".equals(m.getName()) && Modifier.isProtected(m.getModifiers()))); }
  @Test public void isNamedUserTypesAcceptable_protected() { assertTrue(Arrays.stream(mgrClass.getDeclaredMethods()).anyMatch(m -> "isNamedUserTypesAcceptableForSelection".equals(m.getName()) && Modifier.isProtected(m.getModifiers()))); }
  @Test public void getCameraFieldsMenu_private() { assertTrue(Arrays.stream(mgrClass.getDeclaredMethods()).anyMatch(m -> "getCameraFieldsMenu".equals(m.getName()) && Modifier.isPrivate(m.getModifiers()))); }
  @Test public void getVrUserFieldsMenu_private() { assertTrue(Arrays.stream(mgrClass.getDeclaredMethods()).anyMatch(m -> "getVrUserFieldsMenu".equals(m.getName()) && Modifier.isPrivate(m.getModifiers()))); }
  @Test public void createDeclNameLabel_private() { assertTrue(Arrays.stream(mgrClass.getDeclaredMethods()).anyMatch(m -> "createDeclarationNameLabel".equals(m.getName()) && Modifier.isPrivate(m.getModifiers()))); }
  @Test public void getSpecificPoseBuilderType_private() { assertTrue(Arrays.stream(mgrClass.getDeclaredMethods()).anyMatch(m -> "getSpecificPoseBuilderType".equals(m.getName()) && Modifier.isPrivate(m.getModifiers()))); }

  // ── Method signature verification ────────────────────────────────

  @Test
  public void isDeclaringTypeForManagedFields_exists() throws NoSuchMethodException {
    Method m = mgrClass.getMethod("isDeclaringTypeForManagedFields", org.lgna.project.ast.UserType.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void isInstanceFactoryDesiredForType_exists() throws NoSuchMethodException {
    Method m = mgrClass.getMethod("isInstanceFactoryDesiredForType", org.lgna.project.ast.AbstractType.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void getInstanceFactorySubMenuForThis_exists() throws NoSuchMethodException {
    mgrClass.getMethod("getInstanceFactorySubMenuForThis", org.lgna.project.ast.AbstractType.class);
  }

  @Test
  public void getInstanceFactorySubMenuForThisFieldAccess_exists() throws NoSuchMethodException {
    mgrClass.getMethod("getInstanceFactorySubMenuForThisFieldAccess", org.lgna.project.ast.UserField.class);
  }

  @Test
  public void getInstanceFactorySubMenuForParameterAccess_exists() throws NoSuchMethodException {
    mgrClass.getMethod("getInstanceFactorySubMenuForParameterAccess", org.lgna.project.ast.UserParameter.class);
  }

  @Test
  public void getInstanceFactorySubMenuForLocalAccess_exists() throws NoSuchMethodException {
    mgrClass.getMethod("getInstanceFactorySubMenuForLocalAccess", org.lgna.project.ast.UserLocal.class);
  }

  @Test
  public void getInstanceFactorySubMenuForParameterAccessMethodInvocation_exists() throws NoSuchMethodException {
    mgrClass.getMethod("getInstanceFactorySubMenuForParameterAccessMethodInvocation",
        org.lgna.project.ast.UserParameter.class, org.lgna.project.ast.AbstractMethod.class);
  }

  @Test
  public void isSignatureLocked_exists() throws NoSuchMethodException {
    Method m = mgrClass.getMethod("isSignatureLocked", org.lgna.project.ast.Code.class);
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void isTabClosable_exists() throws NoSuchMethodException {
    Method m = mgrClass.getMethod("isTabClosable", org.lgna.project.ast.AbstractCode.class);
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void isExportTypeDesiredFor_exists() throws NoSuchMethodException {
    Method m = mgrClass.getMethod("isExportTypeDesiredFor", org.lgna.project.ast.NamedUserType.class);
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void getGalleryResourceParentFor_exists() throws NoSuchMethodException {
    mgrClass.getMethod("getGalleryResourceParentFor", org.lgna.project.ast.JavaType.class);
  }

  @Test
  public void getGalleryResourceChildrenFor_exists() throws NoSuchMethodException {
    mgrClass.getMethod("getGalleryResourceChildrenFor", org.lgna.project.ast.AbstractType.class);
  }

  @Test
  public void augmentTypeIfNecessary_exists() throws NoSuchMethodException {
    mgrClass.getMethod("augmentTypeIfNecessary", org.lgna.project.ast.UserType.class);
  }

  @Test
  public void getBuildMethodPoseBuilderType_exists() throws NoSuchMethodException {
    mgrClass.getMethod("getBuildMethodPoseBuilderType", org.lgna.project.ast.MethodInvocation.class);
  }

  @Test
  public void isBuildMethod_exists() throws NoSuchMethodException {
    Method m = mgrClass.getMethod("isBuildMethod", org.lgna.project.ast.MethodInvocation.class);
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void createReplacementForFieldAccessIfAppropriate_exists() throws NoSuchMethodException {
    mgrClass.getMethod("createReplacementForFieldAccessIfAppropriate", org.lgna.project.ast.FieldAccess.class);
  }

  // ── Field layout verification ────────────────────────────────────

  @Test
  public void categoryProcedureSubComposites_privateFinal() throws NoSuchFieldException {
    Field f = mgrClass.getDeclaredField("categoryProcedureSubComposites");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void categoryFunctionSubComposites_privateFinal() throws NoSuchFieldException {
    Field f = mgrClass.getDeclaredField("categoryFunctionSubComposites");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void categoryOrAlphabeticalProcedureSubComposites_privateFinal() throws NoSuchFieldException {
    Field f = mgrClass.getDeclaredField("categoryOrAlphabeticalProcedureSubComposites");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void categoryOrAlphabeticalFunctionSubComposites_privateFinal() throws NoSuchFieldException {
    Field f = mgrClass.getDeclaredField("categoryOrAlphabeticalFunctionSubComposites");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  // ── SET_ACTIVE_SCENE_METHOD details ──────────────────────────────

  @Test
  public void setActiveSceneMethod_isJavaMethod() throws Exception {
    Object val = mgrClass.getField("SET_ACTIVE_SCENE_METHOD").get(null);
    assertTrue(val instanceof org.lgna.project.ast.JavaMethod);
  }

  @Test
  public void setActiveSceneMethod_name_isSetActiveScene() throws Exception {
    org.lgna.project.ast.JavaMethod jm = (org.lgna.project.ast.JavaMethod) mgrClass.getField("SET_ACTIVE_SCENE_METHOD").get(null);
    assertEquals("setActiveScene", jm.getName());
  }

  // ── Singleton inner class ────────────────────────────────────────

  @Test
  public void singletonHolder_exists() {
    Class<?>[] inner = mgrClass.getDeclaredClasses();
    boolean found = Arrays.stream(inner).anyMatch(c -> c.getSimpleName().equals("SingletonHolder"));
    assertTrue("SingletonHolder inner class must exist", found);
  }

  // ── getBuildMethodPoseBuilderType private overload ────────────────

  @Test
  public void getBuildMethodPoseBuilderType_privateOverload() {
    long count = Arrays.stream(mgrClass.getDeclaredMethods())
        .filter(m -> "getBuildMethodPoseBuilderType".equals(m.getName()))
        .count();
    assertTrue("Should have at least 2 overloads of getBuildMethodPoseBuilderType", count >= 2);
  }
}
