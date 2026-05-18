package org.lgna.issue.swing;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JExceptionSubPaneTest {
  @BeforeClass
  public static void setHeadless() {
    System.setProperty("java.awt.headless", "true");
  }

  @Test
  public void constructorBuildsLabelsFromThrowableAndStackTrace() {
    Thread thread = new Thread("ui-thread");
    IllegalArgumentException originalThrowable = new IllegalArgumentException("bad input");
    originalThrowable.setStackTrace(new StackTraceElement[]{
        new StackTraceElement("example.Type", "run", "Example.java", 23)
    });
    Throwable targetThrowable = new RuntimeException("target");

    JExceptionSubPane pane = new JExceptionSubPane(thread, originalThrowable, targetThrowable);

    assertSame(thread, pane.getThread());
    assertSame(originalThrowable, pane.getOriginalThrowable());
    assertSame(targetThrowable, pane.getOriginalThrowableOrTarget());
    assertTrue(pane.getLayout() instanceof BoxLayout);
    assertEquals(5, pane.getComponentCount());
    assertEquals("IllegalArgumentException[bad input] in Thread[ui-thread]", ((JLabel) pane.getComponent(0)).getText());
    assertEquals("class: example.Type", ((JLabel) pane.getComponent(1)).getText());
    assertEquals("method: run", ((JLabel) pane.getComponent(2)).getText());
    assertEquals("in file Example.java at line number 23", ((JLabel) pane.getComponent(3)).getText());
    assertEquals("<html><u>show complete stack trace...</u></html>", ((JLabel) pane.getComponent(4)).getText());
  }

  @Test
  public void constructorOmitsStackMetadataWhenThrowableHasNoStackTrace() {
    Thread thread = new Thread("worker-thread");
    RuntimeException originalThrowable = new RuntimeException();
    originalThrowable.setStackTrace(new StackTraceElement[0]);

    JExceptionSubPane pane = new JExceptionSubPane(thread, originalThrowable, originalThrowable);

    assertEquals(2, pane.getComponentCount());
    assertEquals("RuntimeException in Thread[worker-thread]", ((JLabel) pane.getComponent(0)).getText());
    assertEquals("<html><u>show complete stack trace...</u></html>", ((JLabel) pane.getComponent(1)).getText());
  }
}
