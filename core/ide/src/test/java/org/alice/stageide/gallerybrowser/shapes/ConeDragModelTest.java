package org.alice.stageide.gallerybrowser.shapes;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConeDragModelTest {
  @Test
  public void singletonReturnsSameInstance() {
    assertSame(ConeDragModel.getInstance(), ConeDragModel.getInstance());
  }

  @Test
  public void placeOnGroundReturnsTrue() {
    assertTrue(ConeDragModel.getInstance().placeOnGround());
  }

  @Test
  public void migrationIdIsAvailable() {
    assertNotNull(ConeDragModel.getInstance().getMigrationId());
  }

  @Test
  public void boundingBoxIsAvailable() {
    assertNotNull(ConeDragModel.getInstance().getBoundingBox());
  }
}
