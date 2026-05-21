package org.alice.ide.ast;

import org.alice.ide.ast.type.merge.croquet.FieldsToolPalette;
import org.alice.ide.ast.type.merge.croquet.FunctionsToolPalette;
import org.alice.ide.ast.type.merge.croquet.MembersToolPalette;
import org.alice.ide.ast.type.merge.croquet.MethodsToolPalette;
import org.alice.ide.ast.type.merge.croquet.ProceduresToolPalette;
import org.junit.Test;
import org.lgna.croquet.Element;
import org.lgna.croquet.PlainStringValue;
import org.lgna.croquet.ToolPaletteCoreComposite;
import org.lgna.croquet.views.ScrollPane;
import org.lgna.project.ast.Member;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MembersToolPaletteStructureTest {

  @Test
  public void membersToolPalette_modifiers_checked_publicAbstract() {
    int modifiers = MembersToolPalette.class.getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertTrue(Modifier.isAbstract(modifiers));
  }

  @Test
  public void membersToolPalette_typeParameters_checked_membersViewAndMemberBounds() {
    TypeVariable<?>[] variables = MembersToolPalette.class.getTypeParameters();
    assertEquals(2, variables.length);
    assertEquals("V", variables[0].getName());
    assertTrue(variables[0].getBounds()[0].getTypeName().contains("MembersView"));
    assertEquals("M", variables[1].getName());
    assertEquals(Member.class.getName(), variables[1].getBounds()[0].getTypeName());
  }

  @Test
  public void membersToolPalette_superclass_checked_toolPaletteCoreComposite() {
    assertEquals(ToolPaletteCoreComposite.class, MembersToolPalette.class.getSuperclass());
  }

  @Test
  public void membersToolPalette_declaredFields_checked_expectedNamesPresent() {
    Set<String> names = Arrays.stream(MembersToolPalette.class.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());

    assertTrue(names.contains("uriForDescriptionPurposesOnly"));
    assertTrue(names.contains("unusedProjectMembers"));
    assertTrue(names.contains("importOnlys"));
    assertTrue(names.contains("differentSignatures"));
    assertTrue(names.contains("differentImplementations"));
    assertTrue(names.contains("identicals"));
    assertTrue(names.contains("projectOnlys"));
    assertTrue(names.contains("fromImportHeader"));
    assertTrue(names.contains("alreadyInProjectHeader"));
    assertTrue(names.contains("resultHeader"));
  }

  @Test
  public void membersToolPalette_keyFields_checked_expectedTypes() throws Exception {
    assertEquals(URI.class, MembersToolPalette.class.getDeclaredField("uriForDescriptionPurposesOnly").getType());
    assertEquals(List.class, MembersToolPalette.class.getDeclaredField("unusedProjectMembers").getType());
    assertEquals(PlainStringValue.class, MembersToolPalette.class.getDeclaredField("fromImportHeader").getType());
    assertEquals(PlainStringValue.class, MembersToolPalette.class.getDeclaredField("alreadyInProjectHeader").getType());
    assertEquals(PlainStringValue.class, MembersToolPalette.class.getDeclaredField("resultHeader").getType());
  }

  @Test
  public void membersToolPalette_constructor_checked_uuidUriListAccepted() throws Exception {
    Constructor<MembersToolPalette> constructor = MembersToolPalette.class.getDeclaredConstructor(UUID.class, URI.class, List.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void membersToolPalette_protectedMethods_checked_overrideSignatures() throws Exception {
    Method modify = MembersToolPalette.class.getDeclaredMethod("modifyLocalizedText", Element.class, String.class);
    assertEquals(String.class, modify.getReturnType());
    assertTrue(Modifier.isProtected(modify.getModifiers()));

    Method createScrollPane = MembersToolPalette.class.getDeclaredMethod("createScrollPaneIfDesired");
    assertEquals(ScrollPane.class, createScrollPane.getReturnType());
    assertTrue(Modifier.isProtected(createScrollPane.getModifiers()));
  }

  @Test
  public void membersToolPalette_mutatorMethods_checked_expectedParameters() throws Exception {
    assertEquals(void.class, MembersToolPalette.class.getDeclaredMethod("addImportOnlyMember", Member.class).getReturnType());
    assertEquals(void.class, MembersToolPalette.class.getDeclaredMethod("addDifferentSignatureMembers", Member.class, Member.class).getReturnType());
    assertEquals(void.class, MembersToolPalette.class.getDeclaredMethod("addDifferentImplementationMembers", Member.class, Member.class).getReturnType());
    assertEquals(void.class, MembersToolPalette.class.getDeclaredMethod("addIdenticalMembers", Member.class, Member.class).getReturnType());
    assertEquals(void.class, MembersToolPalette.class.getDeclaredMethod("reifyProjectOnly").getReturnType());
    assertEquals(void.class, MembersToolPalette.class.getDeclaredMethod("appendStatusPreRejectorCheck", StringBuilder.class).getReturnType());
  }

  @Test
  public void membersToolPalette_accessorMethods_checked_expectedReturnTypes() throws Exception {
    assertEquals(List.class, MembersToolPalette.class.getDeclaredMethod("getImportOnlys").getReturnType());
    assertEquals(List.class, MembersToolPalette.class.getDeclaredMethod("getDifferentSignatures").getReturnType());
    assertEquals(List.class, MembersToolPalette.class.getDeclaredMethod("getDifferentImplementations").getReturnType());
    assertEquals(List.class, MembersToolPalette.class.getDeclaredMethod("getIdenticals").getReturnType());
    assertEquals(List.class, MembersToolPalette.class.getDeclaredMethod("getProjectOnlys").getReturnType());
    assertEquals(PlainStringValue.class, MembersToolPalette.class.getDeclaredMethod("getFromImportHeader").getReturnType());
    assertEquals(PlainStringValue.class, MembersToolPalette.class.getDeclaredMethod("getAlreadyInProjectHeader").getReturnType());
    assertEquals(PlainStringValue.class, MembersToolPalette.class.getDeclaredMethod("getResultHeader").getReturnType());
    assertEquals(int.class, MembersToolPalette.class.getDeclaredMethod("getTotalCount").getReturnType());
  }

  @Test
  public void methodsToolPalette_structure_checked_abstractSubclassAndConstructor() throws Exception {
    int modifiers = MethodsToolPalette.class.getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertTrue(Modifier.isAbstract(modifiers));
    assertEquals(MembersToolPalette.class, MethodsToolPalette.class.getSuperclass());
    Constructor<MethodsToolPalette> constructor = MethodsToolPalette.class.getDeclaredConstructor(UUID.class, URI.class, List.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void functionsToolPalette_structure_checked_finalSubclassAndViewFactory() throws Exception {
    assertTrue(Modifier.isFinal(FunctionsToolPalette.class.getModifiers()));
    assertEquals(MethodsToolPalette.class, FunctionsToolPalette.class.getSuperclass());
    assertNotNull(FunctionsToolPalette.class.getDeclaredConstructor(URI.class, List.class));
    Method method = FunctionsToolPalette.class.getDeclaredMethod("createView");
    assertEquals("org.alice.ide.ast.type.merge.croquet.views.FunctionsView", method.getReturnType().getName());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void proceduresToolPalette_structure_checked_finalSubclassAndViewFactory() throws Exception {
    assertTrue(Modifier.isFinal(ProceduresToolPalette.class.getModifiers()));
    assertEquals(MethodsToolPalette.class, ProceduresToolPalette.class.getSuperclass());
    assertNotNull(ProceduresToolPalette.class.getDeclaredConstructor(URI.class, List.class));
    Method method = ProceduresToolPalette.class.getDeclaredMethod("createView");
    assertEquals("org.alice.ide.ast.type.merge.croquet.views.ProceduresView", method.getReturnType().getName());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void fieldsToolPalette_structure_checked_finalSubclassAndViewFactory() throws Exception {
    assertTrue(Modifier.isFinal(FieldsToolPalette.class.getModifiers()));
    assertEquals(MembersToolPalette.class, FieldsToolPalette.class.getSuperclass());
    assertNotNull(FieldsToolPalette.class.getDeclaredConstructor(URI.class, List.class));
    Method method = FieldsToolPalette.class.getDeclaredMethod("createView");
    assertEquals("org.alice.ide.ast.type.merge.croquet.views.FieldsView", method.getReturnType().getName());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }
}
