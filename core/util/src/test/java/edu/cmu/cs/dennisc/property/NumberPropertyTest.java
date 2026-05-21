package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Assert;
import org.junit.Test;

public class NumberPropertyTest {
  private static class CountingOwner implements InstancePropertyOwner {
    private int changedCount;

    @Override public Iterable<InstanceProperty<?>> getProperties() { return java.util.Collections.emptyList(); }
    @Override public InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(InstanceProperty<?> ip) { return "numberProperty"; }
    @Override public void firePropertyChanging(PropertyEvent e) { }
    @Override public void firePropertyChanged(PropertyEvent e) { this.changedCount++; }
    @Override public void fireAdding(AddListPropertyEvent<?> e) { }
    @Override public void fireAdded(AddListPropertyEvent<?> e) { }
    @Override public void fireClearing(ClearListPropertyEvent<?> e) { }
    @Override public void fireCleared(ClearListPropertyEvent<?> e) { }
    @Override public void fireRemoving(RemoveListPropertyEvent<?> e) { }
    @Override public void fireRemoved(RemoveListPropertyEvent<?> e) { }
    @Override public void fireSetting(SetListPropertyEvent<?> e) { }
    @Override public void fireSet(SetListPropertyEvent<?> e) { }
  }

  @Test
  public void numberPropertyAllowsNaNWhenConfigured() {
    CountingOwner owner = new CountingOwner();
    NumberProperty property = new NumberProperty(owner, 1.0, true);

    property.setValue(Double.NaN);
    property.setValue(Double.NaN);
    property.setValue(5.0);

    Assert.assertEquals(5.0, property.getValue());
    Assert.assertEquals(2, owner.changedCount);
  }

  @Test
  public void floatDoubleAndIntegerPropertiesTrackDistinctChanges() {
    CountingOwner floatOwner = new CountingOwner();
    FloatProperty floatProperty = new FloatProperty(floatOwner, 1.0f, true);
    floatProperty.setValue(Float.NaN);
    floatProperty.setValue(2.0f);

    CountingOwner doubleOwner = new CountingOwner();
    DoubleProperty doubleProperty = new DoubleProperty(doubleOwner, 1.0, true);
    doubleProperty.setValue(Double.NaN);
    doubleProperty.setValue(3.0);

    CountingOwner integerOwner = new CountingOwner();
    IntegerProperty integerProperty = new IntegerProperty(integerOwner, 1);
    integerProperty.setValue(4);
    integerProperty.setValue(4);

    Assert.assertEquals(2.0f, floatProperty.getValue(), 0.0f);
    Assert.assertEquals(2, floatOwner.changedCount);
    Assert.assertEquals(3.0, doubleProperty.getValue(), 0.0);
    Assert.assertEquals(2, doubleOwner.changedCount);
    Assert.assertEquals(Integer.valueOf(4), integerProperty.getValue());
    Assert.assertEquals(1, integerOwner.changedCount);
  }
}
