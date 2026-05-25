package org.alice.stageide.sceneeditor.interact;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class GlobalDragAdapterDeepBehaviorTest {
  @Test
  public void resolveUndoGroupOnlyDependsOnNullability() {
    assertSame(Application.PROJECT_GROUP, GlobalDragAdapterLogic.resolveUndoGroup(new UserField("ship", Object.class)));
    assertSame(Application.PROJECT_GROUP, GlobalDragAdapterLogic.resolveUndoGroup(new UserField("camera", Object.class)));
  }
}
