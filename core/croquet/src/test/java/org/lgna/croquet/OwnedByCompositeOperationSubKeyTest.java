package org.lgna.croquet;
import org.junit.Test;
import static org.junit.Assert.*;
public class OwnedByCompositeOperationSubKeyTest {
  @Test public void constructor_setsText() { assertEquals("test", new OwnedByCompositeOperationSubKey(null, "test").getText()); }
  @Test public void constructor_setsComposite() { assertNull(new OwnedByCompositeOperationSubKey(null, "t").getComposite()); }
  @Test public void equals_sameTextAndComposite() { assertEquals(new OwnedByCompositeOperationSubKey(null, "t"), new OwnedByCompositeOperationSubKey(null, "t")); }
  @Test public void equals_differentText() { assertNotEquals(new OwnedByCompositeOperationSubKey(null, "a"), new OwnedByCompositeOperationSubKey(null, "b")); }
  @Test public void equals_self() { OwnedByCompositeOperationSubKey k=new OwnedByCompositeOperationSubKey(null, "t"); assertEquals(k, k); }
  @Test public void equals_null() { assertNotEquals(new OwnedByCompositeOperationSubKey(null, "t"), null); }
  @Test public void equals_differentType() { assertNotEquals(new OwnedByCompositeOperationSubKey(null, "t"), "string"); }
  @Test public void hashCode_consistent() { assertEquals(new OwnedByCompositeOperationSubKey(null, "t").hashCode(), new OwnedByCompositeOperationSubKey(null, "t").hashCode()); }
  @Test public void getText() { assertEquals("hello", new OwnedByCompositeOperationSubKey(null, "hello").getText()); }
  @Test public void emptyText() { assertEquals("", new OwnedByCompositeOperationSubKey(null, "").getText()); }
}
