package org.alice.ide.ast.resource;

import org.lgna.common.Resource;
import org.lgna.croquet.ImportValueCreator;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Characterization tests for the resource package:
 * ResourceImportValueCreator, ImageResourceImportValueCreator, AudioResourceImportValueCreator.
 */
public class ResourceImportCharacterizationTest {

  // ══════════════════════════════════════════════════════════════
  // ResourceImportValueCreator — abstract base
  // ══════════════════════════════════════════════════════════════

  @Test
  public void resourceImport_isAbstract() {
    assertTrue(Modifier.isAbstract(ResourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void resourceImport_isPublic() {
    assertTrue(Modifier.isPublic(ResourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void resourceImport_extendsImportValueCreator() {
    assertTrue(ImportValueCreator.class.isAssignableFrom(ResourceImportValueCreator.class));
  }

  @Test
  public void resourceImport_inCorrectPackage() {
    assertEquals("org.alice.ide.ast.resource",
        ResourceImportValueCreator.class.getPackage().getName());
  }

  @Test
  public void resourceImport_hasResourceClsField() throws Exception {
    Field f = ResourceImportValueCreator.class.getDeclaredField("resourceCls");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(Class.class, f.getType());
  }

  @Test
  public void resourceImport_hasCreateValueFromImportedValueMethod() {
    boolean found = Arrays.stream(ResourceImportValueCreator.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("createValueFromImportedValue"));
    assertTrue("Must override createValueFromImportedValue()", found);
  }

  @Test
  public void resourceImport_constructorTakesThreeParams() {
    boolean found = Arrays.stream(ResourceImportValueCreator.class.getDeclaredConstructors())
        .anyMatch(c -> c.getParameterCount() == 3);
    assertTrue("Constructor must accept (UUID, Importer, Class)", found);
  }

  // ══════════════════════════════════════════════════════════════
  // ImageResourceImportValueCreator
  // ══════════════════════════════════════════════════════════════

  @Test
  public void imageResource_isPublic() {
    assertTrue(Modifier.isPublic(ImageResourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void imageResource_extendsResourceImportValueCreator() {
    assertTrue(ResourceImportValueCreator.class.isAssignableFrom(ImageResourceImportValueCreator.class));
  }

  @Test
  public void imageResource_isNotAbstract() {
    assertFalse(Modifier.isAbstract(ImageResourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void imageResource_inCorrectPackage() {
    assertEquals("org.alice.ide.ast.resource",
        ImageResourceImportValueCreator.class.getPackage().getName());
  }

  // ══════════════════════════════════════════════════════════════
  // AudioResourceImportValueCreator
  // ══════════════════════════════════════════════════════════════

  @Test
  public void audioResource_isPublic() {
    assertTrue(Modifier.isPublic(AudioResourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void audioResource_extendsResourceImportValueCreator() {
    assertTrue(ResourceImportValueCreator.class.isAssignableFrom(AudioResourceImportValueCreator.class));
  }

  @Test
  public void audioResource_isNotAbstract() {
    assertFalse(Modifier.isAbstract(AudioResourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void audioResource_inCorrectPackage() {
    assertEquals("org.alice.ide.ast.resource",
        AudioResourceImportValueCreator.class.getPackage().getName());
  }

  // ══════════════════════════════════════════════════════════════
  // Inheritance chain correctness
  // ══════════════════════════════════════════════════════════════

  @Test
  public void imageResource_superclassIsResourceImportValueCreator() {
    assertEquals(ResourceImportValueCreator.class, ImageResourceImportValueCreator.class.getSuperclass());
  }

  @Test
  public void audioResource_superclassIsResourceImportValueCreator() {
    assertEquals(ResourceImportValueCreator.class, AudioResourceImportValueCreator.class.getSuperclass());
  }
}
