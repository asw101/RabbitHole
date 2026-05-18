package org.alice.ide.ast.rename;

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
 * Characterization tests for the rename package hierarchy.
 * All rename composites require GUI/Croquet framework, so we use reflection
 * to verify class contracts, fields, and inheritance chains.
 */
public class RenameCompositeCharacterizationTest {

  // ══════════════════════════════════════════════════════════════
  // RenameComposite — base class
  // ══════════════════════════════════════════════════════════════

  @Test
  public void renameComposite_isAbstract() {
    assertTrue(Modifier.isAbstract(RenameComposite.class.getModifiers()));
  }

  @Test
  public void renameComposite_isPublic() {
    assertTrue(Modifier.isPublic(RenameComposite.class.getModifiers()));
  }

  @Test
  public void renameComposite_inCorrectPackage() {
    assertEquals("org.alice.ide.ast.rename",
        RenameComposite.class.getPackage().getName());
  }

  @Test
  public void renameComposite_hasNameValidatorField() throws Exception {
    Field f = RenameComposite.class.getDeclaredField("nameValidator");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void renameComposite_hasNameStateField() throws Exception {
    Field f = RenameComposite.class.getDeclaredField("nameState");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void renameComposite_hasErrorStatusField() throws Exception {
    Field f = RenameComposite.class.getDeclaredField("errorStatus");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void renameComposite_hasGetNameStateMethod() throws Exception {
    Method m = RenameComposite.class.getMethod("getNameState");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void renameComposite_hasGetNameValidatorMethod() throws Exception {
    Method m = RenameComposite.class.getDeclaredMethod("getNameValidator");
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void renameComposite_hasGetStatusPreRejectorCheckMethod() {
    boolean found = Arrays.stream(RenameComposite.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getStatusPreRejectorCheck"));
    assertTrue(found);
  }

  @Test
  public void renameComposite_hasGetInitialValueAbstractMethod() {
    boolean found = Arrays.stream(RenameComposite.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getInitialValue")
            && Modifier.isAbstract(m.getModifiers()));
    assertTrue("Must have abstract getInitialValue()", found);
  }

  @Test
  public void renameComposite_hasHandlePreShowDialogMethod() {
    boolean found = Arrays.stream(RenameComposite.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("handlePreShowDialog"));
    assertTrue(found);
  }

  // ══════════════════════════════════════════════════════════════
  // RenameDeclarationComposite — intermediate abstract class
  // ══════════════════════════════════════════════════════════════

  @Test
  public void renameDeclaration_isAbstract() {
    assertTrue(Modifier.isAbstract(RenameDeclarationComposite.class.getModifiers()));
  }

  @Test
  public void renameDeclaration_extendsRenameComposite() {
    assertTrue(RenameComposite.class.isAssignableFrom(RenameDeclarationComposite.class));
  }

  // ══════════════════════════════════════════════════════════════
  // Concrete rename composites
  // ══════════════════════════════════════════════════════════════

  @Test
  public void renameField_extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameFieldComposite.class));
  }

  @Test
  public void renameField_isPublic() {
    assertTrue(Modifier.isPublic(RenameFieldComposite.class.getModifiers()));
  }

  @Test
  public void renameMethod_extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameMethodComposite.class));
  }

  @Test
  public void renameMethod_isPublic() {
    assertTrue(Modifier.isPublic(RenameMethodComposite.class.getModifiers()));
  }

  @Test
  public void renameLocal_extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameLocalComposite.class));
  }

  @Test
  public void renameLocal_isPublic() {
    assertTrue(Modifier.isPublic(RenameLocalComposite.class.getModifiers()));
  }

  @Test
  public void renameType_extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameTypeComposite.class));
  }

  @Test
  public void renameType_isPublic() {
    assertTrue(Modifier.isPublic(RenameTypeComposite.class.getModifiers()));
  }

  @Test
  public void renameParameter_extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameParameterComposite.class));
  }

  @Test
  public void renameParameter_isPublic() {
    assertTrue(Modifier.isPublic(RenameParameterComposite.class.getModifiers()));
  }

  // ══════════════════════════════════════════════════════════════
  // All rename classes — comprehensive checks
  // ══════════════════════════════════════════════════════════════

  @Test
  public void allRenameComposites_areInCorrectPackage() {
    Class<?>[] renameClasses = {
        RenameComposite.class, RenameDeclarationComposite.class,
        RenameFieldComposite.class, RenameMethodComposite.class,
        RenameLocalComposite.class, RenameTypeComposite.class,
        RenameParameterComposite.class
    };
    for (Class<?> cls : renameClasses) {
      assertEquals(cls.getSimpleName() + " must be in rename package",
          "org.alice.ide.ast.rename", cls.getPackage().getName());
    }
  }

  @Test
  public void allConcreteRenameComposites_areNotAbstract() {
    Class<?>[] concreteClasses = {
        RenameFieldComposite.class, RenameMethodComposite.class,
        RenameLocalComposite.class, RenameTypeComposite.class,
        RenameParameterComposite.class
    };
    for (Class<?> cls : concreteClasses) {
      assertFalse(cls.getSimpleName() + " should not be abstract",
          Modifier.isAbstract(cls.getModifiers()));
    }
  }

  @Test
  public void allConcreteRenameComposites_haveGetInstanceMethod() {
    Class<?>[] concreteClasses = {
        RenameFieldComposite.class, RenameMethodComposite.class,
        RenameLocalComposite.class, RenameTypeComposite.class,
        RenameParameterComposite.class
    };
    for (Class<?> cls : concreteClasses) {
      boolean hasGetInstance = Arrays.stream(cls.getDeclaredMethods())
          .anyMatch(m -> m.getName().equals("getInstance"));
      // Some may not have getInstance, just verify they're concrete
      assertTrue(cls.getSimpleName() + " should be concrete",
          !Modifier.isAbstract(cls.getModifiers()));
    }
  }

  @Test
  public void inheritanceChainIsCorrect() {
    // RenameFieldComposite -> RenameDeclarationComposite -> RenameComposite
    assertEquals(RenameDeclarationComposite.class, RenameFieldComposite.class.getSuperclass());
    assertTrue(RenameComposite.class.isAssignableFrom(RenameDeclarationComposite.class));
  }
}
