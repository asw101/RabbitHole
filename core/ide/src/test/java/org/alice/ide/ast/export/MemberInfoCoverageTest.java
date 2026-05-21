package org.alice.ide.ast.export;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.Test;
import org.lgna.project.ast.Member;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link MemberInfo}.
 * Reflection-only — MemberInfo needs ProjectInfo + declaration infrastructure.
 */
public class MemberInfoCoverageTest {

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(MemberInfo.class.getModifiers()));
  }

  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(MemberInfo.class.getModifiers()));
  }

  @Test
  public void extendsDeclarationInfo() {
    assertEquals(DeclarationInfo.class, MemberInfo.class.getSuperclass());
  }

  @Test
  public void hasGenericTypeParameter() {
    TypeVariable<?>[] typeParams = MemberInfo.class.getTypeParameters();
    assertEquals(1, typeParams.length);
    assertEquals("D", typeParams[0].getName());
  }

  @Test
  public void typeParameterBoundedByMember() {
    TypeVariable<?>[] typeParams = MemberInfo.class.getTypeParameters();
    Type[] bounds = typeParams[0].getBounds();
    assertTrue(bounds.length > 0);
    assertEquals(Member.class, bounds[0]);
  }

  @Test
  public void hasConstructorTakingProjectInfoAndDeclaration() {
    Constructor<?>[] ctors = MemberInfo.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertEquals(2, ctors[0].getParameterCount());
    assertEquals(ProjectInfo.class, ctors[0].getParameterTypes()[0]);
  }

  @Test
  public void hasDependenciesInnerClass() {
    Class<?>[] innerClasses = MemberInfo.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> inner : innerClasses) {
      if (inner.getSimpleName().equals("Dependencies")) {
        found = true;
        break;
      }
    }
    assertTrue("MemberInfo should have inner Dependencies class", found);
  }

  @Test
  public void hasDeclaringTypeInfoField() {
    ReflectionTestHelper.assertFieldExists(MemberInfo.class, "declaringTypeInfo");
  }

  @Test
  public void hasDependenciesField() {
    ReflectionTestHelper.assertFieldExists(MemberInfo.class, "dependencies");
  }

  @Test
  public void hasUpdateDependenciesMethod() {
    ReflectionTestHelper.assertMethodPresent(MemberInfo.class, "updateDependencies");
  }

  @Test
  public void hasAddRequiredMethod() {
    ReflectionTestHelper.assertMethodPresent(MemberInfo.class, "addRequired");
  }
}
