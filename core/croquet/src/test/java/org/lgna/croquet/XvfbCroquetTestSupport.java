package org.lgna.croquet;

import org.junit.Assume;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.undo.UndoHistory;
import org.lgna.croquet.views.Panel;

import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

public final class XvfbCroquetTestSupport {
  private XvfbCroquetTestSupport() {
  }

  public static TestApplication installApplication() {
    Assume.assumeFalse("Xvfb-backed tests require a headed environment", GraphicsEnvironment.isHeadless());
    Application<?> active = Application.getActiveInstance();
    if (active instanceof TestApplication testApplication) {
      ensureDocumentFrameVisible(testApplication);
      return testApplication;
    }
    setSingleton(null);
    TestApplication application = new TestApplication();
    ensureDocumentFrameVisible(application);
    return application;
  }

  private static void ensureDocumentFrameVisible(TestApplication application) {
    onEdt(() -> {
      org.lgna.croquet.views.Frame frame = application.getDocumentFrame().getFrame();
      frame.setSize(360, 240);
      frame.setLocation(120, 120);
      if (!frame.isVisible()) {
        frame.setVisible(true);
      }
      return null;
    });
  }

  public static void flushEdt() {
    onEdt(() -> null);
  }

  public static void tryOnEdt(Runnable runnable) {
    try {
      onEdt(() -> {
        runnable.run();
        return null;
      });
    } catch (Throwable throwable) {
      Assume.assumeNoException("Skipping GUI assertion because the toolkit rejected the operation", throwable);
    }
  }

  public static <T> T onEdt(Callable<T> callable) {
    try {
      if (SwingUtilities.isEventDispatchThread()) {
        return callable.call();
      }
      FutureTask<T> task = new FutureTask<>(callable);
      SwingUtilities.invokeAndWait(task);
      return task.get();
    } catch (Throwable throwable) {
      throw new AssertionError(throwable);
    }
  }

  public static UserActivity newActivity() {
    return new UserActivity();
  }

  private static void setSingleton(Application<?> application) {
    try {
      Field field = Application.class.getDeclaredField("singleton");
      field.setAccessible(true);
      field.set(null, application);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  public static final class TestApplication extends Application<TestDocumentFrame> {
    private final TestDocumentFrame documentFrame = new TestDocumentFrame();

    @Override
    public TestDocumentFrame getDocumentFrame() {
      return this.documentFrame;
    }

    @Override
    protected Operation getAboutOperation() {
      return null;
    }

    @Override
    protected Operation getPreferencesOperation() {
      return null;
    }

    @Override
    protected void handleOpenFiles(List<File> files) {
    }

    @Override
    protected void handleWindowOpened(WindowEvent e) {
    }

    @Override
    public void handleQuit(UserActivity activity) {
      activity.finish();
    }

    @Override
    public String getApplicationSubPath() {
      return "target/test-xvfb-croquet/app";
    }
  }

  public static final class TestDocumentFrame extends DocumentFrame {
    private final TestDocument document = new TestDocument();

    @Override
    public Document getDocument() {
      return this.document;
    }
  }

  public static final class TestDocument implements Document {
    private final UserActivity userActivity = new UserActivity();
    private final Map<Group, UndoHistory> histories = new ConcurrentHashMap<>();

    @Override
    public UserActivity getUserActivity() {
      return this.userActivity;
    }

    @Override
    public UndoHistory getUndoHistory(Group group) {
      return this.histories.computeIfAbsent(group, UndoHistory::new);
    }
  }

  public static class TestPanel extends Panel {
    private final Dimension preferredSize;

    public TestPanel(Composite<?> composite, int width, int height) {
      super(composite);
      this.preferredSize = new Dimension(width, height);
    }

    @Override
    protected LayoutManager createLayoutManager(JPanel jPanel) {
      return new BorderLayout();
    }

    @Override
    protected JPanel createJPanel() {
      return new JPanel() {
        @Override
        public Dimension getPreferredSize() {
          return new Dimension(TestPanel.this.preferredSize);
        }
      };
    }
  }

  public static class TestSimpleComposite extends SimpleComposite<TestPanel> {
    final BooleanState flag;
    private final String repr;
    int preActivationCount;
    int postDeactivationCount;

    public TestSimpleComposite(String repr) {
      super(UUID.randomUUID());
      this.repr = repr;
      this.flag = this.createBooleanState(repr + "Flag", false);
    }

    @Override
    protected TestPanel createView() {
      return new TestPanel(this, 140, 40);
    }

    @Override
    public void appendUserRepr(StringBuilder sb) {
      sb.append(this.repr);
    }

    @Override
    public void handlePreActivation() {
      this.preActivationCount++;
      super.handlePreActivation();
    }

    @Override
    public void handlePostDeactivation() {
      this.postDeactivationCount++;
      super.handlePostDeactivation();
    }
  }

  public static class TestTabComposite extends SimpleTabComposite<TestPanel> {
    private final String repr;

    public TestTabComposite(String repr) {
      super(UUID.randomUUID(), IsCloseable.FALSE);
      this.repr = repr;
    }

    @Override
    protected TestPanel createView() {
      return new TestPanel(this, 180, 60);
    }

    @Override
    public void appendUserRepr(StringBuilder sb) {
      sb.append(this.repr);
    }
  }

  public static class NamedOperation extends Operation {
    private final String name;
    int performCount;

    public NamedOperation(String name) {
      super(Application.DOCUMENT_UI_GROUP, UUID.randomUUID());
      this.name = name;
      this.setName(name);
    }

    @Override
    protected void performInActivity(UserActivity userActivity) {
      this.performCount++;
      userActivity.finish();
    }

    @Override
    protected void localize() {
      this.setName(this.name);
    }
  }

  public static class ClobberingOperation extends NamedOperation {
    public ClobberingOperation(String name) {
      super(name);
    }

    @Override
    public boolean isToolBarTextClobbered() {
      return true;
    }
  }
}
