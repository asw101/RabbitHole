package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.PlanarReflector;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GlrSceneDescendantManagementIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


@Before
  public void setUp() {
    AdapterRenderTestSupport.resetFactory();
  }

  @Test
  public void initializeTracksVisualAndReflectorDescendants() throws Exception {
    Visual visual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(1.0f));
    PlanarReflector reflector = new PlanarReflector();
    Scene scene = AdapterRenderTestSupport.sceneWith(visual, reflector);

    GlrScene sceneAdapter = AdapterRenderTestSupport.sceneAdapter(scene);

    assertEquals(2, childCount(sceneAdapter));
    assertEquals(2, privateListSize(sceneAdapter, "glrVisualDescendants"));
    assertEquals(1, privateListSize(sceneAdapter, "glrPlanarReflectorDescendants"));
    assertEquals(0, privateListSize(sceneAdapter, "glrGhostDescendants"));
  }

  @Test
  public void epicHackRemoveDescendantUpdatesReflectorLists() throws Exception {
    PlanarReflector reflector = new PlanarReflector();
    GlrScene sceneAdapter = AdapterRenderTestSupport.sceneAdapter(AdapterRenderTestSupport.sceneWith(reflector));
    GlrPlanarReflector reflectorAdapter = (GlrPlanarReflector) AdapterFactory.getAdapterFor(reflector);

    sceneAdapter.EPIC_HACK_FOR_THUMBNAIL_MAKER_removeDescendant(reflectorAdapter);

    assertEquals(0, privateListSize(sceneAdapter, "glrVisualDescendants"));
    assertEquals(0, privateListSize(sceneAdapter, "glrPlanarReflectorDescendants"));
  }

  private static int childCount(GlrScene sceneAdapter) {
    int count = 0;
    for (GlrComponent<?> ignored : sceneAdapter.accessChildren()) {
      count++;
    }
    return count;
  }

  private static int privateListSize(GlrScene sceneAdapter, String fieldName) throws Exception {
    Field field = GlrScene.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return ((List<?>) field.get(sceneAdapter)).size();
  }
}
