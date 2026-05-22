package org.alice.ide.coverage;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

final class ClassLoadingSweepSupport {
  private static final String MODULE_LOCAL_SOURCE_ROOT = "src/main/java";
  private static final String MODULE_REPO_SOURCE_ROOT = "core/ide/src/main/java";
  private static final List<String> ALL_CLASS_NAMES = discoverAllClassNames();
  private static final Map<String, List<String>> CLASS_NAMES_BY_PACKAGE = indexByPackage(ALL_CLASS_NAMES);

  static {
    System.setProperty("java.awt.headless", "true");
  }

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

    for (String className : classNames) {
      result.attempted++;
      try {
        Class<?> clazz = Class.forName(className, true, classLoader);
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
    for (Field field : clazz.getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
        continue;
      }
      try {
        field.setAccessible(true);
        field.get(null);
        result.staticFieldsAccessed++;
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
      if ("main".equals(method.getName())) {
        continue;
      }
      try {
        method.setAccessible(true);
        method.invoke(null);
        result.staticMethodsInvoked++;
      } catch (Throwable throwable) {
        result.memberFailures++;
      }
    }
  }

  private static void exerciseNoArgConstructors(Class<?> clazz, SweepResult result) {
    int modifiers = clazz.getModifiers();
    if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum() || Modifier.isAbstract(modifiers)) {
      return;
    }
    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      if (constructor.isSynthetic() || constructor.getParameterCount() != 0) {
        continue;
      }
      try {
        constructor.setAccessible(true);
        constructor.newInstance();
        result.instancesCreated++;
      } catch (Throwable throwable) {
        result.memberFailures++;
      }
    }
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
    int memberFailures;
    final Map<String, String> classLoadFailures = new LinkedHashMap<String, String>();

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
          + ", loadFailures=" + classLoadFailures.size()
          + ", memberFailures=" + memberFailures
          + ']';
    }
  }
}
