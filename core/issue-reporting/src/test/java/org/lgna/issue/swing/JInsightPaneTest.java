package org.lgna.issue.swing;

import edu.cmu.cs.dennisc.issue.Issue;
import edu.cmu.cs.dennisc.issue.IssueType;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.text.JTextComponent;
import java.awt.Dimension;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JInsightPaneTest {
  @BeforeClass
  public static void setHeadless() {
    System.setProperty("java.awt.headless", "true");
  }

  @Test
  public void createIssueBuilderUsesEnteredValuesAndOriginalThrowable() throws Exception {
    Thread thread = new Thread("insight-thread");
    IllegalStateException originalThrowable = new IllegalStateException("boom");
    originalThrowable.setStackTrace(new StackTraceElement[]{
        new StackTraceElement("example.Insight", "inspect", "Insight.java", 11)
    });
    Throwable targetThrowable = new RuntimeException("target");
    JInsightPane pane = new JInsightPane(thread, originalThrowable, targetThrowable);

    setText(pane, "descriptionTextArea", "Detailed description");
    setText(pane, "stepsTextArea", "Step 1 -> Step 2");
    setText(pane, "reportedByTextField", "Alice Tester");
    setText(pane, "emailAddressTextField", "alice@example.com");

    Issue issue = pane.createIssueBuilder().build();

    assertEquals(IssueType.BUG, issue.getType());
    assertEquals("Detailed description", issue.getDescription());
    assertEquals("Step 1 -> Step 2", issue.getSteps());
    assertEquals("Alice Tester", issue.getReportedBy());
    assertEquals("alice@example.com", issue.getEmailAddress());
    assertSame(thread, issue.getThread());
    assertSame(originalThrowable, issue.getThrowable());
    assertEquals(new Dimension(0, 0), pane.getPreferredSize());
  }

  @Test
  public void setExpandedTogglesPreferredSizeVisibilityState() {
    JInsightPane pane = new JInsightPane(new Thread("expanded-thread"), new RuntimeException("x"), new RuntimeException("y"));

    assertFalse(pane.isExpanded());
    assertEquals(new Dimension(0, 0), pane.getPreferredSize());

    pane.setExpanded(true);

    assertTrue(pane.isExpanded());
    assertNotEquals(new Dimension(0, 0), pane.getPreferredSize());

    pane.setExpanded(false);

    assertFalse(pane.isExpanded());
    assertEquals(new Dimension(0, 0), pane.getPreferredSize());
  }

  private static void setText(JInsightPane pane, String fieldName, String value) throws Exception {
    JTextComponent textComponent = getField(pane, fieldName, JTextComponent.class);
    textComponent.setText(value);
  }

  private static <T> T getField(Object instance, String fieldName, Class<T> fieldType) throws Exception {
    Field field = instance.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    return fieldType.cast(field.get(instance));
  }
}
