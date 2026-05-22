package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.PickParameters;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;

import static org.junit.Assert.*;

public class GlrScenePickIntegrationTest {
  @Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void pickScene_traversesHierarchyAndClearsNameMap() {
    Visual visual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(1.0f)); Transformable transformable = AdapterRenderTestSupport.transformableWith(visual, 0.0, 0.0, 0.0); SymmetricPerspectiveCamera camera = AdapterRenderTestSupport.perspectiveCamera(); Scene scene = AdapterRenderTestSupport.sceneWith(camera, transformable); HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); PickContext pc = AdapterRenderTestSupport.pickContext(gl); PickParameters pickParameters = new PickParameters(null, camera, new Point(10, 10), true, null);
    AdapterRenderTestSupport.sceneAdapter(scene).pick(pc, pickParameters);
    assertTrue(gl.calls("glPushName").size() > 0); assertTrue(gl.calls("glPopName").size() > 0);
  }
}
