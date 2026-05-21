package org.alice.ide.ast.export;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.Test;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link FieldInfo}.
 * Reflection-only — tests specialization of MemberInfo&lt;UserField&gt;.
 */
public class FieldInfoCoverageTest {

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(FieldInfo.class.getModifiers()));
  }

  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(FieldInfo.class.getModifiers()));
  }

  @Test
  public void extendsMemberInfo() {
    assertEquals(MemberInfo.class, FieldInfo.class.getSuperclass());
  }

  @Test
  public void genericSuperclassIsParameterizedWithUserField() {
    Type superType = FieldInfo.class.getGenericSuperclass();
    assertTrue(superType instanceof ParameterizedType);
    ParameterizedType paramType = (ParameterizedType) superType;
    assertEquals(UserField.class, paramType.getActualTypeArguments()[0]);
  }

  @Test
  public void hasConstructorTakingProjectInfoAndUserField() {
    Constructor<?>[] ctors = FieldInfo.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    Class<?>[] paramTypes = ctors[0].getParameterTypes();
    assertEquals(2, paramTypes.length);
    assertEquals(ProjectInfo.class, paramTypes[0]);
    assertEquals(UserField.class, paramTypes[1]);
  }

  @Test
  public void constructorIsPublic() {
    Constructor<?>[] ctors = FieldInfo.class.getDeclaredConstructors();
    assertTrue(Modifier.isPublic(ctors[0].getModifiers()));
  }

  @Test
  public void declaresNoAdditionalMethods() {
    assertEquals(0, FieldInfo.class.getDeclaredMethods().length);
  }

  @Test
  public void declaresNoAdditionalFields() {
    assertEquals(0, FieldInfo.class.getDeclaredFields().length);
  }

  @Test
  public void inheritsFromDeclarationInfo() {
    assertTrue(DeclarationInfo.class.isAssignableFrom(FieldInfo.class));
  }

  @Test
  public void isNotFinal() {
    assertFalse(Modifier.isFinal(FieldInfo.class.getModifiers()));
  }
}
