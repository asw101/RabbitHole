package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner;
import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanPropertyDeepTest {

  @Test
  public void setValueAsIntegerConvertsZeroToFalseAndNonZeroToTrue() {
    TestOwner owner = new TestOwner();

    owner.flag.setValueAsInteger(0);
    assertFalse(owner.flag.getValue());

    owner.flag.setValueAsInteger(7);
    assertTrue(owner.flag.getValue());
  }

  @Test
  public void setSameValueLeavesValueUnchanged() {
    TestOwner owner = new TestOwner();
    owner.flag.setValue(true);
    assertTrue(owner.flag.getValue());
  }

  public static final class TestOwner extends AbstractInstancePropertyOwner {
    public final BooleanProperty flag = new BooleanProperty(this, true);
  }
}
