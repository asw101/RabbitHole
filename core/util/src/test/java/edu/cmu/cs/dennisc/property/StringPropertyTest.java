package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Assert;
import org.junit.Test;

public class StringPropertyTest {
  private static class CountingOwner implements InstancePropertyOwner {
    private int changedCount;

    @Override public Iterable<InstanceProperty<?>> getProperties() { return java.util.Collections.emptyList(); }
    @Override public InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(InstanceProperty<?> ip) { return "stringProperty"; }
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

  private static class NullableStringProperty extends StringProperty {
    private NullableStringProperty(InstancePropertyOwner owner, String value) {
      super(owner, value);
    }

    @Override
    protected boolean isNullAcceptable() {
      return true;
    }
  }

  private static class NullableCharSequenceProperty extends CharSequenceProperty {
    private NullableCharSequenceProperty(InstancePropertyOwner owner, CharSequence value) {
      super(owner, value);
    }

    @Override
    protected boolean isNullAcceptable() {
      return true;
    }
  }

  @Test
  public void stringPropertySupportsCustomNullAcceptance() {
    CountingOwner owner = new CountingOwner();
    NullableStringProperty property = new NullableStringProperty(owner, "alpha");

    property.setValue(null);
    property.setValue("beta");

    Assert.assertEquals("beta", property.getValue());
    Assert.assertEquals(2, owner.changedCount);
  }

  @Test
  public void charSequencePropertySupportsCustomNullAcceptance() {
    CountingOwner owner = new CountingOwner();
    NullableCharSequenceProperty property = new NullableCharSequenceProperty(owner, "alpha");

    property.setValue(null);
    property.setValue(new StringBuilder("beta"));

    Assert.assertEquals("beta", property.getValue().toString());
    Assert.assertEquals(2, owner.changedCount);
  }
}
