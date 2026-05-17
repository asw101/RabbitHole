package edu.cmu.cs.dennisc.pattern;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ReferenceableBinaryEncodableAndDecodable;
import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AbstractInstancePropertyOwnerTest {

  // Concrete subclass with public InstanceProperty fields for testing
  public static class ConcreteOwner extends AbstractInstancePropertyOwner {
    public final InstanceProperty<String> name = new InstanceProperty<>(this, "default");
    public final InstanceProperty<Integer> count = new InstanceProperty<>(this, 0);

    @Override
    public void decode(BinaryDecoder binaryDecoder, Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) {
    }
  }

  // Another subclass for isEquivalentTo testing
  public static class AnotherOwner extends AbstractInstancePropertyOwner {
    public final InstanceProperty<String> name = new InstanceProperty<>(this, "default");
    public final InstanceProperty<Integer> count = new InstanceProperty<>(this, 0);

    @Override
    public void decode(BinaryDecoder binaryDecoder, Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) {
    }
  }

  // Subclass with different properties for isEquivalentTo mismatch
  public static class DifferentOwner extends AbstractInstancePropertyOwner {
    public final InstanceProperty<String> label = new InstanceProperty<>(this, "label");

    @Override
    public void decode(BinaryDecoder binaryDecoder, Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) {
    }
  }

  private ConcreteOwner owner;

  @Before
  public void setUp() {
    owner = new ConcreteOwner();
  }

  // --- PropertyListener tests ---

  @Test
  public void addAndRemovePropertyListener() {
    List<PropertyEvent> events = new ArrayList<>();
    PropertyListener listener = events::add;

    owner.addPropertyListener(listener);
    Collection<PropertyListener> listeners = owner.getPropertyListeners();
    assertTrue(listeners.contains(listener));

    owner.removePropertyListener(listener);
    assertFalse(owner.getPropertyListeners().contains(listener));
  }

  @Test
  public void firePropertyChanged_notifiesListeners() {
    List<PropertyEvent> events = new ArrayList<>();
    owner.addPropertyListener(events::add);

    owner.name.setValue("changed");
    assertFalse(events.isEmpty());
  }

  @Test
  public void firePropertyChanging_doesNotThrow() {
    PropertyEvent event = new PropertyEvent(owner.name, owner, "value");
    owner.firePropertyChanging(event);
  }

  // --- ListPropertyListener tests ---

  @Test
  public void addAndRemoveListPropertyListener() {
    ListPropertyListener<String> listener = new ListPropertyListener<String>() {
      @Override public void added(AddListPropertyEvent<String> e) {}
      @Override public void cleared(ClearListPropertyEvent<String> e) {}
      @Override public void removed(RemoveListPropertyEvent<String> e) {}
      @Override public void set(SetListPropertyEvent<String> e) {}
    };

    owner.addListPropertyListener(listener);
    assertTrue(owner.getListPropertyListeners().contains(listener));

    owner.removeListPropertyListener(listener);
    assertFalse(owner.getListPropertyListeners().contains(listener));
  }

  @Test
  public void fireAdding_doesNotThrow() {
    owner.fireAdding(null);
  }

  @Test
  public void fireClearing_doesNotThrow() {
    owner.fireClearing(null);
  }

  @Test
  public void fireRemoving_doesNotThrow() {
    owner.fireRemoving(null);
  }

  @Test
  public void fireSetting_doesNotThrow() {
    owner.fireSetting(null);
  }

  // --- getPropertyNamed ---

  @Test
  public void getPropertyNamed_existingProperty() {
    InstanceProperty<?> prop = owner.getPropertyNamed("name");
    assertNotNull(prop);
    assertEquals("default", prop.getValue());
  }

  @Test
  public void getPropertyNamed_withCapitalFirstLetter() {
    InstanceProperty<?> prop = owner.getPropertyNamed("Name");
    assertNotNull(prop);
    assertEquals("default", prop.getValue());
  }

  @Test
  public void getPropertyNamed_nonExistent() {
    InstanceProperty<?> prop = owner.getPropertyNamed("nonExistent");
    assertNull(prop);
  }

  // --- getProperties ---

  @Test
  public void getProperties_returnsAllInstanceProperties() {
    List<InstanceProperty<?>> properties = owner.getProperties();
    assertEquals(2, properties.size());
  }

  @Test
  public void getProperties_cachedOnSecondCall() {
    List<InstanceProperty<?>> first = owner.getProperties();
    List<InstanceProperty<?>> second = owner.getProperties();
    assertSame(first, second);
  }

  // --- lookupNameFor ---

  @Test
  public void lookupNameFor_existingProperty() {
    String nameForProp = owner.lookupNameFor(owner.name);
    assertEquals("name", nameForProp);
  }

  @Test
  public void lookupNameFor_countProperty() {
    String nameForProp = owner.lookupNameFor(owner.count);
    assertEquals("count", nameForProp);
  }

  @Test
  public void lookupNameFor_unknownProperty() {
    InstanceProperty<String> orphan = new InstanceProperty<>(owner, "orphan");
    String result = owner.lookupNameFor(orphan);
    assertNull(result);
  }

  // --- equals ---

  @Test
  public void equals_identityIsTrue() {
    assertTrue(owner.equals(owner));
  }

  @Test
  public void equals_differentInstanceIsFalse() {
    ConcreteOwner other = new ConcreteOwner();
    assertFalse(owner.equals(other));
  }

  @Test
  public void equals_nullIsFalse() {
    assertFalse(owner.equals(null));
  }

  // --- isEquivalentTo ---

  @Test
  public void isEquivalentTo_sameInstance() {
    assertTrue(owner.isEquivalentTo(owner));
  }

  @Test
  public void isEquivalentTo_equivalentValues() {
    ConcreteOwner other = new ConcreteOwner();
    assertTrue(owner.isEquivalentTo(other));
  }

  @Test
  public void isEquivalentTo_differentValues() {
    ConcreteOwner other = new ConcreteOwner();
    other.name.setValue("different");
    assertFalse(owner.isEquivalentTo(other));
  }

  @Test
  public void isEquivalentTo_differentType() {
    assertFalse(owner.isEquivalentTo("not an owner"));
  }

  @Test
  public void isEquivalentTo_differentPropertyCount() {
    DifferentOwner diff = new DifferentOwner();
    assertFalse(owner.isEquivalentTo(diff));
  }

  @Test
  public void isEquivalentTo_nullOtherPropertyReturnsCorrectly() {
    AnotherOwner other = new AnotherOwner();
    // AnotherOwner has same field names, same defaults - should be equivalent
    assertTrue(owner.isEquivalentTo(other));
  }

  // --- Name from AbstractNameable ---

  @Test
  public void setAndGetName() {
    assertNull(owner.getName());
    owner.setName("testName");
    assertEquals("testName", owner.getName());
  }
}
