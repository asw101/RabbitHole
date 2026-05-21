package org.alice.ide.ast;

import org.alice.ide.ast.type.merge.croquet.IsMemberDesiredState;
import org.junit.Test;
import org.lgna.croquet.BooleanState;
import org.lgna.project.ast.Member;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class IsMemberDesiredStateStructureTest {

  @Test
  public void classReference_loaded_nonNull() {
    assertNotNull(IsMemberDesiredState.class);
  }

  @Test
  public void classModifiers_checked_publicFinal() {
    int modifiers = IsMemberDesiredState.class.getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertTrue(Modifier.isFinal(modifiers));
    assertFalse(Modifier.isAbstract(modifiers));
  }

  @Test
  public void typeParameter_checked_singleMemberBound() {
    TypeVariable<?>[] variables = IsMemberDesiredState.class.getTypeParameters();
    assertEquals(1, variables.length);
    assertEquals("M", variables[0].getName());
    assertEquals(Member.class.getName(), variables[0].getBounds()[0].getTypeName());
  }

  @Test
  public void superclass_checked_booleanState() {
    assertEquals(BooleanState.class, IsMemberDesiredState.class.getSuperclass());
  }

  @Test
  public void interfaces_checked_noneDeclared() {
    assertEquals(0, IsMemberDesiredState.class.getInterfaces().length);
  }

  @Test
  public void declaredFieldNames_checked_onlyMemberPresent() {
    Set<String> names = Arrays.stream(IsMemberDesiredState.class.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());

    assertEquals(1, names.size());
    assertTrue(names.contains("member"));
  }

  @Test
  public void memberField_checked_privateFinalMember() throws Exception {
    Field field = IsMemberDesiredState.class.getDeclaredField("member");
    assertEquals(Member.class, field.getType());
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void constructorSignature_checked_memberAndBooleanAccepted() throws Exception {
    Constructor<IsMemberDesiredState> constructor = IsMemberDesiredState.class.getDeclaredConstructor(Member.class, boolean.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(2, constructor.getParameterCount());
  }

  @Test
  public void constructorCount_checked_singleConstructor() {
    assertEquals(1, IsMemberDesiredState.class.getDeclaredConstructors().length);
  }

  @Test
  public void declaredMethodNames_checked_onlyGetMemberPresent() {
    Set<String> names = Arrays.stream(IsMemberDesiredState.class.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());

    assertEquals(1, names.size());
    assertTrue(names.contains("getMember"));
  }

  @Test
  public void publicMethodNames_checked_onlyGetMemberDeclared() {
    Set<String> names = Arrays.stream(IsMemberDesiredState.class.getDeclaredMethods())
        .filter(method -> Modifier.isPublic(method.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toSet());

    assertEquals(1, names.size());
    assertTrue(names.contains("getMember"));
  }

  @Test
  public void getMember_signature_checked_memberReturned() throws Exception {
    Method method = IsMemberDesiredState.class.getDeclaredMethod("getMember");
    assertEquals(Member.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertFalse(Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void getMember_signature_checked_noParametersDeclared() throws Exception {
    Method method = IsMemberDesiredState.class.getDeclaredMethod("getMember");
    assertEquals(0, method.getParameterCount());
  }

  @Test
  public void declaredConstructors_checked_memberAndBooleanParameterTypesRetained() {
    Class<?>[] parameterTypes = IsMemberDesiredState.class.getDeclaredConstructors()[ 0 ].getParameterTypes();
    assertEquals(2, parameterTypes.length);
    assertEquals(Member.class, parameterTypes[ 0 ]);
    assertEquals(boolean.class, parameterTypes[ 1 ]);
  }
}
