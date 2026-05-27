package edu.cmu.cs.dennisc.codec;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class AbstractBinaryRecordEncodingTest {
  private record Snapshot(String name, int objects) implements java.io.Serializable {
  }

  private static final class TestOwner implements InstancePropertyOwner {
    private final Map<String, InstanceProperty<Object>> properties = new LinkedHashMap<String, InstanceProperty<Object>>();
    private final Map<InstanceProperty<?>, String> names = new IdentityHashMap<InstanceProperty<?>, String>();

    private void register(String name, Object value) {
      InstanceProperty<Object> property = new InstanceProperty<Object>(this, value);
      properties.put(name, property);
      names.put(property, name);
    }

    @Override
    public Iterable<InstanceProperty<?>> getProperties() {
      return new ArrayList<InstanceProperty<?>>(properties.values());
    }

    @Override
    public InstanceProperty<?> getPropertyNamed(String name) {
      return properties.get(name);
    }

    @Override
    public String lookupNameFor(InstanceProperty<?> instanceProperty) {
      return names.get(instanceProperty);
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
  public void encodePropertiesWritesRecordPayloadAndNullMarkerInIterationOrder() {
    TestOwner owner = new TestOwner();
    owner.register("snapshot", new Snapshot("Round84", 12));
    owner.register("empty", null);

    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encodeProperties(owner, new HashMap<ReferenceableBinaryEncodableAndDecodable, Integer>());
    BinaryDecoder decoder = encoder.createDecoder();

    assertEquals("snapshot", decoder.decodeString());
    assertEquals(Snapshot.class.getName(), decoder.decodeString());
    assertEquals(Snapshot.class.getName(), decoder.decodeString());
    assertEquals(new Snapshot("Round84", 12), decoder.decodeRecord());
    assertEquals("empty", decoder.decodeString());
    assertEquals("", decoder.decodeString());
    assertEquals("", decoder.decodeString());
  }
}
