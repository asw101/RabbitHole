package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import static org.junit.Assert.assertSame;

public class JointMethodAugmentorNonJointedTest {
  @Test
  public void augment_returnsSameTypeForNonJointedModels() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("PlainType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    assertSame(type, JointMethodAugmentor.augment(type));
  }
}
