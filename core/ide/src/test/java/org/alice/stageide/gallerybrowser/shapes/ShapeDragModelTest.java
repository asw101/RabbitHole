package org.alice.stageide.gallerybrowser.shapes;

import org.junit.Test;

import static org.junit.Assert.*;

public class ShapeDragModelTest {

  @Test
  public void boxDragModel_singleton() {
    assertSame(BoxDragModel.getInstance(), BoxDragModel.getInstance());
  }

  @Test
  public void coneDragModel_singleton() {
    assertSame(ConeDragModel.getInstance(), ConeDragModel.getInstance());
  }

  @Test
  public void cylinderDragModel_singleton() {
    assertSame(CylinderDragModel.getInstance(), CylinderDragModel.getInstance());
  }

  @Test
  public void sphereDragModel_singleton() {
    assertSame(SphereDragModel.getInstance(), SphereDragModel.getInstance());
  }

  @Test
  public void torusDragModel_singleton() {
    assertSame(TorusDragModel.getInstance(), TorusDragModel.getInstance());
  }

  @Test
  public void discDragModel_singleton() {
    assertSame(DiscDragModel.getInstance(), DiscDragModel.getInstance());
  }

  @Test
  public void groundDragModel_singleton() {
    assertSame(GroundDragModel.getInstance(), GroundDragModel.getInstance());
  }

  @Test
  public void axesDragModel_singleton() {
    assertSame(AxesDragModel.getInstance(), AxesDragModel.getInstance());
  }

  @Test
  public void billboardDragModel_singleton() {
    assertSame(BillboardDragModel.getInstance(), BillboardDragModel.getInstance());
  }

  @Test
  public void textModelDragModel_singleton() {
    assertSame(TextModelDragModel.getInstance(), TextModelDragModel.getInstance());
  }

  @Test
  public void allDragModels_extendShapeDragModel() {
    assertTrue(ShapeDragModel.class.isAssignableFrom(BoxDragModel.class));
    assertTrue(ShapeDragModel.class.isAssignableFrom(ConeDragModel.class));
    assertTrue(ShapeDragModel.class.isAssignableFrom(CylinderDragModel.class));
    assertTrue(ShapeDragModel.class.isAssignableFrom(SphereDragModel.class));
    assertTrue(ShapeDragModel.class.isAssignableFrom(TorusDragModel.class));
    assertTrue(ShapeDragModel.class.isAssignableFrom(DiscDragModel.class));
    assertTrue(ShapeDragModel.class.isAssignableFrom(GroundDragModel.class));
  }
}
