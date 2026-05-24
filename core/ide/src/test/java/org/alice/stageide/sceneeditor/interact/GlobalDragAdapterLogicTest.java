package org.alice.stageide.sceneeditor.interact;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class GlobalDragAdapterLogicTest {
  @Test
  public void resolveUndoGroupUsesDocumentGroupWhenFieldMissing() {
    assertSame(Application.DOCUMENT_UI_GROUP, GlobalDragAdapterLogic.resolveUndoGroup(null));
  }

  @Test
  public void resolveUndoGroupUsesProjectGroupForFields() {
    assertSame(Application.PROJECT_GROUP, GlobalDragAdapterLogic.resolveUndoGroup(new UserField("ship", Object.class)));
  }
}
