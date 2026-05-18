package org.alice.stageide.gallerybrowser.shapes;

import org.junit.Test;

import static org.junit.Assert.*;

public class BoxDragModelTest {
  @Test
  public void singletonReturnsSameInstance() {
    assertSame(BoxDragModel.getInstance(), BoxDragModel.getInstance());
  }

  @Test
  public void placeOnGroundReturnsTrue() {
    assertTrue(BoxDragModel.getInstance().placeOnGround());
  }

  @Test
  public void migrationIdIsAvailable() {
    assertNotNull(BoxDragModel.getInstance().getMigrationId());
  }

  @Test
  public void boundingBoxIsAvailable() {
    assertNotNull(BoxDragModel.getInstance().getBoundingBox());
  }
}
