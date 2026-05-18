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

public class AbstractInstancePropertyAdapterTest {

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

  private static final class TestAdapter extends AbstractInstancePropertyAdapter<String, TestOwner> {
    private TestAdapter(TestOwner owner) {
      super("String", owner, owner.value, null);
    }

    @Override
    public Class<String> getPropertyType() {
      return String.class;
    }

    @Override
    public String getValueCopyIfMutable() {
      return new String(this.getValue());
    }
  }

  @Test
  public void getValue_readsUnderlyingProperty() {
    TestOwner owner = new TestOwner();
    TestAdapter adapter = new TestAdapter(owner);

    assertEquals("initial", adapter.getValue());
  }

  @Test
  public void getPropertyType_returnsStringClass() {
    TestAdapter adapter = new TestAdapter(new TestOwner());

    assertEquals(String.class, adapter.getPropertyType());
  }

  @Test
  public void setValue_updatesPropertyAsynchronously() {
    TestOwner owner = new TestOwner();
    TestAdapter adapter = new TestAdapter(owner);

    adapter.setValue("updated");

    PropertyAdapterTestHelper.waitForValue(owner.value::getValue, "updated");
    assertEquals("updated", owner.value.getValue());
  }

  @Test
  public void propertyListener_notifiesObserversWhenPropertyChanges() {
    TestOwner owner = new TestOwner();
    TestAdapter adapter = new TestAdapter(owner);
    String[] observed = new String[1];
    adapter.addValueChangeObserver(value -> observed[0] = value);

    owner.value.setValue("changed");

    assertEquals("changed", observed[0]);
  }

  @Test
  public void getValueCopyIfMutable_returnsEqualDistinctString() {
    TestAdapter adapter = new TestAdapter(new TestOwner());

    assertEquals(adapter.getValue(), adapter.getValueCopyIfMutable());
    assertNotSame(adapter.getValue(), adapter.getValueCopyIfMutable());
  }
}
