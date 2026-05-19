package edu.cmu.cs.dennisc.pattern;

import org.junit.Test;

import static org.junit.Assert.*;

public class NameCriterionDeepTest {

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
  public void nameEqualsCriterion_constants() {
    assertTrue(NameCriterion.IS_SENSITIVE_TO_CASE);
    assertFalse(NameCriterion.IS_NOT_SENSITIVE_TO_CASE);
  }

  @Test
  public void nameCriterion_rejectsNonNameable_withInteger() {
    NameEqualsCriterion c = new NameEqualsCriterion("test", true);
    assertFalse(c.accept(123));
  }

  @Test
  public void nameCriterion_rejectsNonNameable_withNull() {
    NameEqualsCriterion c = new NameEqualsCriterion("test", true);
    assertFalse(c.accept(null));
  }

  @Test
  public void nameEndsWithCriterion_acceptsOnlyMatchingNames() {
    NameEndsWithCriterion c = new NameEndsWithCriterion(".java", true);
    assertTrue(c.accept(new TestNameable("Main.java")));
    assertFalse(c.accept(new TestNameable("Main.class")));
  }

  @Test
  public void nameEqualsCriterion_caseInsensitive_symmetric() {
    NameEqualsCriterion c = new NameEqualsCriterion("Hello", false);
    assertTrue(c.accept(new TestNameable("hello")));
    assertTrue(c.accept(new TestNameable("HELLO")));
    assertTrue(c.accept(new TestNameable("hElLo")));
  }

  @Test
  public void nameEndsWithCriterion_caseInsensitive_differentSuffixes() {
    NameEndsWithCriterion c = new NameEndsWithCriterion(".XML", false);
    assertTrue(c.accept(new TestNameable("config.xml")));
    assertTrue(c.accept(new TestNameable("data.XML")));
    assertTrue(c.accept(new TestNameable("pom.Xml")));
  }
}
