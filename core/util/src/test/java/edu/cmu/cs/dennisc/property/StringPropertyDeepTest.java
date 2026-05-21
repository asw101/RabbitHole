package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner;
import org.junit.Test;

import static org.junit.Assert.*;

public class StringPropertyDeepTest {

  @Test
  public void setValueUpdatesStoredText() {
    TestOwner owner = new TestOwner();

    owner.text.setValue("updated");

    assertEquals("updated", owner.text.getValue());
  }

  @Test
  public void anonymousSubclassCanAcceptNull() {
    NullableOwner owner = new NullableOwner();

    owner.text.setValue(null);

    assertNull(owner.text.getValue());
  }

  public static final class TestOwner extends AbstractInstancePropertyOwner {
    public final StringProperty text = new StringProperty(this, "start");
  }

  public static final class NullableOwner extends AbstractInstancePropertyOwner {
    public final StringProperty text = new StringProperty(this, "start") {
      @Override
      protected boolean isNullAcceptable() {
        return true;
      }
    };
  }
}
