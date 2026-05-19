package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
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

  // ── Class structure ────────────────────────────────────────────────

  @Test
  public void class_isPublicAndFinal() {
    assertTrue(Modifier.isPublic(OwnedByCompositeOperationSubKey.class.getModifiers()));
    assertTrue(Modifier.isFinal(OwnedByCompositeOperationSubKey.class.getModifiers()));
  }

  @Test
  public void constructor_andBackingFields_matchSource() throws Exception {
    Constructor<OwnedByCompositeOperationSubKey> constructor = OwnedByCompositeOperationSubKey.class
        .getConstructor(OperationOwningComposite.class, String.class);
    Field compositeField = OwnedByCompositeOperationSubKey.class.getDeclaredField("composite");
    Field textField = OwnedByCompositeOperationSubKey.class.getDeclaredField("text");
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(OperationOwningComposite.class, compositeField.getType());
    assertEquals(String.class, textField.getType());
    assertTrue(Modifier.isPrivate(compositeField.getModifiers()));
    assertTrue(Modifier.isPrivate(textField.getModifiers()));
    assertTrue(Modifier.isFinal(compositeField.getModifiers()));
    assertTrue(Modifier.isFinal(textField.getModifiers()));
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
  public void toString_isNonNullAndContainsClassName() {
    assertNotNull(keyA.toString());
    assertFalse(keyA.toString().isEmpty());
    assertTrue(keyA.toString().contains(OwnedByCompositeOperationSubKey.class.getName()));
  }

  @Test
  public void getterValues_canBeRecoveredReflectively() throws Exception {
    Field compositeField = OwnedByCompositeOperationSubKey.class.getDeclaredField("composite");
    Field textField = OwnedByCompositeOperationSubKey.class.getDeclaredField("text");
    compositeField.setAccessible(true);
    textField.setAccessible(true);
    assertNull(compositeField.get(keyA));
    assertEquals("open", textField.get(keyA));
  }

  @Test
  public void hashCode_withNonNullComposite_usesCompositeAndText() {
    OperationOwningComposite<?> composite = createComposite("alpha");
    OwnedByCompositeOperationSubKey key = new OwnedByCompositeOperationSubKey(composite, "launch");
    int expected = 17;
    expected = (37 * expected) + composite.hashCode();
    expected = (37 * expected) + "launch".hashCode();
    assertEquals(expected, key.hashCode());
  }

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

  @SuppressWarnings("unchecked")
  private OperationOwningComposite<?> createComposite(String name) {
    return (OperationOwningComposite<?>) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[]{OperationOwningComposite.class}, new CompositeInvocationHandler(name));
  }

  private static final class CompositeInvocationHandler implements InvocationHandler {
    private final String name; private final UUID cardId = CroquetTestUtils.nextTestUUID();
    private CompositeInvocationHandler(String name) { this.name = name; }
    @Override public Object invoke(Object proxy, Method method, Object[] args) {
      String methodName = method.getName();
      if ("equals".equals(methodName)) return proxy == args[0];
      if ("hashCode".equals(methodName)) return name.hashCode();
      if ("toString".equals(methodName)) return "CompositeProxy[" + name + "]";
      if ("getCardId".equals(methodName)) return cardId;
      if ("modifyNameIfNecessary".equals(methodName)) return args[0];
      if ("contains".equals(methodName)) return false;
      if (method.getReturnType() == boolean.class) return false;
      return null;
    }
  }
}
