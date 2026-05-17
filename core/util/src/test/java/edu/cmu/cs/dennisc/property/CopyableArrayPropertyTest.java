package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CopyableArrayPropertyTest {

  // Concrete subclass for testing with String arrays
  public static class StringArrayOwner extends AbstractInstancePropertyOwner {
    public final CopyableArrayProperty<String> items =
        new CopyableArrayProperty<String>(this, "a", "b", "c") {
          @Override
          protected String[] createArray(int length) {
            return new String[length];
          }

          @Override
          protected String createCopy(String e) {
            return e; // Strings are immutable
          }
        };
  }

  private StringArrayOwner owner;

  @Before
  public void setUp() {
    owner = new StringArrayOwner();
  }

  @Test
  public void getLength_returnsArrayLength() {
    assertEquals(3, owner.items.getLength());
  }

  @Test
  public void getLength_afterSetValue() {
    owner.items.setValue(new String[]{"x", "y"});
    assertEquals(2, owner.items.getLength());
  }

  @Test
  public void getValue_returnsInitialValue() {
    String[] val = owner.items.getValue();
    assertArrayEquals(new String[]{"a", "b", "c"}, val);
  }

  @Test
  public void setValue_updatesValue() {
    String[] newVal = {"d", "e"};
    owner.items.setValue(newVal);
    assertArrayEquals(new String[]{"d", "e"}, owner.items.getValue());
  }

  @Test
  public void getCopy_returnsCopy() {
    String[] copy = owner.items.getCopy();
    assertNotNull(copy);
    assertEquals(3, copy.length);
    assertArrayEquals(new String[]{"a", "b", "c"}, copy);
  }

  @Test
  public void getCopy_isIndependentOfOriginal() {
    String[] copy = owner.items.getCopy();
    copy[0] = "MODIFIED";
    assertEquals("a", owner.items.getValue()[0]);
  }

  @Test
  public void getCopyWithBuffer_fillsProvidedArray() {
    String[] buffer = new String[3];
    String[] result = owner.items.getCopy(buffer);
    assertSame(buffer, result);
    assertArrayEquals(new String[]{"a", "b", "c"}, result);
  }

  @Test
  public void setCopy_setsValue() {
    String[] src = {"x", "y", "z"};
    owner.items.setCopy(src);
    String[] val = owner.items.getValue();
    assertNotNull(val);
    assertEquals(3, val.length);
  }

  @Test
  public void getLength_nullValue_returnsZero() {
    // Create a property with a null-returning subclass to test null branch
    CopyableArrayProperty<String> nullProp =
        new CopyableArrayProperty<String>(owner) {
          @Override
          protected String[] createArray(int length) {
            return new String[length];
          }

          @Override
          protected String createCopy(String e) {
            return e;
          }
        };
    assertEquals(0, nullProp.getLength());
  }

  @Test
  public void setValue_singleElement() {
    owner.items.setValue(new String[]{"only"});
    assertEquals(1, owner.items.getLength());
    assertEquals("only", owner.items.getValue()[0]);
  }
}
