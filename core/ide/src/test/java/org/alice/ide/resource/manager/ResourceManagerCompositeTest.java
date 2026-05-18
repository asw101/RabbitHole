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
 * Reflection-based structural verification for {@link ResourceManagerComposite}.
 *
 * <p>ResourceManagerComposite cannot be safely instantiated in a headless
 * environment — its field initializers construct croquet UI components.
 * These tests verify the API shape, field types, and method signatures
 * without instantiation, ensuring the class contract is preserved.
 */
public class ResourceManagerCompositeTest {

  private static final Class<?> COMPOSITE_CLASS = ResourceManagerComposite.class;

  // ── Class structure ───────────────────────────────────────────────

  @Test
  public void classIsFinal() {
    assertTrue("ResourceManagerComposite must be final",
        Modifier.isFinal(COMPOSITE_CLASS.getModifiers()));
  }

  @Test
  public void classIsPublic() {
    assertTrue("ResourceManagerComposite must be public",
        Modifier.isPublic(COMPOSITE_CLASS.getModifiers()));
  }

  @Test
  public void classExtendsLazyOperationUnadornedDialogCoreComposite() {
    Class<?> superclass = COMPOSITE_CLASS.getSuperclass();
    assertNotNull(superclass);
    assertEquals("LazyOperationUnadornedDialogCoreComposite", superclass.getSimpleName());
  }

  @Test
  public void classHasSingleConstructor() {
    Constructor<?>[] constructors = COMPOSITE_CLASS.getConstructors();
    assertEquals("Should have exactly one public constructor", 1, constructors.length);
  }

  @Test
  public void constructorTakesProjectDocumentFrame() {
    Constructor<?>[] constructors = COMPOSITE_CLASS.getConstructors();
    Class<?>[] params = constructors[0].getParameterTypes();
    assertEquals(1, params.length);
    assertEquals("ProjectDocumentFrame", params[0].getSimpleName());
  }

  // ── Public API methods ────────────────────────────────────────────

  @Test
  public void hasGetImportAudioResourceOperationMethod() throws NoSuchMethodException {
    Method method = COMPOSITE_CLASS.getMethod("getImportAudioResourceOperation");
    assertNotNull(method);
    assertEquals("ImportAudioResourceOperation", method.getReturnType().getSimpleName());
  }

  @Test
  public void hasGetImportImageResourceOperationMethod() throws NoSuchMethodException {
    Method method = COMPOSITE_CLASS.getMethod("getImportImageResourceOperation");
    assertNotNull(method);
    assertEquals("ImportImageResourceOperation", method.getReturnType().getSimpleName());
  }

  @Test
  public void hasGetResourcesStateMethod() throws NoSuchMethodException {
    Method method = COMPOSITE_CLASS.getMethod("getResourcesState");
    assertNotNull(method);
    assertEquals("ResourceSingleSelectTableRowState", method.getReturnType().getSimpleName());
  }

  @Test
  public void hasGetRenameResourceCompositeMethod() throws NoSuchMethodException {
    Method method = COMPOSITE_CLASS.getMethod("getRenameResourceComposite");
    assertNotNull(method);
    assertEquals("RenameResourceComposite", method.getReturnType().getSimpleName());
  }

  @Test
  public void hasGetRemoveResourceOperationMethod() throws NoSuchMethodException {
    Method method = COMPOSITE_CLASS.getMethod("getRemoveResourceOperation");
    assertNotNull(method);
    assertEquals("Operation", method.getReturnType().getSimpleName());
  }

  @Test
  public void hasGetReloadContentOperationMethod() throws NoSuchMethodException {
    Method method = COMPOSITE_CLASS.getMethod("getReloadContentOperation");
    assertNotNull(method);
    assertEquals("Operation", method.getReturnType().getSimpleName());
  }

  // ── Private/internal fields ───────────────────────────────────────

  @Test
  public void hasResourcesStateField() throws NoSuchFieldException {
    Field field = COMPOSITE_CLASS.getDeclaredField("resourcesState");
    assertNotNull(field);
    assertEquals("ResourceSingleSelectTableRowState",
        field.getType().getSimpleName());
    assertTrue("Field should be final",
        Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void hasImportImageResourceOperationField() throws NoSuchFieldException {
    Field field = COMPOSITE_CLASS.getDeclaredField("importImageResourceOperation");
    assertNotNull(field);
    assertEquals("ImportImageResourceOperation",
        field.getType().getSimpleName());
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void hasImportAudioResourceOperationField() throws NoSuchFieldException {
    Field field = COMPOSITE_CLASS.getDeclaredField("importAudioResourceOperation");
    assertNotNull(field);
    assertEquals("ImportAudioResourceOperation",
        field.getType().getSimpleName());
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void hasRenameResourceCompositeField() throws NoSuchFieldException {
    Field field = COMPOSITE_CLASS.getDeclaredField("renameResourceComposite");
    assertNotNull(field);
    assertEquals("RenameResourceComposite",
        field.getType().getSimpleName());
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void hasReloadContentOperationField() throws NoSuchFieldException {
    Field field = COMPOSITE_CLASS.getDeclaredField("reloadContentOperation");
    assertNotNull(field);
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void hasRemoveResourceOperationField() throws NoSuchFieldException {
    Field field = COMPOSITE_CLASS.getDeclaredField("removeResourceOperation");
    assertNotNull(field);
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void hasProjectDocumentFrameField() throws NoSuchFieldException {
    Field field = COMPOSITE_CLASS.getDeclaredField("projectDocumentFrame");
    assertNotNull(field);
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void hasPreviousResourcesField() throws NoSuchFieldException {
    Field field = COMPOSITE_CLASS.getDeclaredField("previousResources");
    assertNotNull(field);
    assertFalse("previousResources should not be final",
        Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void hasProjectBeingListenedToField() throws NoSuchFieldException {
    Field field = COMPOSITE_CLASS.getDeclaredField("projectBeingListenedTo");
    assertNotNull(field);
    assertFalse("projectBeingListenedTo should not be final",
        Modifier.isFinal(field.getModifiers()));
  }

  // ── Overridden methods ────────────────────────────────────────────

  @Test
  public void overridesHandlePreActivation() {
    boolean found = Arrays.stream(COMPOSITE_CLASS.getDeclaredMethods())
        .anyMatch(m -> "handlePreActivation".equals(m.getName()));
    assertTrue("Should override handlePreActivation", found);
  }

  @Test
  public void overridesHandlePostDeactivation() {
    boolean found = Arrays.stream(COMPOSITE_CLASS.getDeclaredMethods())
        .anyMatch(m -> "handlePostDeactivation".equals(m.getName()));
    assertTrue("Should override handlePostDeactivation", found);
  }

  @Test
  public void overridesCreateView() {
    boolean found = Arrays.stream(COMPOSITE_CLASS.getDeclaredMethods())
        .anyMatch(m -> "createView".equals(m.getName()));
    assertTrue("Should override createView", found);
  }

  // ── Public method completeness ────────────────────────────────────

  @Test
  public void publicMethodSetIsComplete() {
    Set<String> publicMethods = Arrays.stream(COMPOSITE_CLASS.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toSet());

    assertTrue("Missing getImportAudioResourceOperation",
        publicMethods.contains("getImportAudioResourceOperation"));
    assertTrue("Missing getImportImageResourceOperation",
        publicMethods.contains("getImportImageResourceOperation"));
    assertTrue("Missing getResourcesState",
        publicMethods.contains("getResourcesState"));
    assertTrue("Missing getRenameResourceComposite",
        publicMethods.contains("getRenameResourceComposite"));
    assertTrue("Missing getRemoveResourceOperation",
        publicMethods.contains("getRemoveResourceOperation"));
    assertTrue("Missing getReloadContentOperation",
        publicMethods.contains("getReloadContentOperation"));
  }

  // ── Field counts ──────────────────────────────────────────────────

  @Test
  public void fieldCountIsExpected() {
    Field[] allFields = COMPOSITE_CLASS.getDeclaredFields();
    // At least the known fields: projectDocumentFrame, resourcesState,
    // reloadContentOperation, removeResourceOperation, renameResourceComposite,
    // importImageResourceOperation, importAudioResourceOperation,
    // previousResources, projectBeingListenedTo, resourceListener, nameListener, rowListener
    assertTrue("Should have at least 10 fields, found " + allFields.length,
        allFields.length >= 10);
  }

  // ── Related classes exist ─────────────────────────────────────────

  @Test
  public void importAudioResourceOperationClassExists() {
    try {
      Class.forName("org.alice.ide.resource.manager.ImportAudioResourceOperation");
    } catch (ClassNotFoundException e) {
      throw new AssertionError("ImportAudioResourceOperation class must exist", e);
    }
  }

  @Test
  public void importImageResourceOperationClassExists() {
    try {
      Class.forName("org.alice.ide.resource.manager.ImportImageResourceOperation");
    } catch (ClassNotFoundException e) {
      throw new AssertionError("ImportImageResourceOperation class must exist", e);
    }
  }

  @Test
  public void resourceSingleSelectTableRowStateClassExists() {
    try {
      Class.forName("org.alice.ide.resource.manager.ResourceSingleSelectTableRowState");
    } catch (ClassNotFoundException e) {
      throw new AssertionError("ResourceSingleSelectTableRowState class must exist", e);
    }
  }

  @Test
  public void renameResourceCompositeClassExists() {
    try {
      Class.forName("org.alice.ide.resource.manager.RenameResourceComposite");
    } catch (ClassNotFoundException e) {
      throw new AssertionError("RenameResourceComposite class must exist", e);
    }
  }

  @Test
  public void removeResourceOperationClassExists() {
    try {
      Class.forName("org.alice.ide.resource.manager.RemoveResourceOperation");
    } catch (ClassNotFoundException e) {
      throw new AssertionError("RemoveResourceOperation class must exist", e);
    }
  }

  @Test
  public void reloadContentResourceOperationClassExists() {
    try {
      Class.forName("org.alice.ide.resource.manager.ReloadContentResourceOperation");
    } catch (ClassNotFoundException e) {
      throw new AssertionError("ReloadContentResourceOperation class must exist", e);
    }
  }

  @Test
  public void resourceManagerViewClassExists() {
    try {
      Class.forName("org.alice.ide.resource.manager.views.ResourceManagerView");
    } catch (ClassNotFoundException e) {
      throw new AssertionError("ResourceManagerView class must exist", e);
    }
  }
}
