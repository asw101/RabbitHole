package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.StandIn;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Before;
import org.junit.Test;
import org.lgna.common.LgnaIllegalArgumentException;
import org.lgna.story.SScene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Behavior tests for EntityImp via {@link StandInImp}. Focuses on the core entity
 * contract: instance registry, naming, vehicle hierarchy, scene traversal,
 * transform queries, and standIn creation.
 *
 * <p>All tests are headless-safe — no AWT or rendering.
 */
public class EntityImpBehaviorTest {

  private StandInImp subject;
  private StandInImp vehicle;

  @Before
  public void setUp() {
    vehicle = new StandInImp();
    subject = new StandInImp();
    subject.setVehicle(vehicle);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Instance registry (putInstance / getInstance)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getInstanceReturnsSelfForSgComposite() {
    EntityImp imp = EntityImp.getInstance(subject.getSgComposite());
    assertSame("Instance registry should map sgComposite back to the StandInImp",
        subject, imp);
  }

  @Test
  public void getInstanceReturnsNullForNullElement() {
    assertNull("getInstance(null) should return null",
        EntityImp.getInstance(null));
  }

  @Test
  public void freshStandInRegistersItself() {
    StandInImp fresh = new StandInImp();
    EntityImp imp = EntityImp.getInstance(fresh.getSgComposite());
    assertSame("Newly created StandInImp should be in the instance registry", fresh, imp);
  }

  @Test
  public void getAbstractionFromSgElementReturnsNullForStandIn() {
    assertNull("StandInImp.getAbstraction() returns null by design",
        EntityImp.getAbstractionFromSgElement(subject.getSgComposite()));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Naming
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void defaultNameIsNull() {
    StandInImp fresh = new StandInImp();
    assertNull("Default name should be null", fresh.getName());
  }

  @Test
  public void setNameRoundTrips() {
    subject.setName("testEntity");
    assertEquals("testEntity", subject.getName());
  }

  @Test
  public void setNameUpdatesSgCompositeName() {
    subject.setName("myEntity");
    String sgName = subject.getSgComposite().getName();
    assertNotNull("sgComposite name should be set", sgName);
    assertTrue("sgComposite name should contain entity name",
        sgName.contains("myEntity"));
  }

  @Test
  public void setNameCanBeChangedMultipleTimes() {
    subject.setName("first");
    subject.setName("second");
    assertEquals("second", subject.getName());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Vehicle hierarchy
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void setVehicleEstablishesParent() {
    assertEquals("getVehicle() should return the set vehicle", vehicle, subject.getVehicle());
  }

  @Test
  public void unparentedEntityHasNullVehicle() {
    StandInImp orphan = new StandInImp();
    assertNull("Unparented entity should have null vehicle", orphan.getVehicle());
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void setVehicleToSelfThrows() {
    subject.setVehicle(subject);
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void setVehicleToCyclicDescendantThrows() {
    // vehicle -> subject -> child; trying to make vehicle's vehicle = child causes cycle
    StandInImp child = new StandInImp();
    child.setVehicle(subject);
    vehicle.setVehicle(child);
  }

  @Test
  public void setVehicleToNullUnparents() {
    subject.setVehicle(null);
    assertNull("After setVehicle(null), vehicle should be null", subject.getVehicle());
  }

  @Test
  public void isDescendantOfReturnsTrueForDirectParent() {
    assertTrue("subject should be descendant of its direct vehicle",
        subject.isDescendantOf(vehicle));
  }

  @Test
  public void isDescendantOfReturnsTrueForGrandparent() {
    StandInImp grandparent = new StandInImp();
    vehicle.setVehicle(grandparent);
    assertTrue("subject should be descendant of grandparent", subject.isDescendantOf(grandparent));
  }

  @Test
  public void isDescendantOfReturnsFalseForUnrelatedEntity() {
    StandInImp unrelated = new StandInImp();
    assertFalse("subject should not be descendant of unrelated entity",
        subject.isDescendantOf(unrelated));
  }

  @Test
  public void isDescendantOfReturnsFalseForChild() {
    StandInImp child = new StandInImp();
    child.setVehicle(subject);
    assertFalse("vehicle should not be descendant of its grandchild",
        vehicle.isDescendantOf(child));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Scene query
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getSceneReturnsNullWhenUnattached() {
    assertNull("getScene() should return null for unattached entity", subject.getScene());
  }

  @Test
  public void getSceneReturnsSceneImpWhenAttached() {
    TestScene scene = new TestScene();
    SceneImp sceneImp = scene.getImplementation();
    subject.setVehicle(sceneImp);
    assertSame("getScene() should return the SceneImp when attached",
        sceneImp, subject.getScene());
  }

  @Test
  public void getSceneTraversesVehicleChain() {
    TestScene scene = new TestScene();
    SceneImp sceneImp = scene.getImplementation();
    vehicle.setVehicle(sceneImp);
    // subject -> vehicle -> sceneImp
    assertSame("getScene() should traverse vehicle chain to find SceneImp",
        sceneImp, subject.getScene());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Transform queries
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getAbsoluteTransformationIsNotNull() {
    assertNotNull("getAbsoluteTransformation() should not be null",
        subject.getAbsoluteTransformation());
  }

  @Test
  public void getTransformationRelativeToOtherIsNotNull() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    assertNotNull("getTransformation(other) should not be null",
        subject.getTransformation(other));
  }

  @Test
  public void getTransformationRelativeToSelfIsIdentity() {
    AffineMatrix4x4 m = subject.getTransformation(subject);
    assertTrue("Transform relative to self should be identity",
        m.isWithinEpsilonOf(AffineMatrix4x4.IDENTITY, 1e-6));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  StandIn creation
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void createStandInReturnsNonNull() {
    StandInImp standIn = subject.createStandIn();
    assertNotNull("createStandIn() should return a non-null StandInImp", standIn);
  }

  @Test
  public void createStandInSetsVehicleToCreator() {
    StandInImp standIn = subject.createStandIn();
    assertSame("Created stand-in's vehicle should be the creator",
        subject, standIn.getVehicle());
  }

  @Test
  public void createOffsetStandInAppliesTranslation() {
    StandInImp standIn = subject.createOffsetStandIn(3.0, 4.0, 5.0);
    assertNotNull("createOffsetStandIn() should return non-null", standIn);
    Point3 pos = standIn.getLocalPosition();
    assertEquals(3.0, pos.x(), 1e-9);
    assertEquals(4.0, pos.y(), 1e-9);
    assertEquals(5.0, pos.z(), 1e-9);
  }

  @Test
  public void createOffsetStandInSetsVehicle() {
    StandInImp standIn = subject.createOffsetStandIn(1, 2, 3);
    assertSame("Offset stand-in's vehicle should be the creator",
        subject, standIn.getVehicle());
  }

  @Test
  public void multipleCreateStandInReturnsDistinctInstances() {
    StandInImp a = subject.createStandIn();
    StandInImp b = subject.createStandIn();
    assertNotSame("Multiple createStandIn calls should return distinct instances", a, b);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Abstraction
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void standInAbstractionIsNull() {
    assertNull("StandInImp.getAbstraction() should return null by design",
        subject.getAbstraction());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Bounding box
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void axisAlignedBoundingBoxIsNotNull() {
    assertNotNull("getAxisAlignedMinimumBoundingBox() should return non-null",
        subject.getAxisAlignedMinimumBoundingBox());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  toString
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void toStringContainsClassName() {
    String str = subject.toString();
    assertNotNull(str);
    assertTrue("toString should contain class name", str.contains("StandInImp"));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Program query
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getProgramReturnsNullWhenUnattached() {
    assertNull("getProgram() should return null for unattached entity",
        subject.getProgram());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Test double
  // ══════════════════════════════════════════════════════════════════════════

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
      // no-op for test
    }
  }
}
