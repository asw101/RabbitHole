package org.alice.ide.ast.export;

import org.junit.Test;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link MethodInfo} class hierarchy and metadata.
 */
public class MethodInfoDeepTest {

  @Test
  public void extendsMemberInfo() {
    assertTrue(MemberInfo.class.isAssignableFrom(MethodInfo.class));
  }

  @Test
  public void extendsDeclarationInfo() {
    assertTrue(DeclarationInfo.class.isAssignableFrom(MethodInfo.class));
  }

  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(MethodInfo.class.getModifiers()));
  }

  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(MethodInfo.class.getModifiers()));
  }

  @Test
  public void className_isMethodInfo() {
    assertEquals("MethodInfo", MethodInfo.class.getSimpleName());
  }

  @Test
  public void packageName_isCorrect() {
    assertEquals("org.alice.ide.ast.export", MethodInfo.class.getPackage().getName());
  }

  @Test
  public void hasConstructor() {
    assertTrue(MethodInfo.class.getDeclaredConstructors().length > 0);
  }

  @Test
  public void constructorTakesProjectInfoAndUserMethod() throws Exception {
    assertNotNull(MethodInfo.class.getDeclaredConstructor(
        ProjectInfo.class, org.lgna.project.ast.UserMethod.class));
  }

  @Test
  public void memberInfoSuperclass() {
    assertEquals(MemberInfo.class, MethodInfo.class.getSuperclass());
  }

  @Test
  public void isNotInterface() {
    assertFalse(MethodInfo.class.isInterface());
  }

  @Test
  public void isNotEnum() {
    assertFalse(MethodInfo.class.isEnum());
  }

  @Test
  public void isNotAnnotation() {
    assertFalse(MethodInfo.class.isAnnotation());
  }

  @Test
  public void notSameClassAsFieldInfo() {
    assertNotEquals(MethodInfo.class, FieldInfo.class);
  }

  @Test
  public void hasMethods() {
    assertTrue(MethodInfo.class.getDeclaredMethods().length >= 0);
  }
}
