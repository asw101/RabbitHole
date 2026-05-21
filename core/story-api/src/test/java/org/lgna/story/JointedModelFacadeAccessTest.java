package org.lgna.story;

import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.UnitQuaternion;
import org.junit.Test;
import org.lgna.story.implementation.BipedImp;
import org.lgna.story.implementation.FlyerImp;
import org.lgna.story.implementation.JointImp;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.implementation.QuadrupedImp;
import org.lgna.story.implementation.SlithererImp;
import org.lgna.story.implementation.SwimmerImp;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.FlyerResource;
import org.lgna.story.resources.JointArrayId;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resources.QuadrupedResource;
import org.lgna.story.resources.SlithererResource;
import org.lgna.story.resources.SwimmerResource;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class JointedModelFacadeAccessTest {
  @Test
  public void bipedJointAccessorsReturnNonNullJoints() throws Exception {
    StubBipedResource resource = new StubBipedResource();
    SBiped biped = new SBiped(resource);
    assertSame(resource, biped.getJointedModelResource());
    invokeAllJointAccessors(biped);
  }

  @Test
  public void quadrupedJointAccessorsReturnNonNullJoints() throws Exception {
    StubQuadrupedResource resource = new StubQuadrupedResource();
    SQuadruped quadruped = new SQuadruped(resource);
    assertSame(resource, quadruped.getJointedModelResource());
    invokeAllJointAccessors(quadruped);
  }

  @Test
  public void flyerJointAccessorsReturnNonNullJoints() throws Exception {
    StubFlyerResource resource = new StubFlyerResource();
    SFlyer flyer = new SFlyer(resource);
    assertSame(resource, flyer.getJointedModelResource());
    invokeAllJointAccessors(flyer);
  }

  @Test
  public void slithererJointAccessorsReturnNonNullJoints() throws Exception {
    StubSlithererResource resource = new StubSlithererResource();
    SSlitherer slitherer = new SSlitherer(resource);
    assertSame(resource, slitherer.getJointedModelResource());
    invokeAllJointAccessors(slitherer);
  }

  @Test
  public void swimmerJointAccessorsReturnNonNullJoints() throws Exception {
    StubSwimmerResource resource = new StubSwimmerResource();
    SSwimmer swimmer = new SSwimmer(resource);
    assertSame(resource, swimmer.getJointedModelResource());
    invokeAllJointAccessors(swimmer);
  }

  private static void invokeAllJointAccessors(Object model) throws Exception {
    for (Method method : model.getClass().getDeclaredMethods()) {
      if ((method.getParameterCount() == 0) && method.getName().startsWith("get")) {
        if (method.getReturnType() == SJoint.class) {
          Object value = method.invoke(model);
          assertNotNull(method.getName(), value);
        } else if (method.getReturnType() == SJoint[].class) {
          SJoint[] value = (SJoint[]) method.invoke(model);
          assertNotNull(method.getName(), value);
          assertTrue(method.getName(), value.length > 0);
        }
      }
    }
  }

  private static final class StubVisualData<R extends JointedModelResource>
      implements JointedModelImp.VisualData<R> {
    private final Visual[] visuals = {new Visual()};
    private final SimpleAppearance[] appearances = {new SimpleAppearance()};
    private Composite parent;

    @Override
    public Visual[] getSgVisuals() {
      return visuals;
    }

    @Override
    public SkeletonVisual getSgVisualForExporting(R resource) {
      return null;
    }

    @Override
    public SimpleAppearance[] getSgAppearances() {
      return appearances;
    }

    @Override
    public void setSGParent(Composite parent) {
      this.parent = parent;
    }

    @Override
    public Composite getSGParent() {
      return parent;
    }
  }

  private static class StubFactory<R extends JointedModelResource>
      implements JointedModelImp.JointImplementationAndVisualDataFactory<R> {
    private final R resource;

    StubFactory(R resource) {
      this.resource = resource;
    }

    @Override
    public R getResource() {
      return resource;
    }

    @Override
    public JointImp createJointImplementation(JointedModelImp<?, R> impl, JointId jointId) {
      Joint joint = new Joint();
      joint.jointID.setValue(jointId.toString());
      return new org.lgna.story.implementation.alice.JointImplementation(impl, jointId, joint) {
        @Override
        protected void copyOnto(JointImp newJoint) {
          if (getJointedModelImplementation() != null) {
            super.copyOnto(newJoint);
          }
        }
      };
    }

    @Override
    public boolean hasJointImplementation(JointedModelImp<?, R> impl, JointId jointId) {
      return true;
    }

    @Override
    public JointId[] getJointArrayIds(JointedModelImp<?, R> impl, JointArrayId jointArrayId) {
      return new JointId[0];
    }

    @Override
    public JointedModelImp.VisualData<R> createVisualData() {
      return new StubVisualData<>();
    }

    @Override
    public UnitQuaternion getOriginalJointOrientation(JointId jointId) {
      return UnitQuaternion.IDENTITY;
    }

    @Override
    public AffineMatrix4x4 getOriginalJointTransformation(JointId jointId) {
      return AffineMatrix4x4.IDENTITY;
    }

    @Override
    public boolean isSims() {
      return false;
    }
  }

  private static final class StubBipedResource implements BipedResource {
    @Override
    public BipedImp createImplementation(SBiped abstraction) {
      return new BipedImp(abstraction, new StubFactory<>(this));
    }

    @Override
    public JointedModelImp.JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }

  private static final class StubQuadrupedResource implements QuadrupedResource {
    @Override
    public JointId[] getTailArray() {
      return DEFAULT_TAIL;
    }

    @Override
    public QuadrupedImp createImplementation(SQuadruped abstraction) {
      return new QuadrupedImp(abstraction, new StubFactory<>(this));
    }

    @Override
    public JointedModelImp.JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }

  private static final class StubFlyerResource implements FlyerResource {
    @Override
    public JointId[] getTailArray() {
      return DEFAULT_TAIL;
    }

    @Override
    public JointId[] getNeckArray() {
      return DEFAULT_NECK;
    }

    @Override
    public JointedModelPose getSpreadWingsPose() {
      return null;
    }

    @Override
    public JointedModelPose getFoldWingsPose() {
      return null;
    }

    @Override
    public FlyerImp createImplementation(SFlyer abstraction) {
      return new FlyerImp(abstraction, new StubFactory<>(this));
    }

    @Override
    public JointedModelImp.JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }

  private static final class StubSlithererResource implements SlithererResource {
    @Override
    public JointId[] getTailArray() {
      return DEFAULT_TAIL;
    }

    @Override
    public SlithererImp createImplementation(SSlitherer abstraction) {
      return new SlithererImp(abstraction, new StubFactory<>(this));
    }

    @Override
    public JointedModelImp.JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }

  private static final class StubSwimmerResource implements SwimmerResource {
    @Override
    public SwimmerImp createImplementation(SSwimmer abstraction) {
      return new SwimmerImp(abstraction, new StubFactory<>(this));
    }

    @Override
    public JointedModelImp.JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }
}
