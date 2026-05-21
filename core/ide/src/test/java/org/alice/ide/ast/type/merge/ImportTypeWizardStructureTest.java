package org.alice.ide.ast.type.merge;

import org.alice.ide.ast.type.croquet.ImportTypeIteratingOperation;
import org.alice.ide.ast.type.croquet.ImportTypeWizard;
import org.alice.ide.ast.type.merge.croquet.AddMembersPage;
import org.alice.ide.ast.type.preview.croquet.PreviewPage;
import org.junit.Test;
import org.lgna.croquet.SimpleOperationWizardDialogCoreComposite;
import org.lgna.croquet.SingleThreadIteratingOperation;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.edits.Edit;
import org.lgna.project.ast.NamedUserType;

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

public class ImportTypeWizardStructureTest {

  @Test
  public void importTypeWizard_modifiers_checked_publicConcrete() {
    assertTrue(Modifier.isPublic(ImportTypeWizard.class.getModifiers()));
    assertFalse(Modifier.isFinal(ImportTypeWizard.class.getModifiers()));
    assertEquals(SimpleOperationWizardDialogCoreComposite.class, ImportTypeWizard.class.getSuperclass());
  }

  @Test
  public void importTypeWizard_fields_checked_expectedTypes() throws Exception {
    Set<String> names = Arrays.stream(ImportTypeWizard.class.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());
    assertEquals(2, names.size());
    assertTrue(names.contains("addMembersPage"));
    assertTrue(names.contains("previewPage"));
    assertEquals(AddMembersPage.class, ImportTypeWizard.class.getDeclaredField("addMembersPage").getType());
    assertEquals(PreviewPage.class, ImportTypeWizard.class.getDeclaredField("previewPage").getType());
  }

  @Test
  public void importTypeWizard_constructor_checked_uriTypesAndResourcesAccepted() throws Exception {
    Constructor<ImportTypeWizard> constructor = ImportTypeWizard.class.getDeclaredConstructor(
        URI.class,
        NamedUserType.class,
        Set.class,
        NamedUserType.class,
        NamedUserType.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void importTypeWizard_getters_checked_expectedReturnTypes() throws Exception {
    assertEquals(AddMembersPage.class, ImportTypeWizard.class.getDeclaredMethod("getAddMembersPage").getReturnType());
    assertEquals(PreviewPage.class, ImportTypeWizard.class.getDeclaredMethod("getPreviewPage").getReturnType());
  }

  @Test
  public void importTypeWizard_protectedMethods_checked_expectedSignatures() throws Exception {
    Method adornment = ImportTypeWizard.class.getDeclaredMethod("isAdornmentDesired");
    assertEquals(boolean.class, adornment.getReturnType());
    assertTrue(Modifier.isProtected(adornment.getModifiers()));

    Method goldenRatio = ImportTypeWizard.class.getDeclaredMethod("getGoldenRatioPolicy");
    assertEquals("GoldenRatioPolicy", goldenRatio.getReturnType().getSimpleName());
    assertTrue(Modifier.isProtected(goldenRatio.getModifiers()));

    Method createEdit = ImportTypeWizard.class.getDeclaredMethod("createEdit");
    assertEquals(Edit.class, createEdit.getReturnType());
    assertTrue(Modifier.isProtected(createEdit.getModifiers()));
  }

  @Test
  public void importTypeWizard_main_checked_publicStaticVoidSignature() throws Exception {
    Method main = ImportTypeWizard.class.getDeclaredMethod("main", String[].class);
    assertEquals(void.class, main.getReturnType());
    assertTrue(Modifier.isPublic(main.getModifiers()));
    assertTrue(Modifier.isStatic(main.getModifiers()));
  }

  @Test
  public void importTypeIteratingOperation_modifiers_checked_publicFinal() {
    assertTrue(Modifier.isPublic(ImportTypeIteratingOperation.class.getModifiers()));
    assertTrue(Modifier.isFinal(ImportTypeIteratingOperation.class.getModifiers()));
    assertEquals(SingleThreadIteratingOperation.class, ImportTypeIteratingOperation.class.getSuperclass());
  }

  @Test
  public void importTypeIteratingOperation_field_checked_privateFinalDstType() throws Exception {
    Field field = ImportTypeIteratingOperation.class.getDeclaredField("dstType");
    assertEquals(NamedUserType.class, field.getType());
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void importTypeIteratingOperation_constructor_checked_namedUserTypeAccepted() throws Exception {
    Constructor<ImportTypeIteratingOperation> constructor = ImportTypeIteratingOperation.class.getDeclaredConstructor(NamedUserType.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void importTypeIteratingOperation_methods_checked_expectedSurface() throws Exception {
    Method hasNext = ImportTypeIteratingOperation.class.getDeclaredMethod("hasNext", List.class);
    assertEquals(boolean.class, hasNext.getReturnType());
    assertTrue(Modifier.isProtected(hasNext.getModifiers()));

    Method getNext = ImportTypeIteratingOperation.class.getDeclaredMethod("getNext", List.class);
    assertEquals(Triggerable.class, getNext.getReturnType());
    assertTrue(Modifier.isProtected(getNext.getModifiers()));
  }

  @Test
  public void importTypeClasses_declaredMethodNames_checked_expectedEntriesPresent() {
    Set<String> wizardMethods = Arrays.stream(ImportTypeWizard.class.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertTrue(wizardMethods.contains("getAddMembersPage"));
    assertTrue(wizardMethods.contains("getPreviewPage"));
    assertTrue(wizardMethods.contains("createEdit"));

    Set<String> operationMethods = Arrays.stream(ImportTypeIteratingOperation.class.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertTrue(operationMethods.contains("hasNext"));
    assertTrue(operationMethods.contains("getNext"));
  }
}
