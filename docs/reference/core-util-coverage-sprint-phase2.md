# core/util coverage sprint phase 2 — 46% → 70%+

Issue #759 pushes `core/util` line coverage from ~46% to 70%+ by expanding
eight existing test files and creating one new test class. This phase targets
the remaining untested utility classes: `ThreadUtilities` (dennisc),
`EnumUtilities`, `SystemProperty`, `InputStreamUtilities`, `FileUtilities`
deep paths, `TextFileUtilities` edge cases, `XMLUtilities` advanced scenarios,
and `ThreadUtilities` (lgna) concurrency patterns.

This builds on the Phase 1 work from Issues #736 and #751 that raised coverage
from 3.63% → ~42.7% → ~46%.

## Test inventory

### New test file

| Test file | Package | Source class | Est. lines |
| --- | --- | --- | ---: |
| `ThreadUtilitiesTest` | `edu.cmu.cs.dennisc.java.lang` | `ThreadUtilities` (sleep happy path, interrupt→RuntimeException) | 200 |

### Expanded test files

| Test file | Package | Source classes covered | Before | After | Net new |
| --- | --- | --- | ---: | ---: | ---: |
| `EnumUtilitiesTest` | `edu.cmu.cs.dennisc.java.lang` | `EnumUtilities` (multi-class array, empty array, reject-all criterion, private test enums) | 47 | 280+ | ~235 |
| `SystemPropertyTest` | `edu.cmu.cs.dennisc.java.lang` | `SystemProperty` (toString format, compareTo reflexivity, empty key/value, null-safe) | 44 | 280+ | ~235 |
| `InputStreamUtilitiesTest` | `edu.cmu.cs.dennisc.java.io` | `InputStreamUtilities` (drain null-OutputStream, getBytes empty/single-byte, class resource) | 69 | 300+ | ~230 |
| `FileUtilitiesTest` | `edu.cmu.cs.dennisc.java.io` | `FileUtilities` (null edge cases, empty dir listing, depth-0 descendants, copyFile parent creation, timestamp nulls) | 321 | 650+ | ~330 |
| `TextFileUtilitiesTest` | `edu.cmu.cs.dennisc.java.io` | `TextFileUtilities` (parent-dir creation on write, nonexistent read→RuntimeException, special chars, encoding) | 148 | 480+ | ~330 |
| `XMLUtilitiesTest` | `edu.cmu.cs.dennisc.xml` | `XMLUtilities` (nested-dir write, malformed XML→RuntimeException, emoji round-trip, multi-match getSingleChild) | 242 | 560+ | ~320 |
| `ThreadUtilitiesTest` (lgna) | `org.lgna.common` | `ThreadUtilities` (10+ runnables, eachInTogether exception propagation, concurrent stress) | 108 | 400+ | ~290 |

**Total estimated net new test lines:** ~2,400+

## Test patterns

### Pattern: Interrupt path testing (dennisc ThreadUtilities)

```java
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
  assertTrue(sleeping.await(2, TimeUnit.SECONDS));
  worker.interrupt();
  worker.join(2000);
  assertNotNull(caught.get());
  assertTrue(caught.get().getCause() instanceof InterruptedException);
}
```

The test spawns a worker thread, waits for it to enter `sleep()` via a
`CountDownLatch`, interrupts it, then asserts the `RuntimeException` wraps
an `InterruptedException`. Runs in <100ms.

### Pattern: Private test enums (EnumUtilities)

```java
private enum Color { RED, GREEN, BLUE }
private enum Size { SMALL, MEDIUM, LARGE }

@Test
public void getEnumConstants_multipleEnumClasses_combinesAll() {
  Class<?>[] classes = { Color.class, Size.class };
  List<?> result = EnumUtilities.getEnumConstants(classes, null);
  assertEquals(6, result.size());
}
```

Self-contained test enums avoid depending on JDK enum internals.

### Pattern: Reject-all criterion

```java
@Test
public void getEnumConstants_rejectAllCriterion_returnsEmptyList() {
  Class<?>[] classes = { Thread.State.class };
  List<?> result = EnumUtilities.getEnumConstants(classes, e -> false);
  assertTrue(result.isEmpty());
}
```

### Pattern: toString format verification (SystemProperty)

```java
@Test
public void toString_matchesExactFormat() {
  SystemProperty prop = new SystemProperty("java.version", "17");
  assertEquals("SystemProperty[java.version:17]", prop.toString());
}
```

Pins the `toString()` contract: `ClassName[key:value]`.

### Pattern: Class resource loading (InputStreamUtilities)

```java
@Test
public void getBytes_classResource_returnsExpectedContent() throws Exception {
  byte[] bytes = InputStreamUtilities.getBytes(
      InputStreamUtilitiesTest.class, "/test-resource.txt");
  assertEquals("test content", new String(bytes, StandardCharsets.UTF_8).trim());
}
```

Uses a test resource file at `core/util/src/test/resources/test-resource.txt`
containing `test content`.

### Pattern: Empty stream edge case

```java
@Test(expected = NullPointerException.class)
public void getBytes_emptyStream_throwsNPE() throws Exception {
  InputStream empty = new ByteArrayInputStream(new byte[0]);
  InputStreamUtilities.getBytes(empty);
}
```

Characterizes the existing behavior: `getBytes()` on a zero-available stream
hits a null `baos.toByteArray()` call. This is a known defect documented but
not fixed per the no-source-modification constraint.

### Pattern: Concurrent stress (lgna ThreadUtilities)

```java
@Test
public void doTogether_tenRunnables_allComplete() {
  AtomicInteger counter = new AtomicInteger(0);
  Runnable[] runnables = new Runnable[10];
  Arrays.fill(runnables, (Runnable) counter::incrementAndGet);
  ThreadUtilities.doTogether(runnables);
  assertEquals(10, counter.get());
}
```

### Pattern: Malformed XML characterization

```java
@Test(expected = RuntimeException.class)
public void read_malformedXml_throwsRuntimeException() throws Exception {
  InputStream is = new ByteArrayInputStream(
      "<root><unclosed>".getBytes(StandardCharsets.UTF_8));
  XMLUtilities.read(is);
}
```

### Pattern: File timestamp null safety

```java
@Test
public void getModifiedDateTime_nullFile_returnsLocalDateTimeMIN() {
  assertEquals(LocalDateTime.MIN, FileUtilities.getModifiedDateTime(null));
}
```

## Test resource file

A single test resource supports the `InputStreamUtilities.getBytes(Class, String)` test:

```
core/util/src/test/resources/test-resource.txt
```

Content: `test content` (12 bytes + newline).

## Hard constraints

| Constraint | Enforced |
| --- | --- |
| JUnit 4 only | `org.junit.Test`, `@Rule TemporaryFolder` — no JUnit 5 |
| No source modifications | All tests characterize existing behavior |
| No GUI/network dependencies | `ByteArrayInputStream`, `TemporaryFolder`, in-memory XML |
| Headless CI safe | No AWT, Swing, or display-dependent paths |
| Thread safety | `CountDownLatch` with ≤2s timeouts; no unbounded waits |
| System property isolation | `@Before`/`@After` save/restore for any modified properties |
| Temp file cleanup | All via `@Rule TemporaryFolder` — auto-deleted |

## Excluded source code

| Exclusion | Reason |
| --- | --- |
| `FileUtilities.getDefaultDirectory()` | Uses Swing `FileSystemView` — `HeadlessException` in CI |
| `FileUtilities.copyFile` exception path | Leaks `FileChannel` — test happy path only, note leak |
| `XMLUtilities` XXE attack surface | Document vulnerability, don't test with attack payloads |
| `DeveloperUtilities` (intrinsically debug) | Side-effect-only class, no testable return values |

## Coverage arithmetic

| Metric | Value |
| --- | --- |
| Total coverable lines (JaCoCo) | ~10,001 |
| Previously covered lines (Phase 1) | ~4,600 (46%) |
| New lines exercised (estimated) | ~2,400+ |
| Total covered lines (estimated) | ~7,000+ |
| New coverage (estimated) | **~70.0%** |

## Source file tree

```
core/util/src/test/java/
├── edu/cmu/cs/dennisc/
│   ├── java/
│   │   ├── io/
│   │   │   ├── FileUtilitiesTest.java       (expanded)
│   │   │   ├── InputStreamUtilitiesTest.java (expanded)
│   │   │   └── TextFileUtilitiesTest.java    (expanded)
│   │   └── lang/
│   │       ├── EnumUtilitiesTest.java        (expanded)
│   │       ├── SystemPropertyTest.java       (expanded)
│   │       └── ThreadUtilitiesTest.java      (NEW)
│   └── xml/
│       └── XMLUtilitiesTest.java             (expanded)
└── org/lgna/common/
    └── ThreadUtilitiesTest.java              (expanded)

core/util/src/test/resources/
└── test-resource.txt                         (NEW)
```

## Running the tests

Run the full `core/util` suite:

```sh
mvn test -pl core/util
```

Run a specific expanded test:

```sh
mvn test -pl core/util -Dtest=edu.cmu.cs.dennisc.java.lang.ThreadUtilitiesTest
mvn test -pl core/util -Dtest=edu.cmu.cs.dennisc.xml.XMLUtilitiesTest
```

Run with coverage:

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/util=70.0
```

## Known behaviors documented by characterization tests

| Class | Behavior | Test |
| --- | --- | --- |
| `InputStreamUtilities.getBytes()` | NPE on empty stream (null `baos`) | `getBytes_emptyStream_throwsNPE` |
| `FileUtilities.copyFile()` | Leaks `FileChannel` on IOException | Happy-path only; leak noted |
| `XMLUtilities.read()` | No XXE protection (accepts entity declarations) | Characterization only; no attack payloads |
| `ThreadUtilities.sleep()` | Wraps `InterruptedException` in `RuntimeException` | `sleep_whenInterrupted_throwsRuntimeException` |
| `FileUtilities.getCreatedDateTime(null)` | NPE — no null guard (unlike `getModifiedDateTime`) | Not tested — null-arg NPE is Java default |
| `EnumUtilities` constructor | Throws `AssertionError` (private, unreachable) | Not tested — private constructor |

## Related documentation

- [Phase 1 coverage sprint](core-util-coverage-sprint.md) — 42.7% → 46%
- [How to raise core/util coverage to 70%](../howto/raise-core-util-coverage-to-70.md)
- [How to run coverage sprint tests](../howto/run-coverage-sprint-tests.md)
- [Tutorial: Edge-case testing patterns](../tutorials/core-util-edge-case-testing.md)
- [Tutorial: Write headless-safe tests](../tutorials/core-util-headless-test-coverage.md)
- [Expand coverage ratchets](../howto/expand-coverage-ratchets.md)
- [Coverage reporting and ratchets](coverage-reporting.md)
