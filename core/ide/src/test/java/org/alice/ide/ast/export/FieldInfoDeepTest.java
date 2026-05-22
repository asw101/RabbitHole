package org.alice.ide.ast.export;

import org.junit.Test;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link FieldInfo} class hierarchy and metadata.
 */
public class FieldInfoDeepTest {

  @Test
  public void extendsMemberInfo() {
    assertTrue(MemberInfo.class.isAssignableFrom(FieldInfo.class));
  }

  @Test
  public void extendsDeclarationInfo() {
    assertTrue(DeclarationInfo.class.isAssignableFrom(FieldInfo.class));
  }

  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(FieldInfo.class.getModifiers()));
  }

  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(FieldInfo.class.getModifiers()));
  }

  @Test
  public void className_isFieldInfo() {
    assertEquals("FieldInfo", FieldInfo.class.getSimpleName());
  }

  @Test
  public void packageName_isCorrect() {
    assertEquals("org.alice.ide.ast.export", FieldInfo.class.getPackage().getName());
  }

  @Test
  public void hasConstructor() {
    assertTrue(FieldInfo.class.getDeclaredConstructors().length > 0);
  }

  @Test
  public void constructorTakesProjectInfoAndUserField() throws Exception {
    assertNotNull(FieldInfo.class.getDeclaredConstructor(
        ProjectInfo.class, org.lgna.project.ast.UserField.class));
  }

  @Test
  public void memberInfoSuperclass() {
    assertEquals(MemberInfo.class, FieldInfo.class.getSuperclass());
  }

  @Test
  public void isNotInterface() {
    assertFalse(FieldInfo.class.isInterface());
  }

  @Test
  public void isNotEnum() {
    assertFalse(FieldInfo.class.isEnum());
  }

  @Test
  public void isNotAnnotation() {
    assertFalse(FieldInfo.class.isAnnotation());
  }
}
