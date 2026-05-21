package edu.cmu.cs.dennisc.property.event;

import edu.cmu.cs.dennisc.property.ListProperty;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClearListPropertyEventTest {

  @Test
  public void constructorRetainsSource() {
    ListProperty<String> property = new ListProperty<>(new AddListPropertyEventTest.StubOwner());
    ClearListPropertyEvent<String> event = new ClearListPropertyEvent<>(property);

    assertSame(property, event.getTypedSource());
  }
}
