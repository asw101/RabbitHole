package org.lgna.croquet;
import org.junit.Test;
import static org.junit.Assert.*;
public class OwnedByCompositeOperationSubKeyTest {
  @Test public void constructor_setsText() { OwnedByCompositeOperationSubKey k=new OwnedByCompositeOperationSubKey(null, "test"); assertEquals("test", k.getText()); }
  @Test public void constructor_setsComposite() { OwnedByCompositeOperationSubKey k=new OwnedByCompositeOperationSubKey(null, "t"); assertNull(k.getComposite()); }
  @Test public void equals_sameTextAndComposite() { OwnedByCompositeOperationSubKey a=new OwnedByCompositeOperationSubKey(null, "t"); OwnedByCompositeOperationSubKey b=new OwnedByCompositeOperationSubKey(null, "t"); assertEquals(a, b); }
  @Test public void equals_differentText() { OwnedByCompositeOperationSubKey a=new OwnedByCompositeOperationSubKey(null, "a"); OwnedByCompositeOperationSubKey b=new OwnedByCompositeOperationSubKey(null, "b"); assertNotEquals(a, b); }
  @Test public void equals_self() { OwnedByCompositeOperationSubKey k=new OwnedByCompositeOperationSubKey(null, "t"); assertEquals(k, k); }
  @Test public void equals_null() { OwnedByCompositeOperationSubKey k=new OwnedByCompositeOperationSubKey(null, "t"); assertNotEquals(k, null); }
  @Test public void equals_differentType() { OwnedByCompositeOperationSubKey k=new OwnedByCompositeOperationSubKey(null, "t"); assertNotEquals(k, "string"); }
  @Test public void hashCode_consistent() { OwnedByCompositeOperationSubKey a=new OwnedByCompositeOperationSubKey(null, "t"); OwnedByCompositeOperationSubKey b=new OwnedByCompositeOperationSubKey(null, "t"); assertEquals(a.hashCode(), b.hashCode()); }
  @Test public void getText_returnsConstructorArg() { assertEquals("hello", new OwnedByCompositeOperationSubKey(null, "hello").getText()); }
  @Test public void emptyText() { OwnedByCompositeOperationSubKey k=new OwnedByCompositeOperationSubKey(null, ""); assertEquals("", k.getText()); }
}
