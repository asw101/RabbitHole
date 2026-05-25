package org.alice.ide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.UserField;

import java.util.List;

import static org.junit.Assert.*;

public class AbstractSceneEditorDeepBehaviorTest {
  @Test
  public void indexOfFieldReturnsMinusOneForNullTargets() {
    assertEquals(-1, AbstractSceneEditorLogic.indexOfField(List.of(new UserField("scene", Object.class)), null));
  }

  @Test
  public void getActiveSceneTypeReturnsNullForNullField() {
    assertNull(AbstractSceneEditorLogic.getActiveSceneType(null));
  }
}
