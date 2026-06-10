package org.alice.netbeans.project;

import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ProjectTestWaitContractTest {
  private static final Pattern RAW_SLEEP = Pattern.compile(
      "Thread[.]sleep\\s*[(]|TimeUnit[.][A-Z]+[.]sleep\\s*[(]|(?:this[.])?robot[.]delay\\s*[(]");

  @Test
  public void projectTestWaitDefinesProcessLauncherAndStreamDrainContract() throws Exception {
    Class<?> helper = helperClass();
    Class<?> checkedBooleanSupplier = nestedType(helper, "CheckedBooleanSupplier");
    Class<?> checkedSupplier = nestedType(helper, "CheckedSupplier");
    Class<?> throwingRunnable = nestedType(helper, "ThrowingRunnable");

    Method until = requiredStaticMethod(helper, "until", checkedBooleanSupplier, String.class);
    Method await = requiredStaticMethod(helper, "await", CountDownLatch.class, String.class);
    Method untilNotNull = requiredStaticMethod(helper, "untilNotNull", checkedSupplier, String.class);
    Method get = requiredStaticMethod(helper, "get", Future.class, String.class);
    Method captureSystemOutUntil = requiredStaticMethod(helper, "captureSystemOutUntil",
        throwingRunnable, checkedBooleanSupplier, String.class);

    invokeStatic(until, checkedBoolean(checkedBooleanSupplier, true),
        "already satisfied generated-project condition");
    invokeStatic(await, new CountDownLatch(0), "already completed stream-drain latch");
    assertEquals("value", invokeStatic(untilNotNull, checkedSupplier(checkedSupplier, "value"),
        "non-null generated field"));
    assertEquals("done", invokeStatic(get, CompletableFuture.completedFuture("done"),
        "completed stream-drain future"));

    PrintStream originalOut = System.out;
    String captured = (String) invokeStatic(captureSystemOutUntil,
        throwingRunnable(throwingRunnable, () -> System.out.print("PROJECT_WAIT_MARKER")),
        checkedBoolean(checkedBooleanSupplier, true),
        "launcher completion marker");

    assertSame("System.out must be restored after scoped capture", originalOut, System.out);
    assertTrue("captured output must include action output", captured.contains("PROJECT_WAIT_MARKER"));
  }

  @Test
  public void captureSystemOutUntilRestoresSystemOutWhenActionFails() throws Exception {
    Class<?> helper = helperClass();
    Class<?> checkedBooleanSupplier = nestedType(helper, "CheckedBooleanSupplier");
    Class<?> throwingRunnable = nestedType(helper, "ThrowingRunnable");
    Method captureSystemOutUntil = requiredStaticMethod(helper, "captureSystemOutUntil",
        throwingRunnable, checkedBooleanSupplier, String.class);
    PrintStream originalOut = System.out;

    try {
      invokeStatic(captureSystemOutUntil,
          throwingRunnable(throwingRunnable, () -> {
            throw new IOException("deliberate capture failure");
          }),
          checkedBoolean(checkedBooleanSupplier, true),
          "failing launcher action");
      fail("captureSystemOutUntil must surface action failures");
    } catch (Throwable expected) {
      assertTrue("action failure must be preserved", containsCause(expected, IOException.class));
      assertSame("System.out must be restored after action failure", originalOut, System.out);
    }
  }

  @Test
  public void netbeansTestsUseDeterministicWaitHelpersInsteadOfRawSleeps() throws Exception {
    List<String> violations = rawSleepViolations(Paths.get("").toAbsolutePath().resolve("src/test/java"),
        "ProjectTestWait.java");

    assertTrue("Replace raw sleeps in NetBeans tests with ProjectTestWait helpers. Violations:\n"
        + String.join("\n", violations), violations.isEmpty());
  }

  private static Object checkedBoolean(Class<?> type, boolean value) {
    return proxy(type, (proxy, method, args) -> Boolean.valueOf(value));
  }

  private static Object checkedSupplier(Class<?> type, Object value) {
    return proxy(type, (proxy, method, args) -> value);
  }

  private static Object throwingRunnable(Class<?> type, ThrowingBlock block) {
    return proxy(type, (proxy, method, args) -> {
      block.run();
      return null;
    });
  }

  private static Object proxy(Class<?> type, java.lang.reflect.InvocationHandler handler) {
    return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, method, args) -> {
      if (method.getDeclaringClass() == Object.class) {
        return switch (method.getName()) {
          case "toString" -> type.getName() + " proxy";
          case "hashCode" -> System.identityHashCode(proxy);
          case "equals" -> proxy == args[0];
          default -> null;
        };
      }
      return handler.invoke(proxy, method, args);
    });
  }

  private static Class<?> helperClass() {
    try {
      return Class.forName("org.alice.netbeans.project.ProjectTestWait");
    } catch (ClassNotFoundException cnfe) {
      fail("Expected module-local test helper org.alice.netbeans.project.ProjectTestWait");
      return null;
    }
  }

  private static Class<?> nestedType(Class<?> outer, String simpleName) {
    try {
      return Class.forName(outer.getName() + "$" + simpleName);
    } catch (ClassNotFoundException cnfe) {
      fail("Missing helper nested interface: " + outer.getName() + "." + simpleName);
      return null;
    }
  }

  private static Method requiredStaticMethod(Class<?> type, String name, Class<?>... parameterTypes) {
    try {
      Method method = type.getDeclaredMethod(name, parameterTypes);
      method.setAccessible(true);
      assertTrue(name + " must be static", Modifier.isStatic(method.getModifiers()));
      return method;
    } catch (NoSuchMethodException nsme) {
      fail("Missing helper method: " + type.getName() + "." + name);
      return null;
    }
  }

  private static Object invokeStatic(Method method, Object... args) throws Exception {
    try {
      return method.invoke(null, args);
    } catch (InvocationTargetException ite) {
      Throwable cause = ite.getCause();
      if (cause instanceof AssertionError assertionError) {
        throw assertionError;
      }
      if (cause instanceof Exception exception) {
        throw exception;
      }
      throw new AssertionError(cause);
    }
  }

  private static boolean containsCause(Throwable throwable, Class<? extends Throwable> expectedType) {
    Throwable current = throwable;
    while (current != null) {
      if (expectedType.isInstance(current)) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  private static List<String> rawSleepViolations(Path testRoot, String... allowedFiles) throws Exception {
    assertTrue("Expected test source root to exist: " + testRoot, Files.isDirectory(testRoot));
    List<String> allowed = List.of(allowedFiles);
    List<String> violations = new ArrayList<>();
    try (var paths = Files.walk(testRoot)) {
      paths.filter(path -> path.toString().endsWith(".java"))
          .filter(path -> !allowed.contains(path.getFileName().toString()))
          .forEach(path -> collectRawSleepViolations(testRoot, path, violations));
    }
    return violations;
  }

  private static void collectRawSleepViolations(Path testRoot, Path path, List<String> violations) {
    try {
      List<String> lines = Files.readAllLines(path);
      for (int i = 0; i < lines.size(); i++) {
        if (RAW_SLEEP.matcher(lines.get(i)).find()) {
          violations.add(testRoot.relativize(path) + ":" + (i + 1) + ": " + lines.get(i).trim());
        }
      }
    } catch (Exception ex) {
      violations.add(testRoot.relativize(path) + ": unable to inspect source: " + ex.getMessage());
    }
  }

  private interface ThrowingBlock {
    void run() throws Exception;
  }
}
