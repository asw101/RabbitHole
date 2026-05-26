package org.alice.ide.ast;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import edu.cmu.cs.dennisc.javax.swing.ColorCustomizer;
import org.alice.ide.ast.type.merge.croquet.ActionMustBeTakenCard;
import org.alice.ide.ast.type.merge.croquet.DifferentImplementation;
import org.alice.ide.ast.type.merge.croquet.DifferentSignature;
import org.alice.ide.ast.type.merge.croquet.KeepImplementationCard;
import org.alice.ide.ast.type.merge.croquet.KeepSignatureCard;
import org.alice.ide.ast.type.merge.croquet.MemberHubWithNameState;
import org.alice.ide.ast.type.merge.croquet.RenameCard;
import org.alice.ide.ast.type.merge.croquet.ReplaceNegativeImplementationCard;
import org.alice.ide.ast.type.merge.croquet.ReplacePositiveImplementationCard;
import org.junit.Test;
import org.lgna.croquet.SimpleComposite;
import org.lgna.croquet.views.Panel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RenameCardStructureTest {

  @Test
  public void renameCard_modifiers_checked_publicFinalSimpleComposite() {
    assertTrue(Modifier.isPublic(RenameCard.class.getModifiers()));
    assertTrue(Modifier.isFinal(RenameCard.class.getModifiers()));
    assertEquals(SimpleComposite.class, RenameCard.class.getSuperclass());
  }

  @Test
  public void renameCard_fields_checked_expectedTypesAndNames() throws Exception {
    Set<String> names = Arrays.stream(declaredFields(RenameCard.class))
        .map(Field::getName)
        .collect(Collectors.toSet());
    assertEquals(2, names.size());
    assertTrue(names.contains("memberHubWithNameState"));
    assertTrue(names.contains("foregroundCustomizer"));

    Field hubField = RenameCard.class.getDeclaredField("memberHubWithNameState");
    assertEquals(MemberHubWithNameState.class, hubField.getType());
    assertTrue(Modifier.isPrivate(hubField.getModifiers()));
    assertTrue(Modifier.isFinal(hubField.getModifiers()));

    Field colorField = RenameCard.class.getDeclaredField("foregroundCustomizer");
    assertEquals(ColorCustomizer.class, colorField.getType());
    assertTrue(Modifier.isPrivate(colorField.getModifiers()));
    assertTrue(Modifier.isFinal(colorField.getModifiers()));
  }

  @Test
  public void renameCard_constructor_checked_hubAndCustomizerAccepted() throws Exception {
    Constructor<RenameCard> constructor = RenameCard.class.getDeclaredConstructor(MemberHubWithNameState.class, ColorCustomizer.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(2, constructor.getParameterCount());
  }

  @Test
  public void renameCard_createView_checked_protectedPanelReturned() throws Exception {
    Method method = RenameCard.class.getDeclaredMethod("createView");
    assertEquals(Panel.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void renameCard_declaredMethodNames_checked_onlyCreateViewPresent() {
    Set<String> names = Arrays.stream(declaredMethods(RenameCard.class))
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertEquals(1, names.size());
    assertTrue(names.contains("createView"));
  }

  @Test
  public void keepSignatureCard_structure_checked_simplePanelCard() throws Exception {
    assertSimpleCardStructure(KeepSignatureCard.class, "differentSignature", DifferentSignature.class, DifferentSignature.class);
  }

  @Test
  public void keepImplementationCard_structure_checked_simplePanelCard() throws Exception {
    assertSimpleCardStructure(KeepImplementationCard.class, "differentImplementation", DifferentImplementation.class, DifferentImplementation.class);
  }

  @Test
  public void replacePositiveImplementationCard_structure_checked_simplePanelCard() throws Exception {
    assertSimpleCardStructure(ReplacePositiveImplementationCard.class, "differentImplementation", DifferentImplementation.class, DifferentImplementation.class);
  }

  @Test
  public void replaceNegativeImplementationCard_structure_checked_simplePanelCard() throws Exception {
    assertSimpleCardStructure(ReplaceNegativeImplementationCard.class, "differentImplementation", DifferentImplementation.class, DifferentImplementation.class);
  }

  @Test
  public void actionMustBeTakenCard_structure_checked_simplePanelCard() throws Exception {
    assertSimpleCardStructure(ActionMustBeTakenCard.class, "differentImplementation", DifferentImplementation.class, DifferentImplementation.class);
  }

  @Test
  public void relatedCards_declaredMethodNames_checked_onlyCreateViewPresent() {
    assertRelatedCardMethods(KeepSignatureCard.class);
    assertRelatedCardMethods(KeepImplementationCard.class);
    assertRelatedCardMethods(ReplacePositiveImplementationCard.class);
    assertRelatedCardMethods(ReplaceNegativeImplementationCard.class);
    assertRelatedCardMethods(ActionMustBeTakenCard.class);
  }

  private static void assertSimpleCardStructure(Class<?> cardClass, String fieldName, Class<?> fieldType, Class<?> constructorType) throws Exception {
    assertTrue(cardClass.getName(), Modifier.isPublic(cardClass.getModifiers()));
    assertTrue(cardClass.getName(), Modifier.isFinal(cardClass.getModifiers()));
    assertEquals(SimpleComposite.class, cardClass.getSuperclass());
    assertEquals(1, declaredFields(cardClass).length);

    Field field = cardClass.getDeclaredField(fieldName);
    assertEquals(fieldType, field.getType());
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));

    Constructor<?> constructor = cardClass.getDeclaredConstructor(constructorType);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));

    Method method = cardClass.getDeclaredMethod("createView");
    assertEquals(Panel.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  private static void assertRelatedCardMethods(Class<?> cardClass) {
    Set<String> names = Arrays.stream(declaredMethods(cardClass))
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertEquals(cardClass.getName(), 1, names.size());
    assertTrue(cardClass.getName(), names.contains("createView"));
  }
}
