package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.AbstractConstructor;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.story.BipedPoseBuilder;
import org.lgna.story.PoseBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StoryApiConfigurationManagerBuildMethodTest {
  @Test
  public void buildMethodDetection_recognizesPoseBuilderBuildCalls() {
    AbstractConstructor ctor = JavaType.getInstance(BipedPoseBuilder.class).getDeclaredConstructors().get(0);
    MethodInvocation buildCall = new MethodInvocation(new InstanceCreation(ctor), JavaMethod.getInstance(PoseBuilder.class, "build"));
    MethodInvocation otherCall = new MethodInvocation(new org.lgna.project.ast.NullLiteral(), JavaMethod.getInstance(Object.class, "toString"));

    StoryApiConfigurationManager manager = StoryApiConfigurationManager.getInstance();
    assertTrue(manager.isBuildMethod(buildCall));
    assertEquals(JavaType.getInstance(BipedPoseBuilder.class), manager.getBuildMethodPoseBuilderType(buildCall));
    assertFalse(manager.isBuildMethod(otherCall));
  }
}
