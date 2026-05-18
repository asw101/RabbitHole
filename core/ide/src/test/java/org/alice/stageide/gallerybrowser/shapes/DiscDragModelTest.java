package org.alice.stageide.gallerybrowser.shapes;

import org.junit.Test;

import static org.junit.Assert.*;

public class DiscDragModelTest {
  @Test
  public void singletonReturnsSameInstance() {
    assertSame(DiscDragModel.getInstance(), DiscDragModel.getInstance());
  }

  @Test
  public void placeOnGroundReturnsTrue() {
    assertTrue(DiscDragModel.getInstance().placeOnGround());
  }

  @Test
  public void migrationIdIsAvailable() {
    assertNotNull(DiscDragModel.getInstance().getMigrationId());
  }

  @Test
  public void boundingBoxIsAvailable() {
    assertNotNull(DiscDragModel.getInstance().getBoundingBox());
  }
}
