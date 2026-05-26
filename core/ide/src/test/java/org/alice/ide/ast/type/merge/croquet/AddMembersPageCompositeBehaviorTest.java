package org.alice.ide.ast.type.merge.croquet;

import org.alice.ide.ast.type.croquet.ImportTypeWizard;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ReturnStatement;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddMembersPageCompositeBehaviorTest {
  private static final URI MERGE_URI = URI.create("file:/merge");

  @Test
  public void constructorClassifiesMembersIntoExpectedCompositeBuckets() {
    AddMembersPage page = createWizard().getAddMembersPage();

    assertPaletteCounts(page.getAddProceduresComposite(), 1, 1, 1, 1, 1, 5);
    assertPaletteCounts(page.getAddFunctionsComposite(), 1, 0, 1, 0, 1, 3);
    assertPaletteCounts(page.getAddFieldsComposite(), 1, 0, 1, 0, 1, 3);
    assertTrue(page.isContainingDifferentImplementations());
  }

  @Test
  public void previewHubsExpandWhenIncludingAllTurnsOn() {
    ImportTypeWizard wizard = createWizard();
    AddMembersPage page = wizard.getAddMembersPage();

    assertEquals(
        List.of("importOnlyProc", "conflictProc", "conflictProc", "identicalProc", "projectOnlyProc"),
        namesOf(page.getPreviewProcedureHubs()));

    wizard.getPreviewPage().getIsIncludingAllState().setValueTransactionlessly(true);

    assertEquals(
        List.of("importOnlyProc", "conflictProc", "conflictProc", "sameSignatureProc", "sameSignatureProc", "identicalProc", "projectOnlyProc"),
        namesOf(page.getPreviewProcedureHubs()));
  }

  @Test
  public void acceptAndRejectAllDifferentImplementationsFlipEveryCompositeSelection() throws Exception {
    AddMembersPage page = createWizard().getAddMembersPage();

    DifferentImplementation<?> procedure = page.getAddProceduresComposite().getDifferentImplementations().get(0);
    DifferentImplementation<?> function = page.getAddFunctionsComposite().getDifferentImplementations().get(0);
    DifferentImplementation<?> field = page.getAddFieldsComposite().getDifferentImplementations().get(0);

    assertDesiredStates(procedure, false, false);
    assertDesiredStates(function, false, false);
    assertDesiredStates(field, false, false);

    invokePrivate(page, "acceptAllDifferentImplementations");
    assertDesiredStates(procedure, true, false);
    assertDesiredStates(function, true, false);
    assertDesiredStates(field, true, false);

    invokePrivate(page, "rejectAllDifferentImplementations");
    assertDesiredStates(procedure, false, true);
    assertDesiredStates(function, false, true);
    assertDesiredStates(field, false, true);
  }

  private static void assertPaletteCounts(MembersToolPalette<?, ?> palette, int importOnlys, int differentSignatures,
                                          int differentImplementations, int identicals, int projectOnlys, int total) {
    assertEquals(importOnlys, palette.getImportOnlys().size());
    assertEquals(differentSignatures, palette.getDifferentSignatures().size());
    assertEquals(differentImplementations, palette.getDifferentImplementations().size());
    assertEquals(identicals, palette.getIdenticals().size());
    assertEquals(projectOnlys, palette.getProjectOnlys().size());
    assertEquals(total, palette.getTotalCount());
  }

  private static void assertDesiredStates(DifferentImplementation<?> differentImplementation, boolean importDesired,
                                          boolean projectDesired) {
    assertEquals(importDesired, differentImplementation.getImportHub().getIsDesiredState().getValue());
    assertEquals(projectDesired, differentImplementation.getProjectHub().getIsDesiredState().getValue());
  }

  private static List<String> namesOf(List<? extends MemberHub<?>> hubs) {
    return hubs.stream().map(hub -> hub.getMember().getName()).collect(Collectors.toList());
  }

  private static void invokePrivate(AddMembersPage page, String methodName) throws Exception {
    Method method = AddMembersPage.class.getDeclaredMethod(methodName);
    method.setAccessible(true);
    method.invoke(page);
  }

  private static ImportTypeWizard createWizard() {
    NamedUserType importedType = new NamedUserType();
    importedType.name.setValue("ImportedScene");
    NamedUserType projectType = new NamedUserType();
    projectType.name.setValue("ProjectScene");

    importedType.methods.add(createProcedure("importOnlyProc", "import only"));
    importedType.methods.add(createProcedure("sameSignatureProc", "import version"));
    importedType.methods.add(createProcedureWithParameter("conflictProc", String.class));
    importedType.methods.add(createProcedure("identicalProc", "same body"));
    importedType.methods.add(createFunction("importOnlyFunction", 5));
    importedType.methods.add(createFunction("score", 1));

    projectType.methods.add(createProcedure("sameSignatureProc", "project version"));
    projectType.methods.add(createProcedureWithParameter("conflictProc", Integer.class));
    projectType.methods.add(createProcedure("identicalProc", "same body"));
    projectType.methods.add(createProcedure("projectOnlyProc", "project only"));
    projectType.methods.add(createFunction("score", 2));
    projectType.methods.add(createFunction("projectOnlyFunction", 9));

    importedType.fields.add(new UserField("speed", Double.class, new DoubleLiteral(1.0)));
    importedType.fields.add(new UserField("importOnlyField", String.class, new StringLiteral("import")));

    projectType.fields.add(new UserField("speed", Double.class, new DoubleLiteral(2.0)));
    projectType.fields.add(new UserField("projectOnlyField", String.class, new StringLiteral("project")));

    return new ImportTypeWizard(MERGE_URI, importedType, Collections.emptySet(), importedType, projectType);
  }

  private static UserMethod createProcedure(String name, String note) {
    return new UserMethod(name, JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement(new Comment(note)));
  }

  private static UserMethod createProcedureWithParameter(String name, Class<?> parameterType) {
    return new UserMethod(
        name,
        JavaType.VOID_TYPE,
        new UserParameter[] {new UserParameter("value", parameterType)},
        new BlockStatement(new Comment(name + parameterType.getSimpleName())));
  }

  private static UserMethod createFunction(String name, int value) {
    return new UserMethod(
        name,
        JavaType.INTEGER_OBJECT_TYPE,
        new UserParameter[0],
        new BlockStatement(new ReturnStatement(JavaType.INTEGER_OBJECT_TYPE, new org.lgna.project.ast.IntegerLiteral(value))));
  }
}
