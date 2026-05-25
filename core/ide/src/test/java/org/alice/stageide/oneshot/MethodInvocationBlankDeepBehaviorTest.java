package org.alice.stageide.oneshot;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import org.alice.ide.instancefactory.InstanceFactory;
import org.junit.Test;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.Pose;
import org.lgna.story.SJointedModel;
import org.lgna.story.StrikePose;

import static org.junit.Assert.*;

public class MethodInvocationBlankDeepBehaviorTest {
  private static final class StubInstanceFactory implements InstanceFactory {
    private final AbstractType<?, ?, ?> valueType;

    private StubInstanceFactory(AbstractType<?, ?, ?> valueType) {
      this.valueType = valueType;
    }

    @Override
    public boolean isValid() {
      return true;
    }

    @Override
    public AbstractType<?, ?, ?> getValueType() {
      return this.valueType;
    }

    @Override
    public Expression createTransientExpression() {
      return null;
    }

    @Override
    public Expression createExpression() {
      return null;
    }

    @Override
    public String getRepr() {
      return "stub";
    }

    @Override
    public IconFactory getIconFactory() {
      return null;
    }

    @Override
    public InstanceProperty<?>[] getMutablePropertiesOfInterest() {
      return new InstanceProperty<?>[0];
    }
  }

  @Test
  public void getInstanceCachesByFactoryIdentity() {
    StubInstanceFactory first = new StubInstanceFactory(JavaType.getInstance(Object.class));

    assertSame(MethodInvocationBlank.getInstance(first), MethodInvocationBlank.getInstance(first));
    assertNotSame(MethodInvocationBlank.getInstance(first), MethodInvocationBlank.getInstance(new StubInstanceFactory(JavaType.getInstance(Object.class))));
  }

  @Test
  public void poseExtractionRecognizesGeneratedStrikePoseMethodsAndSpecialFillIns() {
    JavaMethod strikePose = JavaType.getInstance(SJointedModel.class).getDeclaredMethod("strikePose", Pose.class, StrikePose.Detail[].class);
    UserMethod poseMethod = new UserMethod("pose", void.class, new UserParameter[0], new BlockStatement());
    poseMethod.managementLevel.setValue(ManagementLevel.GENERATED);
    MethodInvocation invocation = new MethodInvocation(new ThisExpression(), strikePose);
    poseMethod.body.getValue().statements.add(new ExpressionStatement(invocation));

    assertSame(invocation, MethodInvocationBlankLogic.getPoseInvocation(poseMethod));
    assertEquals(MethodInvocationBlankLogic.FillInKind.JAVA_DEFINED_STRIKE_POSE,
        MethodInvocationBlankLogic.resolveFillInKind(org.alice.stageide.ast.sort.OneShotSorter.FOLD_WINGS_METHOD, false));
  }
}
