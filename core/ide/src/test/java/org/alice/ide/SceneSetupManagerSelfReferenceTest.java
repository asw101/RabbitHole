package org.alice.ide;

import org.junit.Test;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SceneSetupManagerSelfReferenceTest {
  @Test
  public void reorganizeTypeFieldsIfNecessary_reportsSelfReference() throws Exception {
    NamedUserType type = SceneSetupManagerTestSupport.createType("SelfRefType");
    UserField self = SceneSetupManagerTestSupport.addField(type, "self");
    SceneSetupManagerTestSupport.setInitializer(self, SceneSetupManagerTestSupport.access(self));

    String message = SceneSetupManagerTestSupport.reorganize(type);

    assertNotNull(message);
    assertTrue(message.contains("referencing <strong>itself</strong>"));
    assertTrue(message.contains("self"));
  }
}
