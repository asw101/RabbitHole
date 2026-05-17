package org.lgna.story.implementation;

import org.junit.Before;
import org.junit.Test;
import org.lgna.story.SScene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests for the {@link AsSeenBy} enum — the three reference frame constants
 * (SCENE, PARENT, SELF) and their behavior.
 */
public class AsSeenByTest {

  private StandInImp subject;
  private StandInImp vehicle;
  private SceneImp sceneImp;

  @Before
  public void setUp() {
    TestScene scene = new TestScene();
    sceneImp = scene.getImplementation();
    vehicle = new StandInImp();
    vehicle.setVehicle(sceneImp);
    subject = new StandInImp();
    subject.setVehicle(vehicle);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Enum basics
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void threeValuesExist() {
    AsSeenBy[] values = AsSeenBy.values();
    assertEquals(3, values.length);
  }

  @Test
  public void valueOfScene() {
    assertEquals(AsSeenBy.SCENE, AsSeenBy.valueOf("SCENE"));
  }

  @Test
  public void valueOfParent() {
    assertEquals(AsSeenBy.PARENT, AsSeenBy.valueOf("PARENT"));
  }

  @Test
  public void valueOfSelf() {
    assertEquals(AsSeenBy.SELF, AsSeenBy.valueOf("SELF"));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  getSgReferenceFrame
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void sceneSgReferenceFrameIsNotNull() {
    assertNotNull(AsSeenBy.SCENE.getSgReferenceFrame());
  }

  @Test
  public void parentSgReferenceFrameIsNotNull() {
    assertNotNull(AsSeenBy.PARENT.getSgReferenceFrame());
  }

  @Test
  public void selfSgReferenceFrameIsNotNull() {
    assertNotNull(AsSeenBy.SELF.getSgReferenceFrame());
  }

  @Test
  public void sceneSgReferenceFrameMatchesScenegraphConstant() {
    assertSame(edu.cmu.cs.dennisc.scenegraph.AsSeenBy.SCENE, AsSeenBy.SCENE.getSgReferenceFrame());
  }

  @Test
  public void parentSgReferenceFrameMatchesScenegraphConstant() {
    assertSame(edu.cmu.cs.dennisc.scenegraph.AsSeenBy.PARENT, AsSeenBy.PARENT.getSgReferenceFrame());
  }

  @Test
  public void selfSgReferenceFrameMatchesScenegraphConstant() {
    assertSame(edu.cmu.cs.dennisc.scenegraph.AsSeenBy.SELF, AsSeenBy.SELF.getSgReferenceFrame());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  getActualEntityImplementation
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void selfReturnsSubjectItself() {
    assertSame(subject, AsSeenBy.SELF.getActualEntityImplementation(subject));
  }

  @Test
  public void parentReturnsVehicle() {
    assertSame(vehicle, AsSeenBy.PARENT.getActualEntityImplementation(subject));
  }

  @Test
  public void sceneReturnsSceneImp() {
    assertSame(sceneImp, AsSeenBy.SCENE.getActualEntityImplementation(subject));
  }

  @Test
  public void parentOfVehicleReturnsSceneImp() {
    assertSame(sceneImp, AsSeenBy.PARENT.getActualEntityImplementation(vehicle));
  }

  @Test
  public void parentOfOrphanReturnsNull() {
    StandInImp orphan = new StandInImp();
    assertNull(AsSeenBy.PARENT.getActualEntityImplementation(orphan));
  }

  @Test
  public void sceneOfOrphanReturnsNull() {
    StandInImp orphan = new StandInImp();
    assertNull(AsSeenBy.SCENE.getActualEntityImplementation(orphan));
  }

  @Test
  public void sceneOfSceneImpReturnsSelf() {
    assertSame(sceneImp, AsSeenBy.SCENE.getActualEntityImplementation(sceneImp));
  }

  // ── Test double ──

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}
