# How to raise core/util coverage from 46% to 70%

Use this guide to understand, run, extend, and verify the Phase 2 `core/util`
test expansion that raises line coverage from ~46% to 70%+.

## Prerequisites

```sh
git submodule update --init tweedle-lang
java -version  # JDK 17+
```

## Quick start

### Compile and run all core/util tests

```sh
mvn test -pl core/util
```

All 8 expanded files plus the 1 new `ThreadUtilitiesTest` (dennisc) execute
alongside the existing 80+ test files. Expected: all tests pass in under 90
seconds with no display, GPU, or network required.

### Run only Phase 2 tests

```sh
mvn test -pl core/util \
  -Dtest="edu.cmu.cs.dennisc.java.lang.ThreadUtilitiesTest,\
edu.cmu.cs.dennisc.java.lang.EnumUtilitiesTest,\
edu.cmu.cs.dennisc.java.lang.SystemPropertyTest,\
edu.cmu.cs.dennisc.java.io.InputStreamUtilitiesTest,\
edu.cmu.cs.dennisc.java.io.FileUtilitiesTest,\
edu.cmu.cs.dennisc.java.io.TextFileUtilitiesTest,\
edu.cmu.cs.dennisc.xml.XMLUtilitiesTest,\
org.lgna.common.ThreadUtilitiesTest"
```

### Generate coverage report

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/util=70.0
```

Open the HTML report for visual inspection:

```sh
xdg-open core/util/target/site/jacoco/index.html 2>/dev/null || \
  echo "Report at: core/util/target/site/jacoco/index.html"
```

### Count net new test lines

```sh
git diff --stat main -- core/util/src/test/ | tail -1
```

Target: 2,400+ net new lines across test files.

## What was added

### 1. `dennisc.ThreadUtilitiesTest` (new file)

Tests `ThreadUtilities.sleep()` — the only method in this 56-line class:

- **Happy path**: Sleep 10ms, assert elapsed ≥ 10ms
- **Interrupt path**: Spawn thread → call `sleep(5000)` → interrupt →
  catch `RuntimeException` wrapping `InterruptedException`
- **Zero-duration sleep**: `sleep(0)` returns immediately

Uses `CountDownLatch` with 2-second timeout to prevent runaway threads.

### 2. `EnumUtilitiesTest` (expanded)

New tests exercise `getEnumConstants()` with:

- Multiple enum class arrays (private `Color` + `Size` test enums)
- Empty class array → empty list
- Criterion that rejects all values → empty list
- Criterion that accepts subset → filtered list
- `getFld()` on every value of a test enum

### 3. `SystemPropertyTest` (expanded)

New tests cover:

- `toString()` exact format: `SystemProperty[key:value]`
- `compareTo()` reflexivity (same object → 0)
- `compareTo()` with identical keys but different values → 0
- Empty key and empty value strings
- Sorting a list of `SystemProperty` objects via `Collections.sort()`

### 4. `InputStreamUtilitiesTest` (expanded)

New tests cover:

- `drain()` with null `OutputStream` (discard path)
- `drain()` on exhausted stream returns `true`
- `getBytes()` with single-byte stream
- `getBytes()` with multi-buffer stream (>8KB)
- `getBytes(File)` round-trip via `TemporaryFolder`
- `getBytes(Class, String)` using `test-resource.txt`
- `getBytes()` on empty stream → characterizes NPE behavior

### 5. `FileUtilitiesTest` (expanded)

New tests cover:

- `getExtension()` / `getBaseName()` with null → characterize behavior
- `getCanonicalPathIfPossible()` with null → characterize behavior
- `listFiles()` on empty directory → empty array
- `listDescendants()` with depth 0
- `copyFile()` creates parent directories
- `getModifiedDateTime()` with null → `LocalDateTime.MIN`
- `getCreatedDateTime()` on nonexistent file → `LocalDateTime.MIN` (note:
  `getCreatedDateTime(null)` throws NPE — no null guard unlike `getModifiedDateTime`)
- Various extension edge cases: no dot, multiple dots, trailing dot

### 6. `TextFileUtilitiesTest` (expanded)

New tests cover:

- Write to nested non-existent directory (parent creation)
- Read nonexistent file → `RuntimeException`
- Write/read with special characters: tabs, newlines, Unicode
- Write/read with CJK characters and emoji
- Empty string write/read round-trip
- Large file (10,000 lines) round-trip

### 7. `XMLUtilitiesTest` (expanded)

New tests cover:

- Write to nested directory (parent creation)
- Read malformed XML → `RuntimeException`
- Write/read with emoji and special Unicode (UTF-16 surrogate pairs)
- `getSingleChildElementByTagName()` with multiple matches (returns first)
- `getSingleChildElementByTagName()` with no matches
- `getChildElementsByTagName()` returns complete list
- Document with attributes, CDATA sections, comments
- Empty document round-trip

### 8. `ThreadUtilitiesTest` (lgna, expanded)

New tests cover:

- `doTogether()` with 10+ runnables (concurrent stress)
- `doTogether()` exception in one of many runnables still propagates
- `eachInTogether()` exception propagation from body
- `eachInTogether()` with 20+ items (concurrent stress)
- `eachInTogether()` with null items in array
- Verify all runnables complete before `doTogether()` returns

## Test resource files

| Path | Purpose |
| --- | --- |
| `core/util/src/test/resources/test-resource.txt` | Content: `test content` — used by `InputStreamUtilitiesTest.getBytes_classResource` |

## Troubleshooting

### Thread tests flaky on slow CI

Thread timing tests use `CountDownLatch` with generous 2-second timeouts.
If a thread test fails intermittently:

1. Check if the CI runner is under memory pressure
2. Increase the timeout by editing the `SECONDS` constant in the test
3. If truly timing-dependent, add `@Ignore("flaky on slow CI")` and file an issue

### `HeadlessException` in FileUtilitiesTest

`FileUtilities.getDefaultDirectory()` is intentionally **not tested** — it
calls Swing's `FileSystemView`. If you accidentally add a test that calls
it, you'll see `HeadlessException` in CI. Guard with:

```java
assumeFalse(GraphicsEnvironment.isHeadless());
```

### InputStreamUtilities NPE on empty stream

This is expected, documented behavior. The `getBytes()` method does not
handle the case where `available()` returns 0 on the first call — `baos`
is never initialized and `baos.toByteArray()` throws `NullPointerException`.
The characterization test captures this exact behavior.

### XMLUtilities tests fail with `TransformerException`

Ensure the JDK includes the default XSLT transformer. JDK 17+ includes it.
If using a stripped-down JRE, add `java.xml` to the module path.

### Coverage report shows <70%

1. Verify all Phase 2 test files are present in the test tree
2. Run `mvn test -pl core/util` and confirm all tests pass
3. Check for excluded classes inflating the denominator
4. Open the HTML report and sort by "Missed Lines" to find gaps

## Conventions for adding more tests

Follow the patterns established in Phase 1 and Phase 2:

1. **JUnit 4** — `org.junit.Test`, `org.junit.Assert`, `@Rule TemporaryFolder`
2. **Method naming** — `methodUnderTest_condition_expectedResult`
3. **State isolation** — `@Before`/`@After` for any static state mutation
4. **No filesystem leaks** — `TemporaryFolder` only, never `deleteOnExit()`
5. **Thread safety** — `CountDownLatch` with bounded timeouts
6. **Characterize, don't fix** — Document existing bugs in tests without
   modifying source code
7. **Inner test classes** — Define private enums, POJOs, and test doubles
   inside the test class

## Related documentation

- [Reference: Phase 2 coverage sprint](../reference/core-util-coverage-sprint-phase2.md)
- [Reference: Phase 1 coverage sprint](../reference/core-util-coverage-sprint.md)
- [How to run coverage sprint tests](run-coverage-sprint-tests.md)
- [How to expand coverage ratchets](expand-coverage-ratchets.md)
- [Tutorial: Edge-case testing patterns](../tutorials/core-util-edge-case-testing.md)
- [Tutorial: Write headless-safe tests](../tutorials/core-util-headless-test-coverage.md)
