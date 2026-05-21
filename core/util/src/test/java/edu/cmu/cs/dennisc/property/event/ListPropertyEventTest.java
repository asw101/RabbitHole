package edu.cmu.cs.dennisc.property.event;

import edu.cmu.cs.dennisc.property.ListProperty;
import org.junit.Test;

import static org.junit.Assert.*;

public class ListPropertyEventTest {

  @Test
  public void typedSourceMatchesOriginalProperty() {
    ListProperty<String> property = new ListProperty<>(new AddListPropertyEventTest.StubOwner());
    ListPropertyEvent<String> event = new ClearListPropertyEvent<>(property);

    assertSame(property, event.getTypedSource());
  }
}
