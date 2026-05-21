package org.alice.stageide;

import org.alice.ide.typemanager.TypeManager;
import org.junit.Test;
import org.lgna.project.ast.JavaField;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SBiped;
import org.lgna.story.resources.biped.BlackCatResource;

import static org.junit.Assert.assertTrue;

public class JointMethodAugmentorAugmentBipedTest {
  @Test
  public void augment_addsGeneratedJointMethodsForConcreteJointResources() {
    NamedUserType type = TypeManager.getNamedUserTypeFromArgumentField(
        JavaType.getInstance(SBiped.class),
        JavaField.getInstance(BlackCatResource.class, "DEFAULT"));
    int before = type.methods.size();

    JointMethodAugmentor.augment(type);

    assertTrue(type.methods.size() > before);
    boolean foundGetter = false;
    for (org.lgna.project.ast.UserMethod method : type.methods) {
      if (method.getName().startsWith("get")) {
        foundGetter = true;
        break;
      }
    }
    assertTrue(foundGetter);
  }
}
