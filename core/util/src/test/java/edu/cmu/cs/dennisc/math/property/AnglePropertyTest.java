package edu.cmu.cs.dennisc.math.property;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import edu.cmu.cs.dennisc.property.event.*;
import org.alice.math.immutable.Angle;
import org.alice.math.immutable.AngleInRadians;
import org.junit.Assert;
import org.junit.Test;

public class AnglePropertyTest {
  private static class CountingOwner implements InstancePropertyOwner {
    private int changedCount;

    @Override public Iterable<InstanceProperty<?>> getProperties() { return java.util.Collections.emptyList(); }
    @Override public InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(InstanceProperty<?> ip) { return "angleProperty"; }
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
  public void setValueAcceptsNaNWhenConfigured() {
    CountingOwner owner = new CountingOwner();
    AngleProperty property = new AngleProperty(owner, new AngleInRadians(0.25), true);

    property.setValue(Angle.NaN);
    property.setValue(new AngleInRadians(0.5));

    Assert.assertEquals(0.5, property.getValue().getAsRadians(), 0.0);
    Assert.assertEquals(2, owner.changedCount);
  }
}
