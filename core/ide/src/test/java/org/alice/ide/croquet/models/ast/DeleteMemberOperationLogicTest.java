package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import org.lgna.project.ast.ManagementLevel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeleteMemberOperationLogicTest {
  @Test
  public void hasReferences_tracksReferenceCounts() {
    assertFalse(DeleteMemberOperationLogic.hasReferences(0));
    assertTrue(DeleteMemberOperationLogic.hasReferences(1));
  }

  @Test
  public void createDeleteMethodBlockedMessage_usesSingularProcedureWording() {
    assertEquals(
        "Unable to delete procedure named \"step\" because it has an invocation reference to it.\nYou must remove this reference if you want to delete \"step\" .",
        DeleteMemberOperationLogic.createDeleteMethodBlockedMessage("step", true, 1));
  }

  @Test
  public void createDeleteMethodBlockedMessage_usesPluralFunctionWording() {
    assertEquals(
        "Unable to delete function named \"value\" because it has 3 invocation references to it.\nYou must remove these references if you want to delete \"value\" .",
        DeleteMemberOperationLogic.createDeleteMethodBlockedMessage("value", false, 3));
  }

  @Test
  public void shouldUseManagedFieldPath_onlyForManagedMembers() {
    assertTrue(DeleteMemberOperationLogic.shouldUseManagedFieldPath(ManagementLevel.MANAGED));
    assertFalse(DeleteMemberOperationLogic.shouldUseManagedFieldPath(ManagementLevel.NONE));
  }
}
