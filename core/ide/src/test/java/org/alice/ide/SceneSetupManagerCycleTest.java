package org.alice.ide;

import org.junit.Test;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SceneSetupManagerCycleTest {
  @Test
  public void reorganizeTypeFieldsIfNecessary_reportsCycleWhenFieldAlreadyMoved() throws Exception {
    NamedUserType type = SceneSetupManagerTestSupport.createType("CycleType");
    UserField first = SceneSetupManagerTestSupport.addField(type, "first");
    UserField second = SceneSetupManagerTestSupport.addField(type, "second");
    SceneSetupManagerTestSupport.setInitializer(first, SceneSetupManagerTestSupport.access(second));

    String message = SceneSetupManagerTestSupport.reorganize(type, 0, Collections.singleton(first));

    assertNotNull(message);
    assertTrue(message.contains("Possible cycle detected"));
    assertTrue(message.contains("already attempted to move it once"));
  }
}
