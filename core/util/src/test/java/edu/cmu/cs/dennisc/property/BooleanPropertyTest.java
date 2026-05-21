package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Assert;
import org.junit.Test;

public class BooleanPropertyTest {
  private static class CountingOwner implements InstancePropertyOwner {
    private int changedCount;

    @Override public Iterable<InstanceProperty<?>> getProperties() { return java.util.Collections.emptyList(); }
    @Override public InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(InstanceProperty<?> ip) { return "booleanProperty"; }
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
  public void setValueAndSetValueAsIntegerUpdateValue() {
    CountingOwner owner = new CountingOwner();
    BooleanProperty property = new BooleanProperty(owner, Boolean.FALSE);

    property.setValue(Boolean.TRUE);
    property.setValue(Boolean.TRUE);
    property.setValueAsInteger(0);

    Assert.assertFalse(property.getValue());
    Assert.assertEquals(2, owner.changedCount);
  }
}
