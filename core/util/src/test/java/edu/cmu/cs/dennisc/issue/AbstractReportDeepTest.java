package edu.cmu.cs.dennisc.issue;

import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractReportDeepTest {

  private static class ConcreteReport extends AbstractReport {
  }

  @Test
  public void addAttachment_addsToList() {
    ConcreteReport report = new ConcreteReport();
    Attachment attachment = new StackTraceAttachment(new RuntimeException("test"));

    report.addAttachment(attachment);

    assertEquals(1, report.getAttachments().size());
    assertSame(attachment, report.getAttachments().get(0));
  }

  @Test
  public void removeAttachment_removesFromList() {
    ConcreteReport report = new ConcreteReport();
    Attachment attachment = new StackTraceAttachment(new RuntimeException("test"));

    report.addAttachment(attachment);
    report.removeAttachment(attachment);

    assertTrue(report.getAttachments().isEmpty());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getAttachments_returnsUnmodifiableList() {
    ConcreteReport report = new ConcreteReport();

    report.addAttachment(new StackTraceAttachment(new RuntimeException()));
    report.getAttachments().add(new StackTraceAttachment(new RuntimeException()));
  }

  @Test
  public void multipleAttachments_maintainOrder() {
    ConcreteReport report = new ConcreteReport();
    StackTraceAttachment first = new StackTraceAttachment(new RuntimeException("1"));
    StackTraceAttachment second = new StackTraceAttachment(new RuntimeException("2"));
    StackTraceAttachment third = new StackTraceAttachment(new RuntimeException("3"));

    report.addAttachment(first);
    report.addAttachment(second);
    report.addAttachment(third);

    assertEquals(3, report.getAttachments().size());
    assertSame(first, report.getAttachments().get(0));
    assertSame(second, report.getAttachments().get(1));
    assertSame(third, report.getAttachments().get(2));
  }

  @Test
  public void removeNonExistentAttachment_isNoOp() {
    ConcreteReport report = new ConcreteReport();
    StackTraceAttachment first = new StackTraceAttachment(new RuntimeException("1"));
    StackTraceAttachment second = new StackTraceAttachment(new RuntimeException("2"));

    report.addAttachment(first);
    report.removeAttachment(second);

    assertEquals(1, report.getAttachments().size());
  }

  @Test
  public void emptyReport_hasNoAttachments() {
    ConcreteReport report = new ConcreteReport();

    assertNotNull(report.getAttachments());
    assertTrue(report.getAttachments().isEmpty());
  }

  @Test
  public void addAndRemoveMultiple() {
    ConcreteReport report = new ConcreteReport();
    StackTraceAttachment first = new StackTraceAttachment(new RuntimeException("1"));
    StackTraceAttachment second = new StackTraceAttachment(new RuntimeException("2"));

    report.addAttachment(first);
    report.addAttachment(second);
    report.removeAttachment(first);

    assertEquals(1, report.getAttachments().size());
    assertSame(second, report.getAttachments().get(0));
  }

  @Test
  public void addNullAttachment_addsNullEntry() {
    ConcreteReport report = new ConcreteReport();

    report.addAttachment(null);

    assertEquals(1, report.getAttachments().size());
    assertNull(report.getAttachments().get(0));
  }

  @Test
  public void removeAttachment_fromEmptyReportIsNoOp() {
    ConcreteReport report = new ConcreteReport();

    report.removeAttachment(new StackTraceAttachment(new RuntimeException("unused")));

    assertTrue(report.getAttachments().isEmpty());
  }

  @Test
  public void duplicateAttachments_areStoredSeparately() {
    ConcreteReport report = new ConcreteReport();
    StackTraceAttachment attachment = new StackTraceAttachment(new RuntimeException("dup"));

    report.addAttachment(attachment);
    report.addAttachment(attachment);

    assertEquals(2, report.getAttachments().size());
    assertSame(attachment, report.getAttachments().get(0));
    assertSame(attachment, report.getAttachments().get(1));
  }

  @Test
  public void removeAttachment_onlyRemovesFirstDuplicate() {
    ConcreteReport report = new ConcreteReport();
    StackTraceAttachment attachment = new StackTraceAttachment(new RuntimeException("dup"));

    report.addAttachment(attachment);
    report.addAttachment(attachment);
    report.removeAttachment(attachment);

    assertEquals(1, report.getAttachments().size());
    assertSame(attachment, report.getAttachments().get(0));
  }
}
