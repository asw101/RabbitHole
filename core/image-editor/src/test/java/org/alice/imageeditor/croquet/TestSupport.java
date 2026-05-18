package org.alice.imageeditor.croquet;

import org.alice.imageeditor.croquet.views.ImageEditorPane;
import org.lgna.croquet.Application;
import org.lgna.croquet.DocumentFrame;
import org.lgna.croquet.Operation;
import org.lgna.croquet.history.UserActivity;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public final class TestSupport {
  private TestSupport() {
  }

  public interface ThrowingRunnable {
    void run() throws Exception;
  }

  public interface ThrowingSupplier<T> {
    T get() throws Exception;
  }

  private static final class TestApplication extends Application<DocumentFrame> {
    @Override
    public DocumentFrame getDocumentFrame() {
      return null;
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
    }

    @Override
    public String getApplicationSubPath() {
      return "image-editor-tests";
    }
  }

  public static class HeadlessImageEditorPane extends ImageEditorPane {
    public HeadlessImageEditorPane(ImageEditorFrame composite) {
      super(composite);
    }

    @Override
    public void setDefaultButtonToSave() {
    }

    @Override
    public void setDefaultButtonToCrop() {
    }
  }

  public static class HeadlessImageEditorFrame extends ImageEditorFrame {
    @Override
    protected ImageEditorPane createView() {
      return new HeadlessImageEditorPane(this);
    }
  }

  public static synchronized void ensureApplication() {
    System.setProperty("java.awt.headless", "true");
    if (Application.getActiveInstance() == null) {
      new TestApplication();
    }
  }

  public static void onEdt(ThrowingRunnable runnable) throws Exception {
    callOnEdt(() -> {
      runnable.run();
      return null;
    });
  }

  @SuppressWarnings("unchecked")
  public static <T> T callOnEdt(ThrowingSupplier<T> supplier) throws Exception {
    ensureApplication();
    if (SwingUtilities.isEventDispatchThread()) {
      return supplier.get();
    }
    final Object[] valueHolder = new Object[1];
    final Throwable[] throwableHolder = new Throwable[1];
    SwingUtilities.invokeAndWait(() -> {
      try {
        valueHolder[0] = supplier.get();
      } catch (Throwable throwable) {
        throwableHolder[0] = throwable;
      }
    });
    if (throwableHolder[0] != null) {
      rethrow(throwableHolder[0]);
    }
    return (T) valueHolder[0];
  }

  public static File createEmptyDirectory(String name) throws Exception {
    File base = new File("target");
    if (!base.isDirectory()) {
      base = new File("core/image-editor/target");
    }
    File dir = new File(base, "test-data/" + name);
    deleteRecursively(dir.toPath());
    Files.createDirectories(dir.toPath());
    return dir;
  }

  public static JTextField getEditorTextField(JComboBox comboBox) {
    return (JTextField) comboBox.getEditor().getEditorComponent();
  }

  public static Object invokeDeclared(Class<?> type, Object target, String name, Class<?>[] parameterTypes, Object... args) throws Exception {
    Method method = type.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return method.invoke(target, args);
  }

  public static Object getField(Class<?> type, Object target, String name) throws Exception {
    Field field = type.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(target);
  }

  public static void setField(Class<?> type, Object target, String name, Object value) throws Exception {
    Field field = type.getDeclaredField(name);
    field.setAccessible(true);
    field.set(target, value);
  }

  private static void deleteRecursively(Path path) throws Exception {
    if (!Files.exists(path)) {
      return;
    }
    try (java.util.stream.Stream<Path> stream = Files.walk(path)) {
      stream.sorted(Comparator.reverseOrder()).forEach(current -> {
        try {
          Files.deleteIfExists(current);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  private static void rethrow(Throwable throwable) throws Exception {
    if (throwable instanceof Exception exception) {
      throw exception;
    }
    if (throwable instanceof Error error) {
      throw error;
    }
    throw new RuntimeException(throwable);
  }
}
