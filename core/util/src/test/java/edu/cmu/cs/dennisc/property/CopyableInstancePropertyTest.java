package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Assert;
import org.junit.Test;

public class CopyableInstancePropertyTest {
  private static class StubOwner implements InstancePropertyOwner {
    @Override public Iterable<InstanceProperty<?>> getProperties() { return java.util.Collections.emptyList(); }
    @Override public InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(InstanceProperty<?> ip) { return "copyable"; }
    @Override public void firePropertyChanging(PropertyEvent e) { }
    @Override public void firePropertyChanged(PropertyEvent e) { }
    @Override public void fireAdding(AddListPropertyEvent<?> e) { }
    @Override public void fireAdded(AddListPropertyEvent<?> e) { }
    @Override public void fireClearing(ClearListPropertyEvent<?> e) { }
    @Override public void fireCleared(ClearListPropertyEvent<?> e) { }
    @Override public void fireRemoving(RemoveListPropertyEvent<?> e) { }
    @Override public void fireRemoved(RemoveListPropertyEvent<?> e) { }
    @Override public void fireSetting(SetListPropertyEvent<?> e) { }
    @Override public void fireSet(SetListPropertyEvent<?> e) { }
  }

  private static class CopyableStringProperty extends CopyableInstanceProperty<StringBuilder> {
    private CopyableStringProperty(InstancePropertyOwner owner, StringBuilder value) {
      super(owner, value);
    }

    @Override
    public StringBuilder getCopy(StringBuilder rv) {
      rv.setLength(0);
      rv.append(getValue());
      return rv;
    }

    @Override
    public StringBuilder getCopy() {
      return new StringBuilder(getValue().toString());
    }

    @Override
    public void setCopy(StringBuilder value) {
      setValue(new StringBuilder(value.toString()));
    }
  }

  @Test
  public void copyMethodsReturnDetachedValues() {
    CopyableStringProperty property = new CopyableStringProperty(new StubOwner(), new StringBuilder("alpha"));

    StringBuilder copy = property.getCopy();
    StringBuilder reused = property.getCopy(new StringBuilder());
    property.setCopy(new StringBuilder("beta"));

    copy.append("-changed");
    Assert.assertEquals("alpha", reused.toString());
    Assert.assertEquals("beta", property.getValue().toString());
  }
}
