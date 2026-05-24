package org.alice.stageide.sceneeditor.interact;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.assertSame;

public class GlobalDragAdapterLogicBehaviorTest {
  @Test
  public void resolveUndoGroupUsesDocumentGroupWhenNothingIsManipulated() {
    assertSame(Application.DOCUMENT_UI_GROUP, GlobalDragAdapterLogic.resolveUndoGroup(null));
  }

  @Test
  public void resolveUndoGroupUsesProjectGroupForConcreteFields() {
    assertSame(Application.PROJECT_GROUP, GlobalDragAdapterLogic.resolveUndoGroup(new UserField("ship", Object.class)));
  }
}
