package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;



import com.jogamp.opengl.GLAutoDrawable;
import edu.cmu.cs.dennisc.render.PickSubElementPolicy;
import edu.cmu.cs.dennisc.render.VisualInclusionCriterion;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AngleInDegrees;
import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class PickDisplayTaskBehaviorTest {

  @org.junit.Before
  public void skipIfNoJogl() {
    try { Class.forName("com.jogamp.opengl.GLProfile").getMethod("getDefault").invoke(null); }
    catch (Throwable t) { Assume.assumeTrue("JOGL native init failed: " + t.getMessage(), false); }
  }



@Before
  public void setUp() {
    edu.cmu.cs.dennisc.render.gl.imp.adapters.AdapterFactory.forgetAllElements();
    GlImpTestSupport.resetConformanceTestResults();
  }

  @Test
  public void handleDisplay_setsUpSelectionModeAndFiresDone() {
    Scene scene = new Scene();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.verticalViewingAngle.setValue(new AngleInDegrees(60));
    camera.horizontalViewingAngle.setValue(new AngleInDegrees(60));
    camera.nearClippingPlaneDistance.setValue(1.0);
    camera.farClippingPlaneDistance.setValue(100.0);
    scene.addComponent(camera);
    Visual visual = new Visual();
    visual.geometries.setValue(new edu.cmu.cs.dennisc.scenegraph.Geometry[]{new Box()});
    visual.frontFacingAppearance.setValue(new SimpleAppearance());
    visual.backFacingAppearance.setValue(visual.frontFacingAppearance.getValue());
    Transformable transformable = new Transformable();
    transformable.addComponent(visual);
    scene.addComponent(transformable);
    RenderTargetImp imp = new RenderTargetImp(GlImpTestSupport.renderTargetProxy(new Dimension(80, 60), Map.of(camera, new Rectangle(0, 0, 80, 60)), new boolean[]{true}, new AtomicInteger()));
    imp.addSgCamera(camera, null);
    AtomicBoolean fired = new AtomicBoolean(false);
    PickDisplayTask task = new PickDisplayTask(new Point(20, 20), PickSubElementPolicy.REQUIRED, sgVisual -> true) {
      @Override protected void fireDone(PickParameters pickParameters) { fired.set(pickParameters != null); }
    };
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    GLAutoDrawable drawable = GlImpTestSupport.drawableProxy(gl, new AtomicInteger(), new AtomicInteger(), new AtomicInteger(), new AtomicBoolean(true), 80, 60);

    IsFrameBufferIntact intact = task.handleDisplay(imp, drawable, gl);
    GlImpTestSupport.resetConformanceTestResults();

    assertEquals(IsFrameBufferIntact.TRUE, intact);
    assertTrue(fired.get());
    assertTrue(gl.calls("glSelectBuffer").size() > 0);
    assertTrue(gl.calls("glViewport").size() > 0);
  }
}
