package edu.cmu.cs.dennisc.codec;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class AbstractBinaryPropertiesRoundTripTest {
  private enum Mode {
    ALPHA,
    BETA
  }

  private static final class TestOwner implements InstancePropertyOwner {
    private final Map<String, InstanceProperty<Object>> properties = new LinkedHashMap<String, InstanceProperty<Object>>();
    private final Map<InstanceProperty<?>, String> names = new IdentityHashMap<InstanceProperty<?>, String>();

    private InstanceProperty<Object> register(String name, Object value) {
      InstanceProperty<Object> property = new InstanceProperty<Object>(this, value);
      properties.put(name, property);
      names.put(property, name);
      return property;
    }

    private Object value(String name) {
      return properties.get(name).getValue();
    }

    private TestOwner blankCopy() {
      TestOwner copy = new TestOwner();
      for (String name : properties.keySet()) {
        copy.register(name, null);
      }
      return copy;
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
  public void encodeAndDecodePropertiesRoundTripsPrimitiveObjectArrayAndBufferValues() {
    TestOwner original = new TestOwner();
    original.register("flag", Boolean.TRUE);
    original.register("count", Integer.valueOf(42));
    original.register("distance", Double.valueOf(3.5));
    original.register("label", "Alice");
    original.register("mode", Mode.BETA);
    original.register("booleanArray", new boolean[] {true, false, true});
    original.register("intArray", new int[] {1, 2, 3});
    original.register("stringArray", new String[] {"red", "blue"});
    original.register("enumArray", new Mode[] {Mode.ALPHA, Mode.BETA});
    original.register("boxedArray", new Integer[] {7, 8, 9});


    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encodeProperties(original, new HashMap<ReferenceableBinaryEncodableAndDecodable, Integer>());

    TestOwner decoded = original.blankCopy();
    encoder.createDecoder().decodeProperties(decoded, new HashMap<Integer, ReferenceableBinaryEncodableAndDecodable>());

    assertEquals(Boolean.TRUE, decoded.value("flag"));
    assertEquals(Integer.valueOf(42), decoded.value("count"));
    assertEquals(Double.valueOf(3.5), decoded.value("distance"));
    assertEquals("Alice", decoded.value("label"));
    assertEquals(Mode.BETA, decoded.value("mode"));
    assertArrayEquals(new boolean[] {true, false, true}, (boolean[]) decoded.value("booleanArray"));
    assertArrayEquals(new int[] {1, 2, 3}, (int[]) decoded.value("intArray"));
    assertArrayEquals(new String[] {"red", "blue"}, (String[]) decoded.value("stringArray"));
    assertArrayEquals(new Mode[] {Mode.ALPHA, Mode.BETA}, (Mode[]) decoded.value("enumArray"));
    assertArrayEquals(new Integer[] {7, 8, 9}, (Integer[]) decoded.value("boxedArray"));

  }

}
