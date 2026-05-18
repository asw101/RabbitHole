# Tutorial: Edge-case testing patterns for core/util Phase 2

This tutorial walks through the advanced testing patterns used in the Phase 2
coverage sprint (Issue #759) that pushes `core/util` from 46% to 70%+ line
coverage. Each pattern addresses a specific testing challenge: thread
interrupts, enum reflection, stream edge cases, XML round-trips, and
concurrent execution.

## Prerequisites

- JDK 17+ on `PATH`
- Maven 3.9+
- Tweedle grammar submodule: `git submodule update --init tweedle-lang`

## Pattern 1: Testing thread interrupt paths

**Problem:** `ThreadUtilities.sleep(long)` wraps `InterruptedException` in a
`RuntimeException`. How do you test the interrupt path without flakiness?

**Solution:** Spawn a worker thread, synchronize with a `CountDownLatch`,
interrupt it, then assert the exception type.

```java
package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

public class ThreadUtilitiesTest {

  @Test
  public void sleep_happyPath_sleepsAtLeast10ms() {
    long start = System.nanoTime();
    ThreadUtilities.sleep(10);
    long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    assertTrue("Expected ≥10ms, got " + elapsed + "ms", elapsed >= 10);
  }

  @Test
  public void sleep_whenInterrupted_throwsRuntimeException() throws Exception {
    CountDownLatch sleeping = new CountDownLatch(1);
    AtomicReference<Throwable> caught = new AtomicReference<>();

    Thread worker = new Thread(() -> {
      sleeping.countDown();
      try {
        ThreadUtilities.sleep(5000);
      } catch (RuntimeException e) {
        caught.set(e);
      }
    });

    worker.start();
    assertTrue("Worker did not start", sleeping.await(2, TimeUnit.SECONDS));
    worker.interrupt();
    worker.join(2000);

    assertNotNull("No exception caught", caught.get());
    assertTrue("Cause should be InterruptedException",
        caught.get().getCause() instanceof InterruptedException);
  }

  @Test
  public void sleep_zeroDuration_returnsImmediately() {
    long start = System.nanoTime();
    ThreadUtilities.sleep(0);
    long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    assertTrue("Zero-sleep should be fast", elapsed < 100);
  }
}
```

**Key insight:** The `CountDownLatch` guarantees the worker is inside `sleep()`
before we interrupt it. The 2-second join timeout prevents the test from
hanging if something goes wrong.

Run it:

```sh
mvn test -pl core/util -Dtest=edu.cmu.cs.dennisc.java.lang.ThreadUtilitiesTest
```

## Pattern 2: Private test enums for EnumUtilities

**Problem:** `EnumUtilities.getEnumConstants(Class[], Criterion)` needs enum
class arrays. Using JDK enums couples tests to internal JDK details.

**Solution:** Define private enums inside the test class.

```java
private enum Color { RED, GREEN, BLUE }
private enum Size { SMALL, MEDIUM, LARGE }

@SuppressWarnings("unchecked")
@Test
public void getEnumConstants_multipleEnumClasses_combinesAll() {
  Class<?>[] classes = { Color.class, Size.class };
  List<?> result = EnumUtilities.getEnumConstants(classes, null);
  assertEquals(6, result.size());
  // All Color and Size constants present
}

@SuppressWarnings("unchecked")
@Test
public void getEnumConstants_emptyClassArray_returnsEmptyList() {
  Class<?>[] classes = {};
  List<?> result = EnumUtilities.getEnumConstants(classes, null);
  assertTrue(result.isEmpty());
}

@SuppressWarnings("unchecked")
@Test
public void getEnumConstants_rejectAllCriterion_returnsEmptyList() {
  Class<?>[] classes = { Color.class };
  List<?> result = EnumUtilities.getEnumConstants(classes, e -> false);
  assertTrue(result.isEmpty());
}
```

**Key insight:** Private inner enums are self-contained. The test controls
the exact number of constants and can verify exact counts.

## Pattern 3: Characterizing stream edge cases

**Problem:** `InputStreamUtilities.getBytes()` has a bug where an empty stream
(where `available()` returns 0 on the first call) causes a
`NullPointerException`. How do you test this without fixing the source?

**Solution:** Write a characterization test that documents the existing behavior.

```java
@Test(expected = NullPointerException.class)
public void getBytes_emptyStream_throwsNPE() throws Exception {
  // Characterization: empty ByteArrayInputStream has available()==0,
  // so getBytes() never initializes baos, causing NPE at line 100.
  InputStream empty = new ByteArrayInputStream(new byte[0]);
  InputStreamUtilities.getBytes(empty);
}

@Test
public void getBytes_singleByte_returnsOneByte() throws Exception {
  InputStream is = new ByteArrayInputStream(new byte[] { 42 });
  byte[] result = InputStreamUtilities.getBytes(is);
  assertEquals(1, result.length);
  assertEquals(42, result[0]);
}

@Test
public void drain_nullOutputStream_discardsData() throws Exception {
  InputStream is = new ByteArrayInputStream("hello".getBytes());
  boolean result = InputStreamUtilities.drain(is, null);
  assertTrue(result);
}
```

**Key insight:** The `@Test(expected = NullPointerException.class)` annotation
documents the bug without modifying the source. When the bug is eventually
fixed, this test will fail, signaling that the fix should be verified.

## Pattern 4: Test resource loading

**Problem:** `InputStreamUtilities.getBytes(Class, String)` loads a resource
from the classpath. Tests need a known resource file.

**Solution:** Create a test resource and load it by class.

Create `core/util/src/test/resources/test-resource.txt`:

```
test content
```

Test:

```java
@Test
public void getBytes_classResource_returnsKnownContent() throws Exception {
  byte[] bytes = InputStreamUtilities.getBytes(
      getClass(), "/test-resource.txt");
  String content = new String(bytes, StandardCharsets.UTF_8).trim();
  assertEquals("test content", content);
}
```

**Key insight:** The resource path must start with `/` for an absolute
classpath lookup. The file lives in `src/test/resources/` which Maven adds
to the test classpath automatically.

## Pattern 5: XML round-trip with edge cases

**Problem:** `XMLUtilities` supports read/write of XML documents. Tests need
to verify encoding, malformed input handling, and child element queries.

**Solution:** Use `ByteArrayInputStream`/`ByteArrayOutputStream` for fully
in-memory XML testing.

```java
@Test
public void writeAndRead_withEmoji_roundTrips() throws Exception {
  Document doc = XMLUtilities.createDocument();
  Element root = doc.createElement("msg");
  root.setTextContent("Hello 🌍🎉");
  doc.appendChild(root);

  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  XMLUtilities.write(doc, baos);

  Document parsed = XMLUtilities.read(
      new ByteArrayInputStream(baos.toByteArray()));
  assertEquals("Hello 🌍🎉",
      parsed.getDocumentElement().getTextContent());
}

@Test(expected = RuntimeException.class)
public void read_malformedXml_throwsRuntimeException() throws Exception {
  byte[] bad = "<root><unclosed>".getBytes(StandardCharsets.UTF_8);
  XMLUtilities.read(new ByteArrayInputStream(bad));
}

@Test
public void getSingleChildElementByTagName_multipleMatches_returnsFirst() throws Exception {
  Document doc = XMLUtilities.createDocument();
  Element root = doc.createElement("root");
  doc.appendChild(root);
  Element child1 = doc.createElement("item");
  child1.setTextContent("first");
  root.appendChild(child1);
  Element child2 = doc.createElement("item");
  child2.setTextContent("second");
  root.appendChild(child2);

  Element found = XMLUtilities.getSingleChildElementByTagName(root, "item");
  assertEquals("first", found.getTextContent());
}
```

**Key insight:** No filesystem needed. `ByteArrayOutputStream` captures the
XML bytes, and `ByteArrayInputStream` feeds them back. The emoji test verifies
UTF-16 encoding (configured in `XMLUtilities.getTransformer()`) survives the
round-trip including surrogate pairs.

## Pattern 6: Concurrent stress testing (lgna ThreadUtilities)

**Problem:** `ThreadUtilities.doTogether()` and `eachInTogether()` spawn
threads. Tests must verify all runnables complete and exceptions propagate.

**Solution:** Use `AtomicInteger` for counting, `CountDownLatch` for
synchronization, and generous timeouts.

```java
@Test
public void doTogether_tenRunnables_allComplete() {
  AtomicInteger counter = new AtomicInteger(0);
  Runnable[] runnables = new Runnable[10];
  Arrays.fill(runnables, (Runnable) counter::incrementAndGet);
  ThreadUtilities.doTogether(runnables);
  assertEquals(10, counter.get());
}

@Test
public void eachInTogether_twentyItems_allProcessed() {
  AtomicInteger sum = new AtomicInteger(0);
  Integer[] items = new Integer[20];
  Arrays.setAll(items, i -> i + 1);
  ThreadUtilities.eachInTogether(sum::addAndGet, items);
  assertEquals(210, sum.get()); // sum of 1..20
}

@Test(expected = RuntimeException.class)
public void eachInTogether_bodyThrows_propagatesException() {
  ThreadUtilities.eachInTogether(item -> {
    if ("boom".equals(item)) {
      throw new RuntimeException("body error");
    }
  }, "ok", "boom", "ok");
}
```

**Key insight:** `doTogether()` blocks until all threads complete, so
assertion of `counter.get()` after the call is safe without extra
synchronization. Exception propagation tests verify that the concurrent
framework surfaces errors from worker threads.

## Pattern 7: SystemProperty compareTo and toString contracts

**Problem:** `SystemProperty` implements `Comparable<SystemProperty>` and
overrides `toString()`. Tests must pin these contracts.

```java
@Test
public void toString_matchesExactFormat() {
  SystemProperty prop = new SystemProperty("k", "v");
  assertEquals("SystemProperty[k:v]", prop.toString());
}

@Test
public void compareTo_reflexive() {
  SystemProperty prop = new SystemProperty("key", "value");
  assertEquals(0, prop.compareTo(prop));
}

@Test
public void compareTo_emptyKeys() {
  SystemProperty empty1 = new SystemProperty("", "a");
  SystemProperty empty2 = new SystemProperty("", "b");
  assertEquals(0, empty1.compareTo(empty2));
}

@Test
public void sort_ordersAlphabeticallyByKey() {
  List<SystemProperty> props = Arrays.asList(
      new SystemProperty("zebra", "1"),
      new SystemProperty("alpha", "2"),
      new SystemProperty("middle", "3")
  );
  Collections.sort(props);
  assertEquals("alpha", props.get(0).getKey());
  assertEquals("middle", props.get(1).getKey());
  assertEquals("zebra", props.get(2).getKey());
}
```

## Pattern 8: FileUtilities deep-path edge cases

**Problem:** `FileUtilities` has many methods with null-handling, empty-directory,
and timestamp behaviors that vary. Tests must cover each edge without relying on
Swing-dependent paths.

```java
@Rule
public TemporaryFolder tempDir = new TemporaryFolder();

@Test
public void listFiles_emptyDirectory_returnsEmptyArray() throws Exception {
  File dir = tempDir.newFolder("empty");
  File[] files = FileUtilities.listFiles(dir, f -> true);
  assertNotNull(files);
  assertEquals(0, files.length);
}

@Test
public void getExtension_noExtension_returnsNull() {
  assertNull(FileUtilities.getExtension("README"));
}

@Test
public void getExtension_multipleDots_returnsLast() {
  assertEquals("gz", FileUtilities.getExtension("archive.tar.gz"));
}

@Test
public void copyFile_createsParentDirectories() throws Exception {
  File src = tempDir.newFile("source.txt");
  TextFileUtilities.write(src, "data");
  File dest = new File(tempDir.getRoot(), "sub/dir/copy.txt");
  FileUtilities.copyFile(src, dest);
  assertTrue(dest.exists());
  assertEquals("data", TextFileUtilities.read(dest));
}
```

**Key insight:** Always use `@Rule TemporaryFolder` — it creates unique
directories per test and cleans up automatically. Never hardcode paths or
use `File.createTempFile` with `deleteOnExit()`.

## Verifying your work

After writing tests, verify in three steps:

### Step 1: Compile

```sh
mvn test-compile -pl core/util
```

### Step 2: Run

```sh
mvn test -pl core/util
```

### Step 3: Measure coverage

```sh
mvn -pl core/util -Pcoverage verify
```

Open `core/util/target/site/jacoco/index.html` and navigate to the target
class. Look for green (covered) vs. red (uncovered) highlighting.

### Step 4: Count net new lines

```sh
git diff --stat main -- core/util/src/test/ | tail -1
```

Target for Phase 2: 2,400+ insertions.

## Common mistakes

| Mistake | Fix |
| --- | --- |
| Using `Thread.sleep()` directly in assertions | Use `CountDownLatch` for synchronization |
| Forgetting `@SuppressWarnings("unchecked")` on enum arrays | Add the annotation to suppress generic array creation warnings |
| Testing `FileUtilities.getDefaultDirectory()` | Skip it — `HeadlessException` in CI |
| Using `File.createTempFile` instead of `TemporaryFolder` | Always use `@Rule TemporaryFolder` for auto-cleanup |
| Modifying source code to make tests pass | Characterize existing behavior; never modify production code |
| Unbounded thread waits | Always use `CountDownLatch` or `Thread.join(timeout)` |

## Next steps

- Read the [Phase 2 reference](../reference/core-util-coverage-sprint-phase2.md)
  for the complete test inventory and coverage arithmetic.
- Follow [Expand coverage ratchets](../howto/expand-coverage-ratchets.md) to
  raise the `core/util` floor to 70% after merging.
- Check the JaCoCo HTML report for any remaining uncovered pure-logic methods.
