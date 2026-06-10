package edu.cmu.cs.dennisc;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestWaitContractTest {
  private static final Pattern RAW_SLEEP = Pattern.compile(
      "Thread[.]sleep\\s*[(]|TimeUnit[.][A-Z]+[.]sleep\\s*[(]|(?:this[.])?robot[.]delay\\s*[(]");

  @Test
  public void testWaitDefinesBoundedConditionFutureAndSemanticTimeContract() throws Exception {
    Class<?> helper = helperClass();

    Method until = requiredStaticMethod(helper, "until", BooleanSupplier.class, String.class);
    Method future = requiredStaticMethod(helper, "future", Future.class, String.class);
    Method sleepForSemanticTime = requiredStaticMethod(helper, "sleepForSemanticTime",
        long.class, TimeUnit.class, String.class);

    invokeStatic(until, (BooleanSupplier) () -> true, "already satisfied utility condition");
    assertEquals("done", invokeStatic(future, CompletableFuture.completedFuture("done"),
        "completed future"));
    invokeStatic(sleepForSemanticTime, 0L, TimeUnit.MILLISECONDS, "zero-duration semantic wait");
  }

  @Test
  public void semanticTimeWaitRestoresInterruptedStatusBeforeFailing() throws Exception {
    Class<?> helper = helperClass();
    Method sleepForSemanticTime = requiredStaticMethod(helper, "sleepForSemanticTime",
        long.class, TimeUnit.class, String.class);
    AtomicBoolean interruptedAfterFailure = new AtomicBoolean(false);

    Thread thread = new Thread(() -> {
      Thread.currentThread().interrupt();
      try {
        invokeStatic(sleepForSemanticTime, 1L, TimeUnit.SECONDS, "interrupted semantic wait");
        fail("Interrupted semantic wait should fail");
      } catch (Throwable expected) {
        interruptedAfterFailure.set(Thread.currentThread().isInterrupted());
      }
    }, "semantic-wait-interruption-contract");

    thread.setDaemon(true);
    thread.start();
    thread.join(TimeUnit.SECONDS.toMillis(5));

    assertTrue("semantic wait must restore interrupted status before failing",
        interruptedAfterFailure.get());
  }

  @Test
  public void utilTestsUseDeterministicWaitHelpersInsteadOfRawSleeps() throws Exception {
    List<String> violations = rawSleepViolations(Paths.get("").toAbsolutePath().resolve("src/test/java"),
        "TestWait.java");

    assertTrue("Replace raw sleeps in core-util tests with TestWait helpers. Violations:\n"
        + String.join("\n", violations), violations.isEmpty());
  }

  private static Class<?> helperClass() {
    try {
      return Class.forName("edu.cmu.cs.dennisc.TestWait");
    } catch (ClassNotFoundException cnfe) {
      fail("Expected module-local test helper edu.cmu.cs.dennisc.TestWait");
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
}
