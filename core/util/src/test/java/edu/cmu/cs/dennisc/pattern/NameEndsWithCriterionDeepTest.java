package edu.cmu.cs.dennisc.pattern;

import org.junit.Test;

import static org.junit.Assert.*;

public class NameEndsWithCriterionDeepTest {

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
  public void caseSensitive_endsWith() {
    NameEndsWithCriterion c = new NameEndsWithCriterion("Test", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertTrue(c.accept(new TestNameable("MyTest")));
    assertTrue(c.accept(new TestNameable("Test")));
  }

  @Test
  public void caseSensitive_doesNotEndWith() {
    NameEndsWithCriterion c = new NameEndsWithCriterion("Test", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertFalse(c.accept(new TestNameable("Mytest")));
    assertFalse(c.accept(new TestNameable("TestFoo")));
  }

  @Test
  public void caseInsensitive_endsWith() {
    NameEndsWithCriterion c = new NameEndsWithCriterion("Test", NameCriterion.IS_NOT_SENSITIVE_TO_CASE);
    assertTrue(c.accept(new TestNameable("Mytest")));
    assertTrue(c.accept(new TestNameable("myTEST")));
  }

  @Test
  public void nonNameable_rejected() {
    NameEndsWithCriterion c = new NameEndsWithCriterion("x", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertFalse(c.accept("just a string"));
  }

  @Test
  public void nullName_nullTarget() {
    NameEndsWithCriterion c = new NameEndsWithCriterion(null, NameCriterion.IS_SENSITIVE_TO_CASE);
    assertTrue(c.accept(new TestNameable(null)));
  }

  @Test
  public void caseInsensitive_nullTarget_nameNotNull() {
    NameEndsWithCriterion c = new NameEndsWithCriterion(null, NameCriterion.IS_NOT_SENSITIVE_TO_CASE);
    assertFalse(c.accept(new TestNameable("hello")));
  }

  @Test
  public void nullName_nonNullTarget() {
    NameEndsWithCriterion c = new NameEndsWithCriterion("target", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertFalse(c.accept(new TestNameable(null)));
  }

  @Test
  public void emptyTarget_matchesAll() {
    NameEndsWithCriterion c = new NameEndsWithCriterion("", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertTrue(c.accept(new TestNameable("anything")));
    assertTrue(c.accept(new TestNameable("")));
  }

  @Test
  public void caseInsensitive_exactMatch() {
    NameEndsWithCriterion c = new NameEndsWithCriterion("Hello", NameCriterion.IS_NOT_SENSITIVE_TO_CASE);
    assertTrue(c.accept(new TestNameable("HELLO")));
  }

  @Test
  public void caseSensitive_partialMatch() {
    NameEndsWithCriterion c = new NameEndsWithCriterion("ing", NameCriterion.IS_SENSITIVE_TO_CASE);
    assertTrue(c.accept(new TestNameable("testing")));
    assertFalse(c.accept(new TestNameable("ingestion")));
  }
}
