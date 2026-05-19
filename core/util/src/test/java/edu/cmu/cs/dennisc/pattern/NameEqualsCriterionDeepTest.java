package edu.cmu.cs.dennisc.pattern;

import org.junit.Test;

import static org.junit.Assert.*;

public class NameEqualsCriterionDeepTest {

  private static class TestNameable implements Nameable {
    private String name;

    TestNameable(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void setName(String name) {
      this.name = name;
    }
  }

  @Test
  public void caseSensitive_exactMatch() {
    NameEqualsCriterion c = new NameEqualsCriterion("Alice", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertTrue(c.accept(new TestNameable("Alice")));
  }

  @Test
  public void caseSensitive_differentCase_noMatch() {
    NameEqualsCriterion c = new NameEqualsCriterion("Alice", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertFalse(c.accept(new TestNameable("alice")));
    assertFalse(c.accept(new TestNameable("ALICE")));
  }

  @Test
  public void caseInsensitive_matchesDifferentCase() {
    NameEqualsCriterion c = new NameEqualsCriterion("Alice", NameCriterion.IS_NOT_SENSITIVE_TO_CASE);
    assertTrue(c.accept(new TestNameable("alice")));
    assertTrue(c.accept(new TestNameable("ALICE")));
    assertTrue(c.accept(new TestNameable("Alice")));
  }

  @Test
  public void nonNameable_rejected() {
    NameEqualsCriterion c = new NameEqualsCriterion("test", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertFalse(c.accept("not a nameable"));
    assertFalse(c.accept(42));
  }

  @Test
  public void nullName_matchesNullTarget() {
    NameEqualsCriterion c = new NameEqualsCriterion(null, NameCriterion.IS_SENSITIVE_TO_CASE);
    assertTrue(c.accept(new TestNameable(null)));
  }

  @Test
  public void nullName_doesNotMatchNonNullTarget() {
    NameEqualsCriterion c = new NameEqualsCriterion("target", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertFalse(c.accept(new TestNameable(null)));
  }

  @Test
  public void nonNullName_doesNotMatchNullTarget() {
    NameEqualsCriterion c = new NameEqualsCriterion(null, NameCriterion.IS_SENSITIVE_TO_CASE);
    assertFalse(c.accept(new TestNameable("hello")));
  }

  @Test
  public void emptyString_matchesEmpty() {
    NameEqualsCriterion c = new NameEqualsCriterion("", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertTrue(c.accept(new TestNameable("")));
    assertFalse(c.accept(new TestNameable("a")));
  }

  @Test
  public void caseSensitive_substringDoesNotMatch() {
    NameEqualsCriterion c = new NameEqualsCriterion("test", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertFalse(c.accept(new TestNameable("testing")));
    assertFalse(c.accept(new TestNameable("atest")));
  }
}
