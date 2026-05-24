package org.alice.ide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractSceneEditorLogicTest {
  @Test
  public void indexOfFieldFindsMatchingIdentity() {
    UserField first = new UserField("first", Object.class);
    UserField second = new UserField("second", Object.class);
    List<UserField> fields = new ArrayList<>();
    fields.add(first);
    fields.add(second);

    assertEquals(1, AbstractSceneEditorLogic.indexOfField(fields, second));
    assertEquals(-1, AbstractSceneEditorLogic.indexOfField(fields, new UserField("second", Object.class)));
  }

  @Test
  public void getActiveSceneTypeReturnsNamedUserTypeOnly() {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(Object.class));

    assertSame(sceneType, AbstractSceneEditorLogic.getActiveSceneType(new UserField("scene", sceneType)));
    assertNull(AbstractSceneEditorLogic.getActiveSceneType(new UserField("value", Object.class)));
  }
}
