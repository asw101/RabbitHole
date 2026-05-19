package edu.cmu.cs.dennisc.issue;

import org.junit.Test;

import static org.junit.Assert.*;

public class IssueTypeTest {

  @Test
  public void hasThreeValues() {
    assertEquals(3, IssueType.values().length);
  }

  @Test
  public void containsBug() {
    assertNotNull(IssueType.BUG);
  }

  @Test
  public void containsImprovement() {
    assertNotNull(IssueType.IMPROVEMENT);
  }

  @Test
  public void containsNewFeature() {
    assertNotNull(IssueType.NEW_FEATURE);
  }

  @Test
  public void valueOf_bug() {
    assertEquals(IssueType.BUG, IssueType.valueOf("BUG"));
  }

  @Test
  public void valueOf_improvement() {
    assertEquals(IssueType.IMPROVEMENT, IssueType.valueOf("IMPROVEMENT"));
  }

  @Test
  public void valueOf_newFeature() {
    assertEquals(IssueType.NEW_FEATURE, IssueType.valueOf("NEW_FEATURE"));
  }

  @Test
  public void name_returnsCorrectStrings() {
    assertEquals("BUG", IssueType.BUG.name());
    assertEquals("IMPROVEMENT", IssueType.IMPROVEMENT.name());
    assertEquals("NEW_FEATURE", IssueType.NEW_FEATURE.name());
  }

  @Test
  public void ordinal_isSequential() {
    assertEquals(0, IssueType.BUG.ordinal());
    assertEquals(1, IssueType.IMPROVEMENT.ordinal());
    assertEquals(2, IssueType.NEW_FEATURE.ordinal());
  }

  @Test
  public void values_areInDeclarationOrder() {
    IssueType[] values = IssueType.values();

    assertSame(IssueType.BUG, values[0]);
    assertSame(IssueType.IMPROVEMENT, values[1]);
    assertSame(IssueType.NEW_FEATURE, values[2]);
  }

  @Test
  public void toString_matchesNameForEveryValue() {
    for (IssueType issueType : IssueType.values()) {
      assertEquals(issueType.name(), issueType.toString());
    }
  }

  @Test
  public void compareTo_respectsOrdinalOrder() {
    assertTrue(IssueType.BUG.compareTo(IssueType.IMPROVEMENT) < 0);
    assertTrue(IssueType.IMPROVEMENT.compareTo(IssueType.NEW_FEATURE) < 0);
  }
}
