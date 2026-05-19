package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.UUID;

import static org.junit.Assert.*;

public class OwnedByCompositeOperationSubKeyTest {
  private OperationOwningComposite<?> compositeAlpha;
  private OperationOwningComposite<?> compositeBeta;
  private OwnedByCompositeOperationSubKey base;
  private OwnedByCompositeOperationSubKey sameAsBase;
  private OwnedByCompositeOperationSubKey transitive;

  @Before
  public void setUp() {
    compositeAlpha = createComposite("alpha");
    compositeBeta = createComposite("beta");
    base = new OwnedByCompositeOperationSubKey(compositeAlpha, "launch");
    sameAsBase = new OwnedByCompositeOperationSubKey(compositeAlpha, "launch");
    transitive = new OwnedByCompositeOperationSubKey(compositeAlpha, "launch");
  }

  // ── Class structure ───────────────────────────────────────────────

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

  // ── Getters and equality ──────────────────────────────────────────

  @Test
  public void getters_returnConstructorArguments() {
    assertSame(compositeAlpha, base.getComposite());
    assertEquals("launch", base.getText());
  }

  @Test
  public void getters_allowNullValues() {
    OwnedByCompositeOperationSubKey key = new OwnedByCompositeOperationSubKey(null, null);
    assertNull(key.getComposite());
    assertNull(key.getText());
  }

  @Test
  public void equals_isReflexiveSymmetricAndTransitive() {
    assertEquals(base, base);
    assertEquals(base, sameAsBase);
    assertEquals(sameAsBase, base);
    assertEquals(sameAsBase, transitive);
    assertEquals(base, transitive);
  }

  @Test
  public void equals_rejectsDifferentCompositeTextNullAndType() {
    assertNotEquals(base, new OwnedByCompositeOperationSubKey(compositeBeta, "launch"));
    assertNotEquals(base, new OwnedByCompositeOperationSubKey(compositeAlpha, "configure"));
    assertNotEquals(base, null);
    assertNotEquals(base, "launch");
  }

  @Test
  public void equals_handlesNullMembers() {
    OwnedByCompositeOperationSubKey left = new OwnedByCompositeOperationSubKey(null, null);
    OwnedByCompositeOperationSubKey right = new OwnedByCompositeOperationSubKey(null, null);
    OwnedByCompositeOperationSubKey withText = new OwnedByCompositeOperationSubKey(null, "launch");
    assertEquals(left, right);
    assertNotEquals(left, withText);
  }

  // ── hashCode and representation ───────────────────────────────────

  @Test
  public void hashCode_matchesForEqualObjects_andIsStable() {
    assertEquals(base.hashCode(), sameAsBase.hashCode());
    assertEquals(base.hashCode(), transitive.hashCode());
    assertEquals(base.hashCode(), base.hashCode());
  }

  @Test
  public void hashCode_usesExpectedFormulaForNullState() {
    assertEquals(17, new OwnedByCompositeOperationSubKey(null, null).hashCode());
  }

  @Test
  public void hashCode_usesCompositeAndTextContributions() {
    int expected = 17;
    expected = (37 * expected) + compositeAlpha.hashCode();
    expected = (37 * expected) + "launch".hashCode();
    assertEquals(expected, base.hashCode());
  }

  @Test
  public void toString_isNonNullAndContainsClassName() {
    String representation = base.toString();
    assertNotNull(representation);
    assertFalse(representation.isEmpty());
    assertTrue(representation.contains(OwnedByCompositeOperationSubKey.class.getName()));
  }

  @Test
  public void getterValues_canBeRecoveredReflectively() throws Exception {
    Field compositeField = OwnedByCompositeOperationSubKey.class.getDeclaredField("composite");
    Field textField = OwnedByCompositeOperationSubKey.class.getDeclaredField("text");
    compositeField.setAccessible(true);
    textField.setAccessible(true);
    assertSame(compositeAlpha, compositeField.get(base));
    assertEquals("launch", textField.get(base));
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
