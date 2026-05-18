package org.alice.stageide.gallerybrowser.shapes;

import org.junit.Test;

import static org.junit.Assert.*;

public class GroundDragModelTest {
  @Test
  public void singletonReturnsSameInstance() {
    assertSame(GroundDragModel.getInstance(), GroundDragModel.getInstance());
  }

  @Test
  public void placeOnGroundReturnsTrue() {
    assertTrue(GroundDragModel.getInstance().placeOnGround());
  }

  @Test
  public void migrationIdIsAvailable() {
    assertNotNull(GroundDragModel.getInstance().getMigrationId());
  }

  @Test
  public void boundingBoxIsAvailable() {
    assertNotNull(GroundDragModel.getInstance().getBoundingBox());
  }
}
