package edu.cmu.cs.dennisc.render.gl.imp.adapters;


import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Background;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class GlrScenePropertyBehaviorIntegrationTest {


@Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void propertyChanges_syncBackgroundAndGlobalBrightness() throws Exception {
    Scene scene = new Scene(); scene.background.setValue(new Background()); scene.globalBrightness.setValue(0.35f); GlrScene sceneAdapter = AdapterRenderTestSupport.sceneAdapter(scene); HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    sceneAdapter.setupAffectors(rc);
    assertNotNull(sceneAdapter.getBackgroundAdapter()); Field field = RenderContext.class.getDeclaredField("globalBrightness"); field.setAccessible(true); assertEquals(0.35f, field.getFloat(rc), 0.0001f);
  }
}
