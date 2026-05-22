package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix4x4;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SkeletonWeightProcessorBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void processWeightedMeshesTraversesHierarchyAndInvokesControllerHooks() {
    Joint root = new Joint();
    root.jointID.setValue("root");
    root.localTransformation.setValue(AffineMatrix4x4.createTranslation(1.0, 0.0, 0.0));
    Joint child = new Joint();
    child.jointID.setValue("child");
    child.localTransformation.setValue(AffineMatrix4x4.createTranslation(0.0, 2.0, 0.0));
    root.addComponent(child);

    RecordingWeightedMeshControl controlA = new RecordingWeightedMeshControl();
    RecordingWeightedMeshControl controlB = new RecordingWeightedMeshControl();
    Map<Integer, WeightedMeshControl[]> controllers = new HashMap<>();
    controllers.put(1, new WeightedMeshControl[]{controlA, controlB});

    SkeletonWeightProcessor.processWeightedMeshes(root, new SkeletonVisual(), controllers);

    assertEquals(1, controlA.preProcessCount);
    assertEquals(1, controlA.postProcessCount);
    assertEquals(2, controlA.processedJointIds.size());
    assertEquals("root", controlA.processedJointIds.get(0));
    assertEquals("child", controlA.processedJointIds.get(1));
    assertEquals(1.0, ((AffineMatrix4x4) controlA.transforms.get(0)).translation().x(), 0.00001);
    assertEquals(2.0, ((AffineMatrix4x4) controlA.transforms.get(1)).translation().y(), 0.00001);
    assertEquals(2, controlB.processedJointIds.size());
  }

  @Test
  public void renderSkeletonDrawsAxesForEveryJoint() {
    Joint root = new Joint();
    root.jointID.setValue("root");
    Joint child = new Joint();
    child.jointID.setValue("child");
    child.localTransformation.setValue(AffineMatrix4x4.createTranslation(0.5, 0.0, 0.0));
    root.addComponent(child);

    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    RenderContext rc = new RenderContext();
    rc.setGL(gl);

    SkeletonWeightProcessor.renderSkeleton(rc, root);

    assertEquals(2, gl.calls("glPushMatrix").size());
    assertEquals(2, gl.calls("glPopMatrix").size());
    assertEquals(2, gl.calls("glBegin").size());
    assertEquals(16, gl.calls("glVertex3d").size());
    assertTrue(gl.wasCalledWith("glDepthFunc", com.jogamp.opengl.GL2.GL_ALWAYS));
  }

  private static final class RecordingWeightedMeshControl extends WeightedMeshControl {
    private int preProcessCount;
    private int postProcessCount;
    private final List<String> processedJointIds = new ArrayList<>();
    private final List<Matrix4x4> transforms = new ArrayList<>();

    @Override
    void preProcess() {
      this.preProcessCount++;
    }

    @Override
    void process(Joint joint, Matrix4x4 jointTransform) {
      this.processedJointIds.add(joint.jointID.getValue());
      this.transforms.add(jointTransform);
    }

    @Override
    void postProcess() {
      this.postProcessCount++;
    }
  }
}
