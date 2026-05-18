package org.alice.ide.resource.manager;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Reflection-based API structure verification for {@link ResourceManagerComposite}.
 *
 * <p>ResourceManagerComposite cannot be instantiated without a running IDE
 * (its field initializers create croquet operations that need singletons).
 * Instead, we verify the class's API shape: hierarchy, method signatures,
 * field types, and inner class structure using reflection.
 *
 * <p>JUnit 4, headless, no runtime dependencies.
 */
public class ResourceManagerCompositeStructureTest {

  private static final Class<?> CLASS = ResourceManagerComposite.class;

  // ── Class hierarchy ──────────────────────────────────────────────

  @Test
  public void class_isFinal() {
    assertTrue("ResourceManagerComposite must be final",
        Modifier.isFinal(CLASS.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue("ResourceManagerComposite must be public",
        Modifier.isPublic(CLASS.getModifiers()));
  }

  @Test
  public void class_extendsLazyOperationUnadornedDialogCoreComposite() {
    Class<?> superclass = CLASS.getSuperclass();
    assertNotNull(superclass);
    assertEquals("LazyOperationUnadornedDialogCoreComposite",
        superclass.getSimpleName());
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse("ResourceManagerComposite must not be abstract",
        Modifier.isAbstract(CLASS.getModifiers()));
  }

  // ── Constructor ──────────────────────────────────────────────────

  @Test
  public void constructor_takesProjectDocumentFrame() {
    Constructor<?>[] constructors = CLASS.getConstructors();
    assertEquals("Exactly one public constructor", 1, constructors.length);
    Class<?>[] params = constructors[0].getParameterTypes();
    assertEquals(1, params.length);
    assertEquals("org.alice.ide.ProjectDocumentFrame",
        params[0].getName());
  }

  // ── Public accessor methods ──────────────────────────────────────

  @Test
  public void method_getImportAudioResourceOperation_exists() throws Exception {
    Method method = CLASS.getMethod("getImportAudioResourceOperation");
    assertNotNull(method);
    assertEquals("ImportAudioResourceOperation",
        method.getReturnType().getSimpleName());
  }

  @Test
  public void method_getImportImageResourceOperation_exists() throws Exception {
    Method method = CLASS.getMethod("getImportImageResourceOperation");
    assertNotNull(method);
    assertEquals("ImportImageResourceOperation",
        method.getReturnType().getSimpleName());
  }

  @Test
  public void method_getResourcesState_exists() throws Exception {
    Method method = CLASS.getMethod("getResourcesState");
    assertNotNull(method);
    assertEquals("ResourceSingleSelectTableRowState",
        method.getReturnType().getSimpleName());
  }

  @Test
  public void method_getRenameResourceComposite_exists() throws Exception {
    Method method = CLASS.getMethod("getRenameResourceComposite");
    assertNotNull(method);
    assertEquals("RenameResourceComposite",
        method.getReturnType().getSimpleName());
  }

  @Test
  public void method_getRemoveResourceOperation_exists() throws Exception {
    Method method = CLASS.getMethod("getRemoveResourceOperation");
    assertNotNull(method);
    assertTrue("Returns Operation or subclass",
        org.lgna.croquet.Operation.class.isAssignableFrom(
            method.getReturnType()));
  }

  @Test
  public void method_getReloadContentOperation_exists() throws Exception {
    Method method = CLASS.getMethod("getReloadContentOperation");
    assertNotNull(method);
    assertTrue("Returns Operation or subclass",
        org.lgna.croquet.Operation.class.isAssignableFrom(
            method.getReturnType()));
  }

  // ── Field types ──────────────────────────────────────────────────

  @Test
  public void field_projectDocumentFrame_isFinal() throws Exception {
    Field field = CLASS.getDeclaredField("projectDocumentFrame");
    assertTrue("projectDocumentFrame must be final",
        Modifier.isFinal(field.getModifiers()));
    assertTrue("projectDocumentFrame must be private",
        Modifier.isPrivate(field.getModifiers()));
    assertEquals("ProjectDocumentFrame", field.getType().getSimpleName());
  }

  @Test
  public void field_resourcesState_exists() throws Exception {
    Field field = CLASS.getDeclaredField("resourcesState");
    assertTrue("resourcesState must be final",
        Modifier.isFinal(field.getModifiers()));
    assertEquals("ResourceSingleSelectTableRowState",
        field.getType().getSimpleName());
  }

  @Test
  public void field_importImageResourceOperation_exists() throws Exception {
    Field field = CLASS.getDeclaredField("importImageResourceOperation");
    assertTrue("importImageResourceOperation must be final",
        Modifier.isFinal(field.getModifiers()));
    assertEquals("ImportImageResourceOperation",
        field.getType().getSimpleName());
  }

  @Test
  public void field_importAudioResourceOperation_exists() throws Exception {
    Field field = CLASS.getDeclaredField("importAudioResourceOperation");
    assertTrue("importAudioResourceOperation must be final",
        Modifier.isFinal(field.getModifiers()));
    assertEquals("ImportAudioResourceOperation",
        field.getType().getSimpleName());
  }

  @Test
  public void field_renameResourceComposite_exists() throws Exception {
    Field field = CLASS.getDeclaredField("renameResourceComposite");
    assertTrue("renameResourceComposite must be final",
        Modifier.isFinal(field.getModifiers()));
    assertEquals("RenameResourceComposite",
        field.getType().getSimpleName());
  }

  @Test
  public void field_removeResourceOperation_exists() throws Exception {
    Field field = CLASS.getDeclaredField("removeResourceOperation");
    assertTrue("removeResourceOperation must be final",
        Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void field_reloadContentOperation_exists() throws Exception {
    Field field = CLASS.getDeclaredField("reloadContentOperation");
    assertTrue("reloadContentOperation must be final",
        Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void field_previousResources_exists() throws Exception {
    Field field = CLASS.getDeclaredField("previousResources");
    assertNotNull(field);
    assertTrue("previousResources must be private",
        Modifier.isPrivate(field.getModifiers()));
  }

  @Test
  public void field_projectBeingListenedTo_exists() throws Exception {
    Field field = CLASS.getDeclaredField("projectBeingListenedTo");
    assertNotNull(field);
    assertTrue("projectBeingListenedTo must be private",
        Modifier.isPrivate(field.getModifiers()));
  }

  // ── Overridden methods ───────────────────────────────────────────

  @Test
  public void method_handlePreActivation_overridden() throws Exception {
    Method method = CLASS.getDeclaredMethod("handlePreActivation");
    assertNotNull(method);
    assertEquals(CLASS, method.getDeclaringClass());
  }

  @Test
  public void method_handlePostDeactivation_overridden() throws Exception {
    Method method = CLASS.getDeclaredMethod("handlePostDeactivation");
    assertNotNull(method);
    assertEquals(CLASS, method.getDeclaringClass());
  }

  @Test
  public void method_createView_overridden() throws Exception {
    Method method = CLASS.getDeclaredMethod("createView");
    assertNotNull(method);
    assertEquals(CLASS, method.getDeclaringClass());
    assertEquals("ResourceManagerView", method.getReturnType().getSimpleName());
  }

  // ── Method coverage ──────────────────────────────────────────────

  @Test
  public void publicMethodCount_isReasonable() {
    Method[] publicMethods = Arrays.stream(CLASS.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .toArray(Method[]::new);
    // Should have the 6 getters
    assertTrue("Should have at least 6 public methods",
        publicMethods.length >= 6);
  }

  @Test
  public void allPublicMethods_haveNonVoidReturnOrAreLifecycle() {
    for (Method method : CLASS.getDeclaredMethods()) {
      if (Modifier.isPublic(method.getModifiers())) {
        String name = method.getName();
        if (name.startsWith("get")) {
          assertFalse("Getters should not return void",
              method.getReturnType() == void.class);
        }
      }
    }
  }

  // ── Private method structure ─────────────────────────────────────

  @Test
  public void privateMethods_includeReloadTableModel() {
    Set<String> privateMethodNames = Arrays.stream(CLASS.getDeclaredMethods())
        .filter(m -> Modifier.isPrivate(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertTrue("Should have reloadTableModel",
        privateMethodNames.contains("reloadTableModel"));
    assertTrue("Should have getProject",
        privateMethodNames.contains("getProject"));
    assertTrue("Should have handleSelection",
        privateMethodNames.contains("handleSelection"));
  }

  // ── Companion class verification ─────────────────────────────────

  @Test
  public void importImageResourceOperation_classExists() throws Exception {
    Class<?> clazz = Class.forName(
        "org.alice.ide.resource.manager.ImportImageResourceOperation");
    assertNotNull(clazz);
  }

  @Test
  public void importAudioResourceOperation_classExists() throws Exception {
    Class<?> clazz = Class.forName(
        "org.alice.ide.resource.manager.ImportAudioResourceOperation");
    assertNotNull(clazz);
  }

  @Test
  public void resourceSingleSelectTableRowState_classExists() throws Exception {
    Class<?> clazz = Class.forName(
        "org.alice.ide.resource.manager.ResourceSingleSelectTableRowState");
    assertNotNull(clazz);
  }

  @Test
  public void renameResourceComposite_classExists() throws Exception {
    Class<?> clazz = Class.forName(
        "org.alice.ide.resource.manager.RenameResourceComposite");
    assertNotNull(clazz);
  }

  @Test
  public void removeResourceOperation_classExists() throws Exception {
    Class<?> clazz = Class.forName(
        "org.alice.ide.resource.manager.RemoveResourceOperation");
    assertNotNull(clazz);
  }

  @Test
  public void reloadContentResourceOperation_classExists() throws Exception {
    Class<?> clazz = Class.forName(
        "org.alice.ide.resource.manager.ReloadContentResourceOperation");
    assertNotNull(clazz);
  }

  @Test
  public void resourceManagerView_classExists() throws Exception {
    Class<?> clazz = Class.forName(
        "org.alice.ide.resource.manager.views.ResourceManagerView");
    assertNotNull(clazz);
  }

  // ── ResourceSingleSelectTableRowState API ────────────────────────

  @Test
  public void resourceSingleSelectTableRowState_hasIsReferencedColumnIndex() throws Exception {
    Class<?> clazz = Class.forName(
        "org.alice.ide.resource.manager.ResourceSingleSelectTableRowState");
    Field field = clazz.getDeclaredField("IS_REFERENCED_COLUMN_INDEX");
    assertTrue("IS_REFERENCED_COLUMN_INDEX must be static",
        Modifier.isStatic(field.getModifiers()));
  }

  // ── Anonymous inner class structure ──────────────────────────────

  @Test
  public void class_hasAnonymousInnerClasses() {
    Class<?>[] declaredClasses = CLASS.getDeclaredClasses();
    // The anonymous classes (resourceListener, nameListener, rowListener)
    // are defined as anonymous inner classes, which won't show up in
    // getDeclaredClasses(). But the fields referencing them exist:
    try {
      Field resourceListener = CLASS.getDeclaredField("resourceListener");
      assertNotNull(resourceListener);
      assertTrue(Modifier.isFinal(resourceListener.getModifiers()));

      Field nameListener = CLASS.getDeclaredField("nameListener");
      assertNotNull(nameListener);
      assertTrue(Modifier.isFinal(nameListener.getModifiers()));

      Field rowListener = CLASS.getDeclaredField("rowListener");
      assertNotNull(rowListener);
      assertTrue(Modifier.isFinal(rowListener.getModifiers()));
    } catch (NoSuchFieldException e) {
      throw new AssertionError("Expected listener fields missing", e);
    }
  }

  // ── Field count ──────────────────────────────────────────────────

  @Test
  public void fieldCount_matchesExpected() {
    Field[] allFields = CLASS.getDeclaredFields();
    // projectDocumentFrame, resourcesState, reloadContentOperation,
    // removeResourceOperation, renameResourceComposite,
    // importImageResourceOperation, importAudioResourceOperation,
    // previousResources, projectBeingListenedTo,
    // resourceListener, nameListener, rowListener = 12 fields
    assertTrue("Should have at least 10 declared fields",
        allFields.length >= 10);
    assertTrue("Should have at most 15 declared fields",
        allFields.length <= 15);
  }
}
