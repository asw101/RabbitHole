package edu.cmu.cs.dennisc.property.event;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import edu.cmu.cs.dennisc.property.ListProperty;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SetListPropertyEventTest {

  @Test
  public void varargsConstructorStoresReplacementValues() {
    ListProperty<String> property = new ListProperty<>(new AddListPropertyEventTest.StubOwner());
    SetListPropertyEvent<String> event = new SetListPropertyEvent<>(property, 4, "one", "two");

    assertEquals(4, event.getStartIndex());
    assertEquals(Arrays.asList("one", "two"), event.getElements());
  }

  @Test
  public void collectionConstructorStoresReplacementCollection() {
    ListProperty<String> property = new ListProperty<>(new AddListPropertyEventTest.StubOwner());
    SetListPropertyEvent<String> event = new SetListPropertyEvent<>(property, 1, Arrays.asList("x", "y"));

    assertSame(property, event.getTypedSource());
    assertEquals(Arrays.asList("x", "y"), event.getElements());
  }
}
