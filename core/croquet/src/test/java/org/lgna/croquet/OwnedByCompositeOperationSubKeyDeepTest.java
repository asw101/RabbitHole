package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.UUID;

import static org.junit.Assert.*;

public class OwnedByCompositeOperationSubKeyDeepTest {

  private OwnedByCompositeOperationSubKey keyA;
  private OwnedByCompositeOperationSubKey keyB;

  @Before
  public void setUp() {
    keyA = new OwnedByCompositeOperationSubKey(null, "open");
    keyB = new OwnedByCompositeOperationSubKey(null, "close");
  }

  // ── Equals symmetry ───────────────────────────────────────────────

  @Test
  public void equals_symmetric() {
    OwnedByCompositeOperationSubKey x = new OwnedByCompositeOperationSubKey(null, "test");
    OwnedByCompositeOperationSubKey y = new OwnedByCompositeOperationSubKey(null, "test");

    assertTrue(x.equals(y));
    assertTrue(y.equals(x));
  }

  @Test
  public void equals_transitive() {
    OwnedByCompositeOperationSubKey x = new OwnedByCompositeOperationSubKey(null, "abc");
    OwnedByCompositeOperationSubKey y = new OwnedByCompositeOperationSubKey(null, "abc");
    OwnedByCompositeOperationSubKey z = new OwnedByCompositeOperationSubKey(null, "abc");

    assertTrue(x.equals(y));
    assertTrue(y.equals(z));
    assertTrue(x.equals(z));
  }

  @Test
  public void equals_reflexive() {
    assertTrue(keyA.equals(keyA));
  }

  @Test
  public void equals_null_returnsFalse() {
    assertFalse(keyA.equals(null));
  }

  @Test
  public void equals_differentType_returnsFalse() {
    assertFalse(keyA.equals("open"));
  }

  @Test
  public void equals_differentText_returnsFalse() {
    assertFalse(keyA.equals(keyB));
  }

  // ── Null field handling ────────────────────────────────────────────

  @Test
  public void equals_bothNullComposite_sameText_returnsTrue() {
    OwnedByCompositeOperationSubKey x = new OwnedByCompositeOperationSubKey(null, "x");
    OwnedByCompositeOperationSubKey y = new OwnedByCompositeOperationSubKey(null, "x");
    assertTrue(x.equals(y));
  }

  @Test
  public void equals_nullText_sameComposite_returnsTrue() {
    OwnedByCompositeOperationSubKey x = new OwnedByCompositeOperationSubKey(null, null);
    OwnedByCompositeOperationSubKey y = new OwnedByCompositeOperationSubKey(null, null);
    assertTrue(x.equals(y));
  }

  @Test
  public void equals_nullText_vs_nonNull_returnsFalse() {
    OwnedByCompositeOperationSubKey x = new OwnedByCompositeOperationSubKey(null, null);
    OwnedByCompositeOperationSubKey y = new OwnedByCompositeOperationSubKey(null, "test");
    assertFalse(x.equals(y));
  }

  // ── hashCode consistency ───────────────────────────────────────────

  @Test
  public void hashCode_consistentWithEquals() {
    OwnedByCompositeOperationSubKey x = new OwnedByCompositeOperationSubKey(null, "test");
    OwnedByCompositeOperationSubKey y = new OwnedByCompositeOperationSubKey(null, "test");
    assertTrue(x.equals(y));
    assertEquals(x.hashCode(), y.hashCode());
  }

  @Test
  public void hashCode_consistentOnMultipleCalls() {
    int h1 = keyA.hashCode();
    int h2 = keyA.hashCode();
    assertEquals(h1, h2);
  }

  @Test
  public void hashCode_nullBothFields() {
    OwnedByCompositeOperationSubKey k = new OwnedByCompositeOperationSubKey(null, null);
    assertEquals(17, k.hashCode());
  }

  @Test
  public void hashCode_nullComposite_nonNullText() {
    OwnedByCompositeOperationSubKey k = new OwnedByCompositeOperationSubKey(null, "x");
    int expected = (37 * 17) + "x".hashCode();
    assertEquals(expected, k.hashCode());
  }

  // ── Getters ────────────────────────────────────────────────────────

  @Test
  public void getComposite_withNull() {
    assertNull(keyA.getComposite());
  }

  @Test
  public void getText_returnsConstructorValue() {
    assertEquals("open", keyA.getText());
  }

  @Test
  public void getText_withNull() {
    OwnedByCompositeOperationSubKey k = new OwnedByCompositeOperationSubKey(null, null);
    assertNull(k.getText());
  }

  // ── Different texts produce different hashCodes (usually) ──────────

  @Test
  public void hashCode_differentTexts_differ() {
    assertNotEquals(keyA.hashCode(), keyB.hashCode());
  }

  // ── Pattern: instanceof check in equals ────────────────────────────

  @Test
  public void equals_againstAnonymousSubclass_returnsFalse() {
    Object anon = new Object() {
      @Override
      public boolean equals(Object obj) {
        return false;
      }
    };
    assertFalse(keyA.equals(anon));
  }
}
