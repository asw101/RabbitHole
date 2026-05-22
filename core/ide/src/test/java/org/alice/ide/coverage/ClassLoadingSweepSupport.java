package org.alice.ide.coverage;

import org.lgna.croquet.Composite;
import org.lgna.croquet.Model;
import org.lgna.croquet.views.AwtComponentView;

import javax.swing.Icon;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

final class ClassLoadingSweepSupport {
  private static final String MODULE_LOCAL_SOURCE_ROOT = "src/main/java";
  private static final String MODULE_REPO_SOURCE_ROOT = "core/ide/src/main/java";
  private static final int DEFAULT_COMPONENT_WIDTH = 320;
  private static final int DEFAULT_COMPONENT_HEIGHT = 180;
  private static final List<String> ALL_CLASS_NAMES = discoverAllClassNames();
  private static final Map<String, List<String>> CLASS_NAMES_BY_PACKAGE = indexByPackage(ALL_CLASS_NAMES);

  private ClassLoadingSweepSupport() {
  }

  static SweepResult sweepAllClasses() {
    return sweepClasses(ALL_CLASS_NAMES, false);
  }

  static SweepResult sweepExactPackage(String packageName) {
    return sweepClasses(classNamesInExactPackage(packageName), true);
  }

  static SweepResult sweepPackageTree(String packagePrefix) {
    return sweepClasses(classNamesInPackageTree(packagePrefix), true);
  }

  private static List<String> classNamesInExactPackage(String packageName) {
    List<String> classNames = CLASS_NAMES_BY_PACKAGE.get(packageName);
    if (classNames == null) {
      return Collections.emptyList();
    }
    return classNames;
  }

  private static List<String> classNamesInPackageTree(String packagePrefix) {
    List<String> classNames = new ArrayList<String>();
    String prefixWithDot = packagePrefix + ".";
    for (String className : ALL_CLASS_NAMES) {
      int lastDot = className.lastIndexOf('.');
      String packageName = lastDot >= 0 ? className.substring(0, lastDot) : "";
      if (packageName.equals(packagePrefix) || packageName.startsWith(prefixWithDot)) {
        classNames.add(className);
      }
    }
    return classNames;
  }

  private static SweepResult sweepClasses(List<String> classNames, boolean exerciseExtras) {
    SweepResult result = new SweepResult(classNames.size());
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = ClassLoadingSweepSupport.class.getClassLoader();
    }

    try {
      for (String className : classNames) {
        result.attempted++;
        try {
          Class<?> clazz = Class.forName(className, false, classLoader);
          result.loaded++;
          exerciseEnumConstants(clazz, result);
          exerciseStaticFields(clazz, result);
          if (exerciseExtras) {
            exerciseStaticMethods(clazz, result);
            exerciseNoArgConstructors(clazz, result);
          }
        } catch (Throwable throwable) {
          result.classLoadFailures.put(className, summarize(throwable));
        }
      }

      System.out.println(result.summary());
      return result;
    } finally {
      resetGlobalState();
    }
  }

  private static void exerciseEnumConstants(Class<?> clazz, SweepResult result) {
    if (!clazz.isEnum()) {
      return;
    }
    try {
      Method valuesMethod = clazz.getDeclaredMethod("values");
      valuesMethod.setAccessible(true);
      Object values = valuesMethod.invoke(null);
      if (values instanceof Object[]) {
        result.enumConstantsAccessed += ((Object[]) values).length;
      }
    } catch (Throwable throwable) {
      try {
        Object[] constants = clazz.getEnumConstants();
        if (constants != null) {
          result.enumConstantsAccessed += constants.length;
        }
      } catch (Throwable ignored) {
        result.memberFailures++;
      }
    }
  }

  private static void exerciseStaticFields(Class<?> clazz, SweepResult result) {
    if (!shouldExerciseMembers(clazz)) {
      return;
    }
    for (Field field : clazz.getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
        continue;
      }
      try {
        field.setAccessible(true);
        Object value = field.get(null);
        result.staticFieldsAccessed++;
        exerciseObject(value, result);
      } catch (Throwable throwable) {
        result.memberFailures++;
      }
    }
  }

  private static void exerciseStaticMethods(Class<?> clazz, SweepResult result) {
    for (Method method : clazz.getDeclaredMethods()) {
      int modifiers = method.getModifiers();
      if (!Modifier.isStatic(modifiers) || Modifier.isAbstract(modifiers) || method.isSynthetic()) {
        continue;
      }
      if (method.getParameterCount() != 0 || method.getReturnType() == Void.TYPE) {
        continue;
      }
      if ("main".equals(method.getName()) || !shouldInvokeStaticMethod(clazz, method)) {
        continue;
      }
      try {
        method.setAccessible(true);
        Object value = invokeNoArgMethod(method, clazz, result);
        result.staticMethodsInvoked++;
        exerciseObject(value, result);
      } catch (Throwable throwable) {
        result.memberFailures++;
      }
    }
  }

  private static void exerciseNoArgConstructors(Class<?> clazz, SweepResult result) {
    int modifiers = clazz.getModifiers();
    if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum() || Modifier.isAbstract(modifiers) || !shouldInstantiate(clazz)) {
      return;
    }
    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      if (constructor.isSynthetic() || constructor.getParameterCount() != 0) {
        continue;
      }
      try {
        constructor.setAccessible(true);
        Object value = invokeConstructor(constructor, clazz, result);
        result.instancesCreated++;
        exerciseObject(value, result);
      } catch (Throwable throwable) {
        result.memberFailures++;
      }
    }
  }

  private static Object invokeNoArgMethod(Method method, Class<?> clazz, SweepResult result) throws Throwable {
    if (shouldUseEdt(clazz) || shouldUseEdt(method.getReturnType())) {
      return invokeOnEdt(() -> method.invoke(null), result);
    }
    return method.invoke(null);
  }

  private static boolean shouldInvokeStaticMethod(Class<?> clazz, Method method) {
    Class<?> returnType = method.getReturnType();
    String methodName = method.getName();
    if (methodName.startsWith("getInstance") || methodName.startsWith("createInstance")) {
      return isExpandedExerciseType(returnType);
    }
    if (Icon.class.isAssignableFrom(returnType)) {
      return true;
    }
    if (isExpandedExerciseType(returnType)) {
      return true;
    }
    String simpleName = returnType.getSimpleName();
    return simpleName.endsWith("Icon");
  }

  private static Object invokeConstructor(Constructor<?> constructor, Class<?> clazz, SweepResult result) throws Throwable {
    if (shouldUseEdt(clazz)) {
      return invokeOnEdt(() -> constructor.newInstance(), result);
    }
    return constructor.newInstance();
  }

  private static boolean isExpandedExerciseType(Class<?> clazz) {
    if (clazz == null) {
      return false;
    }
    String className = clazz.getName();
    if (className.startsWith("org.alice.ide.croquet.models.projecturi.")) {
      return false;
    }
    return className.equals("org.alice.ide.croquet.models")
        || className.startsWith("org.alice.ide.croquet.models.")
        || className.equals("org.lgna.ik")
        || className.startsWith("org.lgna.ik.");
  }

  private static boolean shouldUseEdt(Class<?> clazz) {
    return clazz != null && (AwtComponentView.class.isAssignableFrom(clazz)
        || (isExpandedExerciseType(clazz) && Composite.class.isAssignableFrom(clazz))
        || Component.class.isAssignableFrom(clazz)
        || Icon.class.isAssignableFrom(clazz));
  }

  private static boolean shouldExerciseMembers(Class<?> clazz) {
    if (clazz == null) {
      return false;
    }
    if (isExpandedExerciseType(clazz)) {
      return true;
    }
    if (java.awt.Window.class.isAssignableFrom(clazz)) {
      return false;
    }
    String simpleName = clazz.getSimpleName();
    if (simpleName.endsWith("Dialog") || simpleName.endsWith("Frame") || simpleName.endsWith("Window")) {
      return false;
    }
    return shouldUseEdt(clazz)
        || simpleName.endsWith("Panel")
        || simpleName.endsWith("View")
        || simpleName.endsWith("Editor")
        || simpleName.endsWith("Component")
        || simpleName.endsWith("Adapter")
        || simpleName.endsWith("Icon");
  }

  private static boolean shouldInstantiate(Class<?> clazz) {
    if (!shouldExerciseMembers(clazz)) {
      return false;
    }
    String className = clazz.getName();
    String simpleName = clazz.getSimpleName();
    if (className.contains(".sceneeditor.") && simpleName.endsWith("SceneEditor")) {
      return false;
    }
    return true;
  }

  private static <T> T invokeOnEdt(ThrowingSupplier<T> supplier, SweepResult result) throws Throwable {
    if (SwingUtilities.isEventDispatchThread()) {
      return supplier.get();
    }
    final Object[] valueHolder = new Object[1];
    final Throwable[] failureHolder = new Throwable[1];
    SwingUtilities.invokeAndWait(() -> {
      try {
        valueHolder[0] = supplier.get();
      } catch (Throwable throwable) {
        failureHolder[0] = throwable;
      }
    });
    result.edtTasks++;
    if (failureHolder[0] != null) {
      throw failureHolder[0];
    }
    @SuppressWarnings("unchecked")
    T value = (T) valueHolder[0];
    return value;
  }

  private static void exerciseObject(Object value, SweepResult result) {
    if (value == null || !result.visitedObjects.add(value)) {
      return;
    }
    try {
      if ((value instanceof Composite<?>) && isExpandedExerciseType(value.getClass())) {
        exerciseComposite((Composite<?>) value, result);
      } else if ((value instanceof Model) && isExpandedExerciseType(value.getClass())) {
        exerciseModel((Model) value, result);
      } else if (value instanceof AwtComponentView<?>) {
        exerciseCroquetView((AwtComponentView<?>) value, result);
      } else if (value instanceof Component) {
        exerciseComponent((Component) value, result);
      } else if (value instanceof Icon) {
        exerciseIcon((Icon) value, result);
      }
      if (isExpandedExerciseType(value.getClass())) {
        exerciseSafeInstanceMethods(value, result);
      }
    } catch (Throwable throwable) {
      result.memberFailures++;
    }
  }

  private static void exerciseComposite(Composite<?> composite, SweepResult result) throws Throwable {
    invokeOnEdt(() -> {
      exerciseObject(composite.getView(), result);
      exerciseObject(composite.getRootComponent(), result);
      composite.releaseView();
      return null;
    }, result);
  }

  private static void exerciseModel(Model model, SweepResult result) throws Throwable {
    boolean enabled = model.isEnabled();
    model.setEnabled(enabled);
    model.relocalize();
    invokeNamedNoArgMethodIfPresent(model, "getSidekickLabel", result);
    invokeNamedNoArgMethodIfPresent(model, "getMenuModel", result);
    invokeNamedNoArgMethodIfPresent(model, "getMenuItemPrepModel", result);
    invokeNamedNoArgMethodIfPresent(model, "createButton", result);
    invokeNamedNoArgMethodIfPresent(model, "createHyperlink", result);
  }

  private static void exerciseCroquetView(AwtComponentView<?> view, SweepResult result) throws Throwable {
    invokeOnEdt(() -> {
      Component component = view.getAwtComponent();
      layoutAndPaint(component);
      return null;
    }, result);
    result.viewsExercised++;
  }

  private static void exerciseComponent(Component component, SweepResult result) throws Throwable {
    invokeOnEdt(() -> {
      layoutAndPaint(component);
      return null;
    }, result);
    result.componentsPainted++;
  }

  private static void invokeNamedNoArgMethodIfPresent(Object value, String methodName, SweepResult result) throws Throwable {
    Method method;
    try {
      method = value.getClass().getMethod(methodName);
    } catch (NoSuchMethodException nsme) {
      return;
    }
    invokeInstanceMethod(value, method, result);
  }

  private static void exerciseSafeInstanceMethods(Object value, SweepResult result) {
    for (Method method : value.getClass().getMethods()) {
      if (method.getDeclaringClass() == Object.class || Modifier.isStatic(method.getModifiers()) || method.isSynthetic()) {
        continue;
      }
      if (method.getParameterCount() != 0 || method.getReturnType() == Void.TYPE || !isSafeInstanceMethod(method)) {
        continue;
      }
      try {
        invokeInstanceMethod(value, method, result);
      } catch (Throwable throwable) {
        result.memberFailures++;
      }
    }
  }

  private static boolean isSafeInstanceMethod(Method method) {
    String name = method.getName();
    if ("getClass".equals(name) || "hashCode".equals(name) || "clone".equals(name)) {
      return false;
    }
    if (!(name.startsWith("get") || name.startsWith("is") || name.startsWith("has") || name.startsWith("peek"))) {
      return false;
    }
    String lowerName = name.toLowerCase();
    return !lowerName.contains("dialog")
        && !lowerName.contains("window")
        && !lowerName.contains("frame")
        && !lowerName.contains("popup")
        && !lowerName.contains("rootdirectory")
        && !lowerName.contains("graphicsconfiguration");
  }

  private static void invokeInstanceMethod(Object value, Method method, SweepResult result) throws Throwable {
    try {
      Object nested;
      if (shouldUseEdt(method.getReturnType()) || shouldUseEdt(value.getClass())) {
        nested = invokeOnEdt(() -> invokeMethod(method, value), result);
      } else {
        nested = invokeMethod(method, value);
      }
      exerciseObject(nested, result);
    } catch (Throwable throwable) {
      result.memberFailures++;
    }
  }

  private static Object invokeMethod(Method method, Object value) throws Throwable {
    try {
      return method.invoke(value);
    } catch (InvocationTargetException ite) {
      throw ite.getCause() != null ? ite.getCause() : ite;
    }
  }

  private static void exerciseIcon(Icon icon, SweepResult result) {
    int width = Math.max(1, icon.getIconWidth());
    int height = Math.max(1, icon.getIconHeight());
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    try {
      icon.paintIcon(null, graphics, 0, 0);
    } finally {
      graphics.dispose();
    }
    result.iconsPainted++;
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

  private static void resetGlobalState() {
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
    try {
      Class<?> applicationClass = Class.forName("org.lgna.croquet.Application");
      Field singletonField = applicationClass.getDeclaredField("singleton");
      singletonField.setAccessible(true);
      singletonField.set(null, null);
    } catch (Throwable ignored) {
    }
  }

  private static int clamp(int value, int fallback) {
    if (value <= 0) {
      return fallback;
    }
    return Math.min(value, 1024);
  }

  private interface ThrowingSupplier<T> {
    T get() throws Throwable;
  }

  private static Map<String, List<String>> indexByPackage(List<String> classNames) {
    Map<String, List<String>> byPackage = new LinkedHashMap<String, List<String>>();
    for (String className : classNames) {
      int lastDot = className.lastIndexOf('.');
      String packageName = lastDot >= 0 ? className.substring(0, lastDot) : "";
      List<String> packageClasses = byPackage.get(packageName);
      if (packageClasses == null) {
        packageClasses = new ArrayList<String>();
        byPackage.put(packageName, packageClasses);
      }
      packageClasses.add(className);
    }
    for (Map.Entry<String, List<String>> entry : byPackage.entrySet()) {
      entry.setValue(Collections.unmodifiableList(entry.getValue()));
    }
    return Collections.unmodifiableMap(byPackage);
  }

  private static List<String> discoverAllClassNames() {
    Path sourceRoot = locateSourceRoot();
    List<String> classNames = new ArrayList<String>();
    try (Stream<Path> stream = Files.walk(sourceRoot)) {
      stream.filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".java"))
          .filter(path -> {
            String fileName = path.getFileName().toString();
            return !"package-info.java".equals(fileName) && !"module-info.java".equals(fileName);
          })
          .sorted()
          .forEach(path -> classNames.add(toClassName(sourceRoot, path)));
    } catch (IOException ioe) {
      throw new IllegalStateException("Unable to scan " + sourceRoot, ioe);
    }
    return Collections.unmodifiableList(classNames);
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

  private static String summarize(Throwable throwable) {
    StringBuilder builder = new StringBuilder();
    Throwable current = throwable;
    int depth = 0;
    while (current != null && depth < 3) {
      if (builder.length() > 0) {
        builder.append(" -> ");
      }
      builder.append(current.getClass().getSimpleName());
      String message = current.getMessage();
      if (message != null && !message.isEmpty()) {
        builder.append('(').append(message).append(')');
      }
      current = current.getCause();
      depth++;
    }
    return builder.toString();
  }

  static final class SweepResult {
    final int discovered;
    int attempted;
    int loaded;
    int enumConstantsAccessed;
    int staticFieldsAccessed;
    int staticMethodsInvoked;
    int instancesCreated;
    int viewsExercised;
    int componentsPainted;
    int iconsPainted;
    int edtTasks;
    int memberFailures;
    final Map<String, String> classLoadFailures = new LinkedHashMap<String, String>();
    final Set<Object> visitedObjects = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());

    private SweepResult(int discovered) {
      this.discovered = discovered;
    }

    String summary() {
      return "ClassLoadingSweep[discovered=" + discovered
          + ", attempted=" + attempted
          + ", loaded=" + loaded
          + ", enumConstants=" + enumConstantsAccessed
          + ", staticFields=" + staticFieldsAccessed
          + ", staticMethods=" + staticMethodsInvoked
          + ", instances=" + instancesCreated
          + ", views=" + viewsExercised
          + ", components=" + componentsPainted
          + ", icons=" + iconsPainted
          + ", edtTasks=" + edtTasks
          + ", loadFailures=" + classLoadFailures.size()
          + ", memberFailures=" + memberFailures
          + ']';
    }
  }
}
