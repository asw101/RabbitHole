package org.alice.ide.ast;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.alice.ide.ast.type.merge.croquet.edits.ImportTypeEdit;
import org.alice.ide.ast.type.merge.croquet.edits.RenameMemberData;
import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.Member;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NodeListProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ImportTypeEditStructureTest {

  @Test
  public void classModifiers_checked_publicConcrete() {
    int modifiers = ImportTypeEdit.class.getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertFalse(Modifier.isAbstract(modifiers));
  }

  @Test
  public void superclass_checked_abstractEdit() {
    assertEquals(AbstractEdit.class, ImportTypeEdit.class.getSuperclass());
  }

  @Test
  public void declaredFields_checked_expectedNamesPresent() {
    Set<String> names = Arrays.stream(ImportTypeEdit.class.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());

    assertTrue(names.contains("uriForDescriptionPurposesOnly"));
    assertTrue(names.contains("existingType"));
    assertTrue(names.contains("methodsToAdd"));
    assertTrue(names.contains("methodsToRemove"));
    assertTrue(names.contains("fieldsToAdd"));
    assertTrue(names.contains("fieldsToRemove"));
    assertTrue(names.contains("renames"));
  }

  @Test
  public void fieldTypes_checked_expectedRawAndGenericTypes() throws Exception {
    assertEquals(URI.class, ImportTypeEdit.class.getDeclaredField("uriForDescriptionPurposesOnly").getType());
    assertEquals(NamedUserType.class, ImportTypeEdit.class.getDeclaredField("existingType").getType());
    assertListFieldContains("methodsToAdd", "UserMethod");
    assertListFieldContains("methodsToRemove", "UserMethod");
    assertListFieldContains("fieldsToAdd", "UserField");
    assertListFieldContains("fieldsToRemove", "UserField");
    assertListFieldContains("renames", RenameMemberData.class.getSimpleName());
  }

  @Test
  public void fields_checked_allPrivateFinal() {
    for (Field field : ImportTypeEdit.class.getDeclaredFields()) {
      assertTrue(field.getName(), Modifier.isPrivate(field.getModifiers()));
      assertTrue(field.getName(), Modifier.isFinal(field.getModifiers()));
    }
  }

  @Test
  public void constructors_checked_activityAndDecoderVariantsPresent() throws Exception {
    Constructor<ImportTypeEdit> activityConstructor = ImportTypeEdit.class.getDeclaredConstructor(
        UserActivity.class,
        URI.class,
        NamedUserType.class,
        List.class,
        List.class,
        List.class,
        List.class,
        List.class);
    assertTrue(Modifier.isPublic(activityConstructor.getModifiers()));

    Constructor<ImportTypeEdit> decoderConstructor = ImportTypeEdit.class.getDeclaredConstructor(BinaryDecoder.class, Object.class);
    assertTrue(Modifier.isPublic(decoderConstructor.getModifiers()));
    assertEquals(2, ImportTypeEdit.class.getDeclaredConstructors().length);
  }

  @Test
  public void encode_signature_checked_publicVoid() throws Exception {
    Method method = ImportTypeEdit.class.getDeclaredMethod("encode", BinaryEncoder.class);
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void helperAdd_signature_checked_privateStaticVoid() throws Exception {
    Method method = ImportTypeEdit.class.getDeclaredMethod("add", NodeListProperty.class, Member.class);
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isPrivate(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void helperRemove_signature_checked_privateStaticVoid() throws Exception {
    Method method = ImportTypeEdit.class.getDeclaredMethod("remove", NodeListProperty.class, Member.class);
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isPrivate(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void doOrRedoInternal_signature_checked_protectedFinalVoid() throws Exception {
    Method method = ImportTypeEdit.class.getDeclaredMethod("doOrRedoInternal", boolean.class);
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertTrue(Modifier.isFinal(method.getModifiers()));
  }

  @Test
  public void undoInternal_signature_checked_protectedFinalVoid() throws Exception {
    Method method = ImportTypeEdit.class.getDeclaredMethod("undoInternal");
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertTrue(Modifier.isFinal(method.getModifiers()));
  }

  @Test
  public void appendDescription_signature_checked_protectedVoid() throws Exception {
    Class<?> descriptionStyle = Class.forName("org.lgna.croquet.edits.AbstractEdit$DescriptionStyle");
    Method method = ImportTypeEdit.class.getDeclaredMethod("appendDescription", StringBuilder.class, descriptionStyle);
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void declaredMethodNames_checked_expectedSurfacePresent() {
    Set<String> names = Arrays.stream(ImportTypeEdit.class.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());

    assertTrue(names.contains("encode"));
    assertTrue(names.contains("add"));
    assertTrue(names.contains("remove"));
    assertTrue(names.contains("doOrRedoInternal"));
    assertTrue(names.contains("undoInternal"));
    assertTrue(names.contains("appendDescription"));
  }

  private static void assertListFieldContains(String fieldName, String typeFragment) throws Exception {
    Field field = ImportTypeEdit.class.getDeclaredField(fieldName);
    assertEquals(List.class, field.getType());
    assertTrue(field.getGenericType().getTypeName(), field.getGenericType().getTypeName().contains(typeFragment));
  }
}
