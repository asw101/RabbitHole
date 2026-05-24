package org.alice.ide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class AbstractSceneEditorLogicBehaviorTest {
  @Test
  public void indexOfFieldMatchesByIdentityOnly() {
    UserField first = new UserField("first", Object.class);
    UserField second = new UserField("second", Object.class);

    assertEquals(1, AbstractSceneEditorLogic.indexOfField(List.of(first, second), second));
    assertEquals(-1, AbstractSceneEditorLogic.indexOfField(List.of(first, second), new UserField("second", Object.class)));
  }

  @Test
  public void getActiveSceneTypeReturnsNamedUserTypeAndRejectsOtherTypes() {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(Object.class));

    assertSame(sceneType, AbstractSceneEditorLogic.getActiveSceneType(new UserField("scene", sceneType)));
    assertNull(AbstractSceneEditorLogic.getActiveSceneType(new UserField("label", String.class)));
    assertNull(AbstractSceneEditorLogic.getActiveSceneType(null));
  }
}
