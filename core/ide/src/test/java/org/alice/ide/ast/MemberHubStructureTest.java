package org.alice.ide.ast;

import org.alice.ide.ast.type.merge.croquet.ActionStatus;
import org.alice.ide.ast.type.merge.croquet.BareBonesMemberHub;
import org.alice.ide.ast.type.merge.croquet.MemberHub;
import org.alice.ide.ast.type.merge.croquet.MemberPopupCoreComposite;
import org.junit.Test;
import org.lgna.project.ast.Member;

import javax.swing.Icon;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MemberHubStructureTest {

  @Test
  public void classReference_loaded_nonNull() {
    assertNotNull(MemberHub.class);
  }

  @Test
  public void classModifiers_checked_publicAbstract() {
    int modifiers = MemberHub.class.getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertTrue(Modifier.isAbstract(modifiers));
  }

  @Test
  public void typeParameter_checked_singleMemberBound() {
    TypeVariable<?>[] variables = MemberHub.class.getTypeParameters();
    assertEquals(1, variables.length);
    assertEquals("M", variables[0].getName());
    assertEquals(Member.class.getName(), variables[0].getBounds()[0].getTypeName());
  }

  @Test
  public void superclass_checked_bareBonesMemberHub() {
    assertEquals(BareBonesMemberHub.class, MemberHub.class.getSuperclass());
  }

  @Test
  public void interfaces_checked_noneDeclared() {
    assertEquals(0, MemberHub.class.getInterfaces().length);
  }

  @Test
  public void declaredFields_checked_expectedNames() {
    Set<String> names = Arrays.stream(MemberHub.class.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());

    assertEquals(2, names.size());
    assertTrue(names.contains("popup"));
    assertTrue(names.contains("icon"));
  }

  @Test
  public void popupField_checked_privateFinalMemberPopupCoreComposite() throws Exception {
    Field field = MemberHub.class.getDeclaredField("popup");
    assertEquals(MemberPopupCoreComposite.class, field.getType());
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void iconField_checked_privateFinalIcon() throws Exception {
    Field field = MemberHub.class.getDeclaredField("icon");
    assertEquals(Icon.class, field.getType());
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void constructorSignature_checked_memberAndBooleanAccepted() throws Exception {
    Constructor<MemberHub> constructor = MemberHub.class.getDeclaredConstructor(Member.class, boolean.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(2, constructor.getParameterCount());
  }

  @Test
  public void publicMethodNames_checked_expectedSurfacePresent() {
    Set<String> names = Arrays.stream(MemberHub.class.getDeclaredMethods())
        .filter(method -> Modifier.isPublic(method.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toSet());

    assertTrue(names.contains("getIcon"));
    assertTrue(names.contains("getPopup"));
    assertTrue(names.contains("getActionStatus"));
    assertTrue(names.contains("getDescriptionText"));
  }

  @Test
  public void getIcon_signature_checked_iconReturned() throws Exception {
    Method method = MemberHub.class.getDeclaredMethod("getIcon");
    assertEquals(Icon.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertFalse(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void getPopup_signature_checked_popupReturned() throws Exception {
    Method method = MemberHub.class.getDeclaredMethod("getPopup");
    assertEquals(MemberPopupCoreComposite.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertFalse(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void getActionStatus_signature_checked_publicAbstract() throws Exception {
    Method method = MemberHub.class.getDeclaredMethod("getActionStatus");
    assertEquals(ActionStatus.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void getDescriptionText_signature_checked_stringReturned() throws Exception {
    Method method = MemberHub.class.getDeclaredMethod("getDescriptionText");
    assertEquals(String.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertFalse(Modifier.isAbstract(method.getModifiers()));
  }
}
