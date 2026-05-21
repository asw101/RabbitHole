package org.alice.ide;

import org.junit.Test;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SceneSetupManagerMoveFieldTest {
  @Test
  public void reorganizeTypeFieldsIfNecessary_movesDependentFieldToTheEnd() throws Exception {
    NamedUserType type = SceneSetupManagerTestSupport.createType("OrderedType");
    UserField dependent = SceneSetupManagerTestSupport.addField(type, "dependent");
    UserField anchor = SceneSetupManagerTestSupport.addField(type, "anchor");
    SceneSetupManagerTestSupport.setInitializer(dependent, SceneSetupManagerTestSupport.access(anchor));

    String message = SceneSetupManagerTestSupport.reorganize(type);

    assertNull(message);
    assertEquals(anchor, type.fields.get(0));
    assertEquals(dependent, type.fields.get(1));
  }
}
