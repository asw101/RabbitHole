package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.StandIn;
import org.junit.Test;
import org.lgna.story.SBox;
import org.lgna.story.STarget;

import static org.junit.Assert.*;

/**
 * Tests for {@link StandInImp} and {@link TargetImp} (via {@link STarget}).
 *
 * <p>StandInImp is unique: it has a no-arg constructor and getAbstraction()
 * returns null. TargetImp wraps STarget via the standard facade pattern.
 */
public class StandInAndTargetImpTest {

  // ══════════════════════════════════════════════════════════════════════
  //  StandInImp
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void standIn_constructsWithoutError() {
    StandInImp imp = new StandInImp();
    assertNotNull(imp);
  }

  @Test
  public void standIn_abstractionIsNull() {
    StandInImp imp = new StandInImp();
    assertNull(imp.getAbstraction());
  }

  @Test
  public void standIn_sgCompositeIsStandIn() {
    StandInImp imp = new StandInImp();
    assertTrue(imp.getSgComposite() instanceof StandIn);
  }

  @Test
  public void standIn_sgCompositeNotNull() {
    StandInImp imp = new StandInImp();
    assertNotNull(imp.getSgComposite());
  }

  @Test
  public void standIn_initialVehicleIsNull() {
    StandInImp imp = new StandInImp();
    assertNull(imp.getSgVehicle());
  }

  @Test
  public void standIn_setVehicle_viaBoxImp() {
    StandInImp standIn = new StandInImp();
    SBox box = new SBox();
    BoxImp boxImp = box.getImplementation();
    standIn.setVehicle(boxImp);
    assertSame(boxImp.getSgComposite(), standIn.getSgVehicle());
  }

  @Test
  public void standIn_release_clearsVehicle() {
    StandInImp standIn = new StandInImp();
    SBox box = new SBox();
    standIn.setVehicle(box.getImplementation());
    assertNotNull(standIn.getSgVehicle());
    standIn.release();
    assertNull(standIn.getSgVehicle());
  }

  @Test
  public void standIn_instanceRegistryWorks() {
    StandInImp imp = new StandInImp();
    EntityImp found = EntityImp.getInstance(imp.getSgComposite());
    assertSame(imp, found);
  }

  @Test
  public void standIn_extendsAbstractTransformableImp() {
    assertTrue(AbstractTransformableImp.class.isAssignableFrom(StandInImp.class));
  }

  @Test
  public void standIn_implementsReusable() {
    assertTrue(edu.cmu.cs.dennisc.pattern.Reusable.class.isAssignableFrom(StandInImp.class));
  }

  @Test
  public void standIn_applyAnimation_doesNotThrow() {
    StandInImp imp = new StandInImp();
    imp.applyAnimation();
  }

  @Test
  public void standIn_localTransformIsIdentity() {
    StandInImp imp = new StandInImp();
    assertTrue(imp.getLocalTransformation().isIdentity());
  }

  @Test
  public void standIn_setName_roundTrips() {
    StandInImp imp = new StandInImp();
    imp.setName("TestStandIn");
    assertEquals("TestStandIn", imp.getName());
  }

  @Test
  public void standIn_multipleReleaseCycles() {
    StandInImp standIn = new StandInImp();
    SBox box = new SBox();
    standIn.setVehicle(box.getImplementation());
    standIn.release();
    assertNull(standIn.getSgVehicle());
    standIn.setVehicle(box.getImplementation());
    assertNotNull(standIn.getSgVehicle());
    standIn.release();
    assertNull(standIn.getSgVehicle());
  }

  @Test
  public void standIn_axisAlignedBoundingBox_notNull() {
    StandInImp imp = new StandInImp();
    assertNotNull(imp.getAxisAlignedMinimumBoundingBox());
  }

  // ══════════════════════════════════════════════════════════════════════
  //  STarget / TargetImp
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void target_constructsWithoutError() {
    STarget target = new STarget();
    assertNotNull(target.getImplementation());
  }

  @Test
  public void target_abstractionRoundTrips() {
    STarget target = new STarget();
    assertSame(target, target.getImplementation().getAbstraction());
  }

  @Test
  public void target_sgCompositeNotNull() {
    STarget target = new STarget();
    assertNotNull(target.getImplementation().getSgComposite());
  }

  @Test
  public void target_extendsTransformableImp() {
    assertTrue(TransformableImp.class.isAssignableFrom(TargetImp.class));
  }

  @Test
  public void target_localTransformIsIdentity() {
    STarget target = new STarget();
    assertTrue(target.getImplementation().getLocalTransformation().isIdentity());
  }

  @Test
  public void target_setName_roundTrips() {
    STarget target = new STarget();
    target.getImplementation().setName("TestTarget");
    assertEquals("TestTarget", target.getImplementation().getName());
  }

  @Test
  public void target_axisAlignedBoundingBox_notNull() {
    STarget target = new STarget();
    assertNotNull(target.getImplementation().getAxisAlignedMinimumBoundingBox());
  }

  @Test
  public void target_instanceRegistryWorks() {
    STarget target = new STarget();
    EntityImp found = EntityImp.getInstance(target.getImplementation().getSgComposite());
    assertSame(target.getImplementation(), found);
  }

  @Test
  public void target_multipleInstances_areIndependent() {
    STarget t1 = new STarget();
    STarget t2 = new STarget();
    assertNotSame(t1.getImplementation(), t2.getImplementation());
    assertNotSame(t1.getImplementation().getSgComposite(), t2.getImplementation().getSgComposite());
  }

  @Test
  public void target_setVehicle_acceptsNull() {
    STarget target = new STarget();
    target.getImplementation().setVehicle(null);
  }

  @Test
  public void target_setVehicle_acceptsBoxImp() {
    STarget target = new STarget();
    SBox box = new SBox();
    target.getImplementation().setVehicle(box.getImplementation());
  }
}
