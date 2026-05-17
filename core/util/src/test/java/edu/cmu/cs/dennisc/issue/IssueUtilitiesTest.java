package edu.cmu.cs.dennisc.issue;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class IssueUtilitiesTest {

  @Test
  public void getSystemPropertiesForEnvironmentField_notEmpty() {
    List<String> props = IssueUtilities.getSystemPropertiesForEnvironmentField();
    assertFalse(props.isEmpty());
    assertTrue(props.contains("java.version"));
    assertTrue(props.contains("os.name"));
    assertTrue(props.contains("os.arch"));
  }

  @Test
  public void getSystemPropertiesForEnvironmentField_unmodifiable() {
    List<String> props = IssueUtilities.getSystemPropertiesForEnvironmentField();
    try {
      props.add("should.fail");
      fail("Should throw UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void getEnvironmentLongDescription_containsProperties() {
    String desc = IssueUtilities.getEnvironmentLongDescription();
    assertNotNull(desc);
    assertTrue(desc.contains("java.version:"));
    assertTrue(desc.contains("os.name:"));
    assertTrue(desc.contains("os.arch:"));
  }

  @Test
  public void getEnvironmentShortDescription_notEmpty() {
    String desc = IssueUtilities.getEnvironmentShortDescription();
    assertNotNull(desc);
    assertFalse(desc.isEmpty());
    assertTrue(desc.contains(";"));
  }

  @Test
  public void getThrowableText_withThrowable() {
    String text = IssueUtilities.getThrowableText(new RuntimeException("test error"));
    assertNotNull(text);
    assertTrue(text.contains("RuntimeException"));
    assertTrue(text.contains("test error"));
  }

  @Test
  public void getThrowableText_null() {
    String text = IssueUtilities.getThrowableText(null);
    assertEquals("", text);
  }

  @Test
  public void getThrowableText_withCause() {
    Exception cause = new IllegalArgumentException("root cause");
    Exception wrapper = new RuntimeException("wrapper", cause);
    String text = IssueUtilities.getThrowableText(wrapper);
    assertTrue(text.contains("wrapper"));
    assertTrue(text.contains("root cause"));
  }
}
