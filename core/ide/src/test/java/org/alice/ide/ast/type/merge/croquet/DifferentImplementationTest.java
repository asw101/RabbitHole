package org.alice.ide.ast.type.merge.croquet;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.net.URI;

import static org.junit.Assert.*;

public class DifferentImplementationTest {
  private static UserMethod createProcedure(String name) {
    return new UserMethod(name, JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void constructorStoresImportAndProjectMembers() {
    UserMethod importMethod = createProcedure("step");
    UserMethod projectMethod = createProcedure("step");

    DifferentImplementation<UserMethod> differentImplementation = new DifferentImplementation<>(URI.create("file:/merge"), importMethod, projectMethod);

    assertSame(importMethod, differentImplementation.getImportHub().getMember());
    assertSame(projectMethod, differentImplementation.getProjectHub().getMember());
  }

  @Test
  public void constructorCreatesCardOwners() {
    DifferentImplementation<UserMethod> differentImplementation = new DifferentImplementation<>(
        URI.create("file:/merge"),
        createProcedure("step"),
        createProcedure("step"));

    assertNotNull(differentImplementation.getImportCardOwner());
    assertNotNull(differentImplementation.getProjectCardOwner());
  }

  @Test
  public void methodMembersCreateMethodHelpComposite() {
    DifferentImplementation<UserMethod> differentImplementation = new DifferentImplementation<>(
        URI.create("file:/merge"),
        createProcedure("step"),
        createProcedure("step"));

    assertNotNull(differentImplementation.getHelpComposite());
  }

  @Test
  public void fieldMembersCreateFieldHelpComposite() {
    DifferentImplementation<UserField> differentImplementation = new DifferentImplementation<>(
        URI.create("file:/merge"),
        new UserField("speed", String.class),
        new UserField("speed", String.class));

    assertNotNull(differentImplementation.getHelpComposite());
  }

  @Test
  public void appendStatusPreRejectorCheckRequiresActionWhenNeitherVersionIsSelected() {
    DifferentImplementation<UserMethod> differentImplementation = new DifferentImplementation<>(
        URI.create("file:/merge"),
        createProcedure("step"),
        createProcedure("step"));
    StringBuilder sb = new StringBuilder();

    differentImplementation.appendStatusPreRejectorCheck(sb);

    assertTrue(sb.toString().contains("must take action"));
  }

  @Test
  public void selectingBothVersionsWithSameNameRequiresRename() {
    DifferentImplementation<UserMethod> differentImplementation = new DifferentImplementation<>(
        URI.create("file:/merge"),
        createProcedure("step"),
        createProcedure("step"));
    differentImplementation.getImportHub().getIsDesiredState().setValueTransactionlessly(true);
    differentImplementation.getProjectHub().getIsDesiredState().setValueTransactionlessly(true);
    StringBuilder sb = new StringBuilder();

    assertEquals(ActionStatus.RENAME_REQUIRED, differentImplementation.getImportHub().getActionStatus());
    assertEquals(ActionStatus.RENAME_REQUIRED, differentImplementation.getProjectHub().getActionStatus());

    differentImplementation.appendStatusPreRejectorCheck(sb);

    assertTrue(sb.toString().contains("must not have same name"));
  }
}
