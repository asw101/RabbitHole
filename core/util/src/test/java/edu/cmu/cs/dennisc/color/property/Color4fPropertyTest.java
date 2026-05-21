package edu.cmu.cs.dennisc.color.property;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Assert;
import org.junit.Test;

public class Color4fPropertyTest {
  private static class CountingOwner implements InstancePropertyOwner {
    private int changedCount;

    @Override public Iterable<InstanceProperty<?>> getProperties() { return java.util.Collections.emptyList(); }
    @Override public InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(InstanceProperty<?> ip) { return "colorProperty"; }
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
    Color4fProperty property = new Color4fProperty(owner, Color4f.WHITE, true);

    property.setValue(Color4f.createNaN());
    property.setValue(Color4f.BLACK);

    Assert.assertEquals(Color4f.BLACK, property.getValue());
    Assert.assertEquals(2, owner.changedCount);
  }
}
