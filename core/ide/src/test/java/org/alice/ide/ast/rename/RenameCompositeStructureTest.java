package org.alice.ide.ast.rename;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for rename composite and panel classes.
 */
public class RenameCompositeStructureTest {

  // ---- RenameDeclarationComposite ----

  @Test
  public void renameDeclarationComposite_classIsAccessible() {
    assertNotNull(RenameDeclarationComposite.class);
  }

  @Test
  public void renameDeclarationComposite_isPublic() {
    assertTrue(Modifier.isPublic(RenameDeclarationComposite.class.getModifiers()));
  }

  @Test
  public void renameDeclarationComposite_isAbstract() {
    assertTrue(Modifier.isAbstract(RenameDeclarationComposite.class.getModifiers()));
  }

  // ---- RenameParameterComposite ----

  @Test
  public void renameParameterComposite_classIsAccessible() {
    assertNotNull(RenameParameterComposite.class);
  }

  @Test
  public void renameParameterComposite_isPublic() {
    assertTrue(Modifier.isPublic(RenameParameterComposite.class.getModifiers()));
  }

  @Test
  public void renameParameterComposite_extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameParameterComposite.class));
  }

  @Test
  public void renameParameterComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(RenameParameterComposite.class.getModifiers()));
  }

  // ---- RenameMethodComposite ----

  @Test
  public void renameMethodComposite_classIsAccessible() {
    assertNotNull(RenameMethodComposite.class);
  }

  @Test
  public void renameMethodComposite_isPublic() {
    assertTrue(Modifier.isPublic(RenameMethodComposite.class.getModifiers()));
  }

  @Test
  public void renameMethodComposite_extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameMethodComposite.class));
  }

  // ---- RenameFieldComposite ----

  @Test
  public void renameFieldComposite_classIsAccessible() {
    assertNotNull(RenameFieldComposite.class);
  }

  @Test
  public void renameFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(RenameFieldComposite.class.getModifiers()));
  }

  @Test
  public void renameFieldComposite_extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameFieldComposite.class));
  }

  // ---- RenameLocalComposite ----

  @Test
  public void renameLocalComposite_classIsAccessible() {
    assertNotNull(RenameLocalComposite.class);
  }

  @Test
  public void renameLocalComposite_isPublic() {
    assertTrue(Modifier.isPublic(RenameLocalComposite.class.getModifiers()));
  }

  @Test
  public void renameLocalComposite_extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameLocalComposite.class));
  }

  // ---- RenameTypeComposite ----

  @Test
  public void renameTypeComposite_classIsAccessible() {
    assertNotNull(RenameTypeComposite.class);
  }

  @Test
  public void renameTypeComposite_isPublic() {
    assertTrue(Modifier.isPublic(RenameTypeComposite.class.getModifiers()));
  }

  @Test
  public void renameTypeComposite_extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameTypeComposite.class));
  }

  // ---- All concrete rename composites extend RenameDeclarationComposite ----

  @Test
  public void allConcreteRenameComposites_extendBase() {
    Class<?> base = RenameDeclarationComposite.class;
    assertTrue(base.isAssignableFrom(RenameParameterComposite.class));
    assertTrue(base.isAssignableFrom(RenameMethodComposite.class));
    assertTrue(base.isAssignableFrom(RenameFieldComposite.class));
    assertTrue(base.isAssignableFrom(RenameLocalComposite.class));
    assertTrue(base.isAssignableFrom(RenameTypeComposite.class));
  }

  // ---- All concrete composites are not abstract ----

  @Test
  public void allConcreteComposites_areNotAbstract() {
    assertFalse(Modifier.isAbstract(RenameParameterComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(RenameMethodComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(RenameFieldComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(RenameLocalComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(RenameTypeComposite.class.getModifiers()));
  }

  // ---- All rename classes are public ----

  @Test
  public void allRenameClasses_arePublic() {
    Class<?>[] classes = {
      RenameDeclarationComposite.class, RenameParameterComposite.class,
      RenameMethodComposite.class, RenameFieldComposite.class,
      RenameLocalComposite.class, RenameTypeComposite.class
    };
    for (Class<?> cls : classes) {
      assertTrue(cls.getSimpleName() + " should be public",
        Modifier.isPublic(cls.getModifiers()));
    }
  }

  // ---- RenamePanel ----

  @Test
  public void renamePanel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName("org.alice.ide.ast.rename.components.RenamePanel"));
  }

  @Test
  public void renamePanel_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(
      Class.forName("org.alice.ide.ast.rename.components.RenamePanel").getModifiers()));
  }
}
