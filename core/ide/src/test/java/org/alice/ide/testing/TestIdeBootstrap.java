package org.alice.ide.testing;

import edu.cmu.cs.dennisc.crash.CrashDetector;
import org.alice.ide.ProjectDocument;
import org.alice.ide.ProjectDocumentFrame;
import org.alice.ide.project.ProjectDocumentState;
import org.alice.ide.uricontent.UriProjectLoader;
import org.alice.stageide.StageIDE;
import org.junit.Assume;
import org.lgna.croquet.Application;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public final class TestIdeBootstrap {
  private static final Object LOCK = new Object();
  private static TestStageIDE installedIde;

  private TestIdeBootstrap() {
  }

  public static TestStageIDE ensureInstalled() {
    synchronized (LOCK) {
      Assume.assumeFalse("TestStageIDE requires Xvfb or another headed environment", GraphicsEnvironment.isHeadless());
      configureRootDirectoryProperty();
      if (installedIde == null) {
        installedIde = onEdt(() -> {
          setActiveApplication(null);
          TestStageIDE ide = new TestStageIDE(new CrashDetector(TestIdeBootstrap.class));
          ide.initialize(new String[0]);
          org.lgna.croquet.views.Frame frame = ide.getDocumentFrame().getFrame();
          frame.setSize(640, 480);
          frame.setLocation(80, 80);
          return ide;
        });
      }
      reset();
      return installedIde;
    }
  }

  public static void reset() {
    synchronized (LOCK) {
      if (installedIde == null) {
        setActiveApplication(null);
        ProjectDocumentState.getInstance().setValueTransactionlessly(null);
        clearProjectChangeListeners();
        return;
      }
      onEdt(() -> {
        setActiveApplication(installedIde);
        finishOpenActivities(installedIde);
        ProjectDocumentState.getInstance().setValueTransactionlessly(new ProjectDocument(createMinimalProject(), new UserActivity()));
        injectUriProjectLoader(installedIde, new NewProjectLoader());
        return null;
      });
      clearProjectChangeListeners();
    }
  }

  public static ProjectDocumentFrame getDocumentFrame() {
    return ensureInstalled().getDocumentFrame();
  }

  public static TestStageIDE getInstalledIde() {
    return ensureInstalled();
  }

  public static Project createMinimalProject() {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    NamedUserType programType = AstUtilities.createType("Program", JavaType.getInstance(SProgram.class));
    programType.fields.add(new UserField("myScene", sceneType));
    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }

  public static void setActiveApplication(Application<?> application) {
    try {
      Field field = Application.class.getDeclaredField("singleton");
      field.setAccessible(true);
      field.set(null, application);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  public static void runOnEdt(Runnable runnable) {
    onEdt(() -> {
      runnable.run();
      return null;
    });
  }

  public static <T> T onEdt(Callable<T> callable) {
    try {
      if (SwingUtilities.isEventDispatchThread()) {
        return callable.call();
      }
      FutureTask<T> task = new FutureTask<>(callable);
      SwingUtilities.invokeAndWait(task);
      return task.get();
    } catch (Exception exception) {
      throw new AssertionError(exception);
    }
  }

  private static void configureRootDirectoryProperty() {
    if (System.getProperty("org.alice.ide.rootDirectory") != null) {
      return;
    }
    Path[] candidates = new Path[] {
        Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize(),
        Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize().resolve("core/ide").normalize(),
        Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize().getParent() != null
            ? Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize().getParent()
            : null
    };
    for (Path candidate : candidates) {
      if (candidate != null && Files.isDirectory(candidate)) {
        System.setProperty("org.alice.ide.rootDirectory", candidate.toString());
        return;
      }
    }
  }

  private static void finishOpenActivities(Application<?> application) {
    for (int i = 0; i < 16 && application.getOpenActivity() != null; i++) {
      application.getOpenActivity().finish();
    }
  }

  private static void injectUriProjectLoader(StageIDE ide, UriProjectLoader loader) {
    try {
      Class<?> c = ide.getClass();
      while (c != null) {
        try {
          Field field = c.getDeclaredField("uriProjectLoader");
          field.setAccessible(true);
          field.set(ide, loader);
          return;
        } catch (NoSuchFieldException nsfe) {
          c = c.getSuperclass();
        }
      }
      throw new NoSuchFieldException("uriProjectLoader not found in class hierarchy");
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private static void clearProjectChangeListeners() {
    try {
      Class<?> managerClass = Class.forName("org.alice.ide.project.ProjectChangeOfInterestManager");
      @SuppressWarnings("unchecked")
      Enum<?> singleton = Enum.valueOf((Class<? extends Enum>) managerClass.asSubclass(Enum.class), "SINGLETON");
      Field listenersField = managerClass.getDeclaredField("listeners");
      listenersField.setAccessible(true);
      Object listeners = listenersField.get(singleton);
      if (listeners instanceof java.util.Collection<?>) {
        ((java.util.Collection<?>) listeners).clear();
      }
    } catch (Throwable ignored) {
    }
  }

  public static final class TestStageIDE extends StageIDE {
    public TestStageIDE(CrashDetector crashDetector) {
      super(crashDetector);
    }

    @Override
    protected void promptForLicenseAgreements() {
    }

    @Override
    protected void handleWindowOpened(WindowEvent e) {
    }
  }

  private static final class NewProjectLoader extends UriProjectLoader {
    private NewProjectLoader() {
      super(false);
    }

    @Override
    public URI getUri() {
      return URI.create("blank://test-project");
    }

    @Override
    public boolean isNewProject() {
      return true;
    }

    @Override
    protected Project load() {
      return createMinimalProject();
    }
  }
}
