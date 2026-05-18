package org.alice.ide.properties.adapter;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import edu.cmu.cs.dennisc.property.event.AddListPropertyEvent;
import edu.cmu.cs.dennisc.property.event.ClearListPropertyEvent;
import edu.cmu.cs.dennisc.property.event.PropertyEvent;
import edu.cmu.cs.dennisc.property.event.RemoveListPropertyEvent;
import edu.cmu.cs.dennisc.property.event.SetListPropertyEvent;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class StringPropertyAdapterTest {

  private static final class TestOwner implements InstancePropertyOwner {
    private final InstanceProperty<String> value = new InstanceProperty<>(this, "initial");

    @Override
    public Iterable<InstanceProperty<?>> getProperties() {
      return Collections.singletonList(this.value);
    }

    @Override
    public InstanceProperty<?> getPropertyNamed(String name) {
      return "value".equals(name) ? this.value : null;
    }

    @Override
    public String lookupNameFor(InstanceProperty<?> instanceProperty) {
      return "value";
    }

    @Override public void firePropertyChanging(PropertyEvent e) {}
    @Override public void firePropertyChanged(PropertyEvent e) {}
    @Override public void fireAdding(AddListPropertyEvent<?> e) {}
    @Override public void fireAdded(AddListPropertyEvent<?> e) {}
    @Override public void fireClearing(ClearListPropertyEvent<?> e) {}
    @Override public void fireCleared(ClearListPropertyEvent<?> e) {}
    @Override public void fireRemoving(RemoveListPropertyEvent<?> e) {}
    @Override public void fireRemoved(RemoveListPropertyEvent<?> e) {}
    @Override public void fireSetting(SetListPropertyEvent<?> e) {}
    @Override public void fireSet(SetListPropertyEvent<?> e) {}
  }

  @Test
  public void defaultConstructor_usesStringRepresentation() {
    StringPropertyAdapter<TestOwner> adapter = new StringPropertyAdapter<>(new TestOwner(), new TestOwner().value, null);

    assertEquals("String", adapter.getRepr());
  }

  @Test
  public void getPropertyType_returnsStringClass() {
    TestOwner owner = new TestOwner();
    StringPropertyAdapter<TestOwner> adapter = new StringPropertyAdapter<>(owner, owner.value, null);

    assertEquals(String.class, adapter.getPropertyType());
  }

  @Test
  public void getUndoRedoDescription_returnsString() {
    TestOwner owner = new TestOwner();
    StringPropertyAdapter<TestOwner> adapter = new StringPropertyAdapter<>(owner, owner.value, null);

    assertEquals("String", adapter.getUndoRedoDescription());
  }

  @Test
  public void getValueCopyIfMutable_returnsEqualDistinctString() {
    TestOwner owner = new TestOwner();
    StringPropertyAdapter<TestOwner> adapter = new StringPropertyAdapter<>(owner, owner.value, null);

    assertEquals(adapter.getValue(), adapter.getValueCopyIfMutable());
    assertNotSame(adapter.getValue(), adapter.getValueCopyIfMutable());
  }

  @Test
  public void setValue_updatesUnderlyingProperty() {
    TestOwner owner = new TestOwner();
    StringPropertyAdapter<TestOwner> adapter = new StringPropertyAdapter<>(owner, owner.value, null);

    adapter.setValue("updated");

    waitForValue(owner.value, "updated");
    assertEquals("updated", owner.value.getValue());
  }

  private static void waitForValue(InstanceProperty<String> property, String expected) {
    long deadline = System.currentTimeMillis() + 2000;
    while (System.currentTimeMillis() < deadline) {
      if (expected.equals(property.getValue())) {
        return;
      }
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    }
    fail("Timed out waiting for property value " + expected);
  }
}
