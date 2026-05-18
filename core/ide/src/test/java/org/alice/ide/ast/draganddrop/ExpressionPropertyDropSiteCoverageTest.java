package org.alice.ide.ast.draganddrop;

import org.junit.Test;
import org.lgna.croquet.DropSite;
import org.lgna.project.ast.ExpressionProperty;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ExpressionPropertyDropSite}.
 * Tests equals/hashCode contract, null property handling,
 * toString format, DropSite interface compliance, structural
 * verification, and BinaryDecoder constructor presence.
 *
 * Complements the basic tests in ExpressionPropertyDropSiteTest.
 * Pure behavioral — no GUI dependency.
 */
public class ExpressionPropertyDropSiteCoverageTest {

  private final ExpressionPropertyDropSite nullDropSite =
      new ExpressionPropertyDropSite((ExpressionProperty) null);

  // ── DropSite interface compliance ──────────────────────────────

  @Test
  public void implementsDropSiteInterface() {
    assertTrue("ExpressionPropertyDropSite must implement DropSite",
        DropSite.class.isAssignableFrom(ExpressionPropertyDropSite.class));
  }

  // ── Constructor with null property ─────────────────────────────

  @Test
  public void constructorAcceptsNull() {
    assertNotNull("Should be constructible with null property", nullDropSite);
  }

  @Test
  public void getExpressionProperty_returnsNullWhenConstructedWithNull() {
    assertNull("getExpressionProperty should return null", nullDropSite.getExpressionProperty());
  }

  // ── Equals: reflexive ──────────────────────────────────────────

  @Test
  public void equals_reflexive() {
    assertEquals("Reflexive: x.equals(x) must be true", nullDropSite, nullDropSite);
  }

  // ── Equals: symmetric ─────────────────────────────────────────

  @Test
  public void equals_symmetric_bothNull() {
    ExpressionPropertyDropSite other = new ExpressionPropertyDropSite((ExpressionProperty) null);
    assertEquals("Symmetric: a.equals(b)", nullDropSite, other);
    assertEquals("Symmetric: b.equals(a)", other, nullDropSite);
  }

  // ── Equals: transitivity ──────────────────────────────────────

  @Test
  public void equals_transitive_allNull() {
    ExpressionPropertyDropSite b = new ExpressionPropertyDropSite((ExpressionProperty) null);
    ExpressionPropertyDropSite c = new ExpressionPropertyDropSite((ExpressionProperty) null);
    assertEquals("a.equals(b)", nullDropSite, b);
    assertEquals("b.equals(c)", b, c);
    assertEquals("Transitive: a.equals(c)", nullDropSite, c);
  }

  // ── Equals: null ───────────────────────────────────────────────

  @Test
  public void equals_nullReturnsFalse() {
    assertNotEquals("x.equals(null) must be false", nullDropSite, null);
  }

  // ── Equals: different type ─────────────────────────────────────

  @Test
  public void equals_differentTypeReturnsFalse() {
    assertNotEquals("Different type should not be equal", nullDropSite, "a string");
  }

  @Test
  public void equals_selfReferenceViaCast() {
    Object same = nullDropSite;
    assertEquals("Same reference via Object should be equal", nullDropSite, same);
  }

  // ── HashCode consistency ───────────────────────────────────────

  @Test
  public void hashCode_consistentAcrossMultipleCalls() {
    int hash1 = nullDropSite.hashCode();
    int hash2 = nullDropSite.hashCode();
    assertEquals("hashCode must be consistent across calls", hash1, hash2);
  }

  @Test
  public void hashCode_equalObjectsHaveEqualHashes() {
    ExpressionPropertyDropSite other = new ExpressionPropertyDropSite((ExpressionProperty) null);
    assertEquals("Equal objects must have same hashCode", nullDropSite.hashCode(), other.hashCode());
  }

  @Test
  public void hashCode_nullProperty_returns17() {
    assertEquals("Null property should give hashCode = 17", 17, nullDropSite.hashCode());
  }

  // ── toString ───────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    assertTrue("toString should contain class name",
        nullDropSite.toString().contains("ExpressionPropertyDropSite"));
  }

  @Test
  public void toString_containsExpressionPropertyLabel() {
    assertTrue("toString should contain 'expressionProperty='",
        nullDropSite.toString().contains("expressionProperty="));
  }

  @Test
  public void toString_nullPropertyShowsNull() {
    assertTrue("toString with null property should contain 'null'",
        nullDropSite.toString().contains("null"));
  }

  @Test
  public void toString_endsWithBracket() {
    assertTrue("toString should end with ']'", nullDropSite.toString().endsWith("]"));
  }

  // ── Structural verification ────────────────────────────────────

  @Test
  public void hasExpressionPropertyField() {
    org.alice.ide.ast.ReflectionTestHelper.assertFieldIsPrivateFinal(
        ExpressionPropertyDropSite.class, "expressionProperty");
  }

  @Test
  public void hasGetExpressionPropertyMethod() throws Exception {
    Method method = ExpressionPropertyDropSite.class.getMethod("getExpressionProperty");
    assertNotNull(method);
    assertEquals("getExpressionProperty should return ExpressionProperty",
        ExpressionProperty.class, method.getReturnType());
  }

  @Test
  public void hasEncodeMethod() throws Exception {
    // DropSite contract requires encode(BinaryEncoder)
    boolean found = Arrays.stream(ExpressionPropertyDropSite.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("encode") && m.getParameterCount() == 1);
    assertTrue("Must have encode method from DropSite interface", found);
  }

  @Test
  public void hasBinaryDecoderConstructor() {
    boolean found = Arrays.stream(ExpressionPropertyDropSite.class.getDeclaredConstructors())
        .anyMatch(c -> {
          Class<?>[] params = c.getParameterTypes();
          return params.length == 1
              && params[0].getSimpleName().equals("BinaryDecoder");
        });
    assertTrue("Must have BinaryDecoder constructor for deserialization", found);
  }

  @Test
  public void hasExpressionPropertyConstructor() {
    boolean found = Arrays.stream(ExpressionPropertyDropSite.class.getDeclaredConstructors())
        .anyMatch(c -> {
          Class<?>[] params = c.getParameterTypes();
          return params.length == 1
              && ExpressionProperty.class.isAssignableFrom(params[0]);
        });
    assertTrue("Must have ExpressionProperty constructor", found);
  }

  @Test
  public void hasGetOwningDropReceptorMethod() throws Exception {
    Method method = ExpressionPropertyDropSite.class.getMethod("getOwningDropReceptor");
    assertNotNull("Must have getOwningDropReceptor from DropSite", method);
  }

  // ── Class is not abstract or final ─────────────────────────────

  @Test
  public void isConcreteClass() {
    assertFalse("ExpressionPropertyDropSite should not be abstract",
        Modifier.isAbstract(ExpressionPropertyDropSite.class.getModifiers()));
  }

  @Test
  public void isPublicClass() {
    assertTrue("ExpressionPropertyDropSite should be public",
        Modifier.isPublic(ExpressionPropertyDropSite.class.getModifiers()));
  }

  // ── Method count check ─────────────────────────────────────────

  @Test
  public void overridesEqualsHashCodeToString() {
    boolean hasEquals = false;
    boolean hasHashCode = false;
    boolean hasToString = false;
    for (Method m : ExpressionPropertyDropSite.class.getDeclaredMethods()) {
      if ("equals".equals(m.getName()) && m.getParameterCount() == 1) hasEquals = true;
      if ("hashCode".equals(m.getName()) && m.getParameterCount() == 0) hasHashCode = true;
      if ("toString".equals(m.getName()) && m.getParameterCount() == 0) hasToString = true;
    }
    assertTrue("Must override equals", hasEquals);
    assertTrue("Must override hashCode", hasHashCode);
    assertTrue("Must override toString", hasToString);
  }
}
