package org.lgna.story;

import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.UnitQuaternion;
import org.lgna.story.implementation.BipedImp;
import org.lgna.story.implementation.JointImp;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.implementation.QuadrupedImp;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.JointArrayId;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resources.QuadrupedResource;

final class JointedModelStubSupport {
  private JointedModelStubSupport() {
  }

  static final class StubBipedResource implements BipedResource {
    @Override
    public BipedImp createImplementation(SBiped abstraction) {
      return new BipedImp(abstraction, new StubFactory<>(this));
    }

    @Override
    public JointedModelImp.JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }

  static final class StubQuadrupedResource implements QuadrupedResource {
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

  private static final class StubVisualData<R extends JointedModelResource> implements JointedModelImp.VisualData<R> {
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

  private static final class StubFactory<R extends JointedModelResource> implements JointedModelImp.JointImplementationAndVisualDataFactory<R> {
    private final R resource;

    private StubFactory(R resource) {
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
}
