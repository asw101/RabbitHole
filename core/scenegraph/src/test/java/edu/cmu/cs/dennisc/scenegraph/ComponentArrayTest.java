package edu.cmu.cs.dennisc.scenegraph;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ComponentArrayTest {
  @Test
  public void componentPropertyDefaultsToNull() {
    ComponentArray array = new ComponentArray();

    assertNull(array.component.getValue());
  }

  @Test
  public void componentPropertyStoresAssignedComponent() {
    ComponentArray array = new ComponentArray();
    Transformable component = new Transformable();

    array.component.setValue(component);

    assertSame(component, array.component.getValue());
  }

  @Test
  public void boundingBoxIsNullWithoutGeometryData() {
    ComponentArray array = new ComponentArray();

    assertNull(array.getAxisAlignedMinimumBoundingBox());
  }
}
