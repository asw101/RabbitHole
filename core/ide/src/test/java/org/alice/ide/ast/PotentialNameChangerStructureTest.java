package org.alice.ide.ast;

import edu.cmu.cs.dennisc.javax.swing.ColorCustomizer;
import org.alice.ide.ast.type.merge.croquet.MemberHubWithNameState;
import org.alice.ide.ast.type.merge.croquet.PotentialNameChanger;
import org.junit.Test;
import org.lgna.project.ast.Member;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class PotentialNameChangerStructureTest {

  @Test
  public void classReference_loaded_nonNull() {
    assertNotNull(PotentialNameChanger.class);
  }

  @Test
  public void classModifiers_checked_publicAbstract() {
    int modifiers = PotentialNameChanger.class.getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertTrue(Modifier.isAbstract(modifiers));
  }

  @Test
  public void typeParameter_checked_singleMemberBound() {
    TypeVariable<?>[] variables = PotentialNameChanger.class.getTypeParameters();
    assertEquals(1, variables.length);
    assertEquals("M", variables[0].getName());
    assertEquals(Member.class.getName(), variables[0].getBounds()[0].getTypeName());
  }

  @Test
  public void superclass_checked_objectOnly() {
    assertEquals(Object.class, PotentialNameChanger.class.getSuperclass());
  }

  @Test
  public void interfaces_checked_noneDeclared() {
    assertEquals(0, PotentialNameChanger.class.getInterfaces().length);
  }

  @Test
  public void declaredFields_checked_expectedNamesPresent() {
    Set<String> names = Arrays.stream(PotentialNameChanger.class.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());

    assertEquals(2, names.size());
    assertTrue(names.contains("foregroundCustomizer"));
    assertTrue(names.contains("uriForDescriptionPurposesOnly"));
  }

  @Test
  public void foregroundCustomizerField_checked_privateFinalColorCustomizer() throws Exception {
    Field field = PotentialNameChanger.class.getDeclaredField("foregroundCustomizer");
    assertEquals(ColorCustomizer.class, field.getType());
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void uriField_checked_privateFinalUri() throws Exception {
    Field field = PotentialNameChanger.class.getDeclaredField("uriForDescriptionPurposesOnly");
    assertEquals(URI.class, field.getType());
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void constructorSignature_checked_uriAccepted() throws Exception {
    Constructor<PotentialNameChanger> constructor = PotentialNameChanger.class.getDeclaredConstructor(URI.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(1, constructor.getParameterCount());
  }

  @Test
  public void methodNames_checked_expectedPublicAndProtectedSurfacePresent() {
    Set<String> names = Arrays.stream(PotentialNameChanger.class.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());

    assertTrue(names.contains("getUriForDescriptionPurposesOnly"));
    assertTrue(names.contains("getImportHub"));
    assertTrue(names.contains("getProjectHub"));
    assertTrue(names.contains("isRenameRequired"));
    assertTrue(names.contains("getForegroundCustomizer"));
  }

  @Test
  public void getUriForDescriptionPurposesOnly_signature_checked_uriReturned() throws Exception {
    Method method = PotentialNameChanger.class.getDeclaredMethod("getUriForDescriptionPurposesOnly");
    assertEquals(URI.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void getImportHub_signature_checked_publicAbstractMemberHubReturned() throws Exception {
    Method method = PotentialNameChanger.class.getDeclaredMethod("getImportHub");
    assertEquals(MemberHubWithNameState.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void getProjectHub_signature_checked_publicAbstractMemberHubReturned() throws Exception {
    Method method = PotentialNameChanger.class.getDeclaredMethod("getProjectHub");
    assertEquals(MemberHubWithNameState.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void isRenameRequired_signature_checked_protectedAbstractBooleanReturned() throws Exception {
    Method method = PotentialNameChanger.class.getDeclaredMethod("isRenameRequired");
    assertEquals(boolean.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void getForegroundCustomizer_signature_checked_colorCustomizerReturned() throws Exception {
    Method method = PotentialNameChanger.class.getDeclaredMethod("getForegroundCustomizer");
    assertEquals(ColorCustomizer.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertFalse(Modifier.isAbstract(method.getModifiers()));
  }
}
