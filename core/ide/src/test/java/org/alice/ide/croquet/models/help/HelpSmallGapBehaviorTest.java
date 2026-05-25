package org.alice.ide.croquet.models.help;

import org.alice.ide.croquet.models.help.views.GraphicsHelpView;
import org.alice.ide.croquet.models.help.views.ShowAllSystemPropertiesView;
import org.alice.ide.croquet.models.help.views.ShowPathPropertyView;
import org.junit.Test;

import javax.swing.SwingUtilities;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class HelpSmallGapBehaviorTest {
  private static <T> T onEdt(ThrowingSupplier<T> supplier) throws Exception {
    AtomicReference<T> value = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> {
      try {
        value.set(supplier.get());
      } catch (Throwable throwable) {
        failure.set(throwable);
      }
    });
    if (failure.get() != null) {
      if (failure.get() instanceof Exception exception) {
        throw exception;
      }
      throw new RuntimeException(failure.get());
    }
    return value.get();
  }

  @FunctionalInterface
  private interface ThrowingSupplier<T> {
    T get() throws Exception;
  }

  @Test
  public void pathPropertyCompositesExposePropertyNamesDialogTitlesAndViews() throws Exception {
    ShowClassPathPropertyComposite classPath = new ShowClassPathPropertyComposite();
    ShowLibraryPathPropertyComposite libraryPath = new ShowLibraryPathPropertyComposite();

    assertEquals("java.class.path", classPath.getPropertyName());
    assertEquals("System Property: java.class.path", classPath.getDialogTitle());
    assertTrue(onEdt(classPath::createView) instanceof ShowPathPropertyView);

    assertEquals("java.library.path", libraryPath.getPropertyName());
    assertEquals("System Property: java.library.path", libraryPath.getDialogTitle());
    assertTrue(onEdt(libraryPath::createView) instanceof ShowPathPropertyView);
  }

  @Test
  public void helpCompositesCreateExpectedNonGuiViews() throws Exception {
    assertTrue(onEdt(new ShowAllSystemPropertiesComposite()::createView) instanceof ShowAllSystemPropertiesView);
    assertTrue(onEdt(new GraphicsHelpComposite()::createView) instanceof GraphicsHelpView);
  }

  @Test
  public void reportIssueCompositeExposesDefaultNonGuiStateAndDerivedFlags() {
    ReportIssueComposite composite = new ReportIssueComposite();

    assertEquals(BugSubmitVisibility.PRIVATE, composite.getVisibilityState().getValue());
    assertNull(composite.getAttachmentState().getValue());
    assertNull(composite.getReportTypeState().getValue());
    assertFalse(composite.isPublic());
    assertFalse(composite.isProjectAttachmentDesired());
    assertNull(composite.getDefaultTitleText());
    assertNull(composite.getThread());
    assertNull(composite.getThrowable());
    assertNotNull(composite.getBrowserOperation());
    assertNotNull(composite.getReportBugLaunchOperation());
    assertNotNull(composite.getSubmitBugOperation());
  }

  @Test
  public void bugSubmitEnumsPreserveCompactSourceOrder() {
    assertArrayEquals(new BugSubmitAttachment[] {BugSubmitAttachment.YES, BugSubmitAttachment.NO}, BugSubmitAttachment.values());
    assertArrayEquals(new BugSubmitVisibility[] {BugSubmitVisibility.PUBLIC, BugSubmitVisibility.PRIVATE}, BugSubmitVisibility.values());
  }
}
