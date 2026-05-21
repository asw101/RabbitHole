package org.alice.ide;

import org.junit.Test;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SceneSetupManagerNoOpTest {
  @Test
  public void reorganizeTypeFieldsIfNecessary_leavesIndependentFieldsInPlace() throws Exception {
    NamedUserType type = SceneSetupManagerTestSupport.createType("IndependentType");
    UserField first = SceneSetupManagerTestSupport.addField(type, "first");
    UserField second = SceneSetupManagerTestSupport.addField(type, "second");

    String message = SceneSetupManagerTestSupport.reorganize(type);

    assertNull(message);
    assertEquals(first, type.fields.get(0));
    assertEquals(second, type.fields.get(1));
  }
}
