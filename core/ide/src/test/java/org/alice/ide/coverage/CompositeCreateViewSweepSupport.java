package org.alice.ide.coverage;

import org.alice.ide.testing.TestIdeBootstrap;
import org.lgna.croquet.Composite;
import org.lgna.croquet.views.AwtComponentView;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.stream.Stream;

final class CompositeCreateViewSweepSupport {
  private static final String MODULE_LOCAL_SOURCE_ROOT = "src/main/java";
  private static final String MODULE_REPO_SOURCE_ROOT = "core/ide/src/main/java";
  private static final int DEFAULT_COMPONENT_WIDTH = 320;
  private static final int DEFAULT_COMPONENT_HEIGHT = 180;
  private static final Method RESOLVE_DEFAULT_VALUE_METHOD;
  private static final Method SUMMARIZE_METHOD;
  private static final Constructor<?> SWEEP_RESULT_CONSTRUCTOR;
  private static final Field UNRESOLVED_FIELD;
  private static final List<String> CONCRETE_COMPOSITE_CLASS_NAMES = discoverConcreteCompositeClassNames();

  static {
    try {
      RESOLVE_DEFAULT_VALUE_METHOD = ClassLoadingSweepSupport.class.getDeclaredMethod(
          "resolveDefaultValue",
          Class.class,
          int.class,
          Set.class,
          Class.forName("org.alice.ide.coverage.ClassLoadingSweepSupport$SweepResult")
      );
      RESOLVE_DEFAULT_VALUE_METHOD.setAccessible(true);
      SUMMARIZE_METHOD = ClassLoadingSweepSupport.class.getDeclaredMethod("summarize", Throwable.class);
      SUMMARIZE_METHOD.setAccessible(true);
      SWEEP_RESULT_CONSTRUCTOR = Class.forName("org.alice.ide.coverage.ClassLoadingSweepSupport$SweepResult")
          .getDeclaredConstructor(int.class, boolean.class);
      SWEEP_RESULT_CONSTRUCTOR.setAccessible(true);
      UNRESOLVED_FIELD = ClassLoadingSweepSupport.class.getDeclaredField("UNRESOLVED");
      UNRESOLVED_FIELD.setAccessible(true);
    } catch (ReflectiveOperationException roe) {
      throw new ExceptionInInitializerError(roe);
    }
  }

  private CompositeCreateViewSweepSupport() {
  }

  static CompositeCreateViewSweepResult sweepPartition(int partitionIndex, int partitionCount) {
    List<String> classNames = classNamesForPartition(partitionIndex, partitionCount);
    Object helperResult = createHelperResult(classNames.size());
    CompositeCreateViewSweepResult result = new CompositeCreateViewSweepResult(
        CONCRETE_COMPOSITE_CLASS_NAMES.size(),
        partitionIndex,
        partitionCount,
        classNames.size()
    );
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = CompositeCreateViewSweepSupport.class.getClassLoader();
    }
    final ClassLoader finalClassLoader = classLoader;

    for (String className : classNames) {
      result.attempted++;
      try {
        TestIdeBootstrap.ensureInstalled();
        TestIdeBootstrap.reset();
        final Class<?> compositeClass = Class.forName(className, false, finalClassLoader);
        Object value = onEdt(() -> resolveDefaultValue(compositeClass, helperResult));
        if (value == unresolvedMarker()) {
          throw new IllegalStateException("Unable to instantiate composite");
        }
        if (!(value instanceof Composite)) {
          throw new IllegalStateException("Resolved value was not a Composite");
        }
        Composite<?> composite = (Composite<?>) value;
        Object createdView = onEdt(() -> invokeCreateView(composite));
        if (createdView == null) {
          throw new AssertionError("createView returned null");
        }
        final Object finalCreatedView = createdView;
        onEdt(() -> {
          touchView(finalCreatedView);
          return null;
        });
        result.succeeded++;
      } catch (Throwable throwable) {
        result.failures.put(className, summarize(throwable));
      } finally {
        try {
          TestIdeBootstrap.reset();
        } catch (Throwable ignored) {
        }
      }
    }

    result.edtTasks = readIntField(helperResult, "edtTasks");
    result.memberFailures = readIntField(helperResult, "memberFailures");
    return result;
  }

  private static List<String> classNamesForPartition(int partitionIndex, int partitionCount) {
    if (partitionCount <= 0) {
      throw new IllegalArgumentException("partitionCount must be positive");
    }
    if (partitionIndex < 0 || partitionIndex >= partitionCount) {
      throw new IllegalArgumentException("partitionIndex out of range: " + partitionIndex);
    }
    int total = CONCRETE_COMPOSITE_CLASS_NAMES.size();
    int start = (partitionIndex * total) / partitionCount;
    int end = ((partitionIndex + 1) * total) / partitionCount;
    return CONCRETE_COMPOSITE_CLASS_NAMES.subList(start, end);
  }

  private static List<String> discoverConcreteCompositeClassNames() {
    Path sourceRoot = locateSourceRoot();
    List<String> classNames = new ArrayList<String>();
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = CompositeCreateViewSweepSupport.class.getClassLoader();
    }
    final ClassLoader finalClassLoader = classLoader;
    try (Stream<Path> stream = Files.walk(sourceRoot)) {
      stream.filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".java"))
          .filter(path -> {
            String fileName = path.getFileName().toString();
            return !"package-info.java".equals(fileName) && !"module-info.java".equals(fileName);
          })
          .filter(CompositeCreateViewSweepSupport::containsCompositeExtendsLine)
          .sorted()
          .forEach(path -> {
            String className = toClassName(sourceRoot, path);
            try {
              Class<?> clazz = Class.forName(className, false, finalClassLoader);
              if (Composite.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                classNames.add(className);
              }
            } catch (Throwable throwable) {
              throw new IllegalStateException("Unable to load composite candidate " + className, throwable);
            }
          });
    } catch (java.io.IOException ioe) {
      throw new IllegalStateException("Unable to scan composite candidates from " + sourceRoot, ioe);
    }
    return Collections.unmodifiableList(classNames);
  }

  private static boolean containsCompositeExtendsLine(Path path) {
    try (Stream<String> lines = Files.lines(path)) {
      return lines.anyMatch(line -> line.matches(".*extends.*Composite\\b.*"));
    } catch (java.io.IOException ioe) {
      throw new IllegalStateException("Unable to inspect " + path, ioe);
    }
  }

  private static Path locateSourceRoot() {
    Path[] candidates = new Path[] {
        Paths.get(MODULE_LOCAL_SOURCE_ROOT),
        Paths.get(MODULE_REPO_SOURCE_ROOT),
        Paths.get(System.getProperty("user.dir"), MODULE_LOCAL_SOURCE_ROOT),
        Paths.get(System.getProperty("user.dir"), MODULE_REPO_SOURCE_ROOT)
    };
    for (Path candidate : candidates) {
      Path normalized = candidate.toAbsolutePath().normalize();
      if (Files.isDirectory(normalized)) {
        return normalized;
      }
    }
    throw new IllegalStateException("Unable to locate core/ide source root from "
        + Paths.get("").toAbsolutePath().normalize());
  }

  private static String toClassName(Path sourceRoot, Path sourceFile) {
    Path relative = sourceRoot.relativize(sourceFile);
    String joined = relative.toString().replace(relative.getFileSystem().getSeparator(), ".");
    return joined.substring(0, joined.length() - ".java".length());
  }

  private static Object createHelperResult(int discovered) {
    try {
      return SWEEP_RESULT_CONSTRUCTOR.newInstance(discovered, false);
    } catch (ReflectiveOperationException roe) {
      throw new IllegalStateException(roe);
    }
  }

  private static Object unresolvedMarker() {
    try {
      return UNRESOLVED_FIELD.get(null);
    } catch (IllegalAccessException iae) {
      throw new IllegalStateException(iae);
    }
  }

  private static Object resolveDefaultValue(Class<?> type, Object helperResult) throws Throwable {
    try {
      return RESOLVE_DEFAULT_VALUE_METHOD.invoke(null, type, Integer.valueOf(0), new HashSet<Class<?>>(), helperResult);
    } catch (InvocationTargetException ite) {
      throw ite.getCause() != null ? ite.getCause() : ite;
    }
  }

  private static String summarize(Throwable throwable) {
    try {
      return (String) SUMMARIZE_METHOD.invoke(null, throwable);
    } catch (ReflectiveOperationException roe) {
      throw new IllegalStateException(roe);
    }
  }

  private static int readIntField(Object target, String fieldName) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.getInt(target);
    } catch (ReflectiveOperationException roe) {
      throw new IllegalStateException(roe);
    }
  }

  private static Object invokeCreateView(Composite<?> composite) throws Throwable {
    Method createViewMethod = findConcreteCreateViewMethod(composite.getClass());
    if (createViewMethod == null) {
      throw new NoSuchMethodException("createView");
    }
    createViewMethod.setAccessible(true);
    try {
      return createViewMethod.invoke(composite);
    } catch (InvocationTargetException ite) {
      throw ite.getCause() != null ? ite.getCause() : ite;
    }
  }

  private static Method findConcreteCreateViewMethod(Class<?> clazz) {
    for (Class<?> type = clazz; type != null; type = type.getSuperclass()) {
      try {
        Method method = type.getDeclaredMethod("createView");
        if (!Modifier.isAbstract(method.getModifiers())) {
          return method;
        }
      } catch (NoSuchMethodException nsme) {
      }
    }
    return null;
  }

  private interface ThrowingCallable<T> {
    T call() throws Throwable;
  }

  private static <T> T onEdt(ThrowingCallable<T> callable) throws Throwable {
    if (SwingUtilities.isEventDispatchThread()) {
      return callable.call();
    }
    FutureTask<T> task = new FutureTask<T>(new Callable<T>() {
      @Override
      public T call() throws Exception {
        try {
          return callable.call();
        } catch (Exception exception) {
          throw exception;
        } catch (Throwable throwable) {
          throw new RuntimeException(throwable);
        }
      }
    });
    SwingUtilities.invokeAndWait(task);
    try {
      return task.get();
    } catch (java.util.concurrent.ExecutionException ee) {
      Throwable cause = ee.getCause();
      if (cause instanceof RuntimeException && cause.getCause() != null) {
        throw cause.getCause();
      }
      throw cause != null ? cause : ee;
    }
  }

  private static void touchView(Object createdView) throws Throwable {
    if (createdView instanceof AwtComponentView<?>) {
      layoutAndPaint(((AwtComponentView<?>) createdView).getAwtComponent());
    } else if (createdView instanceof Component) {
      layoutAndPaint((Component) createdView);
    }
  }

  private static void layoutAndPaint(Component component) {
    Dimension preferredSize = component.getPreferredSize();
    int width = clamp(preferredSize != null ? preferredSize.width : DEFAULT_COMPONENT_WIDTH, DEFAULT_COMPONENT_WIDTH);
    int height = clamp(preferredSize != null ? preferredSize.height : DEFAULT_COMPONENT_HEIGHT, DEFAULT_COMPONENT_HEIGHT);
    component.setSize(width, height);
    if (component instanceof JComponent) {
      ((JComponent) component).setOpaque(true);
    }
    if (component instanceof Container) {
      ((Container) component).doLayout();
      ((Container) component).validate();
    }
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    try {
      component.paint(graphics);
    } finally {
      graphics.dispose();
    }
  }

  private static int clamp(int value, int fallback) {
    if (value <= 0) {
      return fallback;
    }
    return Math.min(value, 1024);
  }

  static final class CompositeCreateViewSweepResult {
    final int concreteCompositeClasses;
    final int partitionIndex;
    final int partitionCount;
    final int assignedClassCount;
    int attempted;
    int succeeded;
    int edtTasks;
    int memberFailures;
    final Map<String, String> failures = new LinkedHashMap<String, String>();

    private CompositeCreateViewSweepResult(int concreteCompositeClasses, int partitionIndex, int partitionCount,
        int assignedClassCount) {
      this.concreteCompositeClasses = concreteCompositeClasses;
      this.partitionIndex = partitionIndex;
      this.partitionCount = partitionCount;
      this.assignedClassCount = assignedClassCount;
    }

    String summary() {
      return "CompositeCreateViewSweep[concreteComposites=" + concreteCompositeClasses
          + ", partition=" + (partitionIndex + 1) + '/' + partitionCount
          + ", assigned=" + assignedClassCount
          + ", attempted=" + attempted
          + ", succeeded=" + succeeded
          + ", edtTasks=" + edtTasks
          + ", memberFailures=" + memberFailures
          + ", failures=" + failures.size()
          + ']';
    }
  }
}
